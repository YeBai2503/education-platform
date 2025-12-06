<template>
    <div class="homework-header">
        <a-button type="primary" class="homework-submit" @click="checkSubmit">提交作业</a-button>
        <a-button v-if="isPreview" class="back-preview" shape="round" @click="isPreview = false">
            <template #icon><icon-left /></template>
            返回</a-button>
        <p class="homework-title">{{ homeworkData.title || "作业" }}</p>
    </div>
    <div class="homework-start">
        <div class="homework-info">
            <div class="common-style" style="position:sticky;top:0px">
                <div class="number-desc">
                    <span class="number"> 已答：<a-tag color="green">{{ answerNumber }}</a-tag></span>
                    <span class="number"> 题数：<a-tag color="orangered">{{ questionList.length }}</a-tag></span>
                </div>
            </div>

            <div class="common-style homework-info-box">
                <h1>{{ homeworkData.title || "加载中..." }}</h1>
                <div class="homework-time-list">
                    <div class="homework-time-item">
                        <span class="time-label">开始时间：</span>
                        <span class="time-value">{{ homeworkData.startTime }}</span>
                    </div>
                    <div class="homework-time-item">
                        <span class="time-label">结束时间：</span>
                        <span class="time-value">{{ homeworkData.endTime }}</span>
                    </div>
                    <div class="homework-time-item" v-if="homeworkData.allowLateSubmit">
                        <span class="time-label">补交截止时间：</span>
                        <span class="time-value">{{ homeworkData.lateEndTime }}</span>
                    </div>
                </div>
                <p v-if="homeworkData.description" class="description">{{ homeworkData.description }}</p>
            </div>
            
            <div class="common-style">
                <QuestionNumber :status-visible="true" title="标记区" @numberClick="numberChange"
                    scroll-container=".question-list" :number-list="markNumberList" group-class="common-style">
                    <template #title>
                        <div style="display:flex;align-items: center;">
                            <h5>标记区</h5>
                            <p style="margin-left: 10px;font-size:12px;color:var(--color-text-2)">答案不确定，点击题目序号进行标记</p>
                        </div>
                    </template>
                </QuestionNumber>
                <a-empty v-if="markNumberList[0].list.length==0" description="暂无标记题目" />
            </div>
        </div>
        
        <div class="question-list">
            <div>
                <div class="question-pre-next" style="display: flex; justify-content: space-around" v-if="!isPreview">
                    <a-button-group>
                        <a-button long :disabled="currQuestIndex == 0" @click="switchQuestion(currQuestIndex - 1)">上一题
                            <template #icon>
                                <icon-left />
                            </template>
                        </a-button>
                        <a-button class="nextquestBtn" long type="primary"
                            :disabled="currQuestIndex == questionList.length - 1"
                            @click="switchQuestion(currQuestIndex + 1)">
                            <template #icon>
                                <icon-right />
                            </template>
                            下一题</a-button>
                        <a-button long @click="isPreview = true">整卷预览</a-button>
                    </a-button-group>
                </div>
                
                <a-spin :loading="loading" class="spin-container">
                    <a-list :data="getHomeworkQuestions" :bordered="false" v-if="questionList.length > 0">
                        <template #item="{ item, index }">
                            <a-list-item :id="`question-${item.id}`">
                                <BaseQuestionPreview 
                                    @editorBlur="submitAnswer(item.id)"
                                    :key="item.id"
                                    @choiceCorrect="(selects) => choiceCorrect(item.id, selects)"
                                    @markQuestion="markQuestion"
                                    mode="answer"
                                    :show-analysis="false"
                                    :topic-type="item.type"
                                    :question="item"
                                    :number="isPreview ? (index + 1) : (currQuestIndex + 1)"
                                    v-model:options="item.options"
                                    :lazy="isPreview"
                                >
                                    <template #question="{ question, options, type }">
                                        <Transition name="fade">
                                            <span class="sub-info" v-if="question['subInfo'] != undefined">{{
                                                showSubInfo(question["subInfo"], options.length, getQuestionType(question.type))
                                            }}</span>
                                        </Transition>
                                    </template>
                                </BaseQuestionPreview>
                            </a-list-item>
                        </template>
                    </a-list>
                    <a-empty v-else description="暂无题目"></a-empty>
                </a-spin>
            </div>
        </div>
        
        <div class="homework-number common-style">
            <QuestionNumber 
                :status-list="answerStatus" 
                :status-visible="true" 
                title="作答状态" 
                @numberClick="numberChange"
                scroll-container=".question-list" 
                :number-list="getNumberInfo" 
                group-class="common-style" 
            />
        </div>
    </div>
    <QuestionImagePreview click-area=".question-list" />
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message, Modal } from '@arco-design/web-vue';
import dayjs from 'dayjs';
import BaseQuestionPreview from '../../components/BaseQuestionPreview.vue';
import QuestionNumber from '../../components/QuestionNumber.vue';
import QuestionImagePreview from '../../components/QuestionImagePreview.vue';
import { getQuestionType } from '../../utils/question-config';
import { getHomeworkDetailRequest, submitHomeworkRequest, startHomeworkRequest, getHomeworkInfoRequest } from '../../apis/homework-api';
import { questionDetailRequest } from '../../apis/question-api';
import { IconLeft, IconRight } from '@arco-design/web-vue/es/icon';
import useUserStore from '../../sotre/user-store';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const homeworkId = route.params.homeworkId;
const loading = ref(true);
const isPreview = ref(false);
const currQuestIndex = ref(0);

