<template>
    <a-page-header title="作业中心">
        <template #extra>
            <a-radio-group type="button" @change="getHomeworkList" v-model:model-value="status"
                style="margin-right:10px">
                <a-radio :value="0">全部</a-radio>
                <a-radio :value="1">未开始</a-radio>
                <a-radio :value="2">进行中</a-radio>
                <a-radio :value="3">已结束</a-radio>
            </a-radio-group>
            <a-button type="primary" v-if="isTeacher" @click="toHomeworkPaperManger" style="margin-right:10px">作业管理</a-button>
            <a-button type="primary" v-if="isTeacher" @click="showCreateHomeworkModal">发布作业</a-button>
        </template>
    </a-page-header>
    <ul class="homework-list">
        <li class="homework-item ebutton-hover" v-for="item in list" :key="item.id">
            <div class="homework-info-wrap">
                <a-avatar class="avatar" :size="40" :style="{'background-color': getHomeworkStatus(item).color }" shape="square">
                    {{ getHomeworkStatus(item).info }}
                </a-avatar>
                <div class="homework-info">
                    <p class="title">{{ item.title }}</p>
                    <p class="date">{{ item.startTime }} ~ {{ item.endTime }}</p>
                    <p class="description" v-if="item.description">{{ item.description }}</p>
                </div>
            </div>
            <div class="table-edit" v-if="isTeacher">
                <a-button status="danger" @click="delHomework(item.id)" style="margin-right: 10px;">
                    <template #icon>
                        <icon-delete />
                    </template>
                </a-button>
                <a-button type="primary" @click="getHomeworkDetail(item.id)" style="margin-right: 10px;">
                    <template #icon>
                        <icon-edit />
                    </template>
                </a-button>
                <a-button type="primary" @click="toReviewList(item.id)">
                    批阅
                </a-button>
            </div>
            <div v-else>
                <homework-info-button :item="item" />
            </div>
        </li>
    </ul>
    <a-empty v-if="list.length === 0" description="暂无作业" />
    
    <!-- 创建作业对话框 -->
    <a-modal 
        v-model:visible="homeworkVisible" 
        :title="isEdit ? '修改作业' : '发布作业'" 
        @cancel="handleCancel" 
        @before-ok="handleBeforeOk"
        :ok-loading="confirmLoading"
        :unmount-on-close="true">
        <a-form :model="form" ref="formRef">
            <a-form-item field="title" label="作业标题" :rules="[{ required: true, message: '请输入作业标题' }]">
                <a-input v-model="form.title" placeholder="请输入作业标题" />
            </a-form-item>
            <a-form-item field="description" label="作业描述">
                <a-textarea v-model="form.description" placeholder="请输入作业描述" />
            </a-form-item>
            <a-form-item field="startTime" label="开始时间" :rules="[{ required: true, message: '请选择开始时间' }]">
                <a-date-picker v-model="form.startTime" show-time />
            </a-form-item>
            <a-form-item field="endTime" label="结束时间" :rules="[{ required: true, message: '请选择结束时间' }]">
                <a-date-picker v-model="form.endTime" show-time />
            </a-form-item>
            <a-form-item field="allowLateSubmit" label="允许补交">
                <a-switch v-model="form.allowLateSubmit" />
            </a-form-item>
            <a-form-item field="lateEndTime" label="补交截止时间" v-if="form.allowLateSubmit">
                <a-date-picker v-model="form.lateEndTime" show-time />
            </a-form-item>
            <a-form-item field="lateDeduction" label="补交扣分(%)" v-if="form.allowLateSubmit">
                <a-input-number v-model="form.lateDeduction" placeholder="请输入补交扣分比例" :min="0" :max="100" />
            </a-form-item>
            <a-form-item field="questionDisorder" label="题目乱序">
                <a-switch v-model="form.questionDisorder" />
            </a-form-item>
            <a-form-item field="optionDisorder" label="选项乱序">
                <a-switch v-model="form.optionDisorder" />
            </a-form-item>
            <a-form-item field="endVisible" label="结束后可见">
                <a-switch v-model="form.endVisible" />
            </a-form-item>
            <a-form-item field="homeworkId" label="选择作业" :rules="[{ required: true, message: '请选择作业' }]">
                <a-select v-model="form.homeworkId" placeholder="请选择作业" allow-search>
                    <a-option v-for="item in paperList" :key="item.id" :value="item.id">
                        {{ item.title }}
                    </a-option>
                </a-select>
            </a-form-item>
            <a-form-item field="classes" label="班级" :rules="[{ required: true, message: '请选择班级' }]">
                <a-select v-model="classList" placeholder="请选择班级" multiple allow-search @change="classChange">
                    <a-option v-for="item in courseStore.classList" :key="item.id" :value="item.id">
                        {{ item.name }}
                    </a-option>
                </a-select>
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import dayjs from 'dayjs';
import { useRoute, useRouter } from 'vue-router';
import useCourseStore from '../../sotre/course-store';
import useUserStore from '../../sotre/user-store';
import HomeworkInfoButton from '../../components/HomeworkInfoButton.vue';
import { Message, Modal } from '@arco-design/web-vue';
import { IconDelete, IconEdit } from '@arco-design/web-vue/es/icon';
import { 
    getHomeworkInfoListRequest, 
    getHomeworkInfoDetailRequest, 
    updateHomeworkInfoRequest, 
    deleteHomeworkInfoRequest,
    getHomeworkPaperListRequest
} from '../../apis/homework-api';

