<template>
    <div class="homework-review-container">
        <div class="review-header">
            <h2>作业批阅</h2>
            <div class="filter-options">
                <div class="filter-label">选择班级：</div>
                <a-select v-model="currClassId" placeholder="选择班级" style="width: 200px; margin-right: 10px;" :loading="classLoading">
                    <a-option v-for="classId in classIds" :key="classId.value" :value="classId.value">
                        {{ classId.label }}
                    </a-option>
                </a-select>
                <a-tooltip content="请选择班级查看对应学生的作业批阅情况">
                    <a-button type="outline" shape="circle">
                        <template #icon><icon-question-circle /></template>
                    </a-button>
                </a-tooltip>
            </div>
        </div>

        <div v-if="classIds.length <= 1" class="no-class-tip">
            <a-alert type="warning" show-icon>
                <template #message>未获取到班级信息，请确保已创建班级并有学生提交了作业</template>
            </a-alert>
        </div>

        <a-spin :loading="loading" tip="加载中，请稍候..." class="full-width-spin">
            <a-alert v-if="loadingDetails" type="info" show-icon style="margin-bottom: 16px;">
                <template #message>正在获取学生作业详情，请稍候...</template>
            </a-alert>
            
            <a-table 
                row-key="id" 
                :columns="columns" 
                :data="studentList" 
                :loading="tableLoading"
                :pagination="{ total, current: page, pageSize }" 
                page-position="bottom" 
                @page-change="pageChange"
                :bordered="true"
                :stripe="true"
                size="medium"
                :scroll="{ x: '100%' }"
            >
                <template #userInfo="{ record }">
                    <div class="user-info">
                        <a-avatar shape="square" class="avatar">
                            <img v-loadImg alt="avatar" :src="record.userAuthInfo?.picture || ''" />
                        </a-avatar>
                        <div>
                            <h3 style="text-overflow: ellipsis;white-space: nowrap;max-width: 120px;overflow: hidden;">
                                {{ record.userAuthInfo?.nickname || record.nickname || '未知用户' }}
                            </h3>
                            <a-tag style="font-weight:bold" color="gray">
                                {{ record.userAuthInfo?.realName || record.realName || '未认证' }}
                            </a-tag>
                        </div>
                    </div>
                </template>
                <template #authInfo="{ record }">
                    <a-trigger>
                        <div class="authInfo">
                            <a-tag color="orangered">
                                {{ record.userAuthInfo?.jobNo || record.jobNo || '信息未认证' }}
                            </a-tag>
                            <a-tag color="blue" v-if="record.userAuthInfo?.schoolName || record.schoolName">
                                {{ record.userAuthInfo?.schoolName || record.schoolName }}
                            </a-tag>
                        </div>
                    </a-trigger>
                </template>
                <template #score="{ record }">
                    <div>
                        <a-tag color="green">{{ record.score || 0 }}</a-tag>
                    </div>
                </template>
                <template #status="{ record }">
                    <a-badge :status="getStatusType(record)" :text="getHomeworkStatus(record)" />
                </template>
                <template #correctNumber="{ record }">
                    <a-tag color="orange">{{ record.correctNumber || 0 }}</a-tag>
                </template>
                <template #reviewStatus="{ record }">
                    <a-tag>{{ record.reviewCount || 0 }}/{{ record.reviewTotal || 0 }}</a-tag>
                </template>
                <template #operate="{ record }">
                    <div class="operate">
                        <a-button type="primary" 
                            @click="toReviewPage(record.userAuthInfo?.userId || record.userId || '')"
                            :disabled="!canReview(record)">
                            查看/批阅
                        </a-button>
                    </div>
                </template>
            </a-table>
        </a-spin>
    </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getHomeworkReviewStudentListRequest, getHomeworkSubmitStatusRequest, getHomeworkStudentDetailRequest } from '@/apis/homework-api';
import { getClassListRequest } from '@/apis/course-api';
import dayjs from 'dayjs';
import { Message } from '@arco-design/web-vue';
import { IconQuestionCircle } from '@arco-design/web-vue/es/icon';

const route = useRoute();
const router = useRouter();
const homeworkInfoId = route.params['homeworkId'];
const studentList = ref([]);
const loading = ref(true);
const tableLoading = ref(true);
const loadingDetails = ref(false);
const page = ref(1);
const pageSize = 10;
const total = ref(0);

// 班级相关
const classIds = ref([]);
const currClassId = ref('');
const classLoading = ref(false); // 用于控制班级选择器的加载状态