const homeworkData = ref({
    id: homeworkId,
    title: '',
    startTime: '',
    endTime: '',
    description: '',
    allowLateSubmit: false,
    lateEndTime: '',
    lateDeduction: 0
});

const questionList = ref([]);
const numberGroup = ref({});
const answerStatus = ref([
    {
        key: 'none',
        status: '未作答',
        style: { backgroundColor: 'var(--color-fill-4)' }
    },
    {
        key: 'start',
        status: '部分作答',
        style: { backgroundColor: 'rgb(var(--warning-6))' }
    },
    {
        key: 'end',
        status: '已作答',
        style: { backgroundColor: 'rgb(var(--success-6))' }
    }
]);
const answerNumber = ref(0);
// 标记题目列表
const markNumberList = ref([{list:[]}]);

// 获取当前显示的题目列表
const getHomeworkQuestions = computed(() => {
    if (isPreview.value) {
        return questionList.value;
    } else {
        return questionList.value.length > 0 ? [questionList.value[currQuestIndex.value]] : [];
    }
});

// 获取作业详情
const getHomeworkDetail = () => {
    loading.value = true;
    
    // 获取作业基本信息
    getHomeworkInfoRequest(homeworkId)
        .then(infoRes => {
            console.log('作业基本信息API返回数据:', infoRes);
            
            if (infoRes && infoRes.data && infoRes.data.data) {
                const homeworkInfo = infoRes.data.data;
                
                // 设置作业基本信息
                homeworkData.value = {
                    id: homeworkId,
                    title: homeworkInfo.title || '未命名作业',
                    startTime: homeworkInfo.startTime || dayjs().format('YYYY-MM-DD HH:mm:ss'),
                    endTime: homeworkInfo.endTime || dayjs().add(1, 'day').format('YYYY-MM-DD HH:mm:ss'),
                    description: homeworkInfo.description || '',
                    allowLateSubmit: homeworkInfo.allowLateSubmit || false,
                    lateEndTime: homeworkInfo.lateEndTime || '',
                    lateDeduction: homeworkInfo.lateDeduction || 0
                };
            }
            
            // 调用开始作答 API
            return startHomeworkRequest(homeworkId);
        })
        .then(startRes => {
            console.log('开始作答API返回数据:', startRes);
            // 获取作业详情
            return getHomeworkDetailRequest(homeworkId);
        })
        .then(res => {
            console.log('作业详情API返回数据:', res);
            
            // 检查API返回的数据结构
            if (!res || !res.data) {
                throw new Error('API返回数据格式错误');
            }
            
            const responseData = res.data;
            
            // 检查业务状态码
            if (responseData.code !== '00000' && responseData.code !== 200) {
                throw new Error(responseData.msg || '获取作业详情失败');
            }
            
            // 获取实际数据
            const homeworkDetail = responseData.data;
            
            if (!homeworkDetail) {
                throw new Error('作业详情数据为空');
            }
            
            console.log('作业详情数据:', homeworkDetail);
            
            // 如果作业详情中包含标题和时间信息，则更新
            if (homeworkDetail.title) homeworkData.value.title = homeworkDetail.title;
            if (homeworkDetail.startTime) homeworkData.value.startTime = homeworkDetail.startTime;
            if (homeworkDetail.endTime) homeworkData.value.endTime = homeworkDetail.endTime;
            if (homeworkDetail.description) homeworkData.value.description = homeworkDetail.description;
            if (homeworkDetail.allowLateSubmit !== undefined) homeworkData.value.allowLateSubmit = homeworkDetail.allowLateSubmit;
            if (homeworkDetail.lateEndTime) homeworkData.value.lateEndTime = homeworkDetail.lateEndTime;
            if (homeworkDetail.lateDeduction !== undefined) homeworkData.value.lateDeduction = homeworkDetail.lateDeduction;
            
            // 处理题目数据
            if (homeworkDetail.questions && Array.isArray(homeworkDetail.questions) && homeworkDetail.questions.length > 0) {
                console.log(`获取到 ${homeworkDetail.questions.length} 道题目`);
                // 检查第一个题目的数据类型
                const firstQuestion = homeworkDetail.questions[0];
                console.log('第一个题目的数据类型:', typeof firstQuestion);
                console.log('第一个题目的数据:', firstQuestion);
                if (typeof firstQuestion === 'number' || typeof firstQuestion === 'string') {
                    console.log('题目列表是ID数组，需要获取完整题目信息');
                } else {
                    console.log('题目列表是完整题目对象数组');
                }
                
                getQuestionList(homeworkDetail.questions);
                
                // 默认加载第一个题目
                if (questionList.value.length > 0) {
                    switchQuestion(0);
                }
            } else {
                console.warn('作业中没有题目');
                questionList.value = [];
                Message.warning('该作业没有题目');
            }
        })
        .catch(err => {
            console.error('获取作业详情失败', err);
            console.error('错误详情:', err.response ? err.response.data : err);
            Message.error(err.message || '获取作业详情失败，请刷新页面重试');
            
            // 设置默认值，防止界面出错
            homeworkData.value = {
                id: homeworkId,
                title: '加载失败',
                startTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
                endTime: dayjs().add(1, 'day').format('YYYY-MM-DD HH:mm:ss'),
                description: '无法获取作业详情',
                allowLateSubmit: false,
                lateEndTime: '',
                lateDeduction: 0
            };
        })
        .finally(() => {
            loading.value = false;
        });
};

