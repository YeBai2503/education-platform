<template>
  <div class="course-problem-center">
    <a-page-header title="课程问题中心" subtitle="在这里提问和解答课程相关问题" :show-back="false">
      <template #extra>
        <a-button type="primary" @click="showAddProblemModal">
          <template #icon>
            <icon-plus />
          </template>
          提问
        </a-button>
      </template>
    </a-page-header>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <a-space>
        <a-radio-group type="button" v-model="filterType">
          <a-radio value="all">全部问题</a-radio>
          <a-radio value="my">我的问题</a-radio>
          <a-radio value="answered">已解答</a-radio>
          <a-radio value="unanswered">未解答</a-radio>
        </a-radio-group>

        <a-input-search
          placeholder="搜索问题"
          search-button
          style="width: 300px"
          v-model="searchKeyword"
          @search="handleSearch"
        />
      </a-space>
    </div>

    <!-- 问题列表 -->
    <div class="problem-list" v-if="loading">
      <a-skeleton :animation="true" :loading="loading" v-for="i in 3" :key="i">
        <a-space direction="vertical" style="width: 100%">
          <a-skeleton-line :rows="1" :widths="['80%']" />
          <a-skeleton-line :rows="2" :widths="['100%', '90%']" />
        </a-space>
      </a-skeleton>
    </div>
    <div v-else-if="filteredProblems.length > 0">
      <a-list :bordered="false">
        <a-list-item v-for="problem in filteredProblems" :key="problem.id" class="problem-item">
          <div class="problem-id">#{{ problem.id }}</div>
          <a-list-item-meta>
            <template #title>
              <div class="problem-title" @click="showProblemDetail(problem)">
                <a-tag color="orange" v-if="problem.status === 0">待解决</a-tag>
                <a-tag color="arcoblue" v-else>已解决</a-tag>
                {{ problem.title }}
              </div>
            </template>
            <template #description>
              <div class="problem-meta">
                <span>
                  <icon-user /> {{ problem.username }}
                </span>
                <span>
                  <icon-calendar /> {{ problem.createdAt }}
                </span>
                <span>
                  <icon-eye /> {{ problem.views }}
                </span>
                <span>
                  <icon-message /> {{ problem.answerCount || 0 }}
                </span>
                <span>
                  <icon-star /> {{ problem.favorites || 0 }}
                </span>
                <!-- 当前用户的问题才显示删除按钮 -->
                <span v-if="isCurrentUserQuestion(problem)" class="delete-action">
                  <a-button type="text" status="danger" size="mini" @click.stop="confirmDeleteQuestion(problem)">
                    <icon-delete /> 删除
                  </a-button>
                </span>
              </div>
              <div class="problem-desc">{{ problem.content?.substring(0, 150) }}{{ problem.content?.length > 150 ? '...' : '' }}</div>
            </template>
          </a-list-item-meta>
        </a-list-item>
      </a-list>
      <div class="pagination">
        <a-pagination 
          :total="totalProblems" 
          show-total 
          show-jumper 
          v-model:current="currentPage"
          @change="handlePageChange"
        />
      </div>
    </div>
    <a-empty v-else description="暂无问题" />

    <!-- 问题详情页面 -->
    <div v-if="showingProblemDetail" class="problem-detail-page">
      <a-page-header 
        :title="currentProblem.title" 
        @back="backToList"
      >
        <template #extra>
          <a-space>
            <a-tag color="orange" v-if="currentProblem.status === 0">待解决</a-tag>
            <a-tag color="arcoblue" v-else>已解决</a-tag>
          </a-space>
        </template>
      </a-page-header>

      <div class="problem-detail-container">
        <div class="problem-detail">
          <div class="problem-info">
            <div class="problem-author">
              <a-avatar>
                <img v-if="currentProblem.userAvatar" :src="currentProblem.userAvatar" />
                <template v-else>{{ currentProblem.username?.substring(0, 1) }}</template>
              </a-avatar>
              <span>{{ currentProblem.username }}</span>
            </div>
            <div class="problem-time">{{ currentProblem.createdAt }}</div>
          </div>
          <div class="problem-content">
            {{ currentProblem.content }}
          </div>
          
          <a-divider>回答 ({{ questionAnswers.length || 0 }})</a-divider>
          
          <div class="answers-list" v-if="loadingAnswers">
            <a-skeleton :animation="true" :loading="loadingAnswers" v-for="i in 3" :key="i">
              <a-space direction="vertical" style="width: 100%">
                <a-skeleton-line :rows="1" :widths="['30%']" />
                <a-skeleton-line :rows="2" :widths="['100%', '90%']" />
              </a-space>
            </a-skeleton>
          </div>
          <div v-else-if="questionAnswers.length > 0" class="answers-list">
            <!-- 已解决问题：显示所有回答，被采纳的回答显示为正确答案 -->
            <template v-if="currentProblem.status === 1">
              <div v-for="answer in filteredAnswers" :key="answer.id" class="answer-item" :class="{'accepted-answer': answer.isAccepted === 1}">
                <div class="answer-id">#{{ answer.id }}</div>
                <div class="answer-author">
                  <a-avatar>
                    <img v-if="answer.userAvatar" :src="answer.userAvatar" />
                    <template v-else>{{ answer.username?.substring(0, 1) }}</template>
                  </a-avatar>
                  <span>{{ answer.username }}</span>
                  <a-tag color="green" v-if="answer.isAccepted === 1">正确答案</a-tag>
                </div>
                <div class="answer-content">{{ answer.content }}</div>
                <div class="answer-meta">
                  <span>{{ answer.createdAt }}</span>
                  <span v-if="answer.commentCount > 0">
                    <icon-message /> {{ answer.commentCount }} 条评论
                  </span>
                </div>
                <div class="answer-actions">
                  <a-space>
                    <!-- 删除点赞按钮 -->
                  </a-space>
                </div>
              </div>
            </template>
            <!-- 未解决问题：显示所有回答 -->
            <template v-else>
              <div v-for="answer in questionAnswers" :key="answer.id" class="answer-item">
                <div class="answer-id">#{{ answer.id }}</div>
                <div class="answer-author">
                  <a-avatar>
                    <img v-if="answer.userAvatar" :src="answer.userAvatar" />
                    <template v-else>{{ answer.username?.substring(0, 1) }}</template>
                  </a-avatar>
                  <span>{{ answer.username }}</span>
                </div>
                <div class="answer-content">{{ answer.content }}</div>
                <div class="answer-meta">
                  <span>{{ answer.createdAt }}</span>
                  <span v-if="answer.commentCount > 0">
                    <icon-message /> {{ answer.commentCount }} 条评论
                  </span>
                </div>
                <div class="answer-actions">
                  <a-space>
                    <!-- 删除点赞按钮 -->
                    <a-button 
                      type="text" 
                      size="small" 
                      v-if="currentProblem.userId === userInfo.userId && currentProblem.status === 0 && answer.isAccepted !== 1"
                      @click="markAsBest(answer)"
                    >
                      采纳回答
                    </a-button>
                  </a-space>
                </div>
              </div>
            </template>
          </div>
          <a-empty v-else description="暂无回答" />
          
          <a-divider>我的回答</a-divider>
          
          <div class="add-answer">
            <a-textarea 
              v-model="newAnswer" 
              placeholder="写下您的回答，分享您的知识和经验..." 
              :auto-size="{ minRows: 4, maxRows: 8 }" 
              :max-length="2000"
              show-word-limit
              allow-clear
            />
            <div class="answer-tips">
              <span>提示：详细、有针对性的回答更有帮助</span>
              <span v-if="newAnswer.length < 10" class="answer-warning">回答内容过短，请详细描述</span>
            </div>
            <div class="answer-actions">
              <a-button type="primary" @click="submitAnswer" :disabled="newAnswer.trim().length < 10">
                <template #icon><icon-send /></template>
                提交回答
              </a-button>
            </div>
          </div>
          
          <!-- 评论区域 -->
          <QuestionCommentSection
            :related-id="currentProblem.id"
            :comment-list="questionComments"
            :is-loading="loadingComments"
            @refresh="refreshComments"
          />
        </div>
      </div>
    </div>

    <!-- 添加问题弹窗 -->
    <a-modal 
      v-model:visible="addProblemVisible" 
      title="提出问题" 
      @ok="handleAddProblem" 
      ok-text="提交问题"
      cancel-text="取消"
      :mask-closable="false"
      :unmount-on-close="false"
    >
      <a-form :model="newProblem" layout="vertical">
        <a-form-item field="title" label="问题标题" validate-trigger="blur" required>
          <a-input 
            v-model="newProblem.title" 
            placeholder="请输入问题标题" 
            :max-length="50"
            show-word-limit
            allow-clear
          />
        </a-form-item>
        <a-form-item field="content" label="问题描述" validate-trigger="blur" required>
          <a-textarea 
            v-model="newProblem.content" 
            placeholder="请详细描述您的问题，这将有助于其他人更好地理解和回答" 
            :auto-size="{ minRows: 6, maxRows: 10 }" 
            :max-length="2000"
            show-word-limit
            allow-clear
          />
        </a-form-item>
        <p class="form-tips">提示：清晰、具体的问题更容易得到回答</p>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import useUserStore from '../../sotre/user-store';