// 获取作业状态类型
const getStatusType = (record) => {
    if (record.answerStatus) {
        if (record.answerStatus.value === 21) return 'success'; // 机器批阅
        if (record.answerStatus.value === 22) return 'success'; // 教师批阅
        if (record.answerStatus.value === 11) return 'processing'; // 进行中
    }
    
    if (record.submitTime) return 'success';
    if (record.startTime) return 'processing';
    if (record.score && record.score > 0) return 'success';
    if (record.reviewCount && record.reviewCount > 0) return 'warning';
    
    return 'default';
};

// 获取作业状态
const getHomeworkStatus = (record) => {
    // 如果有answerStatus且有action，则使用它
    if (record.answerStatus && record.answerStatus.action) {
        return record.answerStatus.action;
    }
    
    // 如果有submitTime，说明已提交
    if (record.submitTime) {
        return '已提交';
    }
    
    // 如果有startTime但没有submitTime，说明已开始但未提交
    if (record.startTime) {
        return '进行中';
    }
    
    // 如果有score，说明已经被批阅过
    if (record.score && record.score > 0) {
        return '已批阅';
    }
    
    // 如果reviewCount大于0，说明已经有部分题目被批阅
    if (record.reviewCount && record.reviewCount > 0) {
        return '部分批阅';
    }
    
    // 默认状态
    return '未参加';
};

// 判断是否可以批阅
const canReview = (record) => {
    // 如果有submitTime，说明已提交，可以批阅
    if (record.submitTime) {
        return true;
    }
    
    // 如果answerStatus存在且value为21，说明已经被机器批阅，可以批阅
    if (record.answerStatus && record.answerStatus.value === 21) {
        return true;
    }
    
    // 如果answerStatus存在且value为11，说明正在进行中，不可批阅
    if (record.answerStatus && record.answerStatus.value === 11) {
        return false;
    }
    
    // 如果有score，说明已经被批阅过，可以查看
    if (record.score && record.score > 0) {
        return true;
    }
    
    // 如果reviewCount大于0，说明已经有部分题目被批阅，可以继续批阅
    if (record.reviewCount && record.reviewCount > 0) {
        return true;
    }
    
    // 如果没有startTime，说明未开始，不可批阅
    if (!record.startTime) {
        return false;
    }
    
    // 其他情况，默认可以查看
    return true;
};

// 判断是否可以查看作业结果
const canViewHomework = (record) => {
    // 如果已经有分数，说明已经批阅过，可以查看结果
    if (record.score && record.score > 0) {
        return true;
    }
    
    // 如果answerStatus存在且value为21或22，说明已经被批阅，可以查看结果
    if (record.answerStatus && (record.answerStatus.value === 21 || record.answerStatus.value === 22)) {
        return true;
    }
    
    // 如果reviewCount大于0，说明已经有部分题目被批阅，可以查看结果
    if (record.reviewCount && record.reviewCount > 0) {
        return true;
    }
    
    // 如果有submitTime，并且作业已结束，可以查看结果
    if (record.submitTime && record.endTime && dayjs().isAfter(dayjs(record.endTime))) {
        return true;
    }
    
    // 其他情况，不可查看
    return false;
};

// 获取班级列表
const getClassList = () => {
    // 获取课程ID，如果路由中有则使用，否则尝试从父级路径获取
    let courseId = route.params.courseId;
    
    // 如果没有直接的courseId，尝试从URL中提取
    if (!courseId && route.path.includes('/course/')) {
        const pathParts = route.path.split('/');
        const courseIndex = pathParts.findIndex(part => part === 'course');
        if (courseIndex !== -1 && pathParts.length > courseIndex + 1) {
            courseId = pathParts[courseIndex + 1];
        }
    }
    
    if (!courseId) {
        console.error('无法获取课程ID');
        Message.error('无法获取课程ID，请确保在课程内查看作业');
        classIds.value = [{ value: '', label: '全部班级' }];
        // 即使没有课程ID，也尝试获取学生列表（可能是在非课程上下文中）
        getReviewList();
        return;
    }
    
    console.log('获取班级列表，课程ID:', courseId);
    
    classLoading.value = true; // 开始加载
    getClassListRequest(courseId).then(res => {
        if (res && res.data && res.data.data) {
            // 先添加"全部班级"选项
            const classList = [{ value: '', label: '全部班级' }];
            
            // 再添加所有班级
            classList.push(...res.data.data.map(item => ({
                value: item.id.toString(), // 确保ID是字符串类型
                label: item.name
            })));
            
            classIds.value = classList;
            console.log('班级列表:', classIds.value);
            
            // 获取班级列表成功后，获取第一个班级的学生列表
            if (classList.length > 1) {
                currClassId.value = classList[1].value; // 选择第一个实际班级，跳过"全部班级"
            }
        } else {
            Message.error('获取班级列表失败');
            classIds.value = [{ value: '', label: '全部班级' }];
        }
    }).catch(err => {
        console.error('获取班级列表失败:', err);
        Message.error('获取班级列表失败，请稍后重试');
        classIds.value = [{ value: '', label: '全部班级' }];
    }).finally(() => {
        classLoading.value = false; // 结束加载
        // 无论成功失败，都尝试获取学生列表
        getReviewList();
    });
};