// 处理题目列表
const getQuestionList = async (qList) => {
    loading.value = true;
    const groupedQuestions = {};
    const questionPromises = [];
    
    // 检查qList中的元素是否只是题目ID
    const isIdOnly = qList.length > 0 && (typeof qList[0] === 'number' || typeof qList[0] === 'string');
    
    if (isIdOnly) {
        console.log('收到的是题目ID列表，需要获取完整题目信息');
        Message.loading({
            content: `正在加载 ${qList.length} 道题目，请稍候...`,
            duration: 0
        });
        
        let loadedCount = 0;
        const totalCount = qList.length;
        
        // 对每个题目ID获取完整信息
        for (const questionId of qList) {
            const promise = questionDetailRequest(questionId)
                .then(res => {
                    if (res && res.data && res.data.data) {
                        loadedCount++;
                        if (loadedCount % 5 === 0 || loadedCount === totalCount) {
                            Message.loading({
                                content: `已加载 ${loadedCount}/${totalCount} 道题目，请稍候...`,
                                duration: 0
                            });
                        }
                        return res.data.data;
                    } else {
                        console.error(`获取题目 ${questionId} 详情失败`);
                        return null;
                    }
                })
                .catch(err => {
                    console.error(`获取题目 ${questionId} 详情出错:`, err);
                    return null;
                });
            questionPromises.push(promise);
        }
        
        try {
            // 等待所有题目信息获取完成
            const fullQuestions = await Promise.all(questionPromises);
            // 过滤掉获取失败的题目
            const validQuestions = fullQuestions.filter(q => q !== null);
            
            Message.clear();
            
            if (validQuestions.length === 0) {
                console.error('所有题目信息获取失败');
                Message.error('获取题目信息失败，请刷新页面重试');
                return;
            }
            
            if (validQuestions.length < totalCount) {
                Message.warning(`共 ${totalCount} 道题目，成功加载 ${validQuestions.length} 道，${totalCount - validQuestions.length} 道加载失败`);
            } else {
                Message.success(`成功加载全部 ${validQuestions.length} 道题目`);
            }
            
            // 使用获取到的完整题目信息
            processQuestions(validQuestions);
        } catch (error) {
            console.error('获取题目信息时发生错误:', error);
            Message.error('加载题目时发生错误，请刷新页面重试');
        }
    } else {
        // 已经是完整题目信息
        processQuestions(qList);
    }
    
    loading.value = false;
};

