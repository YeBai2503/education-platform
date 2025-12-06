<template>
    <div class="message-card-wrap">
            <!-- 用户消息 -->
            <div class="user-message" v-if="cardType==0">
                <TextEditor mode="preview" :model-value="props.messageInfo.introduce"></TextEditor>
            </div>
            <!-- 课程通知/超链接 -->
            <div class="card-message" @click="toLink" v-else-if="cardType==1">
                <AAvatar :size="50" style="background-color: var(--color-primary-light-4) "  shape="square">{{ getNotifyText(props.messageInfo.type.info) }}</AAvatar>
                <div class="voice-warp">
                    <h1 style="margin-bottom: 5px;">{{ props.messageInfo.title }}</h1>
                    <p>{{ props.messageInfo.introduce }}</p>
                </div>
            </div>
            <!-- 图片 -->
            <div class="card-picture" v-else-if="cardType==2">

            </div>
             <!--视频 -->
            <div class="card-picture" v-else-if="cardType==3">
            </div>

</div>
</template>
<script setup>
import { useRouter } from 'vue-router';
import TextEditor from '../TextEditor.vue';

const props=defineProps({
    messageInfo:Object,
})
const cardType=(props.messageInfo.type.value+"").charAt(2)
console.log(cardType)
const router=useRouter()
const toLink=()=>{
    try {
        // 输出调试信息
        console.log('点击消息卡片，消息类型:', props.messageInfo.type.value, '消息标题:', props.messageInfo.title);
        console.log('消息完整内容:', props.messageInfo);
        
        // 检查路径是否为JSON格式
        if (props.messageInfo.path) {
            console.log('原始路径字符串:', props.messageInfo.path);
            
            // 尝试解析JSON
            let routeInfo;
            try {
                routeInfo = JSON.parse(props.messageInfo.path);
            } catch (error) {
                console.error('解析路由信息失败:', error);
                // 如果解析失败，尝试处理可能的特殊格式
                routeInfo = props.messageInfo.path;
            }
            
            console.log('解析后的路由信息:', routeInfo);
            
            // 检查消息类型，处理不同类型的消息
            const messageType = props.messageInfo.type.value;
            console.log('消息类型值:', messageType);
            
            // 修复路由名称问题
            if (routeInfo.name === 'HomeworkManage') {
                console.log('检测到HomeworkManage路由，修正为CourseHomeworkPaperManger');
                
                // 获取courseId参数
                const courseId = routeInfo.params?.courseId || routeInfo.courseId || routeInfo.params?.[''];
                
                if (courseId) {
                    router.push({
                        name: 'Homework',
                        params: {
                            courseId: courseId
                        }
                    });
                    return;
                }
            }
            
            // 如果消息标题包含"作业"关键字，优先作为作业消息处理
            if (messageType >= 3010 && messageType < 3020 || 
                (props.messageInfo.title && props.messageInfo.title.includes('作业'))) {
                console.log('检测到作业相关消息');
                
                // 解析作业ID和课程ID
                const homeworkId = routeInfo.params?.homeworkId || routeInfo.homeworkId;
                const courseId = routeInfo.params?.courseId || routeInfo.courseId || routeInfo.params?.[''];
                
                console.log('提取的作业ID:', homeworkId, '课程ID:', courseId);
                
                // 如果没有明确的作业ID，但有name属性指向作业相关路由
                if (!homeworkId && routeInfo.name && routeInfo.name.toLowerCase().includes('homework')) {
                    // 处理特定的作业路由
                    if (routeInfo.name === 'HomeworkManage' || routeInfo.name === 'HomeworkPaperManger') {
                        if (courseId) {
                            console.log('跳转到课程作业管理页面');
                            router.push({
                                name: 'CourseHomeworkPaperManger',
                                params: {
                                    courseId
                                }
                            });
                            return;
                        }
                    }
                    
                    // 尝试使用原始路由信息
                    console.log('通过路由名称识别为作业相关路由');
                    try {
                        router.push(routeInfo);
                        return;
                    } catch (e) {
                        console.error('路由跳转失败，尝试备用方案:', e);
                    }
                }
                
                if (homeworkId) {
                    if (courseId) {
                        // 如果有课程ID，跳转到课程内的作业中心
                        console.log('跳转到课程内的作业中心');
                        router.push({
                            name: 'Homework',
                            params: {
                                courseId
                            }
                        });
                    } else {
                        // 否则跳转到独立的作业页面
                        console.log('跳转到独立的作业页面');
                        router.push({
                            name: 'HomeworkStart',
                            params: {
                                homeworkId
                            }
                        });
                    }
                    return;
                } else if (courseId) {
                    // 如果只有课程ID，也跳转到该课程的作业中心
                    console.log('只有课程ID，跳转到课程作业中心');
                    router.push({
                        name: 'Homework',
                        params: {
                            courseId
                        }
                    });
                    return;
                }
            }
            
            // 对于其他类型的消息，使用常规路由导航
            console.log('使用常规路由导航');
            if (typeof routeInfo === 'object') {
                try {
                    router.push(routeInfo);
                } catch (e) {
                    console.error('常规路由导航失败:', e);
                    // 尝试提取课程ID并跳转到课程主页
                    if (routeInfo.params && (routeInfo.params.courseId || routeInfo.params[''])) {
                        const courseId = routeInfo.params.courseId || routeInfo.params[''];
                        console.log('尝试使用提取的课程ID跳转到课程页面:', courseId);
                        router.push({
                            name: 'Course',
                            params: {
                                courseId
                            }
                        });
                    }
                }
            } else if (typeof routeInfo === 'string' && routeInfo.startsWith('/')) {
                router.push(routeInfo);
            } else {
                console.error('无效的路由信息:', routeInfo);
            }
        } else {
            console.warn('消息中没有路径信息');
        }
    } catch (error) {
        console.error('导航失败:', error);
    }
}

const getNotifyText = (info) => {
  if (info && info.includes('通知')) {
    return '通知';
  }
  return info;
}
</script>
<style lang="less" scoped>
.user-message{
    padding: 10px;
    border-radius: 5px;
    display: flex;
}
.card-message{
    padding: 15px 10px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    background-color: var(--color-fill-2);
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    :first-child{
        flex-shrink: 0;
    }
    .voice-warp{
        margin: 0 10px;
        :first-child{
            line-height: 1.5;
            color: var(--color-text-1);
        }
        :last-child{
            font-size: 14px;
            color: var(--color-text-2);
        }
    }
}
</style>