import useCourseStore from '../../sotre/course-store';
import { useRoute } from 'vue-router';
import { IconThumbUp, IconUser, IconCalendar, IconEye, IconMessage, IconPlus, IconStar, IconSend, IconDelete } from '@arco-design/web-vue/es/icon';
import { getQuestionListByCourseId, createQuestion, getQuestionDetail, submitAnswer as postAnswer, markBestAnswer, getQuestionAnswers, getQuestionComments, deleteQuestion } from '../../apis/problem-api';
import { getUserInfoById } from '../../apis/user-api';
import QuestionCommentSection from '../../components/QuestionCommentSection.vue';

const userStore = useUserStore();
const courseStore = useCourseStore();
const route = useRoute();
const userInfo = computed(() => userStore.userInfo);
// 从主路由中获取courseId
const courseId = route.params.courseId || (courseStore.courseInfo ? courseStore.courseInfo.id : null);

// 状态变量
const loading = ref(false);
const filterType = ref('all');
const searchKeyword = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const totalProblems = ref(0);
const problems = ref([]);

// 添加问题相关
const addProblemVisible = ref(false);
const newProblem = reactive({
  title: '',
  content: '',
  courseId: Number(courseId)
});

  // 问题详情相关
const showingProblemDetail = ref(false);
const currentProblem = ref({});
const questionAnswers = ref([]);
const loadingAnswers = ref(false);
const newAnswer = ref('');
// 问题评论相关
const questionComments = ref([]);
const loadingComments = ref(false);

