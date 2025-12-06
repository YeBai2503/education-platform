<template>
    <div class="exam-paper">
        <div>
            <a-form :model="form">
                <a-form-item field="name" label="作业名称" label-col-flex="80px">
                    <a-input v-model="form.title" placeholder="输入作业名称" />
                </a-form-item>
                <a-form-item field="post" label="作业介绍" label-col-flex="80px">
                    <a-input v-model="form.introduce" placeholder="输入作业介绍" />
                </a-form-item>
                <!-- <a-row>
                        <a-col :span="4">
                            <a-form-item field="post" label="题目乱序" label-col-flex="80px">
                                <a-switch />
                            </a-form-item>
                        </a-col>
                        <a-col :span="4">
                            <a-form-item field="post" label="选项乱序" label-col-flex="80px">
                                <a-switch />
                            </a-form-item>
                        </a-col>
                        <a-col :span="4">
                            <a-form-item field="post" label="批阅可见" label-col-flex="80px">
                                <a-switch />
                            </a-form-item>
                        </a-col>
                    </a-row> -->
                <a-form-item label-col-flex="80px">
                    <template #label>
                        <span class="select-info">
                            已选择:<span class="count">{{ questionKey.length }}</span>
                        </span>
                    </template>
                    <ARow style="width:100%" :gutter="10">
                        <ACol :span="12">
                            <a-button long @click="visible = true">添加题目</a-button>
                        </ACol>
                        <ACol :span="12">
                            <a-button long type="primary" @click="save">保存作业</a-button>
                        </ACol>
                    </ARow>
                </a-form-item>
            </a-form>
        </div>
        <a-modal v-model:visible="visible" simple width="800px" :body-style="{ overflow: 'hidden' }">
            <Question v-model:select-kes="questionKey" :select-mode="true"></Question>
        </a-modal>
    </div>
</template>
<script setup>
import { ref, watch, computed } from 'vue';
import Question from '../course/Question.vue'
import { updateHomeworkPaperRequest, getHomeworkPaperDetailRequest } from '../../apis/homework-api'
import { useRoute, useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';

const form = ref({
    introduce: "",
    title: "",
    courseId: ""
})
const props = defineProps({
    keys: {
        type: Array,
        default: () => []
    },
    courseId: {
        type: [String, Number],
        default: ''
    }
})
const route = useRoute()
const router = useRouter()
const homeworkId = route.params['homeworkId']
const courseId = route.params['courseId'] || props.courseId
const visible = ref(false)
const questionKey = ref([])
const loading = ref(false)

// 校验表单
const validateForm = () => {
    if (!form.value.title || form.value.title.trim() === '') {
        Message.warning('请输入作业名称');
        return false;
    }
    
    if (questionKey.value.length === 0) {
        Message.warning('请至少选择一个题目');
        return false;
    }
    
    if (!courseId) {
        Message.error('未获取到课程ID');
        return false;
    }
    
    return true;
}

const save = () => {
    if (!validateForm()) return;
    
    loading.value = true;
    
    // 准备保存的数据
    const homeworkData = { ...form.value };
    
    if (homeworkId) {
        homeworkData.id = homeworkId;
    }
    
    homeworkData.courseId = courseId;
    
    console.log('保存作业数据:', {
        homeworkPaper: homeworkData,
        questions: questionKey.value
    });
    
    updateHomeworkPaperRequest(homeworkData, questionKey.value)
        .then(res => {
            console.log('保存作业返回结果:', res);
            Message.success('保存作业成功');
            router.back();
        })
        .catch(err => {
            console.error('保存作业失败:', err);
            Message.error('保存作业失败');
        })
        .finally(() => {
            loading.value = false;
        });
}

// 加载作业详情
const loadHomeworkDetail = () => {
    if (!homeworkId) return;
    
    loading.value = true;
    
    getHomeworkPaperDetailRequest(homeworkId)
        .then(res => {
            console.log('作业详情数据:', res.data.data);
            
            if (res.data && res.data.data) {
                const data = res.data.data;
                
                if (data.homeworkPaper) {
                    form.value = data.homeworkPaper;
                }
                
                if (data.questions && Array.isArray(data.questions)) {
                    console.log(`加载了 ${data.questions.length} 道题目`);
                    questionKey.value = data.questions.map(q => q.id || q);
                } else {
                    console.warn('未找到题目数据');
                }
            }
        })
        .catch(err => {
            console.error('获取作业详情失败:', err);
            Message.error('获取作业详情失败');
        })
        .finally(() => {
            loading.value = false;
        });
}

// 初始化
if (homeworkId) {
    loadHomeworkDetail();
}

// 监听外部传入的题目ID列表
watch(() => props.keys, (value) => {
    if (value && value.length > 0) {
        console.log('从父组件接收到题目列表:', value);
        questionKey.value = value;
    }
});

// 监听课程ID变化，确保表单中始终有最新的课程ID
watch(() => courseId, (value) => {
    if (value) {
        form.value.courseId = value;
    }
}, { immediate: true });
</script>
<style lang="less" scoped>
.operate {
    display: flex;
    align-items: center;
    position: sticky;
}

.select-info {
    font-weight: bold;
    font-size: 16px;
    color: var(--color-text-4);

    .count {
        color: rgba(var(--primary-4));
        font-style: oblique;
        font-size: 18px;
    }
}
</style> 