const pageChange = (current) => {
    page.value = current;
    getReviewList();
};

const getReviewList = () => {
    loading.value = true;
    tableLoading.value = true;
    
    // 调试日志
    console.log(`获取批阅学生列表：作业ID=${homeworkInfoId}, 页码=${page.value}, 班级ID=${currClassId.value || '全部班级'}`);
    
    // 确保classId是数字类型，如果是字符串则转换
    let classIdParam = currClassId.value;
    if (classIdParam && !isNaN(parseInt(classIdParam))) {
        classIdParam = parseInt(classIdParam);
    }
    
    console.log('发送API请求参数:', {
        homeworkInfoId,
        page: page.value,
        pageSize,
        classId: classIdParam || undefined
    });
    
    getHomeworkReviewStudentListRequest(homeworkInfoId, page.value, pageSize, classIdParam || undefined)
        .then(res => {
            console.log('API返回的原始数据:', res);
            
            if (res && res.data && res.data.data) {
                const data = res.data.data;
                console.log('学生列表原始数据:', data.list);
                
                // 确保每条记录都有userAuthInfo属性
                const processedList = (data.list || []).map(item => {
                    // 检查item本身是否就是userAuthInfo
                    if (item.nickname && item.userId && !item.userAuthInfo) {
                        console.log('检测到item本身是userAuthInfo格式:', item);
                        // 把item作为userAuthInfo附加到对象上
                        return {
                            userId: item.userId,
                            userAuthInfo: item
                        };
                    }
                    
                    // 如果item中有userAuthInfo但不完整，补充完整
                    if (item.userAuthInfo) {
                        console.log('检测到已有userAuthInfo:', item.userAuthInfo);
                        // 确保userAuthInfo中的userId字段存在
                        if (!item.userAuthInfo.userId && item.userId) {
                            item.userAuthInfo.userId = item.userId;
                        }
                        return item;
                    }
                    
                    // 如果item中没有userAuthInfo，创建一个
                    console.log('创建新的userAuthInfo:', item);
                    item.userAuthInfo = {
                        userId: item.userId || item.id,
                        nickname: item.nickname || item.userName || '未知用户',
                        realName: item.realName || '',
                        jobNo: item.jobNo || '',
                        schoolName: item.schoolName || '',
                        picture: item.picture || ''
                    };
                    return item;
                });
                
                studentList.value = processedList;
                total.value = data.total || 0;
                
                console.log('处理后的学生列表:', processedList);
                
                if (processedList.length === 0) {
                    if (currClassId.value) {
                        Message.info('该班级暂无需要批阅的学生作业');
                    } else {
                        Message.info('暂无需要批阅的学生作业');
                    }
                    tableLoading.value = false;
                    loading.value = false;
                } else {
                    // 获取每个学生的作业详情
                    tableLoading.value = false; // 先显示列表，然后在后台加载详情
                    fetchStudentHomeworkDetails(processedList);
                }
            } else {
                Message.error('获取学生列表失败');
                studentList.value = [];
                total.value = 0;
                tableLoading.value = false;
                loading.value = false;
            }
        })
        .catch(err => {
            console.error('获取学生列表失败:', err);
            Message.error('获取学生列表失败，请稍后重试');
            studentList.value = [];
            total.value = 0;
            tableLoading.value = false;
            loading.value = false;
        });
};

