<template>
    <div class="course-video">
      <a-spin :loading="loading" class="loading">
        <span></span>
      </a-spin>
      
      <div v-if="!loading" class="video-container">
        <!-- 视频播放区 -->
        <div class="video-main">
          <div class="video-header">
            <h2 class="video-title">{{ currentVideo.title }}</h2>
            <div class="video-meta">
              <a-tag color="blue">{{ getChapterName(currentVideo.chapterId) }}</a-tag>
              <span class="update-time">更新时间: {{ formatDate(currentVideo.updatedAt) }}</span>
              <a-button v-if="courseStore.isTeacher" type="text" @click="goToVideoManage">
                <template #icon><icon-settings /></template>
                视频管理
              </a-button>
            </div>
          </div>
          
          <!-- 使用视频播放器组件 -->
          <VideoPlayer
            :video-url="currentVideo.videoUrl"
            :poster="currentVideo.coverUrl"
            :initial-time="getInitialTime()"
            @time-update="handleTimeUpdate"
            @ended="handleVideoEnded"
          />
          
          <!-- 视频信息标签页 -->
          <a-tabs>
            <a-tab-pane key="description" title="视频介绍">
              <div class="tab-content" v-html="currentVideo.description || '暂无介绍'"></div>
            </a-tab-pane>
          </a-tabs>
        </div>
        
        <!-- 使用视频列表组件 -->
        <div class="video-list-container">
          <VideoList
            :chapters="chapters"
            :current-video-id="currentVideo.id"
            title="课程视频"
            :show-progress="true"
            @select="selectVideo"
          />
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted, nextTick } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { Message } from '@arco-design/web-vue';
  import { getImageUrl } from '../../utils/image.js';
  import useCourseStore from '../../sotre/course-store';
  import dayjs from 'dayjs';
  import VideoPlayer from '../../components/video/VideoPlayer.vue';
  import VideoList from '../../components/video/VideoList.vue';
  import { 
    getCourseChaptersRequest, 
    getSectionDetailRequest, 
    updateSectionProgressRequest 
  } from '../../apis/video-api';
  
  const route = useRoute();
  const router = useRouter();
  const courseStore = useCourseStore();
  const courseId = route.params.courseId;
  const videoIdFromRoute = route.query.videoId;
  
  // 状态变量
  const loading = ref(true);
  const currentVideo = ref({});
  const chapters = ref([]);
  const lastProgressUpdate = ref(0);
  
  // 初始化数据
  const initData = async () => {
    loading.value = true;
    
    try {
      // 获取课程信息
      await courseStore.getCourseInfo(courseId);
      
      // 获取课程的所有章节和小节
      const response = await getCourseChaptersRequest(courseId);
      const data = response.data.data || response.data;
      
      if (Array.isArray(data)) {
        // 转换API返回的章节和小节数据为组件需要的格式
        chapters.value = data.map(chapter => ({
          id: chapter.id,
          title: chapter.title,
          description: chapter.description,
          videos: chapter.sections.map(section => ({
            id: section.id,
            title: section.title,
            duration: section.duration || 0,
            coverUrl: section.coverUrl || '',
            videoUrl: section.videoUrl || '',
            progress: section.progress || 0,
            completed: section.completed || false,
            chapterId: chapter.id
          }))
        }));
        
        console.log('课程章节数据:', chapters.value);
        
        // 确定要播放的视频
        let targetVideoId = videoIdFromRoute;
        
        // 如果没有指定视频ID或指定的视频不存在，使用第一个视频
        if (!targetVideoId || !findVideoById(targetVideoId)) {
          if (chapters.value.length > 0 && chapters.value[0].videos && chapters.value[0].videos.length > 0) {
            targetVideoId = chapters.value[0].videos[0].id;
          }
        }
        
        if (targetVideoId) {
          await selectVideo(findVideoById(targetVideoId));
        } else {
          loading.value = false;
          Message.warning('没有可播放的视频');
        }
      } else {
        console.error('获取章节数据格式错误:', data);
        Message.error('获取章节数据失败');
        loading.value = false;
      }
    } catch (error) {
      console.error('初始化数据失败', error);
      Message.error('加载数据失败');
      loading.value = false;
    }
  };
  
  // 选择视频进行播放
  const selectVideo = async (video) => {
    if (!video || currentVideo.value.id === video.id) return;
    
    try {
      loading.value = true;
      
      // 获取小节详情
      const response = await getSectionDetailRequest(video.id);
      const sectionData = response.data.data || response.data;
      
      // 更新当前视频
      currentVideo.value = {
        ...video,
        description: sectionData.description || `<p>${video.title}</p>`,
        videoUrl: sectionData.videoUrl || video.videoUrl,
        coverUrl: sectionData.coverUrl || video.coverUrl,
        duration: sectionData.duration || video.duration,
        progress: sectionData.progress || video.progress,
        completed: sectionData.completed || video.completed,
        updatedAt: sectionData.updatedAt || new Date().toISOString()
      };
      
      // 更新URL，不刷新页面
      router.replace({
        path: route.path,
        query: { ...route.query, videoId: video.id }
      });
      
    } catch (error) {
      console.error('获取视频详情失败', error);
      Message.error('获取视频详情失败');
      
      // 即使失败也尝试使用基本信息显示
      currentVideo.value = { ...video };
    } finally {
      loading.value = false;
    }
  };
  
  // 获取视频初始播放位置
  const getInitialTime = () => {
    if (currentVideo.value && currentVideo.value.progress > 0 && !currentVideo.value.completed) {
      return (currentVideo.value.progress / 100) * currentVideo.value.duration;
    }
    return 0;
  };

  // 视频播放进度更新
  const handleTimeUpdate = (data) => {
    const { currentTime, duration, percent } = data;
    // 确保进度值在0-100之间
    const progress = Math.min(Math.floor(percent), 100);
    
    // 每30秒更新一次进度
    const now = Date.now();
    if (now - lastProgressUpdate.value > 30000) {
      updateVideoProgress(currentTime, progress);
      lastProgressUpdate.value = now;
    }
  };

  // 视频播放结束
  const handleVideoEnded = () => {
    // 更新为100%完成
    updateVideoProgress(currentVideo.value.duration, 100, true);
    
    // 寻找下一个视频
    const nextVideo = findNextVideo();
    if (nextVideo) {
      Message.info('即将播放下一个视频');
      setTimeout(() => selectVideo(nextVideo), 1500);
    } else {
      Message.success('恭喜你完成了所有视频学习！');
    }
  };

  // 更新视频进度
  const updateVideoProgress = async (watchTime, progress, completed = false) => {
    try {
      // 调用API更新进度
      await updateSectionProgressRequest(currentVideo.value.id, progress);
      
      // 更新本地数据
      // 找到当前视频并更新进度
      chapters.value.forEach(chapter => {
        chapter.videos.forEach(video => {
          if (video.id === currentVideo.value.id) {
            video.progress = progress;
            video.completed = completed;
          }
        });
      });
      
      // 更新当前视频对象的进度
      currentVideo.value.progress = progress;
      currentVideo.value.completed = completed;
      
      console.log(`视频 ${currentVideo.value.id} 进度更新: ${progress}%, 完成: ${completed}`);
    } catch (error) {
      console.error('更新视频进度失败', error);
      // 不显示错误消息，避免干扰用户体验
    }
  };

  // 辅助函数
  const findVideoById = (videoId) => {
    for (const chapter of chapters.value) {
      for (const video of chapter.videos) {
        if (video.id === videoId) {
          return video;
        }
      }
    }
    return null;
  };

  const findNextVideo = () => {
    let foundCurrent = false;
    
    for (const chapter of chapters.value) {
      for (const video of chapter.videos) {
        if (foundCurrent) {
          return video;
        }
        if (video.id === currentVideo.value.id) {
          foundCurrent = true;
        }
      }
    }
    
    return null;
  };

  const getChapterName = (chapterId) => {
    const chapter = chapters.value.find(c => c.id === chapterId);
    return chapter ? chapter.title : '';
  };

  const formatDate = (dateString) => {
    return dayjs(dateString).format('YYYY-MM-DD HH:mm');
  };

  const formatDuration = (seconds) => {
    if (!seconds) return '00:00';
    
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = Math.floor(seconds % 60);
    
    if (h > 0) {
      return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    } else {
      return `${m}:${s.toString().padStart(2, '0')}`;
    }
  };

  // 跳转到视频管理页面
  const goToVideoManage = () => {
    router.push({
      name: 'CourseVideoManage',
      params: { courseId }
    });
  };

  // 组件挂载时初始化数据
  onMounted(initData);
