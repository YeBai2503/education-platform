<template>
    <a-page-header :title="isReview ? '作业批阅' : '作业详情'" @back="$router.back" class="page-header">
        <template #extra v-if="isReview">
            <a-button type="primary" @click="submitReview">提交批阅</a-button>
        </template>
    </a-page-header>
    <a-spin :loading="loading" dot class="homework-container">
        <div class="left">
            <div class="result_desc common-style">
                <div class="user-info">
                    <a-avatar shape="square" class="avatar" v-loadImg :image-url="userAuthInfo?.picture || ''">
                    </a-avatar>
                    <div class="desc">
                        <div>
                            <a-tag color="orange">{{ userAuthInfo?.nickname || '未知用户' }}</a-tag>
                            <span class="real-name">({{ userAuthInfo?.realName || '未认证' }})</span>
                        </div>
                        <div>
                            <a-tag color="blue">{{ userAuthInfo?.schoolName || '未认证' }}</a-tag>
                            <a-tag color="blue">{{ userAuthInfo?.jobNo || '未认证' }}</a-tag>
                        </div>
                    </div>
                </div>
                <div class="number-desc">
                    <span class="number"> <span>得分</span><a-tag color="green">{{ score }}</a-tag></span>
                    <span class="number"> <span>正确题数</span><a-tag color="orangered">{{ correctNumber }}</a-tag></span>
                    <span class="number"> <span>批阅</span><a-tag color="cyan">{{ reviewCount }}/{{ reviewTotal }}</a-tag></span>
                    <span class="number"> <span>状态</span><a-tag>{{ answerStatus?.action || '未参加' }}</a-tag></span>
                </div>
            </div>

            <div class="numbers common-style">
                <QuestionNumber title="批阅状态" :statusList="reviewStatus" :status-visible="isReview"
                    scroll-container=".question-list" :number-list="getNumberInfo" group-class="common-style" />
            </div>
        </div>
        <div class="question-list common-style">
            <BaseQuestionPreview :id="`question-${index}`" v-for="(item, index) of answerResults" mode="review"
                :showArea="true" :number="index + 1" :question="item.questionInfo" :topic-type="item.questionInfo.type"
                :options="item.questionInfo.options || []" :my-options="item.answerResult">
                <template #body>
                    <div v-if="item.answerResult && item.answerResult.length != 0">
                        <div class="result-info" v-if="isReview">
                            <a-tag color="blue" style="margin-top: 5px" class="title">评分：</a-tag>
                            <a-input-number v-model="reviewList[item.scoreRecord.id]" mode="button"
                                :default-value="item.scoreRecord.score" :min="0" :max="item.questionInfo.score"
                                style="width: 200px;" />
                        </div>
                        <div class="result-info" v-else>
                            <a-tag color="blue" class="title"> 得分：</a-tag>
                            <a-tag color="cyan">{{ item.scoreRecord.score }}</a-tag>
                        </div>
                        <div class="result-info">
                            <a-tag color="blue" class="title">结果：</a-tag>
                            <a-tag color="red">{{ getResultType(item.scoreRecord.score == item.questionInfo.score ? 'CORRECT' : (item.scoreRecord.resultType || 'WRONG')).label }}</a-tag>
                        </div>
                        <div class="result-info" v-if="isReview">
                            <a-tag color="blue" class="title">批阅类型：</a-tag>
                            <a-tag color="#ff7d00">{{ getReviewType(item.scoreRecord.reviewType || 'ROBOT').label }}</a-tag>
                        </div>
                    </div>
                    <div v-else class="result-info">
                        <a-tag color="blue" class="title">结果：</a-tag>
                        <a-tag color="red">未作答</a-tag>
                    </div>
                </template>
            </BaseQuestionPreview>
        </div>
    </a-spin>
    <QuestionImagePreview click-area=".question-list" />
</template>

<script setup>
import { getHomeworkStudentDetailRequest, reviewHomeworkRequest } from '@/apis/homework-api.js';
import { useRoute } from 'vue-router';
import { ref, shallowRef, computed } from 'vue';
import { getQuestionType } from "../../utils/question-config";
import QuestionNumber from "../../components/QuestionNumber.vue";
import BaseQuestionPreview from '../../components/BaseQuestionPreview.vue';
import { getResultType, getReviewType } from '../../utils/review-info.js';
import QuestionImagePreview from '../../components/QuestionImagePreview.vue';
import { Message } from '@arco-design/web-vue';
import useUserStore from '../../sotre/user-store';