// 获取学生作业详情
const fetchStudentHomeworkDetails = (studentsList) => {
    // 创建一个副本，避免直接修改原始数据
    const updatedList = [...studentsList];
    
    // 如果列表为空，直接返回
    if (updatedList.length === 0) {
        loadingDetails.value = false;
        loading.value = false;
        return;
    }
    
    // 显示加载中提示
    loadingDetails.value = true;
    
    // 限制并发请求数量
    const maxConcurrentRequests = 3;
    let currentIndex = 0;
    let completedRequests = 0;
    const totalStudents = updatedList.length;
    
    // 处理下一批请求
    const processNextBatch = () => {
        // 计算本批次要处理的请求数量
        const remainingStudents = totalStudents - currentIndex;
        const batchSize = Math.min(maxConcurrentRequests, remainingStudents);
        
        if (batchSize <= 0) {
            // 所有批次处理完毕
            loadingDetails.value = false;
            loading.value = false;
            return;
        }
        
        // 处理当前批次的请求
        const batchPromises = [];
        
        for (let i = 0; i < batchSize; i++) {
            const studentIndex = currentIndex + i;
            const student = updatedList[studentIndex];
            const studentId = student.userAuthInfo?.userId || student.userId;
            
            if (!studentId) {
                console.error('无法获取学生ID:', student);
                completedRequests++;
                continue;
            }
            
            // 创建获取学生作业详情的Promise
            const promise = getHomeworkStudentDetailRequest(homeworkInfoId, studentId)
                .then(res => {
                    console.log(`学生 ${studentId} 的作业详情:`, res);
                    
                    if (res && res.data && res.data.data) {
                        const detailData = res.data.data;
                        
                        // 更新学生信息
                        updatedList[studentIndex] = {
                            ...updatedList[studentIndex],
                            score: detailData.score || updatedList[studentIndex].score || 0,
                            correctNumber: detailData.correctNumber || updatedList[studentIndex].correctNumber || 0,
                            reviewCount: detailData.reviewCount || updatedList[studentIndex].reviewCount || 0,
                            reviewTotal: detailData.reviewTotal || updatedList[studentIndex].reviewTotal || 0,
                            answerStatus: detailData.answerStatus || updatedList[studentIndex].answerStatus,
                            startTime: detailData.startTime || updatedList[studentIndex].startTime,
                            submitTime: detailData.submitTime || updatedList[studentIndex].submitTime,
                        };
                        
                        // 如果有userAuthInfo，更新它
                        if (detailData.userAuthInfo) {
                            updatedList[studentIndex].userAuthInfo = {
                                ...updatedList[studentIndex].userAuthInfo,
                                ...detailData.userAuthInfo
                            };
                        }
                    }
                })
                .catch(err => {
                    console.error(`获取学生 ${studentId} 的作业详情失败:`, err);
                })
                .finally(() => {
                    completedRequests++;
                });
            
            batchPromises.push(promise);
        }
        
        // 更新当前索引
        currentIndex += batchSize;
        
        // 等待当前批次完成后处理下一批
        Promise.all(batchPromises).then(() => {
            if (completedRequests < totalStudents) {
                // 还有未处理的学生，继续处理下一批
                processNextBatch();
            } else {
                // 所有学生都已处理完毕
                studentList.value = updatedList;
                loadingDetails.value = false;
                loading.value = false;
                console.log('所有学生作业详情获取完成:', updatedList);
            }
        });
    };
    
    // 开始处理第一批
    processNextBatch();
};

const toReviewPage = (studentId) => {
    if (!studentId) {
        Message.error('无法获取学生ID，请刷新页面重试');
        return;
    }
    
    console.log(`跳转到学生ID为 ${studentId} 的批阅页面`);
    
    // 判断当前是否在课程内的作业批阅页面
    if (route.params.courseId) {
        // 课程内的作业批阅
        router.push({
            name: 'CourseHomeworkReview',
            params: {
                courseId: route.params.courseId,
                homeworkId: homeworkInfoId,
                studentId
            }
        });
    } else {
        // 独立的作业批阅
        router.push({
            name: 'HomeworkReview',
            params: {
                homeworkId: homeworkInfoId,
                studentId
            }
        });
    }
};

const toHomeworkView = (studentId) => {
    if (!studentId) {
        Message.error('无法获取学生ID，请刷新页面重试');
        return;
    }

    console.log(`跳转到学生ID为 ${studentId} 的作业详情页面`);

    // 判断当前是否在课程内的作业批阅页面
    if (route.params.courseId) {
        // 课程内的作业查看
        router.push({
            name: 'CourseHomeworkView',
            params: {
                courseId: route.params.courseId,
                homeworkId: homeworkInfoId,
                studentId: studentId
            }
        });
    } else {
        // 独立的作业查看
        router.push({
            name: 'HomeworkView',
            params: {
                homeworkId: homeworkInfoId,
                studentId: studentId
            }
        });
    }
};

watch(() => currClassId.value, () => {
    page.value = 1;
    getReviewList();
});