// 处理题目数据
const processQuestions = (questions) => {
    const groupedQuestions = {};
    
    questions.forEach(question => {
        if (!question) return;
        
        const type = question.type;
        if (!groupedQuestions[type]) {
            groupedQuestions[type] = [];
        }
        
        // 确保每个题目都有options属性
        if (!question.options || !Array.isArray(question.options)) {
            question.options = [];
            console.warn(`题目 ${question.id} 没有选项数据或选项格式不正确，已创建空数组`);
        }
        
        // 确保每个选项都有answer属性
        question.options = question.options.map(option => {
            return { ...option, answer: null };
        });
        
        groupedQuestions[type].push(question);
    });
    
    // 清空现有题目列表，避免重复添加
    questionList.value = [];
    numberGroup.value = {};
    
    Object.keys(groupedQuestions).forEach((key) => {
        questionList.value.push(...groupedQuestions[key]);
        numberGroup.value[key] = groupedQuestions[key].length;
    });
    
    console.log(`成功处理 ${questionList.value.length} 道题目`);
};

// 切换题目
const switchQuestion = (index) => {
    if (isPreview.value || index < 0 || index >= questionList.value.length) {
        return;
    }
    
    currQuestIndex.value = index;
    const question = questionList.value[index];
    
    // 确保题目有选项
    if (!question.options) {
        question.options = [];
    }
    
    // 如果题目选项为空，或者选项数量为0，则获取题目详情
    if (!question.options || question.options.length === 0) {
        console.log(`题目 ${question.id} 选项为空，正在获取题目详情...`);
        
        questionDetailRequest(question.id)
            .then(res => {
                if (res && res.data && res.data.data) {
                    const fullQuestion = res.data.data;
                    
                    // 更新题目选项，但保留已有的答案
                    if (fullQuestion.options && Array.isArray(fullQuestion.options)) {
                        // 保存当前答案
                        const currentAnswers = {};
                        if (question.options && Array.isArray(question.options)) {
                            question.options.forEach(option => {
                                if (option.id && option.answer) {
                                    currentAnswers[option.id] = option.answer;
                                }
                            });
                        }
                        
                        // 更新选项并恢复答案
                        question.options = fullQuestion.options.map(option => {
                            return { 
                                ...option, 
                                answer: currentAnswers[option.id] || null 
                            };
                        });
                        
                        console.log(`题目 ${question.id} 选项加载成功，共 ${question.options.length} 个选项`);
                    } else {
                        console.warn(`题目 ${question.id} 详情中没有选项数据`);
                    }
                    
                    // 更新其他题目属性
                    if (fullQuestion.content) question.content = fullQuestion.content;
                    if (fullQuestion.analysis) question.analysis = fullQuestion.analysis;
                    if (fullQuestion.difficulty) question.difficulty = fullQuestion.difficulty;
                    
                } else {
                    console.error(`获取题目 ${question.id} 详情失败`);
                }
            })
            .catch(err => {
                console.error(`获取题目 ${question.id} 详情出错:`, err);
                Message.error(`加载题目选项失败: ${err.message || '未知错误'}`);
            });
    }
    
    console.log(`切换到题目 ${index + 1}/${questionList.value.length}，ID: ${question.id}，类型: ${question.type}`);
};

