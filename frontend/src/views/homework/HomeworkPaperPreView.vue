<template>
    <a-page-header title="作业预览" :subtitle="title" @back="$router.back">
    </a-page-header>
    <BaseQuestionPreview 
        v-for="(item, index) in list" 
        :key="item.id" 
        :topic-type="item.type" 
        :number="index+1" 
        :question="item" 
        :options="item.options || []"
        :showArea="true"
    />
</template>
<script setup>
import { ref, shallowRef, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getHomeworkPaperDetailRequest } from '../../apis/homework-api';
import { questionDetailRequest } from '../../apis/question-api';
import BaseQuestionPreview from '../../components/BaseQuestionPreview.vue';
import { Message } from '@arco-design/web-vue';

const route = useRoute();
const homeworkId = route.params['homeworkId'];
const title = route.query["title"];
const list = shallowRef([]);
const loading = ref(true);

// 获取完整题目内容
const getFullQuestionDetails = async (questionIds) => {
    if (!questionIds || questionIds.length === 0) {
        return [];
    }
    
    console.log(`开始获取 ${questionIds.length} 道题目的详细内容`);
    
    // 逐个获取题目详情
    const fullQuestions = [];
    for (let i = 0; i < questionIds.length; i++) {
        try {
            const id = questionIds[i];
            console.log(`获取题目 ${i+1}/${questionIds.length} (ID: ${id}) 的详情`);
            const response = await questionDetailRequest(id);
            
            if (response.data && response.data.data) {
                const questionDetail = response.data.data;
                console.log(`题目 ${id} 详情获取成功`);
                fullQuestions.push(questionDetail);
                
                // 更新当前题目显示
                const updatedList = [...list.value];
                updatedList[i] = questionDetail;
                list.value = updatedList;
            } else {
                console.error(`题目 ${id} 详情获取失败:`, response);
            }
        } catch (error) {
            console.error(`获取题目 ${questionIds[i]} 详情时出错:`, error);
        }
    }
    
    return fullQuestions;
};

// 加载作业数据
const loadHomeworkData = async () => {
    try {
        const response = await getHomeworkPaperDetailRequest(homeworkId);
        
        if (response.data && response.data.data) {
            const data = response.data.data;
            
            // 检查是否有questions字段
            if (data.questions && Array.isArray(data.questions)) {
                if (data.questions.length > 0) {
                    // 检查第一个元素是否为数字（题目ID）
                    if (typeof data.questions[0] === 'number' || 
                        (typeof data.questions[0] === 'object' && !data.questions[0].content)) {
                        // 如果是ID列表，获取完整题目内容
                        const questionIds = data.questions.map(q => typeof q === 'number' ? q : q.id);
                        await getFullQuestionDetails(questionIds);
                    } else {
                        // 已经是完整题目对象
                        list.value = data.questions;
                    }
                }
            } else {
                console.error('作业预览数据中没有有效的题目数组');
            }
        } else {
            console.error('获取作业预览数据失败');
        }
    } catch (error) {
        console.error('获取作业预览失败:', error);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    loadHomeworkData();
});
</script>

<style lang="less" scoped>
</style> 