const route = useRoute();
const router = useRouter();
const courseId = route.params['courseId'];
const courseStore = useCourseStore();
const userStore = useUserStore();
const isTeacher = computed(() => courseStore.isTeacher);

const homeworkVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const loading = ref(false);
const confirmLoading = ref(false); // 添加确认按钮加载状态

const form = ref({
    id: null,
    title: '',
    description: '',
    startTime: '',
    endTime: '',
    homeworkId: null,
    questionDisorder: false,
    optionDisorder: false,
    endVisible: false,
    allowLateSubmit: false,
    lateEndTime: '',
    lateDeduction: 5
});

const classList = ref([]);
const getClassIds = computed(() => {
    return classList.value;
});

const classChange = (keys, data) => {
    console.log('班级选择变更:', keys, data);
    classList.value = keys;
};

const currpage = ref(1);
const total = ref(1);
const pageSize = ref(10);
const status = ref(0);
const list = ref([]);
const paperList = ref([]);

// 确保用户信息已加载
const ensureUserInfo = async () => {
    if (!userStore.userInfo || !userStore.userInfo.id) {
        console.log('用户信息未加载或不完整，正在获取...');
        try {
            await userStore.getUserInfo();
            console.log('用户信息加载成功:', userStore.userInfo);
            return true;
        } catch (err) {
            console.error('获取用户信息失败', err);
            Message.error('获取用户信息失败，请刷新页面后重试');
            return false;
        }
    }
    return true;
};

// 显示创建作业对话框
const showCreateHomeworkModal = async () => {
    isEdit.value = false;
    resetForm();
    
    // 确保用户信息已加载
    const userInfoLoaded = await ensureUserInfo();
    if (!userInfoLoaded) {
        Message.error('无法创建作业，用户信息获取失败');
        return;
    }
    
    homeworkVisible.value = true;
    getPaperList();
};

// 获取试卷列表
const getPaperList = () => {
    getHomeworkPaperListRequest(courseId, 1, 100)
        .then(res => {
            console.log('获取试卷列表API返回数据:', res);
            // 处理可能的嵌套数据结构
            const responseData = res.data && res.data.data ? res.data.data : res.data;
            
            if (responseData && responseData.list) {
                console.log('试卷列表数据:', responseData);
                paperList.value = responseData.list;
            } else {
                console.log('未获取到试卷列表数据', res);
                paperList.value = [];
            }
        })
        .catch(err => {
            console.error('获取试卷列表失败', err);
            console.error('错误详情:', err.response ? err.response.data : err);
            Message.error('获取试卷列表失败');
        });
};

// 处理模态框取消
const handleCancel = () => {
    console.log('模态框取消，重置数据');
    resetForm();
    // 确保班级选择被正确清除
    classList.value = [];
    // 关闭模态框
    homeworkVisible.value = false;
    // 重置确认按钮状态
    confirmLoading.value = false;
};

// 处理Modal确认前的验证
const handleBeforeOk = () => {
    return new Promise((resolve, reject) => {
        // 设置确认按钮为加载状态
        confirmLoading.value = true;
        
        // 调用更新作业函数
        updateHomework()
            .then(() => {
                // 成功时关闭对话框
                homeworkVisible.value = false;
                confirmLoading.value = false;
                resolve(true);
            })
            .catch(() => {
                // 失败时不关闭对话框，但要重置按钮状态
                confirmLoading.value = false;
                reject(false);
            });
    });
};

