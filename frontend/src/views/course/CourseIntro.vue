<template>
  <div class="course-intro">
    <!-- 课程基本信息 -->
    <div class="course-header">
      <div class="course-cover">
        <a-image :src="courseInfo.cover || defaultCover" :preview="false" fit="cover" />
      </div>
      <div class="course-info">
        <h1 class="course-title">{{ courseInfo.name }}</h1>
        <div class="course-meta">
          <span class="meta-item"><icon-user /> {{ courseInfo.teacherName || '未知讲师' }}</span>
          <span class="meta-item"><icon-calendar /> {{ formatDate(courseInfo.createdAt || courseInfo.createTime) }}</span>
          <span class="meta-item"><icon-user-group /> {{ courseInfo.studentCount || 0 }}人学习</span>
        </div>
        <div class="course-tags">
          <a-tag v-for="tag in courseTags" :key="tag" color="arcoblue">{{ tag }}</a-tag>
        </div>
        <div class="course-actions">
          <a-button type="primary" @click="startLearning">立即学习</a-button>
        </div>
      </div>
    </div>

    <!-- 课程详情内容 -->
    <div class="course-content">
      <!-- 课程简历 -->
      <div class="content-section course-resume">
        <h2 class="section-title">课程简历</h2>
        <div class="section-content">
          <div v-if="courseInfo.introduce" v-html="courseInfo.introduce"></div>
          <a-empty v-else description="暂无课程简历" />
        </div>
      </div>
      
      <!-- 课程大纲 -->
      <div class="content-section">
        <h2 class="section-title">课程大纲</h2>
        <div class="section-content">
          <a-spin :loading="chaptersLoading">
            <a-empty v-if="chapters.length === 0" description="暂无课程章节" />
            <a-collapse accordion v-else>
              <a-collapse-item v-for="(chapter, index) in chapters" :key="chapter.id" :header="chapter.title">
                <div class="chapter-sections">
                  <div v-for="(section, sIndex) in chapter.sections" :key="section.id" class="section-item">
                    <div class="section-info">
                      <icon-play-circle-fill v-if="section.videoUrl" />
                      <icon-file v-else />
                      <span class="section-title">{{ section.title }}</span>
                    </div>
                    <div class="section-meta">
                      <span class="section-duration">{{ formatDuration(section.duration) }}</span>
                    </div>
                  </div>
                </div>
              </a-collapse-item>
            </a-collapse>
          </a-spin>
        </div>
      </div>
      
      <!-- 学员评价 -->
      <div class="content-section">
        <h2 class="section-title">学员评价</h2>
        <div class="section-content">
          <div class="reviews-summary">
            <div class="rating-overall">
              <span class="rating-number">{{ courseScores?.sumScore?.toFixed(1) || '暂无' }}</span>
              <div class="rating-stars">
                <a-rate :model-value="courseScores?.sumScore || 0" allow-half readonly />
              </div>
              <span class="rating-count">{{ courseScores?.scoreCount || 0 }}条评价</span>
            </div>
          </div>
          
          <a-spin :loading="reviewsLoading">
            <div class="reviews-list" v-if="reviews.length > 0">
              <div v-for="(review, index) in reviews" :key="index" class="review-item">
                <div class="review-header">
                  <a-avatar :size="40" :image-url="review.picture"></a-avatar>
                  <div class="review-user">
                    <div class="user-name">{{ review.nickname || '匿名用户' }}</div>
                    <div class="review-meta">
                      <a-rate :model-value="review.courseScore?.sumScore || 0" allow-half readonly size="small" />
                      <span class="review-date">{{ formatDate(review.courseScore?.createdAt) }}</span>
                    </div>
                  </div>
                </div>
                <div class="review-content">
                  {{ review.courseScore?.detail || '该用户未留下评价内容' }}
                </div>
              </div>
              
              <div class="pagination-container" v-if="reviewsPagination.total > reviewsPagination.pageSize">
                <a-pagination
                  v-model:current="reviewsPagination.current"
                  :total="reviewsPagination.total"
                  :page-size="reviewsPagination.pageSize"
                  @change="handleReviewPageChange"
                />
              </div>
            </div>
            <a-empty v-else description="暂无评价" />
          </a-spin>
          
          <!-- 评价课程按钮 -->
          <div class="rate-course-action" v-if="isLogin()">
            <a-button type="primary" @click="showRateCourseModal">评价课程</a-button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 评价课程模态框 -->
  <a-modal
    v-model:visible="rateModalVisible"
    :title="`评价课程: ${courseInfo.name}`"
    :footer="null"
    :loading="rateFormLoading"
    @cancel="rateModalVisible = false"
    :modal-class="'rate-course-modal'"
  >
    <a-form :model="rateForm" layout="vertical">
      <a-form-item label="总体评分" :rules="[{ required: true, message: '请选择总体评分' }]">
        <a-rate v-model="rateForm.sumScore" allow-half />
      </a-form-item>
      <a-form-item label="评价详情" :rules="[{ required: true, message: '请填写评价详情' }]">
        <a-textarea
          v-model="rateForm.detail"
          placeholder="请输入您对课程的评价和建议..."
          :rows="4"
        />
      </a-form-item>
      <div class="modal-footer">
        <a-button @click="rateModalVisible = false">取消</a-button>
        <a-button type="primary" :loading="rateFormLoading" @click="submitRateCourse">提交评价</a-button>
      </div>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message, Modal, Input } from '@arco-design/web-vue';