const route = useRoute();
const userStore = useUserStore();
const homeworkInfoId = route.params['homeworkId'];
const isReview = route.name === 'HomeworkReview' || route.name === 'CourseHomeworkReview';

const studentId = isReview ? route.params['studentId'] : userStore.userInfo.userId;

const userAuthInfo = ref({});
const score = ref(0);
const correctNumber = ref(0);
const numberGroup = ref({});
const answerResults = shallowRef([]);
const loading = ref(true);
const reviewList = ref({});
const answerStatus = ref(null);

const reviewTotal = ref(0);
const reviewCount = ref(0);

const getQuestionList = (qList) => {
    Object.keys(qList).forEach((key) => {
        console.log('处理题目类型:', key);
        console.log('题目数据:', qList[key]);
        
        // 确保每个题目都有完整的数据
        const processedQuestions = qList[key].map(q => {
            // 确保questionInfo存在且有必要的属性
            if (q.questionInfo) {
                // 如果options为null，设置为空数组
                if (q.questionInfo.options === null || q.questionInfo.options === undefined) {
                    q.questionInfo.options = [];
                }
                
                // 如果type不存在，从键名中推断
                if (!q.questionInfo.type) {
                    q.questionInfo.type = key;
                }
            }
            
            // 确保scoreRecord存在
            if (!q.scoreRecord) {
                q.scoreRecord = {
                    id: null,
                    score: 0,
                    resultType: 'WRONG',
                    reviewType: 'NONE'
                };
            }
            
            return q;
        });
        
        answerResults.value.push(...processedQuestions);
        numberGroup.value[key] = processedQuestions.length;
    });
};

const submitReview = () => {
    if (Object.keys(reviewList.value).length == 0) {
        Message.info("批阅列表为空~");
        return;
    }
    
    const reviewData = [];
    Object.keys(reviewList.value).forEach(value => {
        reviewData.push({
            id: value,
            score: reviewList.value[value],
            resultType: reviewList.value[value] > 0 ? 'CORRECT' : 'ERROR'
        });
    });
    
    reviewHomeworkRequest(homeworkInfoId, studentId, reviewData).then(res => {
        if (res && res.data && (res.data.code === '00000' || res.data.code === 200)) {
            Message.success('批阅提交成功');
            getStudentAnswerResult();
        } else {
            Message.error(res?.data?.msg || '批阅提交失败');
        }
    }).catch(err => {
        console.error('批阅提交失败:', err);
        Message.error('批阅提交失败，请稍后重试');
    });
};

// 获取序号选项
const getNumberInfo = computed(() => {
    const numberInfo = [];
    const results = answerResults.value;
    let i = 0;
    
    for (const key in numberGroup.value) {
        const name = getQuestionType(key).simpleName;
        const info = [];
        const length = (i + numberGroup.value[key]);
        
        for (; i < length; i++) {
            let color = 'NONE';
            const result = results[i];
            if (isReview && result.scoreRecord) {
                color = result.scoreRecord.reviewType || 'ROBOT'; // 如果reviewType为null，则默认为机器批阅
            }
            
            info.push({
                key: i,
                number: i + 1,
                href: `question-${i}`,
                statusKey: color
            });
        }
        
        numberInfo.push({
            title: name,
            list: info
        });
    }
    
    return numberInfo;
});