// 更新作业信息
const updateHomework = async () => {
    try {
        // 确保用户信息已加载
        const userInfoLoaded = await ensureUserInfo();
        if (!userInfoLoaded) {
            Message.error('无法提交作业，用户信息获取失败');
            return Promise.reject(new Error('用户信息获取失败'));
        }
        
        // 表单验证
        try {
            await formRef.value.validate();
        } catch (err) {
            console.error('表单验证失败:', err);
            return Promise.reject(new Error('表单验证失败'));
        }
        
        loading.value = true;
        
        // 检查时间关系
        const startTime = dayjs(form.value.startTime);
        const endTime = dayjs(form.value.endTime);
        
        if (endTime.isBefore(startTime)) {
            Message.error('结束时间不能早于开始时间');
            loading.value = false;
            return Promise.reject(new Error('结束时间不能早于开始时间'));
        }
        
        if (form.value.allowLateSubmit && form.value.lateEndTime) {
            const lateEndTime = dayjs(form.value.lateEndTime);
            if (lateEndTime.isBefore(endTime)) {
                Message.error('补交截止时间不能早于结束时间');
                loading.value = false;
                return Promise.reject(new Error('补交截止时间不能早于结束时间'));
            }
        }
        
        // 检查班级选择
        if (!classList.value || classList.value.length === 0) {
            Message.error('请选择至少一个班级');
            loading.value = false;
            return Promise.reject(new Error('请选择至少一个班级'));
        }
        
        console.log('提交前的班级数据:', classList.value);
        
        const homeworkData = {
            homeworkInfo: {
                ...form.value,
                startTime: dayjs(form.value.startTime).format('YYYY-MM-DD HH:mm:ss'),
                endTime: dayjs(form.value.endTime).format('YYYY-MM-DD HH:mm:ss'),
                courseId: parseInt(courseId),
                teacherId: userStore.userInfo.id
            },
            classIds: classList.value,
            paper: null,
            classList: null
        };
        
        if (form.value.allowLateSubmit && form.value.lateEndTime) {
            homeworkData.homeworkInfo.lateEndTime = dayjs(form.value.lateEndTime).format('YYYY-MM-DD HH:mm:ss');
        }
        
        console.log('提交的作业数据:', homeworkData);
        
        try {
            const res = await updateHomeworkInfoRequest(homeworkData);
            console.log('更新作业API返回数据:', res);
            // 处理可能的嵌套响应
            const responseData = res.data ? res.data : res;
            const code = responseData.code;
            
            if (code === 'A0400' || code === 500) {
                console.error('创建作业失败, 错误信息:', responseData.msg || '未知错误');
                Message.error(responseData.msg || '创建作业失败');
                loading.value = false;
                return Promise.reject(new Error(responseData.msg || '创建作业失败'));
            } else {
                Message.success(isEdit.value ? '修改作业成功' : '创建作业成功');
                getHomeworkList();
                loading.value = false;
                return Promise.resolve(true);
            }
        } catch (err) {
            console.error(isEdit.value ? '修改作业失败' : '创建作业失败', err);
            console.error('错误详情:', err.response ? err.response.data : err);
            Message.error(isEdit.value ? '修改作业失败' : '创建作业失败');
            loading.value = false;
            return Promise.reject(err);
        }
    } catch (err) {
        console.error('处理作业提交时发生错误:', err);
        loading.value = false;
        return Promise.reject(err);
    }
};

// 重置表单
const resetForm = () => {
    form.value = {
        id: null,
        title: '',
        description: '',
        startTime: '',
        endTime: '',
        homeworkId: null,
        questionDisorder: false,
        optionDisorder: false,
        endVisible: false,
        allowLateSubmit: false,
        lateEndTime: '',
        lateDeduction: 5
    };
    // 显式重置班级选择
    classList.value = [];
    console.log('重置表单，班级数据已清空');
    
    // 如果表单引用存在，重置表单验证状态
    if (formRef.value) {
        formRef.value.resetFields();
    }
};

