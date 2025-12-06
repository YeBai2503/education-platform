<template>
    <div class="homework-view">
        <a-page-header title="作业详情" @back="$router.back()">
            <template #extra>
                <a-tag color="green" v-if="homeworkData.totalScore">得分: {{ homeworkData.totalScore }}</a-tag>
                <a-tag color="blue" v-else>等待批阅</a-tag>
            </template>
        </a-page-header>
        <a-spin :loading="loading" class="spin-container">
            <div class="homework-content">
                <h2>{{ homeworkData.homeworkInfo?.title || '作业详情' }}</h2>
                <div class="homework-info">
                    <p v-if="homeworkData.homeworkInfo?.startTime">开始时间: {{ homeworkData.homeworkInfo.startTime }}</p>
                    <p v-if="homeworkData.homeworkInfo?.endTime">结束时间: {{ homeworkData.homeworkInfo.endTime }}</p>
                    <p v-if="homeworkData.homeworkInfo?.description" class="description">
                        {{ homeworkData.homeworkInfo.description }}
                    </p>
                    <p v-if="homeworkData.homeworkInfo?.allowLateSubmit" class="late-info">
                        <a-tag color="orange">允许迟交</a-tag>
                        迟交截止时间: {{ homeworkData.homeworkInfo.lateEndTime }}
                        <span v-if="homeworkData.homeworkInfo.lateDeduction">
                            (扣除 {{ homeworkData.homeworkInfo.lateDeduction * 100 }}% 分数)
                        </span>
                    </p>
                </div>
                
                <div class="homework-questions">
                    <div class="question-list">
                        <BaseQuestionPreview 
                            v-for="(question, index) in processedQuestions" 
                            :key="question.id"
                            :id="`question-${question.id}`"
                            :topic-type="question.type"
                            :number="index + 1"
                            :question="formatQuestion(question)"
                            :options="formatOptions(question)"
                            :show-area="{ answer: true, analysis: true }"
                            mode="review"
                        >
                            <template #body>
                                <div class="result-info">
                                    <a-tag color="blue" class="title">得分：</a-tag>
                                    <a-tag v-if="question.scoreRecord" 
                                        :color="getScoreColor(question.scoreRecord.score, question.score)">
                                        {{ question.scoreRecord.score }}/{{ question.score }}
                                    </a-tag>
                                    <a-tag v-else color="blue">等待批阅</a-tag>
                                </div>
                                
                                <div v-if="question.scoreRecord && question.scoreRecord.comment" class="comment">
                                    <a-tag color="blue" class="title">教师评语：</a-tag>
                                    <p>{{ question.scoreRecord.comment }}</p>
                                </div>
                            </template>
                        </BaseQuestionPreview>
                    </div>
                </div>
            </div>
            
            <div class="homework-number common-style">
                <QuestionNumber 
                    title="题目导航" 
                    @numberClick="numberChange"
                    scroll-container=".question-list" 
                    :number-list="getNumberInfo" 
                    group-class="common-style" 
                />
            </div>
        </a-spin>
        <QuestionImagePreview click-area=".question-list" />
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import BaseQuestionPreview from '../../components/BaseQuestionPreview.vue';
import QuestionNumber from '../../components/QuestionNumber.vue';
import QuestionImagePreview from '../../components/QuestionImagePreview.vue';
import { getQuestionType, letterList } from '../../utils/question-config';
import { getHomeworkDetailRequest } from '../../apis/homework-api';
import { questionDetailRequest } from '../../apis/question-api';

const route = useRoute();
const homeworkId = route.params.homeworkId;
const loading = ref(true);
const homeworkData = ref({
    homeworkInfo: null,
    questions: [],
    answerResults: [],
    scoreRecords: [],
    totalScore: 0
});

// 获取作业详情
const getHomeworkDetail = async () => {
    loading.value = true;
    try {
        const response = await getHomeworkDetailRequest(homeworkId);
        if (response.data && response.data.data) {
            homeworkData.value = response.data.data;
            console.log('获取到的作业详情:', homeworkData.value);
            
            // 获取选择题的详细信息
            await fetchQuestionDetails();
        } else {
            Message.error('获取作业详情失败');
        }
    } catch (error) {
        console.error('获取作业详情失败', error);
        Message.error('获取作业详情失败');
    } finally {
        loading.value = false;
    }
};