import { getCourseInfoRequest, getCourseChaptersRequest, checkUserInClassRequest, stuAddCourseRequest, getCourseScore, rateCourse, getCourseReviewsRequest, joinCourseDirectRequest, getCourseStudentCountRequest } from '../../apis/course-api';
import useCourseStore from '../../sotre/course-store';
import { isLogin } from '../../utils/auth-service';
import authService from '../../utils/auth-service';
import { 
  IconUser, 
  IconCalendar, 
  IconUserGroup, 
  IconPlayCircleFill, 
  IconFile, 
  IconCheck
} from '@arco-design/web-vue/es/icon';
import { h } from 'vue';

const route = useRoute();
const router = useRouter();
const courseStore = useCourseStore();

const courseId = computed(() => route.params.courseId);
const courseInfo = ref({});
const defaultCover = '/src/assets/img/home-introduce-bg.png';
const loading = ref(false);
const chaptersLoading = ref(false);
const chapters = ref([]);
const courseScores = ref(null);
const reviews = ref([]);
const reviewsLoading = ref(false);
const reviewsPagination = reactive({
  current: 1,
  pageSize: 5,
  total: 0
});
const studentCount = ref(0);

// 模拟数据 - 实际项目中应该从API获取
const courseTags = ref(['前端开发', 'Vue', 'JavaScript']);

// 获取课程信息
const fetchCourseInfo = async () => {
  loading.value = true;
  try {
    // 优先使用store中的数据
    if (courseStore.courseInfo && courseStore.courseInfo.id == courseId.value) {
      courseInfo.value = courseStore.courseInfo;
    } else {
      // 否则从API获取
      const response = await getCourseInfoRequest(courseId.value);
      if (response.data.code === 200) {
        courseInfo.value = response.data.data;
        // 更新store
        courseStore.courseInfo = response.data.data;
        console.log('获取到的课程信息:', courseInfo.value);
      }
    }
    
    // 获取课程评分
    fetchCourseScore();
  } catch (error) {
    console.error('获取课程信息失败:', error);
    Message.error('获取课程信息失败');
  } finally {
    loading.value = false;
  }
};

// 获取课程评分
const fetchCourseScore = async () => {
  try {
    const response = await getCourseScore(courseId.value);
    if (response.data && response.data.code === '00000' && response.data.data) {
      courseScores.value = response.data.data;
      console.log('获取到的课程评分:', courseScores.value);
      
      // 获取课程评价列表
      fetchCourseReviews();
    }
  } catch (error) {
    console.error('获取课程评分失败:', error);
  }
};

// 获取课程评价列表
const fetchCourseReviews = async () => {
  reviewsLoading.value = true;
  try {
    const response = await getCourseReviewsRequest(
      courseId.value, 
      reviewsPagination.current, 
      reviewsPagination.pageSize
    );
    
    if (response.data && response.data.code === '00000' && response.data.data) {
      // 处理嵌套的数据结构
      reviews.value = response.data.data.records || [];
      reviewsPagination.total = response.data.data.total || 0;
      console.log('获取到的课程评价:', reviews.value);
    } else {
      reviews.value = [];
      console.warn('获取课程评价列表失败:', response);
    }
  } catch (error) {
    console.error('获取课程评价列表出错:', error);
    reviews.value = [];
  } finally {
    reviewsLoading.value = false;
  }
};