// 计算属性：根据筛选条件过滤问题列表
const filteredProblems = computed(() => {
  let result = [...problems.value];
  
  // 根据类型筛选
  if (filterType.value === 'my') {
    result = result.filter(p => p.userId === userInfo.value?.userId);
  } else if (filterType.value === 'answered') {
    result = result.filter(p => p.status === 1);
  } else if (filterType.value === 'unanswered') {
    result = result.filter(p => p.status === 0);
  }
  
  // 根据关键字搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(p => 
      p.title?.toLowerCase().includes(keyword) || 
      p.content?.toLowerCase().includes(keyword)
    );
  }
  
  return result;
});

// 计算属性：筛选和排序回答列表
const filteredAnswers = computed(() => {
  // 已解决问题：先显示被采纳的回答，再显示其他回答
  if (currentProblem.value.status === 1) {
    // 将被采纳的回答排在前面
    return [...questionAnswers.value].sort((a, b) => {
      if (a.isAccepted === 1 && b.isAccepted !== 1) return -1;
      if (a.isAccepted !== 1 && b.isAccepted === 1) return 1;
      return 0;
    });
  }
  
  // 未解决问题：按创建时间排序
  return questionAnswers.value;
});

// 检查是否是当前登录用户的问题
const isCurrentUserQuestion = (problem) => {
  return userInfo.value && problem.userId === userInfo.value.userId;
};