// 获取选择题的详细信息
const fetchQuestionDetails = async () => {
    const questions = homeworkData.value.questions || [];
    const choiceQuestions = questions.filter(q => 
        ['SIGNAL_CHOICE', 'MULTIPLE_CHOICE', 'JUDGMENTAL'].includes(q.type) && 
        (!q.options || q.options.length === 0)
    );
    
    if (choiceQuestions.length === 0) return;
    
    try {
        for (const question of choiceQuestions) {
            const response = await questionDetailRequest(question.id);
            if (response.data && response.data.data) {
                const detailedQuestion = response.data.data;
                // 更新问题的选项
                const index = questions.findIndex(q => q.id === question.id);
                if (index !== -1) {
                    questions[index].options = detailedQuestion.options || [];
                }
            }
        }
    } catch (error) {
        console.error('获取题目详情失败', error);
    }
};

// 处理后的题目列表，包含答案和评分信息
const processedQuestions = computed(() => {
    const questions = homeworkData.value.questions || [];
    const answerResults = homeworkData.value.answerResults || [];
    const scoreRecords = homeworkData.value.scoreRecords || [];
    
    // 按题目ID分组答案，保留最新答案（ID最大的）
    const latestAnswers = {};
    answerResults.forEach(answer => {
        if (!latestAnswers[answer.questionId] || 
            latestAnswers[answer.questionId].id < answer.id) {
            latestAnswers[answer.questionId] = answer;
        }
    });
    
    // 添加评分记录到题目
    return questions.map(question => {
        // 找到题目对应的评分记录
        const scoreRecord = scoreRecords.find(record => record.questionId === question.id);
        // 找到题目对应的最新答案
        const answer = latestAnswers[question.id];
        
        // 返回扩展的题目对象
        return {
            ...question,
            scoreRecord,
            answer
        };
    });
});

// 格式化问题对象
const formatQuestion = (question) => {
    return {
        id: question.id,
        content: question.content || '',
        type: question.type,
        score: question.score || 0,
        analysis: question.analysis || '',
        difficulty: question.difficulty
    };
};

// 格式化选项，主要处理答案
const formatOptions = (question) => {
    // 选择题类型
    if (['SIGNAL_CHOICE', 'MULTIPLE_CHOICE', 'JUDGMENTAL'].includes(question.type)) {
        // 如果没有选项数据，创建默认选项
        if (!question.options || question.options.length === 0) {
            // 判断题默认创建是/否选项
            if (question.type === 'JUDGMENTAL') {
                return [
                    { id: `${question.id}_1`, content: '是', answer: question.answer?.answer === '是' ? true : null },
                    { id: `${question.id}_2`, content: '否', answer: question.answer?.answer === '否' ? true : null }
                ];
            }
            
            // 其他选择题创建A-D选项
            return Array.from({ length: 4 }, (_, i) => ({
                id: `${question.id}_${i+1}`,
                content: `选项${letterList[i]}`,
                answer: question.answer?.answer === letterList[i] ? true : null
            }));
        }
        
        // 如果有选项数据，处理学生答案
        return (question.options || []).map(option => {
            // 复制选项以避免修改原始数据
            const newOption = { ...option };
            
            // 如果有学生答案，标记选中状态
            if (question.answer) {
                if (question.type === 'MULTIPLE_CHOICE') {
                    // 多选题答案可能是多个选项
                    const answers = question.answer.answer ? question.answer.answer.split(',') : [];
                    newOption.answer = answers.includes(option.id.toString()) || 
                                       answers.includes(letterList[question.options.findIndex(o => o.id === option.id)]) ? 
                                       true : null;
                } else {
                    // 单选题和判断题
                    const studentAnswer = question.answer.answer;
                    const optionIndex = question.options.findIndex(o => o.id === option.id);
                    newOption.answer = (studentAnswer === option.id.toString() || 
                                       studentAnswer === letterList[optionIndex]) ? 
                                       true : null;
                }
            }
            
            return newOption;
        });
    }
    
    // 主观题类型（论述题、填空题等）
    if (['SUBJECTIVE', 'COMPLETION'].includes(question.type)) {
        return [{
            id: question.id + '_answer',
            content: '',
            answer: question.answer?.answer || ''
        }];
    }
    
    // 如果有选项数据，返回原始选项
    return question.options || [];
};

