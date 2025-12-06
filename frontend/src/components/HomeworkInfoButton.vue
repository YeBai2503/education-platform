<template>
    <a-button :type="btnType" :status="btnStatus" :disabled="disabled" @click="handleClick">
        {{ btnText }}
    </a-button>
    <a-modal v-model:visible="visible" :footer="false" :unmount-on-close="true" width="350px">
        <template #title>
            确认开始作业
        </template>
        <div class="start-modal">
            <p>作业：{{ props.item.title }}</p>
            <p>开始时间：{{ props.item.startTime }}</p>
            <p>结束时间：{{ props.item.endTime }}</p>
            <div class="btn-wrap">
                <a-button @click="visible = false">取消</a-button>
                <a-button type="primary" @click="startHomework">开始作业</a-button>
            </div>
        </div>
    </a-modal>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import dayjs from 'dayjs';
import { Message } from '@arco-design/web-vue';

const router = useRouter();
const props = defineProps({
    item: {
        type: Object,
        required: true
    }
});

const visible = ref(false);
const btnText = ref('开始作业');
const btnType = ref('outline');
const btnStatus = ref('normal');
const disabled = ref(false);
const status = ref('');
let interval = null;

const startTime = computed(() => props.item.startTime);
const endTime = computed(() => props.item.endTime);

// 刷新开始时间
const refreshStartTime = () => {
    const now = dayjs();
    const start = dayjs(startTime.value);
    const diff = start.diff(now, 'second');
    
    if (diff <= 0) {
        clearInterval(interval);
        btnText.value = '开始作业';
        btnType.value = 'primary';
        status.value = 'start';
        return;
    }
    
    const hours = Math.floor(diff / 3600);
    const minutes = Math.floor((diff % 3600) / 60);
    const seconds = diff % 60;
    
    btnText.value = `${hours}:${minutes < 10 ? '0' + minutes : minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
};

const startHomework = () => {
    // 关闭模态框
    visible.value = false;
    
    // 获取courseId，优先使用item.courseId，如果不存在则尝试从courseInfo中获取
    const courseId = props.item.courseId || (props.item.courseInfo ? props.item.courseInfo.id : null);
    
    console.log('开始作业，作业ID:', props.item.id, '课程ID:', courseId);
    
    if (courseId) {
        router.push({
            name: 'CourseHomeworkStart',
            params: {
                homeworkId: props.item.id,
                courseId: courseId
            }
        });
    } else {
        router.push({
            name: 'HomeworkStart',
            params: {
                homeworkId: props.item.id
            }
        });
    }
};

const handleClick = () => {
    // 获取courseId，优先使用item.courseId，如果不存在则尝试从courseInfo中获取
    const courseId = props.item.courseId || (props.item.courseInfo ? props.item.courseInfo.id : null);
    
    console.log('点击作业按钮，状态:', status.value, '作业ID:', props.item.id, '课程ID:', courseId);
    
    switch (status.value) {
        case 'start':
            visible.value = true;
            break;
        case 'not-start':
            Message.info('作业未开始~');
            break;
        case 'end':
            // 如果作业已结束，允许查看结果而不是禁止提交
            if (courseId) {
                router.push({
                    name: 'CourseHomeworkView',
                    params: {
                        homeworkId: props.item.id,
                        courseId: courseId
                    }
                });
            } else {
                router.push({
                    name: 'HomeworkView',
                    params: {
                        homeworkId: props.item.id
                    }
                });
            }
            break;
        case 'view':
            if (courseId) {
                router.push({
                    name: 'CourseHomeworkView',
                    params: {
                        homeworkId: props.item.id,
                        courseId: courseId
                    }
                });
            } else {
                router.push({
                    name: 'HomeworkView',
                    params: {
                        homeworkId: props.item.id
                    }
                });
            }
            break;
    }
};

// 初始化数据
const initData = () => {
    // 如果作业已提交
    if (props.item.isSubmitted) {
        btnText.value = "查看作业";
        btnStatus.value = 'success';
        status.value = 'view';
        return;
    }

    if (dayjs(startTime.value).isAfter(dayjs())) {
        status.value = 'not-start';
        interval = setInterval(refreshStartTime, 1000);
    } else if (dayjs(endTime.value).isBefore(dayjs())) {
        btnText.value = "查看结果";
        btnType.value = "outline";
        btnStatus.value = "warning";
        disabled.value = false; // 启用按钮
        status.value = 'end';
    } else {
        btnText.value = "开始作业";
        btnType.value = "primary";
        status.value = 'start';
    }
};

onMounted(() => {
    initData();
});

onUnmounted(() => {
    if (interval) {
        clearInterval(interval);
    }
});
</script>

<style lang="less" scoped>
.start-modal {
    .btn-wrap {
        display: flex;
        justify-content: flex-end;
        gap: 8px;
        margin-top: 20px;
    }
}
</style> 