// 标记题目
const markQuestion = (number, question) => {
    const qId = question.id;
    let i = 0;
    for (let item of markNumberList.value[0].list) {
        if (item.key == qId) {
            markNumberList.value[0].list.splice(i, 1);
            return;
        }
        i++;
    }
    markNumberList.value[0].list.push({
        key: qId,
        number: number,
        href: `question-${qId}`
    });
};

// 获取题号信息
const getNumberInfo = computed(() => {
    const numberInfo = [];
    const questions = questionList.value;
    let i = 0;
    let number = 0, status, question, name, info, length;
    
    for (const key in numberGroup.value) {
        name = getQuestionType(key).simpleName;
        info = [];
        length = (i + numberGroup.value[key]);
        
        for (; i < length; i++) {
            question = questions[i];
            status = getQuestionAnswerStatus(question);
            if (status == 'end') {
                number++;
            }
            info.push({
                key: question.id,
                number: i + 1,
                href: `question-${question.id}`,
                statusKey: status
            });
        }
        
        numberInfo.push({
            title: name,
            list: info
        });
    }
    
    answerNumber.value = number;
    return numberInfo;
});

// 获取题目作答状态
const getQuestionAnswerStatus = (question) => {
    if (!question) {
        console.warn('getQuestionAnswerStatus: 题目对象为空');
        return 'none';
    }
    
    let status = 'none';
    const type = question.type;
    let answerCount = 0;
    
    // 检查question.options是否存在且不为空
    if (question.options && question.options.length > 0) {
        question.options.forEach(option => {
            if (option.answer) {
                answerCount++;
            }
        });
    }
    
    if (type == 'SIGNAL_CHOICE' || type == 'MULTIPLE_CHOICE' || type == 'JUDGMENTAL') {
        if (answerCount > 0) {
            status = 'end';
        }
    } else {
        const optionsLength = question.options ? question.options.length : 0;
        if (optionsLength > 0 && answerCount == optionsLength) {
            status = 'end';
        } else if (answerCount > 0) {
            status = 'start';
        }
    }
    
    return status;
};