// 获取课程章节和视频信息
const fetchCourseChapters = async () => {
  chaptersLoading.value = true;
  try {
    const response = await getCourseChaptersRequest(courseId.value);
    if (response.data && response.data.code === '00000' && response.data.data) {
      chapters.value = response.data.data;
      console.log('获取到的课程章节数据:', chapters.value);
    } else {
      chapters.value = [];
      console.error('获取课程章节失败:', response);
    }
  } catch (error) {
    console.error('获取课程章节出错:', error);
    Message.error('获取课程章节失败');
    chapters.value = [];
  } finally {
    chaptersLoading.value = false;
  }
};

// 获取课程总人数
const fetchCourseStudentCount = async () => {
  try {
    const response = await getCourseStudentCountRequest(courseId.value);
    if (response.data && response.data.code === '00000') {
      studentCount.value = response.data.data || 0;
      courseInfo.value.studentCount = studentCount.value;
    }
  } catch (error) {
    console.error('获取课程总人数失败:', error);
  }
};

// 评价页面变化
const handleReviewPageChange = (page) => {
  reviewsPagination.current = page;
  fetchCourseReviews();
};

// 格式化视频时长
const formatDuration = (seconds) => {
  if (!seconds || isNaN(seconds)) return '未知时长';
  
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  
  if (minutes < 60) {
    return `${minutes}:${String(remainingSeconds).padStart(2, '0')}`;
  } else {
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;
    return `${hours}:${String(remainingMinutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
  }
};

// 开始学习
const startLearning = async () => {
  // 检查用户是否已登录
  if (!isLogin()) {
    // 未登录，显示登录弹窗
    authService.showLoginModal(`/study/course/${courseId.value}`)
      .catch(() => {
        // 用户取消登录
        Message.info('请先登录后再学习课程');
      });
  } else {
    // 已登录，直接加入课程
    try {
      Message.loading({ content: '正在加入课程...', duration: 0 });
      const response = await joinCourseDirectRequest(courseId.value);
      Message.clear();
      
      if (response.data.code === 200 || response.data.code === '00001') {
        Message.success('成功加入课程');
        // 加入成功后跳转到学习页面
        router.push(`/study/course/${courseId.value}`);
      } else {
        Message.error(response.data.msg || '加入课程失败，请稍后重试');
      }
    } catch (error) {
      Message.clear();
      console.error('加入课程出错:', error);
      Message.error('加入课程失败，请稍后重试');
    }
  }
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知日期';
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return '未知日期';
    
    // 计算时间差
    const now = new Date();
    const diffMs = now - date;
    const diffSeconds = Math.floor(diffMs / 1000);
    const diffMinutes = Math.floor(diffSeconds / 60);
    const diffHours = Math.floor(diffMinutes / 60);
    const diffDays = Math.floor(diffHours / 24);
    
    // 根据时间差显示不同格式
    if (diffDays === 0) {
      if (diffHours === 0) {
        if (diffMinutes === 0) {
          return '刚刚';
        }
        return `${diffMinutes}分钟前`;
      }
      return `${diffHours}小时前`;
    } else if (diffDays < 7) {
      return `${diffDays}天前`;
    } else if (date.getFullYear() === now.getFullYear()) {
      // 同一年内显示月日
      return `${date.getMonth() + 1}月${date.getDate()}日`;
    } else {
      // 不同年显示年月日
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    }
  } catch (error) {
    console.error('日期格式化错误:', error);
    return '未知日期';
  }
};

// 显示评价课程的模态框
const rateModalVisible = ref(false);
const rateFormLoading = ref(false);
const rateForm = reactive({
  sumScore: 5,
  detail: ''
});

const showRateCourseModal = () => {
  // 检查用户是否已登录
  if (!isLogin()) {
    authService.showLoginModal()
      .catch(() => {
        Message.info('请先登录后再评价课程');
      });
    return;
  }
  
  // 重置表单
  rateForm.sumScore = 5;
  rateForm.detail = '';
  
  rateModalVisible.value = true;
};

// 提交课程评价
const submitRateCourse = async () => {
  if (!courseId.value) {
    Message.error('课程ID无效');
    return;
  }
  
  rateFormLoading.value = true;
  try {
    const ratingData = {
      ...rateForm,
      // 为了兼容后端API，设置所有评分项为相同值
      videoScore: rateForm.sumScore,
      examScore: rateForm.sumScore,
      experimentScore: rateForm.sumScore,
      projectScore: rateForm.sumScore,
      homeworkScore: rateForm.sumScore,
      courseId: Number(courseId.value)
    };
    
    const response = await rateCourse(ratingData);
    if (response.data && (response.data.code === '00001' || response.data.code === 200)) {
      Message.success('评价提交成功');
      rateModalVisible.value = false;
      
      // 刷新评分和评价列表
      fetchCourseScore();
    } else {
      Message.error(response.data?.msg || '评价提交失败');
    }
  } catch (error) {
    console.error('提交课程评价出错:', error);
    Message.error('评价提交失败，请稍后重试');
  } finally {
    rateFormLoading.value = false;
  }
};

onMounted(() => {
  fetchCourseInfo();
  fetchCourseStudentCount();
  fetchCourseChapters();
});
</script>

<style lang="scss" scoped>
.course-intro {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  padding-top: 92px; /* 添加顶部padding，72px导航栏高度 + 20px原有padding */
  
  .course-header {
    display: flex;
    margin-bottom: 30px;
    gap: 30px;
    
    .course-cover {
      width: 300px;
      height: 200px;
      overflow: hidden;
      border-radius: 8px;
      
      :deep(.arco-image) {
        width: 100%;
        height: 100%;
      }
    }
    
    .course-info {
      flex: 1;
      
      .course-title {
        font-size: 28px;
        margin: 0 0 15px;
        color: var(--color-text-1);
      }
      
      .course-meta {
        display: flex;
        margin-bottom: 15px;
        
        .meta-item {
          display: flex;
          align-items: center;
          margin-right: 20px;
          color: var(--color-text-3);
          
          .arco-icon {
            margin-right: 5px;
          }
        }
      }
      
      .course-tags {
        margin-bottom: 20px;
        
        .arco-tag {
          margin-right: 8px;
        }
      }
      
      .course-actions {
        display: flex;
        gap: 15px;
      }
    }
  }
  
  .course-content {
    background-color: var(--color-bg-2);
    border-radius: 8px;
    padding: 20px;
    
    .content-section {
      margin-bottom: 40px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .section-title {
        font-size: 20px;
        font-weight: 600;
        margin-bottom: 20px;
        padding-bottom: 10px;
        border-bottom: 1px solid var(--color-border-2);
        color: var(--color-text-1);
        position: relative;
        
        &::after {
          content: '';
          position: absolute;
          bottom: -1px;
          left: 0;
          width: 80px;
          height: 3px;
          background-color: var(--color-primary-6);
          border-radius: 3px;
        }
      }
      
      .section-content {
        color: var(--color-text-2);
        line-height: 1.6;
        
        p {
          margin-bottom: 16px;
        }
        
        /* 课程简历的样式 */
        :deep(img) {
          max-width: 100%;
          height: auto;
          margin: 10px 0;
        }
        
        :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
          margin: 16px 0 8px;
          font-weight: 600;
          color: var(--color-text-1);
        }
        
        :deep(ul), :deep(ol) {
          padding-left: 20px;
          margin: 10px 0;
        }
        
        :deep(table) {
          border-collapse: collapse;
          width: 100%;
          margin: 15px 0;
          
          th, td {
            border: 1px solid var(--color-border-2);
            padding: 8px 12px;
          }
          
          th {
            background-color: var(--color-fill-2);
          }
        }
        
        :deep(pre) {
          background-color: var(--color-fill-1);
          padding: 12px;
          border-radius: 4px;
          overflow-x: auto;
          margin: 10px 0;
        }
        
        :deep(code) {
          font-family: monospace;
        }
      }
    }
    
    .chapter-sections {
      padding: 10px 0;
      
      .section-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 10px;
        border-bottom: 1px solid var(--color-border-2);
        
        &:last-child {
          border-bottom: none;
        }
        
        .section-info {
          display: flex;
          align-items: center;
          
          .arco-icon {
            margin-right: 10px;
            font-size: 18px;
          }
          
          .section-title {
            font-size: 14px;
          }
        }
        
        .section-meta {
          display: flex;
          align-items: center;
          
          .section-duration {
            color: var(--color-text-3);
            margin-right: 15px;
            font-size: 13px;
          }
        }
      }
    }
    
    .reviews-summary {
      display: flex;
      margin-bottom: 30px;
      background-color: var(--color-fill-1);
      border-radius: 12px;
      padding: 20px;
      
      .rating-overall {
        display: flex;
        flex-direction: column;
        align-items: center;
        width: 100%;
        
        .rating-number {
          font-size: 48px;
          font-weight: bold;
          color: var(--color-primary-6);
          line-height: 1;
          margin-bottom: 15px;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        
        .rating-stars {
          margin-bottom: 15px;
        }
        
        .rating-count {
          color: var(--color-text-3);
          font-size: 14px;
        }
      }
    }
    
    .reviews-list {
      .review-item {
        margin-bottom: 25px;
        padding-bottom: 25px;
        border-bottom: 1px solid var(--color-border-2);
        transition: all 0.3s ease;
        
        &:hover {
          background-color: var(--color-fill-1);
          border-radius: 8px;
          padding: 15px;
          margin-left: -15px;
          margin-right: -15px;
        }
        
        &:last-child {
          border-bottom: none;
        }
        
        .review-header {
          display: flex;
          margin-bottom: 15px;
          
          .arco-avatar {
            margin-right: 15px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }
          
          .review-user {
            .user-name {
              font-weight: 500;
              margin-bottom: 5px;
              color: var(--color-text-1);
            }
            
            .review-meta {
              display: flex;
              align-items: center;
              
              .review-date {
                margin-left: 10px;
                color: var(--color-text-3);
                font-size: 13px;
                position: relative;
                padding-left: 8px;
                
                &::before {
                  content: '•';
                  position: absolute;
                  left: 0;
                  top: 0;
                  color: var(--color-text-3);
                }
              }
            }
          }
        }
        
        .review-content {
          color: var(--color-text-2);
          line-height: 1.6;
          margin-bottom: 0;
          padding: 0 5px;
          white-space: pre-line;
        }
      }
    }
    
    .pagination-container {
      display: flex;
      justify-content: center;
      margin-top: 20px;
      
      :deep(.arco-pagination) {
        .arco-pagination-item {
          border-radius: 4px;
          transition: all 0.2s ease;
          
          &:hover {
            color: var(--color-primary-6);
            border-color: var(--color-primary-6);
          }
          
          &.arco-pagination-item-active {
            background-color: var(--color-primary-6);
            color: #fff;
            font-weight: 500;
            
            &:hover {
              color: #fff;
            }
          }
        }
        
        .arco-pagination-jumper {
          .arco-input {
            border-radius: 4px;
          }
        }
      }
    }
    
    .rate-course-action {
      display: flex;
      justify-content: center;
      margin-top: 30px;
      
      .arco-btn {
        padding: 8px 24px;
        font-size: 15px;
        border-radius: 20px;
        box-shadow: 0 4px 12px rgba(var(--primary-6), 0.3);
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(var(--primary-6), 0.4);
        }
      }
    }
  }
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
  
  .arco-btn-primary {
    padding: 5px 20px;
    border-radius: 4px;
  }
}

:deep(.rate-course-modal) {
  .arco-modal-header {
    border-bottom: 1px solid var(--color-border-2);
    padding-bottom: 15px;
  }
  
  .arco-form-item-label {
    font-weight: 500;
  }
  
  .arco-textarea {
    border-radius: 4px;
    
    &:focus, &:hover {
      border-color: var(--color-primary-5);
    }
  }
}

@media (max-width: 768px) {
  .course-intro {
    .course-header {
      flex-direction: column;
      
      .course-cover {
        width: 100%;
        height: auto;
        aspect-ratio: 16/9;
      }
    }
  }
}
</style> 