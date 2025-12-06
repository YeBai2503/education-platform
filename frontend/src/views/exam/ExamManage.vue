<template>
    <a-page-header title="考试中心" @back="$router.back">
        <template #extra>
            <a-radio-group type="button" @change="getExamInfoList" v-model:model-value="status"
                style="margin-right:10px">
                <a-radio :value="0">全部</a-radio>
                <a-radio :value="1">未开始</a-radio>
                <a-radio :value="2">进行中</a-radio>
                <a-radio :value="3">已结束</a-radio>
            </a-radio-group>
            <a-button type="primary" v-if="isTeacher" @click="examVisible = true">创建考试</a-button>
        </template>
    </a-page-header>
    <ul class="exam-list">
        <li class="exam-item ebutton-hover" v-for="item in list" :key="item.id">
            <div class="exam-info-wrap">
                <a-avatar class="avatar" :size="40" :style="{'background-color': getExamStatus(item).color }" shape="square">
                    {{ getExamStatus(item).info }}
                </a-avatar>
                <div class="exam-info">
                    <p class="title">{{ item.title }}</p>
                    <p class="date">{{ formatDateTime(item.startTime) }} ~ {{ formatDateTime(item.endTime) }}</p>
                </div>
            </div>
            <div class="table-edit" v-if="isTeacher">
                <a-button status="danger" @click="delExamInfo(item.id)" style="margin-right: 10px;">
                    <template #icon>
                        <icon-delete />
                    </template>
                </a-button>
                <a-button type="primary" @click="getExamInfoDetail(item.id)" style="margin-right: 10px;">
                    <template #icon>
                        <icon-edit />
                    </template>
                </a-button>
                <router-link :to="`/exam/${item.id}/console/outline`">
                    <a-button type="primary">
                        控制台
                    </a-button>
                </router-link>
            </div>
            <ExamInfoButton v-else :item="item" :key="`exam-btn-${item.id}-${item.isSubmitted}`"></ExamInfoButton>
        </li>
    </ul>
    <a-empty v-if="list.length == 0"></a-empty>
    <a-pagination style="justify-content: center;margin:10px 0" v-model:current="currpage" @change="getExamInfoList"
        :total="total" :page-size="pageSize" />
    <a-modal v-model:visible="examVisible" @ok="updateExamInfo" :footer="false" :title="form.id ? '更新考试' : '创建考试'" simple width="600px"
        :body-style="{ overflow: 'hidden' }">
        <a-form :model="form" ref="formRef" @submit-success="updateExamInfo">
            <a-form-item field="title" label="考试标题" label-col-flex="80px" :rules="[{ required: true, message: '必填' }]">
                <a-input v-model="form.title" placeholder="输入考试标题" />
            </a-form-item>
            <a-form-item field="paper" label="考试试卷" label-col-flex="80px">
                <a-input-tag v-model:model-value="paperList" :max-tag-count="1" @click="examPaperVisible = true"
                    placeholder="选择考试试卷" allow-clear />
            </a-form-item>
            <a-form-item field="classList" label="考试班级" label-col-flex="80px"
                >
                <a-input-tag v-model:model-value="classList" @click="classesVisible = true" placeholder="选择考试班级"
                    allow-clear />
            </a-form-item>
            <a-row>
                <a-col :span="12">
                    <a-form-item field="startTime" label="开始时间" label-col-flex="80px"
                        :rules="[{ required: true, message: '必填' }]">
                        <a-date-picker v-model:model-value="form.startTime" show-time
                            :disabledDate="(current) => dayjs(current).isBefore(dayjs().startOf('day'))"
                            format="YYYY-MM-DD HH:mm" />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item field="endTime" label="截止时间" label-col-flex="80px"
                        :rules="[{ required: true, message: '必填' }]">
                        <a-date-picker v-model:model-value="form.endTime" show-time
                            :disabledDate="(current) => current && dayjs(current).isBefore(dayjs().startOf('day'))"
                            format="YYYY-MM-DD HH:mm" />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row>
                <a-col :span="6">
                    <a-form-item field="questionDisorder" label="考试监控" label-col-flex="80px">
                        <a-switch v-model:model-value="form.isMonitor" />
                    </a-form-item>
                </a-col>
                <a-col :span="6">
                    <a-form-item field="optionDisorder" label="允许复制" label-col-flex="80px">
                        <a-switch v-model:model-value="form.isCopyPaste" />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item field="submitTime" label="最早提交" label-col-flex="80px">
                        <a-date-picker v-model:model-value="form.submitTime" show-time
                            :disabledDate="(current) => current && dayjs(current).isBefore(dayjs().startOf('day'))"
                            format="YYYY-MM-DD HH:mm" />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row>
                <a-col :span="6">
                    <a-form-item field="questionDisorder" label="题目乱序" label-col-flex="80px">
                        <a-switch v-model:model-value="form.questionDisorder" />
                    </a-form-item>
                </a-col>
                <a-col :span="6">
                    <a-form-item field="optionDisorder" label="选项乱序" label-col-flex="80px">
                        <a-switch v-model:model-value="form.optionDisorder" />
                    </a-form-item>
                </a-col>
                <a-col :span="6">
                    <a-form-item field="endVisible" label="批阅可见" label-col-flex="80px">
                        <a-switch v-model:model-value="form.endVisible" />
                    </a-form-item>
                </a-col>

            </a-row>

            <a-row :gutter="24">
                <a-col :span="12">
                    <a-button long @click="examVisible = false">取消</a-button>
                </a-col>
                <a-col :span="12">
                    <a-button long html-type="submit" type="primary">{{ form.id ? '更新' : '发布' }}</a-button>
                </a-col>
            </a-row>
        </a-form>
    </a-modal>
    <a-modal v-model:visible="classesVisible" simple title="选择班级">
        <MyClasses :select-key="getClassIds" @select-data="classChange" :select-mode="true"></MyClasses>
    </a-modal>
    <a-modal v-model:visible="examPaperVisible" title="选择试卷" simple>
        <ExamPaperManger :select-key="getPaperIds" @select-data="paperChange" :select-mode="true"></ExamPaperManger>
    </a-modal>