// 获取作业列表
const getHomeworkList = () => {
    loading.value = true;
    console.log('开始获取课程作业列表...', courseId, status.value, currpage.value, pageSize.value);
    
    getHomeworkInfoListRequest(courseId, status.value, currpage.value, pageSize.value)
        .then(res => {
            console.log('课程作业列表API返回数据:', res);
            // 处理可能的嵌套数据结构
            const responseData = res.data && res.data.data ? res.data.data : res.data;
            
            if (responseData) {
                console.log('课程作业列表数据:', responseData);
                list.value = responseData.list || [];
                console.log('解析后的课程作业列表:', list.value);
                currpage.value = responseData.current || 1;
                total.value = responseData.total || 0;
            } else {
                list.value = [];
                total.value = 0;
                console.log('未获取到课程作业列表数据', res);
            }
        })
        .catch(err => {
            console.error('获取课程作业列表失败', err);
            console.error('错误详情:', err.response ? err.response.data : err);
            Message.error('获取作业列表失败');
            list.value = [];
        })
        .finally(() => {
            loading.value = false;
        });
};

// 获取作业详情
const getHomeworkDetail = (id) => {
    loading.value = true;
    
    // 确保用户信息已加载
    if (!userStore.userInfo) {
        console.log('编辑作业前用户信息未加载，正在获取...');
        userStore.getUserInfo().catch(err => {
            console.error('获取用户信息失败', err);
            Message.error('获取用户信息失败，可能无法正确编辑作业');
        });
    }
    
    getHomeworkInfoDetailRequest(id)
        .then(res => {
            console.log('获取作业详情API返回数据:', res);
            // 处理可能的嵌套数据结构
            const data = res.data && res.data.data ? res.data.data : res.data;
            const homeworkInfo = data && data.homeworkInfo ? data.homeworkInfo : data;
            
            if (homeworkInfo) {
                console.log('解析后的作业详情:', homeworkInfo);
                form.value = {
                    id: homeworkInfo.id,
                    title: homeworkInfo.title,
                    description: homeworkInfo.description,
                    startTime: dayjs(homeworkInfo.startTime),
                    endTime: dayjs(homeworkInfo.endTime),
                    homeworkId: homeworkInfo.homeworkId,
                    questionDisorder: homeworkInfo.questionDisorder,
                    optionDisorder: homeworkInfo.optionDisorder,
                    endVisible: homeworkInfo.endVisible,
                    allowLateSubmit: homeworkInfo.allowLateSubmit,
                    lateEndTime: homeworkInfo.lateEndTime ? dayjs(homeworkInfo.lateEndTime) : '',
                    lateDeduction: homeworkInfo.lateDeduction
                };
                
                // 设置班级选择
                if (data.classIds && Array.isArray(data.classIds)) {
                    console.log('设置班级选择:', data.classIds);
                    classList.value = [...data.classIds]; // 创建新数组以确保响应式更新
                } else {
                    console.log('未找到班级数据或数据格式不正确');
                    classList.value = [];
                }
                
                isEdit.value = true;
                homeworkVisible.value = true;
                getPaperList();
            } else {
                console.error('获取作业详情失败，无有效数据:', res);
                Message.error('获取作业详情失败');
            }
        })
        .catch(err => {
            console.error('获取作业详情失败', err);
            console.error('错误详情:', err.response ? err.response.data : err);
            Message.error('获取作业详情失败');
        })
        .finally(() => {
            loading.value = false;
        });
};

// 删除作业
const delHomework = (id) => {
    Modal.confirm({
        title: '确认删除',
        content: '确定要删除这个作业吗？删除后无法恢复。',
        onOk: () => {
            loading.value = true;
            
            deleteHomeworkInfoRequest(id)
                .then(res => {
                    Message.success('删除作业成功');
                    getHomeworkList();
                })
                .catch(err => {
                    console.error('删除作业失败', err);
                    Message.error('删除作业失败');
                })
                .finally(() => {
                    loading.value = false;
                });
        }
    });
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

// 跳转到作业管理页面
const toHomeworkPaperManger = () => {
    router.push({
        name: 'CourseHomeworkPaperManger',
        params: {
            courseId
        }
    });
};

// 跳转到批阅列表
const toReviewList = (homeworkId) => {
    router.push({
        name: 'CourseHomeworkReviewList',
        params: {
            courseId,
            homeworkId
        }
    });
};

// 初始化
onMounted(async () => {
    await ensureUserInfo();
    getHomeworkList();
    
    // 如果是教师，加载班级列表
    if (isTeacher.value && !courseStore.classList.length) {
        await courseStore.getClassList(courseId);
    }
});
</script>

<style lang="less" scoped>
.homework-list {
    margin-top: 20px;
    
    .homework-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px;
        margin-bottom: 16px;
        background-color: var(--color-bg-2);
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
</style> 