// 确认删除问题
const confirmDeleteQuestion = (problem) => {
  Modal.confirm({
    title: '删除问题',
    content: '确定要删除这个问题吗？此操作不可恢复，问题的所有回答和评论也将一并删除。',
    okText: '删除',
    cancelText: '取消',
    okButtonProps: { status: 'danger' },
    onOk: () => handleDeleteQuestion(problem)
  });
};

// 删除问题
const handleDeleteQuestion = (problem) => {
  const loadingMsg = Message.loading({
    content: '正在删除问题...',
    duration: 0
  });
  
  deleteQuestion(problem.id)
    .then(({ data }) => {
      loadingMsg.close();
      
      if (data && data.code === '00000' && data.data === true) {
        Message.success('问题删除成功');
        
        // 从问题列表中移除已删除的问题
        const index = problems.value.findIndex(p => p.id === problem.id);
        if (index !== -1) {
          problems.value.splice(index, 1);
          totalProblems.value = problems.value.length;
        }
        
        // 如果正在查看被删除的问题详情，则返回列表
        if (showingProblemDetail.value && currentProblem.value.id === problem.id) {
          backToList();
        }
      } else {
        Message.error('删除失败: ' + (data?.msg || '未知错误'));
      }
    })
    .catch(error => {
      loadingMsg.close();
      console.error('删除问题失败:', error);
      
      if (error.response) {
        Message.error(`删除失败 (${error.response.status}): ${error.response.statusText}`);
      } else if (error.request) {
        Message.error('网络连接错误，请检查网络');
      } else {
        Message.error('删除失败: ' + error.message);
      }
    });
};

// 方法
const handleSearch = () => {
  currentPage.value = 1;
  fetchQuestions();
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchQuestions();
};

const showAddProblemModal = () => {
  // 检查是否有课程ID
  if (!courseId) {
    Message.error('无法获取课程信息，请刷新页面重试');
    return;
  }
  
  // 初始化新问题数据
  Object.assign(newProblem, {
    title: '',
    content: '',
    courseId: Number(courseId)
  });
  
  addProblemVisible.value = true;
};

const handleAddProblem = () => {
  if (!newProblem.title.trim()) {
    Message.error('请输入问题标题');
    return;
  }
  if (!newProblem.content.trim()) {
    Message.error('请输入问题描述');
    return;
  }
  
  // 确保courseId是数字类型
  newProblem.courseId = Number(courseId);
  
  // 显示提交中状态
  const loadingMsg = Message.loading({
    content: '正在提交问题...',
    duration: 0
  });
  
  createQuestion(newProblem)
    .then(({ data }) => {
      loadingMsg.close();
      if (data && data.code === '00000') {
        Message.success('问题提交成功');
        addProblemVisible.value = false;
        
        // 如果返回了问题数据，将其添加到问题列表
        if (data.data) {
          problems.value.unshift(data.data);
          totalProblems.value = problems.value.length;
        } else {
          // 否则重新获取问题列表
          fetchQuestions();
        }
      } else {
        Message.error('提交失败: ' + (data?.msg || '未知错误'));
      }
    })
    .catch(error => {
      loadingMsg.close();
      console.error('创建问题失败:', error);
      
      if (error.response) {
        Message.error(`提交失败 (${error.response.status}): ${error.response.statusText}`);
      } else if (error.request) {
        Message.error('网络连接错误，请检查网络');
      } else {
        Message.error('提交失败: ' + error.message);
      }
    });
};

