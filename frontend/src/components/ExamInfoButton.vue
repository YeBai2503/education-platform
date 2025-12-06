<template>
    <div>
        <a-button style="width: 150px;" @click="handleClick" :disabled="disabled" :status="btnStatus" :type="btnType">{{
                btnText
        }}</a-button>

        <a-modal :visible="visible" simple title="考试说明" :footer="false">
            <a-typography-paragraph>1、离开或退出考试界面答题计时不停止，请不要中途离开考试界面。</a-typography-paragraph>
            <a-typography-paragraph>2、保持座位前的桌面干净，不要有与考试无关的内容。</a-typography-paragraph>
            <a-typography-paragraph>3、考试时间截止或答题时间结束，如果处于答题页面，将自动提交试卷。</a-typography-paragraph>
            <a-typography-paragraph >4、本次考试答题时长为<a-typography-text code>{{dayjs(endTime).diff(startTime,'minute')}}</a-typography-text>分钟，进入考试后开始计时，中途退出或离开考试界面会继续计时</a-typography-paragraph>
            <a-checkbox v-model="isAgree">
                <h6>我已阅读</h6>
            </a-checkbox>
            <a-button :disabled="!isAgree" @click="startExam" type="primary" style="margin-top:20px" long>开始考试
            </a-button>
        </a-modal>
    </div>
</template>
<script setup>
import { onUnmounted, ref, watch } from 'vue';
import dayjs from 'dayjs'
import { useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';
const router = useRouter()
const props = defineProps({
    item: {
        type: Object,
        defalut: {}
    }
})
const visible = ref(false)
const isAgree = ref(false)

const btnType = ref("dashed")
const btnText = ref("猜猜是啥~")
const disabled = ref(false)
const startTime = props.item.startTime
const endTime = props.item.endTime
const btnStatus = ref('')
const status = ref("none")

let interval;
const startExam = () => {
    // 获取courseId，优先使用item.courseId，如果不存在则尝试从courseInfo中获取
    const courseId = props.item.courseId || (props.item.courseInfo ? props.item.courseInfo.id : null);
    
    console.log('开始考试，考试ID:', props.item.id, '课程ID:', courseId);
    
    // 如果有课程ID，使用课程内的考试路由
    if (courseId) {
        router.push({
            name: 'ExamStart',
            params: {
                examInfoId: props.item.id,
                courseId: courseId
            }
        });
    } else {
        router.push({
            name: 'ExamStart',
            params: {
                examInfoId: props.item.id
            }
        });
    }
}

const handleClick = () => {
    // 获取courseId，优先使用item.courseId，如果不存在则尝试从courseInfo中获取
    const courseId = props.item.courseId || (props.item.courseInfo ? props.item.courseInfo.id : null);
    
    console.log('点击考试按钮，状态:', status.value, '考试ID:', props.item.id, '课程ID:', courseId);
    
    switch (status.value) {
        case 'start':
            visible.value = true
            break;
        case 'not-start':
            Message.info('考试未开始~')
            break
        case 'end':
            Message.warning('考试已结束，禁止查看')
            break
        case 'view':
            if (courseId) {
                router.push({
                    name: 'ExamView',
                    params: {
                        examInfoId: props.item.id,
                        courseId: courseId
                    }
                });
            } else {
                router.push({
                    name: 'ExamView',
                    params: {
                        examInfoId: props.item.id
                    }
                });
            }
            break;
        case 'submitted':
            Message.info('已提交考试，无法重复提交')
            break;
    }
}
//未开始
const initData = () => {
    // 调试输出
    console.log(`ExamInfoButton 初始化 - 考试ID: ${props.item.id}, isSubmitted: ${props.item.isSubmitted}`);
    
    // 如果考试已提交
    if (props.item.isSubmitted) {
        console.log(`考试 ${props.item.id} 已提交，更新按钮状态`);
        btnText.value = "已提交"
        disabled.value = true
        btnStatus.value = 'success'
        status.value = 'submitted'
        return
    }

    if (dayjs(startTime).isAfter(dayjs())) {
        // btnType.value = "primary"
        status.value = 'not-start'
        interval = setInterval(refreshStartTime(), 1000)
        //已结束
    } else if (dayjs(endTime).isBefore(dayjs())) {
        if (props.item.endVisible) {
            btnText.value = "查看"
            btnStatus.value = 'success'
            status.value = 'view'
        } else {
            btnText.value = "已结束"
            disabled.value = true;
            status.value = 'end'
        }
    } else {
        btnText.value = "开始考试"
        btnType.value = "primary"
        status.value = 'start'
    }
}
const refreshStartTime = () => {
    if (dayjs(startTime).isBefore(dayjs())) {
        initData()
    } else {
        const diffTime = dayjs(startTime).diff(dayjs(), 'second');
        const hour = parseInt(diffTime / 3600)
        const minute = parseInt(diffTime / 60 % 60)
        const second = diffTime % 60
        btnText.value = `${hour}:${minute}:${second}`
    }
    return refreshStartTime
}
// 监听isSubmitted属性变化
watch(() => props.item.isSubmitted, (newVal, oldVal) => {
    console.log(`isSubmitted变化 - 考试ID: ${props.item.id}, 旧值: ${oldVal}, 新值: ${newVal}`);
    if (newVal !== oldVal) {
        console.log('重新初始化按钮状态');
        initData();
    }
}, { immediate: false });

onUnmounted(() => {
    clearInterval(interval)
})
initData()
</script>