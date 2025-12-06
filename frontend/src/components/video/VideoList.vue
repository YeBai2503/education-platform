<!-- src/components/video/VideoList.vue -->
<template>
    <div class="video-list-component">
      <div class="list-header" v-if="title">
        <h3>{{ title }}</h3>
        <div class="progress-info" v-if="showProgress">
          <a-progress
            :percent="progressPercent/100"
            size="small"
            :stroke-width="4"
          />
          <span>已学习 {{ progressPercent }}%</span>
        </div>
      </div>
      
      <a-collapse :default-active-key="activeChapters" accordion>
        <a-collapse-item v-for="chapter in chapters" :key="chapter.id" :header="chapter.title">
          <a-list :bordered="false">
            <a-list-item
              v-for="video in chapter.videos"
              :key="video.id"
              class="video-item"
              :class="{ 'active': currentVideoId === video.id }"
              @click="$emit('select', video)"
            >
              <div class="video-item-content">
                <div class="video-status">
                  <icon-check-circle-fill v-if="video.completed" class="completed" />
                  <icon-play-circle-fill v-else-if="video.progress > 0" class="in-progress" />
                  <icon-video-camera v-else class="unwatched" />
                </div>
                
                <div class="video-info">
                  <div class="video-name">{{ video.title }}</div>
                  <div class="video-meta">
                    <span>{{ formatDuration(video.duration) }}</span>
                    <span v-if="video.progress > 0 && !video.completed">
                      已学习 {{ video.progress }}%
                    </span>
                  </div>
                </div>
              </div>
            </a-list-item>
          </a-list>
        </a-collapse-item>
      </a-collapse>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, watch } from 'vue';
  
  const props = defineProps({
    chapters: {
      type: Array,
      required: true
    },
    currentVideoId: String,
    title: String,
    showProgress: {
      type: Boolean,
      default: true
    }
  });
  
  const emit = defineEmits(['select']);
  
  // 状态变量
  const activeChapters = ref([]);
  
  // 根据当前视频ID自动展开对应章节
  watch(() => props.currentVideoId, (videoId) => {
    if (videoId) {
      for (const chapter of props.chapters) {
        for (const video of chapter.videos) {
          if (video.id === videoId) {
            activeChapters.value = [chapter.id];
            break;
          }
        }
      }
    }
  }, { immediate: true });
  
  // 计算总体进度百分比
  const progressPercent = computed(() => {
    let totalDuration = 0;
    let watchedDuration = 0;
    
    props.chapters.forEach(chapter => {
      chapter.videos.forEach(video => {
        // 累加总时长
        totalDuration += video.duration;
        
        if (video.completed) {
          // 已完成的视频算全部时长
          watchedDuration += video.duration;
        } else if (video.progress > 0) {
          // 部分完成的视频按比例计算已看时长
          watchedDuration += (video.duration * (video.progress / 100));
        }
      });
    });
    
    if (totalDuration === 0) return 0;
    
    // 计算总体完成百分比
    const percent = Math.round((watchedDuration / totalDuration) * 100);
    return Math.min(percent, 100);
  });
  
  // 格式化视频时长
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
  </script>
  
  <style lang="less" scoped>
  .video-list-component {
    .list-header {
      margin-bottom: 16px;
      
      h3 {
        margin: 0 0 12px;
        color: var(--color-text-1);
      }
      
      .progress-info {
        display: flex;
        flex-direction: column;
        
        span {
          margin-top: 4px;
          font-size: 12px;
          color: var(--color-text-3);
        }
      }
    }
    
    .video-item {
      cursor: pointer;
      transition: all 0.2s;
      border-radius: 4px;
      
      &:hover {
        background-color: var(--color-fill-2);
      }
      
      &.active {
        background-color: var(--color-primary-light-1);
        
        .video-item-content .video-info .video-name {
          color: rgb(var(--primary-6));
          font-weight: 500;
        }
      }
      
      .video-item-content {
        display: flex;
        padding: 8px;
        
        .video-status {
          margin-right: 12px;
          font-size: 18px;
          display: flex;
          align-items: center;
          
          .completed {
            color: #00b42a;
          }
          
          .in-progress {
            color: #ff7d00;
          }
          
          .unwatched {
            color: var(--color-text-3);
          }
        }
        
        .video-info {
          flex: 1;
          
          .video-name {
            margin-bottom: 4px;
            color: var(--color-text-1);
          }
          
          .video-meta {
            display: flex;
            justify-content: space-between;
            font-size: 12px;
            color: var(--color-text-3);
          }
        }
      }
    }
  }
  </style>