// 获取作业发布信息，检查是否需要班级参数
const checkHomeworkInfo = () => {
    // 导入API
    import('@/apis/homework-api').then(api => {
        api.getHomeworkInfoRequest(homeworkInfoId)
            .then(res => {
                console.log('作业信息:', res);
                if (res && res.data && res.data.data) {
                    // 如果作业不需要班级过滤，则直接获取全部学生
                    if (!res.data.data.classId) {
                        console.log('作业未指定班级，获取全部学生');
                        currClassId.value = '';
                        getReviewList();
                    }
                }
            })
            .catch(err => {
                console.error('获取作业信息失败:', err);
            });
    });
};

// 添加辅助方法，尝试不同的API参数组合
const tryAlternativeRequests = () => {
    // 如果当前请求返回空数据，尝试其他方式
    if (studentList.value.length === 0) {
        console.log('当前请求返回空数据，尝试不带班级ID请求');
        
        // 暂存当前班级ID
        const tempClassId = currClassId.value;
        
        // 清空班级ID，尝试获取全部学生
        currClassId.value = '';
        getReviewList();
        
        // 恢复班级ID
        setTimeout(() => {
            currClassId.value = tempClassId;
        }, 2000);
    }
};

onMounted(() => {
    getClassList();
    checkHomeworkInfo();
    // 延迟执行，确保在需要时尝试替代请求
    setTimeout(() => {
        if (studentList.value.length === 0) {
            tryAlternativeRequests();
        }
    }, 3000);
});

const columns = [
    {
        title: '个人信息',
        slotName: 'userInfo',
        width: 200,
        fixed: 'left'
    },
    {
        title: '得分',
        dataIndex: 'score',
        slotName: 'score',
        width: 80,
    },
    {
        title: '题目对错',
        dataIndex: 'correctNumber',
        slotName: 'correctNumber',
        width: 100,
    },
    {
        title: '批阅题数',
        slotName: 'reviewStatus',
        width: 100,
    },
    {
        title: '状态',
        slotName: 'status',
        width: 100,
    },
    {
        title: '操作',
        slotName: 'operate',
        width: 120,
        fixed: 'right'
    },
];
</script>

<style lang="less" scoped>
.homework-review-container {
    width: 100%;
    padding: 0 20px;
    box-sizing: border-box;
    max-width: 1600px;
    margin: 0 auto;
}

@media screen and (max-width: 768px) {
    .homework-review-container {
        padding: 0 10px;
    }
    
    .review-header {
        flex-direction: column;
        align-items: flex-start;
        
        h2 {
            margin-bottom: 10px;
        }
        
        .filter-options {
            width: 100%;
            flex-wrap: wrap;
            margin-top: 10px;
        }
    }
}

.full-width-spin {
    width: 100%;
    display: block;
}

.review-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    background-color: var(--color-bg-2);
    border-bottom: 1px solid var(--color-border-2);
    margin-bottom: 16px;
    width: 100%;
    box-sizing: border-box;

    h2 {
        margin: 0;
        font-size: 18px;
        font-weight: bold;
    }

    .filter-options {
        display: flex;
        align-items: center;

        .filter-label {
            margin-right: 10px;
            font-size: 14px;
            color: var(--color-text-2);
        }
    }
}

.user-info {
    display: flex;
    align-items: center;

    .avatar {
        margin-right: 10px;
    }
}

.operate {
    display: flex;
}

.authInfo {
    display: flex;
    flex-direction: column;

    span {
        margin: 4px;
        justify-content: center;
        font-weight: bold;
    }
}

.time {
    display: flex;
    flex-direction: column;

    span {
        margin: 4px 0;
        justify-content: center;
    }
}

.no-class-tip {
    margin-bottom: 16px;
    padding: 10px 16px;
    background-color: var(--color-bg-2);
    border: 1px solid var(--color-border-2);
    border-radius: 4px;
    display: flex;
    align-items: center;
    width: 100%;
    box-sizing: border-box;

    .arco-alert {
        margin-bottom: 0;
    }
}

:deep(.arco-table) {
    width: 100%;
    
    .arco-table-container {
        width: 100%;
    }
    
    .arco-table-tr {
        width: 100%;
    }
    
    .arco-table-tbody {
        width: 100%;
    }
}

:deep(.arco-spin) {
    width: 100%;
    
    .arco-spin-content {
        width: 100%;
    }
}

:deep(.arco-table-container) {
    overflow-x: auto;
    width: 100%;
}
</style> 