const showProblemDetail = (problem) => {
  // 先显示当前问题基本信息
  currentProblem.value = problem;
  showingProblemDetail.value = true;
  newAnswer.value = '';
  
  // 尝试获取问题详情
  getQuestionDetail(problem.id)
    .then(({ data }) => {
      if (data.code === '00000') {
        // 更新问题详情
        currentProblem.value = data.data;
      }
    })
    .catch(error => {
      console.error('获取问题详情失败:', error);
      // 即使详情获取失败，仍然显示问题和回答列表
    })
    .finally(() => {
      // 无论成功或失败，都获取回答列表
      fetchQuestionAnswers(problem.id);
      fetchQuestionComments(problem.id); // 获取评论
    });
};

// 获取问题回答列表
const fetchQuestionAnswers = async (questionId) => {
  loadingAnswers.value = true;
  getQuestionAnswers(questionId)
    .then(async ({ data }) => {
      if (data.code === '00000') {
        questionAnswers.value = data.data || [];
        // 补充回答用户头像和昵称
        for (const answer of questionAnswers.value) {
          if (answer.userId) {
            try {
              const userRes = await getUserInfoById(answer.userId);
              const user = userRes.data;
              answer.userAvatar = user.picture;
              answer.username = user.nickname || user.username || '匿名用户';
            } catch (e) {
              answer.userAvatar = '';
              answer.username = '匿名用户';
            }
          }
        }
      } else {
        questionAnswers.value = [];
        console.warn('获取回答列表返回错误:', data.msg);
      }
    })
    .catch(error => {
      console.error('获取回答列表失败:', error);
      // 设置为空数组，显示"暂无回答"
      questionAnswers.value = [];
      // 不显示错误消息，避免用户体验不佳
    })
    .finally(() => {
      loadingAnswers.value = false;
    });
};

// 获取问题评论列表
const fetchQuestionComments = (questionId) => {
  loadingComments.value = true;
  getQuestionComments(questionId)
    .then(({ data }) => {
      if (data.code === '00000') {
        questionComments.value = data.data || [];
      } else {
        questionComments.value = [];
        console.warn('获取评论列表返回错误:', data.msg);
      }
    })
    .catch(error => {
      console.error('获取评论列表失败:', error);
      // 设置为空数组，显示"暂无评论"
      questionComments.value = [];
    })
    .finally(() => {
      loadingComments.value = false;
    });
};

// 刷新问题评论列表
const refreshComments = () => {
  if (currentProblem.value && currentProblem.value.id) {
    fetchQuestionComments(currentProblem.value.id);
  }
};

const backToList = () => {
  showingProblemDetail.value = false;
  // 重新获取问题列表，以更新可能的变化
  fetchQuestions();
};

const submitAnswer = () => {
  if (!newAnswer.value.trim()) {
    Message.error('请输入回答内容');
    return;
  }
  
  // 检查是否已登录
  if (!userInfo.value || !userInfo.value.userId) {
    Message.warning('请先登录后再回答问题');
    return;
  }
  
  // 准备回答数据
  const answerData = {
    questionId: currentProblem.value.id,
    content: newAnswer.value
  };
  
  // 显示提交中状态
  const loadingMsg = Message.loading({
    content: '正在提交回答...',
    duration: 0
  });
  
  postAnswer(answerData)
    .then(({ data }) => {
      loadingMsg.close();
      
      if (data && data.code === '00000') {
        Message.success('回答提交成功');
        newAnswer.value = '';
        
        // 如果返回了回答数据，直接添加到回答列表
        if (data.data) {
          // 添加到回答列表开头
          questionAnswers.value.unshift(data.data);
        } else {
          // 否则刷新回答列表
          fetchQuestionAnswers(currentProblem.value.id);
        }
        
        // 更新问题的回答数量
        currentProblem.value.answerCount = (currentProblem.value.answerCount || 0) + 1;
        
        // 同时更新问题列表中的回答数量
        const index = problems.value.findIndex(p => p.id === currentProblem.value.id);
        if (index !== -1) {
          problems.value[index].answerCount = (problems.value[index].answerCount || 0) + 1;
        }
      } else {
        Message.error('提交失败: ' + (data?.msg || '未知错误'));
      }
    })
    .catch(error => {
      loadingMsg.close();
      console.error('提交回答失败:', error);
      
      if (error.response) {
        Message.error(`提交失败 (${error.response.status}): ${error.response.statusText}`);
      } else if (error.request) {
        Message.error('网络连接错误，请检查网络');
      } else {
        Message.error('提交失败: ' + error.message);
      }
    });
};