// 获取分数颜色
const getScoreColor = (score, totalScore) => {
    if (score === undefined || score === null) return 'blue';
    if (score === totalScore) return 'green';
    if (score >= totalScore * 0.6) return 'orange';
    return 'red';
};

// 按题目类型分组的题目列表
const questionGroups = computed(() => {
    const grouped = {};
    
    processedQuestions.value.forEach(question => {
        const type = question.type;
        if (!grouped[type]) {
            grouped[type] = [];
        }
        grouped[type].push(question);
    });
    
    return grouped;
});

// 获取题号导航信息
const getNumberInfo = computed(() => {
    const numberInfo = [];
    let globalIndex = 0;
    
    Object.keys(questionGroups.value).forEach(type => {
        const questions = questionGroups.value[type];
        const questionType = getQuestionType(type);
        const typeName = questionType ? questionType.name : type;
        const info = [];
        
        questions.forEach((question, index) => {
            const localIndex = globalIndex + index;
            const scoreRecord = question.scoreRecord;
            
            let scoreStatus = 'info';
            if (scoreRecord) {
                scoreStatus = scoreRecord.score === question.score ? 'success' : 
                    scoreRecord.score >= question.score * 0.6 ? 'warning' : 'error';
            }
            
            info.push({
                key: question.id,
                number: localIndex + 1,
                href: `question-${question.id}`,
                statusKey: scoreStatus
            });
        });
        
        if (info.length > 0) {
            numberInfo.push({
                title: typeName,
                list: info
            });
        }
        
        globalIndex += questions.length;
    });
    
    return numberInfo;
});

// 题号点击
const numberChange = (info) => {
    const element = document.getElementById(`question-${info.key}`);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
};

onMounted(() => {
    getHomeworkDetail();
});
</script>

<style lang="less" scoped>
.homework-view {
    position: relative;
    
    .spin-container {
        display: flex;
        min-height: 400px;
    }
    
    .homework-content {
        flex: 1;
        padding: 20px;
        background-color: var(--color-bg-2);
        border-radius: 4px;
        margin-right: 20px;
        
        h2 {
            margin-bottom: 16px;
            font-size: 20px;
        }
        
        h3 {
            margin: 16px 0 8px;
            font-size: 16px;
            border-left: 4px solid var(--color-primary-6);
            padding-left: 10px;
        }
        
        .homework-info {
            margin-bottom: 24px;
            color: var(--color-text-3);
            
            p {
                margin-bottom: 8px;
            }
            
            .description {
                white-space: pre-line;
                margin-top: 10px;
                padding: 10px;
                background-color: var(--color-fill-2);
                border-radius: 4px;
            }
            
            .late-info {
                margin-top: 10px;
                padding: 8px;
                background-color: var(--color-fill-2);
                border-radius: 4px;
                display: flex;
                align-items: center;
                gap: 8px;
            }
        }
        
        .homework-questions {
            .question-list {
                margin-bottom: 20px;
            }
            
            .result-info {
                margin-top: 10px;
                display: flex;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                
                .title {
                    margin-right: 8px;
                }
                
                .comment {
                    margin-top: 5px;
                    width: 100%;
                    padding: 8px;
                    background-color: var(--color-fill-2);
                    border-radius: 4px;
                    font-size: 13px;
                }
                
                .result-type {
                    margin-left: auto;
                }
            }
        }
    }
    
    .homework-number {
        width: 200px;
        background-color: var(--color-bg-2);
        border-radius: 4px;
        padding: 16px;
    }
    
    .common-style {
        background-color: var(--color-bg-2);
        border-radius: 4px;
        padding: 16px;
    }
}
</style>