// 题号点击
const numberChange = (info) => {
    if (isPreview.value) {
        const element = document.getElementById(`question-${info.key}`);
        if (element) {
            element.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    } else {
        // 切换到指定题目
        const index = getQuestionIndex(info.key);
        if (index >= 0) {
            switchQuestion(index);
        }
    }
};

// 根据ID获取题目索引
const getQuestionIndex = (id) => {
    const list = questionList.value;
    for (let i = 0; i < list.length; i++) {
        if (list[i].id == id) {
            return i;
        }
    }
    return -1;
};

// 根据ID获取题目
const getQuestionById = (id) => {
    const index = getQuestionIndex(id);
    return index >= 0 ? questionList.value[index] : null;
};

// 选择答案
const choiceCorrect = (id, selects) => {
    const question = getQuestionById(id);
    if (!question) {
        console.error(`选择答案失败: 未找到ID为 ${id} 的题目`);
        return;
    }
    
    // 确保题目有options属性
    if (!question.options) {
        question.options = [];
        console.warn(`题目 ${id} 没有选项数据，已创建空数组`);
        return;
    }
    
    // 创建答案映射
    const answerMap = {};
    const options = question.options;
    
    // 统一按数组处理
    if (!(selects instanceof Array)) {
        selects = [selects];
    }
    
    options.forEach((value, index) => {
        if (selects.includes(index)) {
            value["answer"] = 1;
            answerMap[value.id] = 1;
        } else {
            value["answer"] = null;
        }
    });
    
    question["subInfo"] = null;
    
    // 在这里可以添加答案保存到服务器的逻辑
    // 由于没有直接对应的API，我们可以在提交作业时统一提交所有答案
    
    // 更新答案计数
    setTimeout(() => {
        question["subInfo"] = countAnswers(question);
    }, 300);
};

// 提交答案
const submitAnswer = (id) => {
    const question = getQuestionById(id);
    if (!question) {
        console.error(`提交答案失败: 未找到ID为 ${id} 的题目`);
        return;
    }
    
    // 确保题目有options属性
    if (!question.options || question.options.length === 0) {
        console.warn(`题目 ${id} 没有选项数据，无法提交答案`);
        return;
    }
    
    // 创建答案映射
    const answerMap = {};
    const options = question.options;
    let flag = false;
    
    options.forEach((value) => {
        if (value.answer != null) {
            answerMap[value.id] = value.answer;
            flag = true;
        }
    });
    
    // 没有答案不提交
    if (!flag) {
        return;
    }
    
    question["subInfo"] = null;
    
    // 在这里可以添加答案保存到服务器的逻辑
    // 由于没有直接对应的API，我们可以在提交作业时统一提交所有答案
    
    // 更新答案计数
    setTimeout(() => {
        question["subInfo"] = countAnswers(question);
    }, 300);
};

// 计算已答题数
const countAnswers = (question) => {
    if (!question || !question.options) {
        return 0;
    }
    
    let count = 0;
    question.options.forEach(option => {
        if (option.answer) {
            count++;
        }
    });
    return count;
};

// 显示提交信息
const showSubInfo = (count, total, type) => {
    let subCount = type.subCount;
    if (subCount == 0) {
        subCount = total;
    } else if (subCount == -1) {
        subCount = count;
    }
    return count == subCount ? "已提交" : `${count}/${subCount}题`;
};

// 检查提交
const checkSubmit = () => {
    if (answerNumber.value != questionList.value.length) {
        const number = questionList.value.length - answerNumber.value;
        Modal.info({
            title: `提交提示`,
            content: `你还有${number}题没有完成，确定提交吗？`,
            onOk: () => {
                submitHomework();
            }
        });
    } else {
        Modal.success({
            title: `确认提交`,
            content: `确认提交作业吗？提交后将不能修改答案`,
            onOk: () => {
                submitHomework();
            }
        });
    }
};

// 提交作业
const submitHomework = () => {
    loading.value = true;
    
    try {
        // 收集答案
        const answersArray = [];
        questionList.value.forEach(question => {
            if (question.options && Array.isArray(question.options)) {
                question.options.forEach(option => {
                    if (option && option.answer) {
                        answersArray.push({
                            questionId: question.id,
                            optionId: option.id,
                            answer: option.answer.toString(),
                            resultType: "NONE"
                        });
                    }
                });
            }
        });
        
        if (answersArray.length === 0) {
            Message.warning('未检测到任何答案，请至少回答一道题目');
            loading.value = false;
            return;
        }
        
        console.log('提交作业数据:', answersArray);
        submitHomeworkRequest(homeworkId, answersArray)
            .then(res => {
                console.log('提交作业API返回数据:', res);
                
                // 检查API返回结果
                if (!res || !res.data) {
                    throw new Error('API返回数据格式错误');
                }
                
                const responseData = res.data;
                
                // 检查业务状态码
                if (responseData.code === '00001' || responseData.code === 200) {
                    Message.success("作业提交成功！");
                    // 获取当前课程ID
                    const courseId = route.query.courseId || route.params.courseId;
                    if (courseId) {
                        // 跳转到课程内的作业中心
                        router.push(`/study/course/${courseId}/homework`);
                    } else {
                        // 如果没有课程ID，则回退到上一页
                        router.back();
                    }
                } else {
                    throw new Error(responseData.msg || '提交作业失败');
                }
            })
            .catch(err => {
                console.error('提交作业失败', err);
                console.error('错误详情:', err.response ? err.response.data : err);
                Message.error(err.message || '提交作业失败，请稍后重试');
            })
            .finally(() => {
                loading.value = false;
            });
    } catch (error) {
        console.error('提交作业过程中发生错误:', error);
        Message.error('提交作业失败，请稍后重试');
        loading.value = false;
    }
};

onMounted(() => {
    getHomeworkDetail();
});
</script>

<style lang="less" scoped>
.homework-header {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
    position: relative;
    border-bottom: 1px solid var(--color-border-2);
    
    .homework-title {
        font-size: 18px;
        font-weight: bold;
        margin: 0;
    }
    
    .homework-submit {
        position: absolute;
        right: 20px;
    }
    
    .back-preview {
        position: absolute;
        left: 20px;
    }
}

.homework-start {
    display: flex;
    height: calc(100vh - 120px);
    
    .homework-info {
        width: 300px;
        padding: 10px;
        overflow-y: auto;
        border-right: 1px solid var(--color-border-2);
        
        .number-desc {
            display: flex;
            justify-content: space-around;
            margin-top: 10px;
        }
        
        .description {
            margin-top: 10px;
            color: var(--color-text-2);
        }
    }
    
    .question-list {
        flex: 1;
        padding: 10px;
        overflow-y: auto;
        
        .question-pre-next {
            margin-bottom: 15px;
        }
        
        .sub-info {
            display: inline-block;
            padding: 2px 8px;
            background-color: rgb(var(--success-1));
            color: rgb(var(--success-6));
            border-radius: 4px;
            margin-left: 10px;
        }
    }
    
    .homework-number {
        width: 250px;
        padding: 10px;
        overflow-y: auto;
        border-left: 1px solid var(--color-border-2);
    }
}

.common-style {
    background-color: var(--color-bg-2);
    border-radius: 4px;
    padding: 15px;
    margin-bottom: 15px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.spin-container {
    width: 100%;
    min-height: 200px;
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.homework-info-box {
    h1 {
        font-size: 20px;
        font-weight: bold;
        margin-bottom: 15px;
        text-align: center;
        color: rgb(var(--primary-6));
        padding-bottom: 10px;
        border-bottom: 2px solid var(--color-border-2);
    }

    .homework-time-list {
        display: flex;
        flex-direction: column;
        margin-bottom: 15px;
        background-color: var(--color-fill-2);
        border-radius: 8px;
        padding: 12px 15px;

        .homework-time-item {
            display: flex;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid var(--color-border-2);

            &:last-child {
                border-bottom: none;
            }

            .time-label {
                font-size: 15px;
                color: var(--color-text-2);
                margin-right: 5px;
                min-width: 110px;
            }

            .time-value {
                font-size: 15px;
                font-weight: bold;
                color: var(--color-text-1);
            }
        }
    }

    .description {
        font-size: 14px;
        color: var(--color-text-2);
        background-color: var(--color-fill-2);
        padding: 12px 15px;
        border-radius: 8px;
        margin-top: 10px;
    }
}
</style> 