</template>
<script setup>
import { computed, ref } from 'vue';
import ExamPaperManger from './ExamPaperManger.vue';
import MyClasses from '../course/MyClasses.vue';
import { updateExamInfoRequest, getExamInfoListRequest, delExamInfoRequest, getExamInfoDetailRequest } from '../../apis/exam-api.js'
import { getExamSubmitStatusRequest } from '../../apis/exam-center-api';
import dayjs from 'dayjs'
import { useRoute } from 'vue-router';
import useCourseStore from '../../sotre/course-store';
import ExamInfoButton from '../../components/ExamInfoButton.vue'
import { Message } from '@arco-design/web-vue';

const route = useRoute()
const courseId = route.params['courseId']
const courseStore = useCourseStore()
const isTeacher = courseStore.isTeacher

const examVisible = ref(false)
const classesVisible = ref(false)
const examPaperVisible = ref(false)
const formRef = ref()
const loading = ref(false)
const form = ref({
    startTime: dayjs().add(1, 'day').hour(9).minute(0).second(0), // 默认为明天上午9点
    endTime: dayjs().add(1, 'day').hour(11).minute(0).second(0),  // 默认为明天上午11点
    submitTime: dayjs().add(1, 'day').hour(9).minute(30).second(0), // 默认为明天上午9:30
    optionDisorder: false,
    questionDisorder: false,
    endVisible: false,
    isMonitor: false,
    isCopyPaste: false,
    title: '',
})
const classList = ref([])
const paperList = ref([])
const getClassIds = computed(() => {
    return classList.value.map(item => item.value)
})
const getPaperIds = computed(() => {
    return paperList.value.map(item => item.value)
})
const classChange = (keys, data) => {
    classList.value=formatTagInputList(data,'id','name')
    console.log("班级改变", keys, data)
}
const paperChange = (keys, data) => {
    console.log("试卷改变", keys, data)
    paperList.value=formatTagInputList(data,'id','title')
}
/** */
const currpage = ref(1);
const total = ref(1)
const pageSize = ref(10)
const status = ref(0)
const list = ref([])
const updateExamInfo = () => {
    try {
        const info = { ...form.value }
        
        // 检查必填项
        if (!info.title) {
            Message.warning('请输入考试标题');
            return;
        }
        
        if (!info.startTime) {
            Message.warning('请选择开始时间');
            return;
        }
        
        if (!getPaperIds.value || getPaperIds.value.length === 0) {
            Message.warning('请选择考试试卷');
            return;
        }
        
        if (!getClassIds.value || getClassIds.value.length === 0) {
            Message.warning('请选择考试班级');
            return;
        }
        
        // 设置试卷ID
        info['examId'] = getPaperIds.value[0]
        
        // 处理开始时间
        if (!info.startTime) {
            Message.warning('请选择开始时间');
            return;
        }
        
        // 处理截止时间
        if (!info.endTime) {
            Message.warning('请选择截止时间');
            return;
        }
        
        // 验证时间顺序
        const startTimeObj = dayjs.isDayjs(info.startTime) ? info.startTime : dayjs(info.startTime);
        const endTimeObj = dayjs.isDayjs(info.endTime) ? info.endTime : dayjs(info.endTime);
        
        console.log('开始时间:', startTimeObj.format('YYYY-MM-DD HH:mm:ss'));
        console.log('截止时间:', endTimeObj.format('YYYY-MM-DD HH:mm:ss'));
        
        if (endTimeObj.isBefore(startTimeObj) || endTimeObj.isSame(startTimeObj)) {
            Message.warning('截止时间必须晚于开始时间');
            return;
        }
        
        // 处理最早提交时间
        let submitTimeObj = null;
        if (info.submitTime) {
            submitTimeObj = dayjs.isDayjs(info.submitTime) ? info.submitTime : dayjs(info.submitTime);
            console.log('最早提交时间:', submitTimeObj.format('YYYY-MM-DD HH:mm:ss'));
            
            if (submitTimeObj.isBefore(startTimeObj)) {
                Message.warning('最早提交时间不能早于开始时间');
                return;
            }
            
            if (submitTimeObj.isAfter(endTimeObj)) {
                Message.warning('最早提交时间不能晚于截止时间');
                return;
            }
        }
        
        // 格式化所有时间
        info.startTime = startTimeObj.format('YYYY-MM-DD HH:mm:ss');
        info.endTime = endTimeObj.format('YYYY-MM-DD HH:mm:ss');
        
        if (submitTimeObj) {
            info.submitTime = submitTimeObj.format('YYYY-MM-DD HH:mm:ss');
        }
        
        console.log('格式化后的时间:');
        console.log('- 开始时间:', info.startTime);
        console.log('- 截止时间:', info.endTime);
        console.log('- 最早提交时间:', info.submitTime || '未设置');
        
        // 设置课程ID
        info['courseId'] = parseInt(courseId);
        
        // 如果是新建考试，确保不传递ID字段
        if (!info.id) {
            delete info.id;
        }
        
        // 最后检查时间格式是否符合 datetime 要求
        const datetimeRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;
        if (!datetimeRegex.test(info.startTime)) {
            console.error('开始时间格式不正确:', info.startTime);
            Message.error('开始时间格式不正确，请重新选择');
            return;
        }
        
        if (!datetimeRegex.test(info.endTime)) {
            console.error('结束时间格式不正确:', info.endTime);
            Message.error('结束时间计算错误，请重试');
            return;
        }
        
        if (info.submitTime && !datetimeRegex.test(info.submitTime)) {
            console.error('提交时间格式不正确:', info.submitTime);
            Message.error('提交时间计算错误，请重试');
            return;
        }
        
        console.log('发送考试信息:', JSON.stringify(info));
        console.log('班级列表:', getClassIds.value);
        
        // 发送请求
        updateExamInfoRequest(info, getClassIds.value).then(res => {
            console.log('考试操作成功响应:', res);
            // 根据是否有ID判断是更新还是创建
            if (!info.id) {
                Message.success('考试创建成功');
            } 
            examVisible.value = false;
            getExamInfoList();
        }).catch(err => {
            console.error('考试操作失败:', err);
            // 根据是否有ID判断是更新还是创建
            if (info.id) {
                Message.error('更新考试失败，请检查参数');
            } else {
                Message.error('创建考试失败，请检查参数');
            }
        });
    } catch (error) {
        console.error('处理考试信息时出错:', error);
        Message.error('处理考试信息时出错，请重试');
    }
}
const getExamInfoList = () => {
    loading.value = true
    getExamInfoListRequest(courseId, status.value, currpage.value, pageSize.value).then(res => {
        const data = res.data.data
        list.value = data.list
        
        // 如果是学生，检查每个考试的提交状态
        if (!isTeacher && list.value.length > 0) {
            try {
                // 获取所有进行中的考试ID
                const examIds = list.value
                    .filter(item => !dayjs(item.endTime).isBefore(dayjs()) && !dayjs(item.startTime).isAfter(dayjs()))
                    .map(item => item.id);
                    
                if (examIds.length > 0) {
                    console.log('查询考试状态，考试ID列表:', examIds);
                    
                    // 批量查询考试提交状态，使用数组格式发送
                    getExamSubmitStatusRequest(examIds)
                        .then(response => {
                            console.log('获取考试提交状态成功:', response);
                            if (response.data && response.data.data) {
                                const submitData = response.data.data;
                                Object.keys(submitData).forEach(examId => {
                                    // 找到对应的考试项，设置提交状态
                                    const exam = list.value.find(item => item.id === parseInt(examId));
                                    if (exam) {
                                        // 根据返回数据判断提交状态："提交试卷" 表示已提交，"未提交" 表示未提交
                                        const isSubmitted = submitData[examId] === "提交试卷";
                                        console.log(`考试ID ${examId} 提交状态: ${submitData[examId]}, isSubmitted: ${isSubmitted}`);
                                        
                                        // 使用Vue的响应式更新方式
                                        if (exam.isSubmitted !== isSubmitted) {
                                            // 创建新对象以触发响应式更新
                                            const index = list.value.findIndex(item => item.id === parseInt(examId));
                                            if (index !== -1) {
                                                const updatedExam = { ...exam, isSubmitted };
                                                list.value.splice(index, 1, updatedExam);
                                                console.log(`已更新考试 ${examId} 的提交状态为 ${isSubmitted}`);
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .catch(error => {
                            console.error('获取考试提交状态失败:', error);
                            // 错误处理，确保不影响页面显示
                            Message.error('获取考试状态信息失败，请刷新重试');
                        });
                }
            } catch (err) {
                console.error('处理考试状态时出错:', err);
            }
        }
        
        currpage.value = data.current
        total.value = data.total
        loading.value = false
    })
}
const getExamInfoDetail = (id) => {
    getExamInfoDetailRequest(id).then(res => {
        const data = res.data.data
        form.value = {
            ...data.examInfo,
            startTime: dayjs(data.examInfo.startTime),
            endTime: dayjs(data.examInfo.endTime),
            submitTime: data.examInfo.submitTime ? dayjs(data.examInfo.submitTime) : null
        }
        classList.value = formatTagInputList(data.classList,'id','name')
        paperList.value = [{ value: data.paper.id, label: data.paper.title }]
        examVisible.value = true;
    })
}
const delExamInfo = (examInfoId) => {
    delExamInfoRequest(examInfoId).then(res => {
        getExamInfoList()
    })
}
const formatTagInputList=(arr,value,label)=>{
    return arr.map(item => {
            return { value: item[value],label:item[label]}
        });
}

const formatDateTime = (dateTimeStr) => {
    if (!dateTimeStr) return '';
    return dayjs(dateTimeStr).format('YYYY-MM-DD HH:mm');
}
const getExamStatus=(examInfo)=>{
    const startTime=examInfo.startTime;
    const endTime=examInfo.endTime;
    const now=dayjs()
    let status={
        info:'进行中',
        color:'rgb(var(--primary-6))'
    }
    if(now.isAfter(endTime)){
        status.info='已结束'
        status.color='rgb(var(--danger-6))'
    }else if(now.isBefore(startTime)){
        status.info='未开始'
        status.color='var(--color-fill-4)'
    }
    return status
}


const range = (start, end) => {
    const result = [];
    for (let i = start; i < end; i++) {
        result.push(i);
    }
    return result;
}
// 移除不需要的时间禁用函数，改用表单提交时的验证
getExamInfoList()
const columns = [
    {
        title: '考试名称',
        dataIndex: 'title',
        ellipsis: true,
        slotName: 'title',
    },
    {
        title: '开始时间',
        dataIndex: 'startTime',
        slotName: 'startTime',
    },
    {
        title: '结束时间',
        dataIndex: 'endTime',
        slotName: 'endTime',
    },
    {
        title: '编辑',
        dataIndex: 'edit',
        slotName: 'edit',
    },
]
</script>
<style lang="less" scoped>
.exam-list {
    .exam-item {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .exam-info-wrap {
            display: flex;

            .avatar {
                margin-right: 10px;
            }

            .exam-info {
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
            }


        }
    }
}
</style>