const markAsBest = (answer) => {
  // 调用API采纳回答
  markBestAnswer(answer.id, currentProblem.value.id)
    .then(({ data }) => {
      if (data && data.code === '00000' && data.data === true) {
        Message.success('已采纳该回答');
        
        // 更新问题状态为已解决
        currentProblem.value.status = 1;
        
        // 更新本地回答的采纳状态
        questionAnswers.value = questionAnswers.value.map(item => {
          if (item.id === answer.id) {
            return { ...item, isAccepted: 1 };
          }
          return item;
        });
        
        // 更新问题列表中的状态
        const index = problems.value.findIndex(p => p.id === currentProblem.value.id);
        if (index !== -1) {
          problems.value[index].status = 1;
        }
      } else {
        Message.error('采纳回答失败: ' + (data?.msg || '未知错误'));
      }
    })
    .catch(error => {
      console.error('采纳回答失败:', error);
      if (error.response) {
        Message.error(`采纳失败 (${error.response.status}): ${error.response.statusText}`);
      } else {
        Message.error('采纳失败: 网络错误');
      }
    });
};

// 获取问题列表
const fetchQuestions = async () => {
  loading.value = true;
  console.log('正在获取课程ID为', courseId, '的问题列表');
  
  if (!courseId) {
    console.error('无效的课程ID');
    loading.value = false;
    Message.error('无法获取课程信息');
    return;
  }
  
  getQuestionListByCourseId(courseId)
    .then(async (response) => {
      console.log('问题列表API响应:', response);
      const { data } = response;
      if (data && data.code === '00000') {
        problems.value = data.data || [];
        totalProblems.value = problems.value.length;
        // 补充用户头像和昵称
        for (const problem of problems.value) {
          if (problem.userId) {
            try {
              const userRes = await getUserInfoById(problem.userId);
              const user = userRes.data;
              problem.userAvatar = user.picture;
              problem.username = user.nickname || user.username || '匿名用户';
            } catch (e) {
              problem.userAvatar = '';
              problem.username = '匿名用户';
            }
          }
        }
        console.log('成功获取问题列表:', problems.value);
      } else {
        // 服务器返回错误
        console.warn('获取问题列表响应异常:', data);
        Message.warning('获取问题列表失败: ' + (data?.msg || '未知错误'));
        problems.value = [];
        totalProblems.value = 0;
      }
      loading.value = false;
    })
    .catch(error => {
      console.error('获取问题列表失败:', error);
      
      if (error.response) {
        // 服务器响应错误
        console.log('服务器响应错误状态码:', error.response.status);
        Message.error(`服务器错误 (${error.response.status}): ${error.response.statusText}`);
      } else if (error.request) {
        // 请求已发送但没有收到响应
        console.log('未收到服务器响应');
        Message.error('网络连接错误，请检查网络');
      } else {
        // 设置请求时发生错误
        console.log('请求错误:', error.message);
        Message.error('请求错误: ' + error.message);
      }
      
      // 回退到使用模拟数据（开发阶段）
      console.log('使用模拟数据...');
      problems.value = [
        {
          id: 1,
          title: '课程相关问题测试',
          content: '这是一个测试问题内容...',
          username: '测试用户',
          userAvatar: '',
          createdAt: new Date().toISOString(),
          status: 0,
          answerCount: 2,
          views: 10,
          likes: 5
        }
      ];
      totalProblems.value = problems.value.length;
      
      loading.value = false;
    });
};

// 初始化加载数据
onMounted(() => {
  fetchQuestions();
});
</script>

