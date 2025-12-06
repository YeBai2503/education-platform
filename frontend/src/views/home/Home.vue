<template>
  <div class="learn-center">
    <!-- 顶部搜索区域 -->
    <div class="search-section">
      <div class="search-container">
        <h2 class="search-title">探索你的学习之旅</h2>
        <p class="search-subtitle">从超过5000+精品课程中找到适合你的学习内容</p>
        <div class="search-box">
          <a-input-search
            v-model="searchKeyword"
            placeholder="搜索课程、讲师或技能..."
            search-button
            :loading="isSearching"
            @search="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #button-icon>
              <icon-search />
            </template>
          </a-input-search>
        </div>
        <div class="search-tags">
          <span class="tag-label">热门搜索：</span>
          <a-tag 
            v-for="tag in hotTags" 
            :key="tag" 
            class="search-tag"
            @click="quickSearch(tag)"
          >{{ tag }}</a-tag>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="container">
        <!-- 课程展示 -->
        <div class="section course-section">
          <div class="section-header">
            <h2 class="section-title">
              <template v-if="showSearchResults">
                "{{ searchKeyword }}" 的搜索结果 ({{ totalCourses }})
              </template>
              <template v-else>
                全部课程
              </template>
            </h2>
          </div>
          
          <!-- 搜索结果为空 -->
          <div v-if="courses.length === 0" class="empty-result">
            <a-empty description="暂无相关课程" />
          </div>
          
          <!-- 课程列表 -->
          <div v-else class="course-grid">
            <div 
              v-for="course in courses" 
              :key="course.id" 
              class="course-card"
              @click="navigateToCourse(course.id)"
            >
              <div class="course-image">
                <img :src="getImageUrl(course.coverImage)" :alt="course.title">
              </div>
              <div class="course-content">
                <h3 class="course-title">{{ course.title }}</h3>
                <p class="course-description">{{ course.description }}</p>
                <div class="course-meta">
                  <div class="course-rating">
                    <icon-star-fill class="star-icon" />
                    <span>{{ course.rating || 5.0 }}</span>
                  </div>
                  <div class="course-stats">
                    <span><icon-calendar /> {{ course.duration || '未知' }}</span>
                  </div>
                </div>
                <div class="course-footer">
                  <div class="course-actions">
                    <a-button type="primary" shape="round" size="small" @click.stop="navigateToCourse(course.id)">立即学习</a-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div v-if="totalCourses > 0" class="pagination-container">
            <a-pagination
              v-model:current="currentPage"
              :total="totalCourses"
              :page-size="pageSize"
              show-total
              show-jumper
              @change="handlePageChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { IconSearch, IconStarFill, IconUser, IconCalendar } from '@arco-design/web-vue/es/icon';
import { Message } from '@arco-design/web-vue';
import { isLogin } from '../../utils/auth-service';
import authService from '../../utils/auth-service';
import { searchCoursesRequest, getCourseScore } from '../../apis/course-api';
import { getImageUrl } from '../../utils/image'; // 导入图片URL处理函数

const router = useRouter();

// 搜索相关
const searchKeyword = ref('');
const hotTags = ref(['Python', '前端开发', 'UI设计', '数据分析', '人工智能']);
const isSearching = ref(false);
const showSearchResults = ref(false);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(8);
const totalCourses = ref(0);

// 课程数据
const courses = ref([]);


// 处理搜索
const handleSearch = async () => {
  try {
    isSearching.value = true;
    const response = await searchCoursesRequest(
      searchKeyword.value,
      currentPage.value,
      pageSize.value
    );
    
    if (response && response.data && response.data.code === '00000' && response.data.data) {
      const data = response.data.data;
      await processCourseData(data);
      showSearchResults.value = true;
    } else {
      courses.value = [];
      totalCourses.value = 0;
      Message.info('未找到相关课程');
    }
  } catch (error) {
    console.error('搜索失败:', error);
    Message.error('搜索失败，请稍后重试');
    courses.value = [];
    totalCourses.value = 0;
  } finally {
    isSearching.value = false;
  }
};

// 快速搜索（点击热门标签）
const quickSearch = (tag) => {
  searchKeyword.value = tag;
  handleSearch();
};

// 处理分页
const handlePageChange = (page) => {
  currentPage.value = page;
  console.log('当前页码:', currentPage.value);
  if (showSearchResults.value) {
    handleSearch();
  } else {
    loadCourses();
  }
};