const getStudentAnswerResult = () => {
    loading.value = true;
    
    getHomeworkStudentDetailRequest(homeworkInfoId, studentId).then(res => {
        console.log('获取学生作业详情API返回:', res);
        
        if (res && res.data && res.data.data) {
            const data = res.data.data;
            
            // 确保userAuthInfo有默认值
            if (!data.userAuthInfo) {
                data.userAuthInfo = {
                    nickname: data.userName || '未知用户',
                    realName: data.realName || '',
                    jobNo: data.jobNo || '',
                    schoolName: data.schoolName || '',
                    picture: ''
                };
            }
            
            userAuthInfo.value = data.userAuthInfo;
            score.value = data.score || 0;
            correctNumber.value = data.correctNumber || 0;
            
            // 处理批阅数量异常的情况
            // 如果reviewCount大于reviewTotal，则可能是数据问题，取二者的较大值
            reviewCount.value = data.reviewCount || 0;
            reviewTotal.value = data.reviewTotal || 0;
            if (reviewCount.value > reviewTotal.value) {
                console.warn('批阅数量异常：reviewCount > reviewTotal', reviewCount.value, reviewTotal.value);
                reviewTotal.value = reviewCount.value;
            }
            
            answerStatus.value = data.answerStatus || { action: '未参加', value: 0 };
            
            // 清空现有数据
            answerResults.value = [];
            numberGroup.value = {};
            reviewList.value = {};
            
            if (data.answerResults) {
                getQuestionList(data.answerResults);
                
                // 初始化批阅列表
                if (isReview) {
                    // 遍历所有题目，为每个题目初始化批阅分数
                    answerResults.value.forEach(item => {
                        if (item.scoreRecord && item.scoreRecord.id) {
                            reviewList.value[item.scoreRecord.id] = item.scoreRecord.score;
                        }
                    });
                }
            }
            
            console.log('处理后的学生作业详情数据:', data);
            console.log('批阅列表初始化:', reviewList.value);
        } else {
            Message.error('获取学生作业详情失败');
            // 设置默认值
            userAuthInfo.value = {
                nickname: '未知用户',
                realName: '',
                jobNo: '',
                schoolName: '',
                picture: ''
            };
            answerStatus.value = { action: '未参加', value: 0 };
        }
        
        loading.value = false;
    }).catch(err => {
        console.error('获取学生作业详情失败:', err);
        Message.error('获取学生作业详情失败，请稍后重试');
        loading.value = false;
    });
};

// 初始化
getStudentAnswerResult();

const reviewStatus = [
    {
        key: 'NONE',
        status: '未作答',
        style: { backgroundColor: 'var(--color-fill-1)' }
    },
    {
        key: 'ROBOT',
        status: '机器批阅',
        style: { backgroundColor: 'rgba(var(--blue-4), 8)', color: 'var(--color-white)' }
    },
    {
        key: 'TEACHER',
        status: '教师批阅',
        style: { backgroundColor: 'rgba(var(--green-4), 8)', color: 'var(--color-white)' }
    }
];
</script>

<style lang="less" scoped>
.common-style {
    background-color: var(--color-menu-light-bg);
    border-radius: 10px;
    padding: 20px;
    margin: 15px 0;
}

.page-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    padding: 16px 20px;
    z-index: 99;
}

.homework-container {
    padding: 0 10px;
    padding-top: 72px;
    height: 100vh;
    min-height: 500px;
    box-sizing: border-box;
    display: flex;
    overflow: hidden;
    background-color: var(--color-fill-1);

    .left {
        width: 300px;
        height: 100%;
        overflow-y: auto;
        overflow-y: overlay;

        .result_desc {
            position: sticky;
            top: 0;
            margin-top: 0;
            z-index: 10;
        }

        .user-info {
            display: flex;
            justify-content: center;
            align-items: center;

            .avatar {
                margin-right: 10px;
            }

            .desc {
                display: flex;
                flex-direction: column;
                justify-content: center;
                flex: 1;

                span {
                    margin: 2px;
                    font-weight: bold;
                }

                .real-name {
                    font-size: 14px;
                    color: var(--color-text-3);
                }
            }
        }

        .number-desc {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-around;
            margin-top: 10px;

            .number {
                display: flex;
                flex-direction: column;
                align-items: center;
                margin: 5px;

                span {
                    font-size: 14px;
                    color: var(--color-text-2);
                    margin-bottom: 5px;
                }
            }
        }
    }

    .question-list {
        flex: 1;
        height: 100%;
        overflow-y: auto;
        overflow-y: overlay;
        margin-left: 10px;
        margin-right: 10px;
    }
}

.result-info {
    display: flex;
    align-items: center;
    margin: 8px 0;

    .title {
        margin-right: 10px;
    }
}
</style> 