<style lang="less" scoped>
.course-problem-center {
  .filter-section {
    margin: 20px 0;
    display: flex;
    justify-content: space-between;
  }
  
  .form-tips {
    color: var(--color-text-3);
    font-size: 12px;
    font-style: italic;
    margin-top: -5px;
  }
  
  .problem-list {
    margin-top: 20px;
  }
  
  .problem-item {
    padding: 16px;
    border-radius: 4px;
    margin-bottom: 10px;
    transition: all 0.3s;
    border-bottom: 1px solid var(--color-border-2);
    position: relative;
    
    &:hover {
      background-color: var(--color-fill-2);
    }
    
    .problem-id {
      position: absolute;
      top: 12px;
      right: 15px;
      font-size: 12px;
      color: var(--color-text-3);
      background-color: var(--color-fill-2);
      padding: 2px 8px;
      border-radius: 10px;
      z-index: 1;
    }
    
    .problem-title {
      font-size: 16px;
      font-weight: bold;
      color: var(--color-text-1);
      cursor: pointer;
      
      &:hover {
        color: rgb(var(--primary-6));
      }
    }
    
    .problem-meta {
      display: flex;
      margin-bottom: 8px;
      color: var(--color-text-3);
      font-size: 12px;
      
      span {
        margin-right: 15px;
        display: flex;
        align-items: center;
        
        .arco-icon {
          margin-right: 4px;
        }
      }
      
      .delete-action {
        margin-left: auto;
        margin-right: 0;
        
        .arco-btn {
          padding: 0 4px;
          
          &:hover {
            color: rgb(var(--danger-6));
            background-color: rgba(var(--danger-1), 0.1);
          }
        }
      }
    }
    
    .problem-desc {
      color: var(--color-text-2);
      font-size: 14px;
      line-height: 1.6;
    }
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
  
  .problem-detail-page {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--color-bg-1);
    z-index: 100;
    padding: 20px;
    overflow-y: auto;
    
    .problem-detail-container {
      max-width: 900px;
      margin: 0 auto;
      padding: 20px;
      background-color: var(--color-bg-2);
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }
  }
  
  .problem-detail {
    .problem-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      .problem-author {
        display: flex;
        align-items: center;
        
        .arco-avatar {
          margin-right: 10px;
        }
        
        .arco-tag {
          margin-left: 10px;
        }
      }
      
      .problem-time {
        color: var(--color-text-3);
        font-size: 12px;
      }
    }
    
    .problem-content {
      font-size: 14px;
      line-height: 1.8;
      margin-bottom: 20px;
      white-space: pre-line;
    }
    
          .add-answer {
        margin-top: 20px;
        
        .answer-tips {
          display: flex;
          justify-content: space-between;
          color: var(--color-text-3);
          font-size: 12px;
          margin: 5px 0 10px;
          
          .answer-warning {
            color: rgb(var(--warning-6));
          }
        }
        
        .answer-actions {
          display: flex;
          justify-content: flex-end;
          margin-top: 10px;
        }
      }
      
      .answers-list {
        .answer-item {
          padding: 15px 0;
          border-bottom: 1px solid var(--color-border-2);
          position: relative;
          
          &:last-child {
            border-bottom: none;
          }
        
        .answer-id {
          position: absolute;
          top: 15px;
          right: 15px;
          font-size: 12px;
          color: var(--color-text-3);
          background-color: var(--color-fill-2);
          padding: 2px 8px;
          border-radius: 10px;
        }
        
        .answer-author {
          display: flex;
          align-items: center;
          margin-bottom: 10px;
          
          .arco-avatar {
            margin-right: 10px;
          }
          
          .arco-tag {
            margin-left: 10px;
          }
        }
        
        .answer-content {
          font-size: 14px;
          line-height: 1.8;
          margin-bottom: 10px;
          white-space: pre-line;
        }
        
        .answer-meta {
          color: var(--color-text-3);
          font-size: 12px;
          margin-bottom: 10px;
          display: flex;
          gap: 15px;
        }
        
        &.accepted-answer {
          background-color: rgba(var(--success-1), 0.1);
          padding: 15px;
          border-radius: 8px;
          border-left: 3px solid rgb(var(--success-6));
          margin-bottom: 15px;
        }
      }
    }
  }
}
</style> 