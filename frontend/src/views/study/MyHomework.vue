<template>
    <a-page-header title="我的作业">
        <template #extra>
            <a-radio-group type="button" @change="getHomeworkList" v-model:model-value="status"
                style="margin-right:10px">
                <a-radio :value="0">全部</a-radio>
                <a-radio :value="1">未开始</a-radio>
                <a-radio :value="2">进行中</a-radio>
                <a-radio :value="3">已结束</a-radio>
            </a-radio-group>
        </template>
    </a-page-header>
    <div class="homework-container">
        <!-- 按课程分组的折叠面板 -->
        <div v-if="groupedHomeworks.length > 0">
            <a-collapse :default-active-key="defaultOpenKeys">
                <a-collapse-item v-for="group in groupedHomeworks" :key="group.courseId" :header="group.courseName">
                    <ul class="homework-list">
                        <li class="homework-item ebutton-hover" v-for="item in group.homeworks" :key="item.id">
                            <div class="homework-info-wrap">
                                <a-avatar class="avatar" :size="40" :style="{'background-color': getHomeworkStatus(item).color }" shape="square">
                                    {{ getHomeworkStatus(item).info }}
                                </a-avatar>
                                <div class="homework-info">
                                    <p class="title">{{ item.title }}</p>
                                    <p class="date">{{ formatDateTime(item.startTime) }} ~ {{ formatDateTime(item.endTime) }}</p>
                                    <p class="description" v-if="item.description">{{ item.description }}</p>
                                </div>
                            </div>
                            <div class="homework-action">
                                <homework-info-button :item="item" />
                            </div>
                        </li>
                    </ul>
                </a-collapse-item>
            </a-collapse>
        </div>
        <a-pagination 
            v-if="list.length > 0 && total > pageSize" 
            :total="total" 
            v-model:current="currpage" 
            :page-size="pageSize" 
            @change="handlePageChange" 
            style="justify-content: center; margin: 10px 0; display: flex;" 
        />
        <a-empty v-if="list.length === 0" description="暂无作业" />
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import dayjs from 'dayjs';
import { Message } from '@arco-design/web-vue';
import HomeworkInfoButton from '../../components/HomeworkInfoButton.vue';
import { getHomeworkInfoListRequest, getHomeworkSubmitStatusRequest } from '../../apis/homework-api';
import { courseListRequest } from '../../apis/course-api.js';

const status = ref(0);
const list = ref([]);
const currpage = ref(1);
const total = ref(0);
const pageSize = ref(10);
const loading = ref(false);
const myCourses = ref([]);
const defaultOpenKeys = ref([]);

// 按课程分组的作业列表
const groupedHomeworks = computed(() => {
    const groups = [];
    const courseMap = new Map();
    
    // 先按课程ID分组
    list.value.forEach(homework => {
        const courseId = homework.courseId;
        if (!courseMap.has(courseId)) {
            courseMap.set(courseId, {
                courseId: courseId,
                courseName: homework.courseName || '未知课程',
                homeworks: []
            });
        }
        courseMap.get(courseId).homeworks.push(homework);
    });
    
    // 转换为数组并排序
    courseMap.forEach(group => {
        // 对每个课程内的作业按时间排序
        group.homeworks.sort((a, b) => {
            const aTime = dayjs(a.startTime);
            const bTime = dayjs(b.startTime);
            
            if (aTime.isAfter(dayjs()) && !bTime.isAfter(dayjs())) {
                return -1;
            }
            if (!aTime.isAfter(dayjs()) && bTime.isAfter(dayjs())) {
                return 1;
            }
            
            return aTime.isBefore(bTime) ? -1 : 1;
        });
        
        groups.push(group);
    });
    
    // 按课程名称排序
    groups.sort((a, b) => a.courseName.localeCompare(b.courseName));
    
    return groups;
});

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
    return dayjs(dateTimeStr).format('YYYY-MM-DD HH:mm');
};

// 获取我的所有课程
const getMyCourses = async () => {
    try {
        const { data } = await courseListRequest('student', 1, 0);
        if (data && data.data && data.data.list) {
            myCourses.value = data.data.list;
            console.log('获取到的课程列表:', myCourses.value);
            return myCourses.value.map(course => course.id);
        } else {
            console.log('未获取到课程列表数据');
            return [];
        }
    } catch (err) {
        console.error('获取课程列表失败', err);
        Message.error('获取课程列表失败');
        return [];
    }
};