// 加载课程
const loadCourses = async () => {
  try {
    const response = await searchCoursesRequest(
      '',  // 空关键词，获取所有课程
      currentPage.value,
      pageSize.value
    );
    
    if (response && response.data && response.data.code === '00000' && response.data.data) {
      const data = response.data.data;
      await processCourseData(data);
      showSearchResults.value = false;
    } else {
      courses.value = [];
      totalCourses.value = 0;
    }
  } catch (error) {
    console.error('加载课程失败:', error);
    Message.error('加载课程失败，请稍后重试');
    courses.value = [];
    totalCourses.value = 0;
  }
};

// 处理课程数据
const processCourseData = async (data) => {
  if (data.list && Array.isArray(data.list)) {
    // 并发请求所有课程评分
    const scorePromises = data.list.map(course => getCourseScore(course.id).then(res => res.data?.data?.sumScore || 0).catch(() => 0));
    const scores = await Promise.all(scorePromises);
    courses.value = data.list.map((course, idx) => ({
      id: course.id,
      title: course.name || '未命名课程',
      description: course.introduce || '暂无课程介绍',
      coverImage: course.cover,
      rating: scores[idx],
      duration: course.status === '1' ? '已结课' : '进行中',
      teacher: course.teacher ? course.teacher.nickname : '未知讲师'
    }));
    totalCourses.value = data.total || 0;
  } else {
    courses.value = [];
    totalCourses.value = 0;
  }
};

// 课程详情导航
const navigateToCourse = (courseId) => {
  // 无论是否登录，都跳转到课程详情页
  router.push(`/course/${courseId}`);
};

// 页面加载时获取数据
onMounted(() => {
  loadCourses();
});
</script>

<style lang="scss" scoped>
.learn-center {
  min-height: 100vh;
  background-color: var(--color-bg-1);
}

// 搜索区域样式
.search-section {
  padding: 60px 0;
  padding-top: 132px;
  background-image: linear-gradient(135deg, var(--color-primary-light-4), var(--color-primary-light-2));
  text-align: center;
  
  .search-container {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;
  }
  
  .search-title {
    font-size: 36px;
    font-weight: 700;
    margin-bottom: 16px;
    color: var(--color-text-1);
  }
  
  .search-subtitle {
    font-size: 18px;
    color: var(--color-text-2);
    margin-bottom: 30px;
  }
  
  .search-box {
    max-width: 600px;
    margin: 0 auto 20px;
  }
  
  .search-tags {
    margin-top: 20px;
    
    .tag-label {
      color: var(--color-text-2);
      margin-right: 10px;
    }
    
    .search-tag {
      margin: 0 5px;
      cursor: pointer;
      transition: all 0.2s;
      
      &:hover {
        background-color: var(--color-primary-light-1);
        color: var(--color-primary-6);
      }
    }
  }
}

// 主要内容区域样式
.main-content {
  padding: 40px 0 60px;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
  
  .section {
    margin-bottom: 50px;
    
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      
      .section-title {
        font-size: 24px;
        font-weight: 600;
        color: var(--color-text-1);
      }
    }
  }
}

// 课程卡片网格
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

// 课程卡片样式
.course-card {
  background-color: var(--color-bg-2);
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }
  
  .course-image {
    height: 160px;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s;
    }
    
    &:hover img {
      transform: scale(1.05);
    }
  }
  
  .course-content {
    padding: 20px;
    
    .course-title {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 10px;
      color: var(--color-text-1);
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      height: 48px;
    }
    
    .course-description {
      font-size: 14px;
      color: var(--color-text-3);
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      height: 40px;
    }
    
    .course-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      .course-rating {
        display: flex;
        align-items: center;
        
        .star-icon {
          color: #ffb800;
          margin-right: 5px;
        }
        
        .review-count {
          color: var(--color-text-3);
          margin-left: 5px;
        }
      }
      
      .course-stats {
        font-size: 14px;
        color: var(--color-text-3);
        
        span {
          margin-left: 10px;
          display: inline-flex;
          align-items: center;
          
          .icon {
            margin-right: 5px;
          }
        }
      }
    }
    
    .course-footer {
      display: flex;
      justify-content: flex-end;
      align-items: center;
    }
  }
}

// 分页容器
.pagination-container {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

// 空结果样式
.empty-result {
  padding: 60px 0;
  text-align: center;
}
</style> 