</script>

<style lang="less" scoped>
.course-video {
  height: 100%;
  position: relative;
  background-color: var(--color-menu-light-bg);
  
  .loading {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 10;
  }
  
  .video-container {
    display: flex;
    height: 100%;
    overflow: hidden;
    
    .video-main {
      flex: 3;
      padding: 20px;
      overflow-y: auto;
      
      .video-header {
        margin-bottom: 16px;
        
        .video-title {
          margin: 0 0 8px;
          color: var(--color-text-1);
        }
        
        .video-meta {
          display: flex;
          align-items: center;
          
          .update-time {
            margin-left: 12px;
            color: var(--color-text-3);
            font-size: 14px;
          }
        }
      }
      
      .tab-content {
        padding: 16px 0;
      }
    }
    
    .video-list-container {
      flex: 1;
      min-width: 300px;
      max-width: 400px;
      padding: 20px;
      border-left: 1px solid var(--color-border);
      overflow-y: auto;
      background-color: var(--color-bg-1);
    }
  }
}

@media screen and (max-width: 768px) {
  .course-video {
    .video-container {
      flex-direction: column;
      
      .video-main {
        flex: none;
        height: auto;
        overflow-y: visible;
      }
      
      .video-list-container {
        flex: none;
        max-width: 100%;
        border-left: none;
        border-top: 1px solid var(--color-border);
      }
    }
  }
}
</style>