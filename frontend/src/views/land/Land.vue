<template>
  <div class="land">
    <!-- 主横幅区域 -->
    <section id="home" class="hero-section">
      <div class="hero-container">
        <div class="hero-content">
          <h1 class="hero-title">开启智慧学习新时代</h1>
          <p class="hero-subtitle">专业的在线教育平台，让学习更高效、更便捷、更有趣</p>
          <div class="hero-buttons">
            <button class="btn btn-primary btn-large" @click="handleNavigation('experience')">立即体验</button>
          </div>
        </div>
        <div class="hero-image">
          <img src="/src/assets/img/home-introduce-bg.png" alt="在线教育" class="hero-img">
        </div>
      </div>
    </section>

    <!-- 数据统计 -->
    <section class="stats-section">
      <div class="stats-container">
        <div class="stat-item">
          <div class="stat-number">50,000+</div>
          <div class="stat-label">注册学员</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">500+</div>
          <div class="stat-label">精品课程</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">100+</div>
          <div class="stat-label">专业讲师</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">98%</div>
          <div class="stat-label">学员满意度</div>
        </div>
      </div>
    </section>

    <!-- 课程中心 -->
    <section id="courses" class="courses-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">热门课程</h2>
          <p class="section-subtitle">精选优质课程，助力你的学习成长</p>
        </div>
        <div v-if="coursesLoading" class="courses-loading">
          <div class="loading-spinner"></div>
          <p>正在加载课程数据...</p>
        </div>
        <div v-else-if="courses.length === 0" class="no-courses">
          <p>暂无课程数据，请稍后再试</p>
        </div>
        <div v-else class="courses-grid">
          <div class="course-card" v-for="course in courses" :key="course.id">
            <div class="course-image">
              <img :src="getImageUrl(course.image)" :alt="course.title">
              <div class="course-overlay">
                <button class="btn btn-primary" @click="goToCourseDetail(course.id)">立即学习</button>
              </div>
            </div>
            <div class="course-content">
              <h3 class="course-title">{{ course.title }}</h3>
              <p class="course-description">{{ course.description }}</p>
              <div class="course-meta">
                <span class="course-duration">{{ course.duration }}</span>
                <span v-if="course.rating" class="course-rating">
                  <i class="icon-star"></i>
                  {{ course.rating.toFixed(1) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 特色功能 -->
    <section id="features" class="features-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">平台特色</h2>
          <p class="section-subtitle">专业的在线教育解决方案</p>
        </div>
        <div class="features-grid">
          <div class="feature-card" v-for="feature in features" :key="feature.id">
            <div class="feature-icon">
              <i :class="feature.icon"></i>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 师资团队 -->
    <section id="teachers" class="teachers-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">优秀师资</h2>
          <p class="section-subtitle">汇聚行业精英，传授专业知识</p>
        </div>
        <div class="teachers-grid">
          <div class="teacher-card" v-for="teacher in teachers" :key="teacher.id">
            <div class="teacher-avatar">
              <img :src="teacher.avatar" :alt="teacher.name">
            </div>
            <h3 class="teacher-name">{{ teacher.name }}</h3>
            <p class="teacher-title">{{ teacher.title }}</p>
            <p class="teacher-description">{{ teacher.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 学员评价 -->
    <section class="testimonials-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">学员评价</h2>
          <p class="section-subtitle">听听他们怎么说</p>
        </div>
        <div class="testimonials-grid">
          <div class="testimonial-card" v-for="testimonial in testimonials" :key="testimonial.id">
            <div class="testimonial-content">
              <p class="testimonial-text">{{ testimonial.content }}</p>
            </div>
            <div class="testimonial-author">
              <img :src="testimonial.avatar" :alt="testimonial.name" class="author-avatar">
              <div class="author-info">
                <h4 class="author-name">{{ testimonial.name }}</h4>
                <p class="author-title">{{ testimonial.title }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h3 class="footer-title">智慧学堂</h3>
            <p class="footer-description">专业的在线教育平台，致力于为学员提供优质的学习体验。</p>
          </div>
          <div class="footer-section">
            <h4 class="footer-subtitle">快速链接</h4>
            <ul class="footer-links">
              <li><a href="#home">首页</a></li>
              <li><a href="#courses">课程中心</a></li>
              <li><a href="#features">特色功能</a></li>
              <li><a href="#teachers">师资团队</a></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4 class="footer-subtitle">联系方式</h4>
            <div class="contact-info">
              <p><i class="icon-phone"></i> xxx-xxx-xxxx</p>
              <p><i class="icon-email"></i> xxx@xxx.com</p>
              <p><i class="icon-location"></i> xxxxxxx</p>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 智慧学堂. 保留所有权利.</p>
        </div>
      </div>
    </footer>

    <!-- 回到顶部按钮 -->
    <div class="back-to-top" v-show="showBackToTop" @click="scrollToTop">
      <i class="back-to-top-icon">↑</i>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import useUserStore from '../../sotre/user-store'
import axios from '../../utils/http'
import { getImageUrl } from '../../utils/image' // 导入图片URL处理函数

const router = useRouter()
const userStore = useUserStore()
const showBackToTop = ref(false)

// 处理导航按钮点击
const handleNavigation = (action) => {
  if (action === 'experience') {
    router.push('/home')
  }
}

// 监听滚动事件，控制回到顶部按钮的显示
const handleScroll = () => {
  showBackToTop.value = window.scrollY > 300
}

// 回到顶部
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 挂载时添加滚动监听
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  // 获取热门课程数据
  fetchHotCourses()
})

// 卸载时移除滚动监听
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 课程数据
const courses = ref([])
const coursesLoading = ref(false)

// 获取热门课程数据
const fetchHotCourses = async () => {
  coursesLoading.value = true
  try {
    // 使用评分最高课程的接口
    const response = await axios.get('/uapi/course-score/top/4')
    console.log('获取到的热门课程数据:', response)
    const result = response.data
    
    if (result.code === '00000' && result.data && result.data.length > 0) {
      // 获取课程ID列表
      const courseIds = result.data.map(course => course.courseId)
      const scoreData = result.data
      
      // 创建一个映射，用于快速查找评分数据
      const scoreMap = {}
      scoreData.forEach(course => {
        scoreMap[course.courseId] = course
      })
      
      // 获取课程详细信息
      const courseDetailsPromises = courseIds.map(id => 
        axios.get(`/uapi/courses/getInfo`, {
          params: {
            courseId: id
          }
        }).catch(err => {
          console.error(`获取课程 ${id} 详情失败:`, err)
          return { data: { code: 'error' } }
        })
      )
      
      // 等待所有请求完成
      const courseDetailsResponses = await Promise.all(courseDetailsPromises)
      
      // 处理课程详情数据
      const validCourseDetails = courseDetailsResponses
        .filter(res => res.data && res.data.code === '00000' && res.data.data)
        .map(res => {
          const courseDetail = res.data.data
          const courseId = courseDetail.id
          const scoreInfo = scoreMap[courseId]
          
          // 确保scoreInfo存在
          if (!scoreInfo) {
            console.error(`未找到课程 ${courseId} 的评分信息`)
            return null
          }
          
          return {
            id: courseId,
            title: courseDetail.name || scoreInfo.courseName,
            description: courseDetail.introduce || `课程评分: ${scoreInfo.sumScore.toFixed(1)}/5.0`,
            image: courseDetail.cover || '/src/assets/img/home-introduce-bg.png',
            duration: courseDetail.status === '1' ? '已结课' : '进行中',
            rating: scoreInfo.sumScore
          }
        })
        .filter(course => course !== null) // 过滤掉无效的课程
      
      // 更新课程列表
      courses.value = validCourseDetails
      console.log('处理后的课程数据:', courses.value)
      
      // 如果没有有效的课程详情，使用默认数据
      if (validCourseDetails.length === 0) {
        useDefaultCourses()
      }
    } else {
      console.error('获取热门课程数据失败:', result)
      // 使用默认数据作为备选
      useDefaultCourses()
    }
  } catch (error) {
    console.error('获取热门课程数据出错:', error)
    // 使用默认数据作为备选
    useDefaultCourses()
  } finally {
    coursesLoading.value = false
  }
}

// 默认课程数据
const useDefaultCourses = () => {
  courses.value = [
    {
      id: 1,
      title: '前端开发入门到精通',
      description: '从HTML/CSS基础到Vue.js框架，系统学习前端开发技能',
      image: '/src/assets/img/home-introduce-bg.png',
      duration: '120课时'
    },
    {
      id: 2,
      title: 'Python数据分析实战',
      description: '掌握Python数据分析核心技能，包括pandas、numpy等库的使用',
      image: '/src/assets/img/home-introduce-bg.png',
      duration: '80课时'
    },
    {
      id: 3,
      title: 'Java企业级开发',
      description: '深入学习Java企业级开发，包括Spring Boot、微服务架构',
      image: '/src/assets/img/home-introduce-bg.png',
      duration: '150课时'
    },
    {
      id: 4,
      title: 'UI/UX设计大师课',
      description: '从设计理论到实践，打造专业的UI/UX设计能力',
      image: '/src/assets/img/home-introduce-bg.png',
      duration: '100课时'
    }
  ]
}

// 特色功能数据
const features = ref([
  {
    id: 1,
    icon: 'icon-video',
    title: '高清视频课程',
    description: '1080P高清视频，支持倍速播放，随时随地学习'
  },
  {
    id: 2,
    icon: 'icon-interactive',
    title: '互动式学习',
    description: '实时问答、讨论区、作业提交，增强学习互动性'
  },
  {
    id: 3,
    icon: 'icon-mobile',
    title: '多端同步',
    description: 'PC、手机、平板多端同步，学习进度实时保存'
  },
  {
    id: 4,
    icon: 'icon-certificate',
    title: '权威认证',
    description: '完成课程获得权威证书，提升个人竞争力'
  }
])

// 师资团队数据
const teachers = ref([
  {
    id: 1,
    name: '张教授',
    title: '前端技术专家',
    description: '10年前端开发经验，曾就职于BAT等知名互联网公司',
    avatar: '/src/assets/img/home-introduce-bg.png'
  },
  {
    id: 2,
    name: '李博士',
    title: '数据科学专家',
    description: '清华大学计算机博士，专注于机器学习和数据分析',
    avatar: '/src/assets/img/home-introduce-bg.png'
  },
  {
    id: 3,
    name: '王老师',
    title: 'Java架构师',
    description: '15年Java开发经验，精通微服务架构和云原生技术',
    avatar: '/src/assets/img/home-introduce-bg.png'
  },
  {
    id: 4,
    name: '陈设计师',
    title: 'UI/UX设计总监',
    description: '8年设计经验，曾为多家知名企业提供设计服务',
    avatar: '/src/assets/img/home-introduce-bg.png'
  }
])

// 学员评价数据
const testimonials = ref([
  {
    id: 1,
    content: '通过这个平台学习前端开发，让我成功转行成为一名前端工程师，课程质量很高，老师讲解很详细。',
    name: '小明',
    title: '前端开发工程师',
    avatar: '/src/assets/img/home-introduce-bg.png'
  },
  {
    id: 2,
    content: 'Python数据分析课程非常实用，学完后能够独立完成数据分析项目，强烈推荐！',
    name: '小红',
    title: '数据分析师',
    avatar: '/src/assets/img/home-introduce-bg.png'
  },
  {
    id: 3,
    content: 'Java企业级开发课程内容全面，从基础到高级都有覆盖，对职业发展很有帮助。',
    name: '小李',
    title: 'Java开发工程师',
    avatar: '/src/assets/img/home-introduce-bg.png'
  }
])

// 跳转到课程详情
const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

</script>

<style lang="css" scoped>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.land {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  line-height: 1.6;
  color: #333;
}

/* 课程加载状态 */
.courses-loading {
  text-align: center;
  padding: 40px 0;
}

.loading-spinner {
  display: inline-block;
  width: 50px;
  height: 50px;
  border: 5px solid rgba(102, 126, 234, 0.2);
  border-radius: 50%;
  border-top-color: #667eea;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.no-courses {
  text-align: center;
  padding: 40px 0;
  color: #666;
}

/* 按钮样式 */
.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.btn-outline {
  background: transparent;
  color: #667eea;
  border: 2px solid #667eea;
}

.btn-outline:hover {
  background: #667eea;
  color: white;
}

.btn-large {
  padding: 16px 32px;
  font-size: 18px;
}

/* 主横幅区域 */
.hero-section {
  padding: 120px 0 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  min-height: 100vh;
  display: flex;
  align-items: center;
}

.hero-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
  padding: 0 2rem;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.hero-buttons {
  display: flex;
  justify-content: flex-start;
}

.hero-image {
  text-align: center;
}

.hero-img {
  max-width: 100%;
  height: auto;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

/* 数据统计 */
.stats-section {
  padding: 80px 0;
  background: #f8f9fa;
}

.stats-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  padding: 0 2rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 3rem;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 1.1rem;
  color: #666;
}

/* 通用区块样式 */
.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.section-header {
  text-align: center;
  margin-bottom: 4rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  color: #333;
}

.section-subtitle {
  font-size: 1.2rem;
  color: #666;
}

/* 课程中心 */
.courses-section {
  padding: 100px 0;
  background: white;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.course-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.course-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.course-card:hover .course-image img {
  transform: scale(1.05);
}

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.course-card:hover .course-overlay {
  opacity: 1;
}

.course-content {
  padding: 20px;
}

.course-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
  height: 54px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
  height: 66px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.course-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: #777;
}

.course-rating {
  display: flex;
  align-items: center;
  color: #ff9800;
  font-weight: bold;
}

.icon-star {
  display: inline-block;
  margin-right: 4px;
  width: 16px;
  height: 16px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23ff9800'%3E%3Cpath d='M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
}

/* 特色功能 */
.features-section {
  padding: 100px 0;
  background: #f8f9fa;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  color: white;
}

.feature-title {
  font-size: 1.25rem;
  font-weight: bold;
  margin-bottom: 1rem;
  color: #333;
}

.feature-description {
  color: #666;
  line-height: 1.6;
}

/* 师资团队 */
.teachers-section {
  padding: 100px 0;
  background: white;
}

.teachers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.teacher-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.teacher-card:hover {
  transform: translateY(-5px);
}

.teacher-avatar {
  width: 120px;
  height: 120px;
  margin: 0 auto 1.5rem;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #667eea;
}

.teacher-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.teacher-name {
  font-size: 1.25rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #333;
}

.teacher-title {
  color: #667eea;
  font-weight: 500;
  margin-bottom: 1rem;
}

.teacher-description {
  color: #666;
  line-height: 1.6;
}

/* 学员评价 */
.testimonials-section {
  padding: 100px 0;
  background: #f8f9fa;
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.testimonial-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.testimonial-text {
  font-style: italic;
  color: #666;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.author-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-weight: bold;
  color: #333;
  margin-bottom: 0.25rem;
}

.author-title {
  color: #666;
  font-size: 0.9rem;
}

/* 页脚 */
.footer {
  background: #2c3e50;
  color: white;
  padding: 60px 0 20px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.footer-description {
  color: #bdc3c7;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.footer-subtitle {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.footer-links {
  list-style: none;
}

.footer-links li {
  margin-bottom: 0.5rem;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: #667eea;
}

.contact-info p {
  color: #bdc3c7;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding-top: 2rem;
  text-align: center;
  color: #bdc3c7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-container {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
  
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .stats-container {
    grid-template-columns: 1fr;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .section-title {
    font-size: 1.75rem;
  }
}

/* 图标样式 */
.icon-video::before { content: "📹"; }
.icon-interactive::before { content: "💬"; }
.icon-mobile::before { content: "📱"; }
.icon-certificate::before { content: "🏆"; }
.icon-phone::before { content: "📞"; }
.icon-email::before { content: "✉️"; }
.icon-location::before { content: "📍"; }
.icon-wechat::before,
.icon-qq::before,
.icon-weibo::before {
  content: none;
}

/* 回到顶部按钮 */
.back-to-top {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  z-index: 999;
  transition: all 0.3s ease;
}

.back-to-top:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(102, 126, 234, 0.4);
}

.back-to-top-icon {
  font-size: 24px;
  font-weight: bold;
}
</style>