// 获取作业列表
const getHomeworkList = async () => {
    loading.value = true;
    
    try {
        // 先获取所有课程ID
        const courseIds = await getMyCourses();
        if (courseIds.length === 0) {
            list.value = [];
            loading.value = false;
            return;
        }
        
        // 存储所有课程的作业
        let allHomeworks = [];
        let totalHomeworks = 0;
        
        // 为每个课程获取作业列表
        for (const courseId of courseIds) {
            try {
                const res = await getHomeworkInfoListRequest(courseId, status.value, 1, 100);
                if (res.data && res.data.data) {
                    const courseHomeworks = res.data.data.list || [];
                    // 添加课程名称
                    const courseName = myCourses.value.find(c => c.id === courseId)?.name || '未知课程';
                    courseHomeworks.forEach(homework => {
                        homework.courseName = courseName;
                        homework.courseId = courseId;
                    });
                    
                    allHomeworks = [...allHomeworks, ...courseHomeworks];
                    totalHomeworks += res.data.data.total || 0;
                }
            } catch (err) {
                console.error(`获取课程 ${courseId} 的作业列表失败`, err);
            }
        }
        
        // 分页处理 - 不再分页，显示全部
        const pagedHomeworks = allHomeworks;
        
        if (pagedHomeworks.length > 0) {
            // 获取作业提交状态
            const homeworkIds = pagedHomeworks.map(item => item.id);
            try {
                const statusRes = await getHomeworkSubmitStatusRequest(homeworkIds);
                if (statusRes.data && statusRes.data.data) {
                    const submitStatus = statusRes.data.data;
                    pagedHomeworks.forEach(item => {
                        item.isSubmitted = submitStatus[item.id] || false;
                    });
                }
            } catch (err) {
                console.error('获取作业提交状态失败', err);
            }
        }
        
        list.value = pagedHomeworks;
        total.value = allHomeworks.length;
        
        // 设置默认展开的面板
        if (groupedHomeworks.value.length > 0) {
            defaultOpenKeys.value = groupedHomeworks.value.map(group => group.courseId);
        }
    } catch (err) {
        console.error('获取作业列表失败', err);
        Message.error('获取作业列表失败');
        list.value = [];
    } finally {
        loading.value = false;
    }
};

// 页码变化
const handlePageChange = (page) => {
    currpage.value = page;
    getHomeworkList();
};

// 获取作业状态
const getHomeworkStatus = (homeworkInfo) => {
    const startTime = homeworkInfo.startTime;
    const endTime = homeworkInfo.endTime;
    const now = dayjs();
    let status = {
        info: '进行中',
        color: 'rgb(var(--primary-6))'
    };
    
    if (now.isAfter(endTime)) {
        status.info = '已结束';
        status.color = 'rgb(var(--danger-6))';
    } else if (now.isBefore(startTime)) {
        status.info = '未开始';
        status.color = 'var(--color-fill-4)';
    }
    
    return status;
};

// 初始加载作业列表
onMounted(() => {
    getHomeworkList();
});
</script>

<style lang="less" scoped>
.homework-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 16px;
}

.homework-list {
    margin: 0;
    padding: 0;
    
    .homework-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px;
        margin-bottom: 16px;
        background-color: var(--color-bg-1);
        border-radius: 4px;
        transition: all 0.2s;
        
        &:hover {
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        }

        .homework-info-wrap {
            display: flex;

            .avatar {
                margin-right: 10px;
            }

            .homework-info {
                display: flex;
                flex-direction: column;
                justify-content: center;

                .title {
                    color: var(--color-text-1);
                    font-weight: bold;
                }

                .date {
                    color: var(--color-text-3);
                    font-size: 14px;
                    margin-top: 6px;
                }
                
                .description {
                    color: var(--color-text-3);
                    font-size: 14px;
                    margin-top: 6px;
                    max-width: 500px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
            }
        }
        
        .homework-action {
            margin-left: 20px;
        }
    }
}

.empty-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;
    margin-top: 20px;
    background-color: var(--color-bg-2);
    border-radius: 4px;
}

:deep(.arco-collapse-item-header) {
    font-size: 16px;
    font-weight: 600;
}

:deep(.arco-collapse) {
    border: none;
    background-color: transparent;
}

:deep(.arco-collapse-item) {
    margin-bottom: 16px;
    border: 1px solid var(--color-border);
    border-radius: 4px;
    overflow: hidden;
}

:deep(.arco-collapse-item-content) {
    background-color: var(--color-bg-2);
    padding: 16px;
}
</style> 