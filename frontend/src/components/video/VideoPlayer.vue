<template>
    <div class="video-player-container">
      <video
        ref="videoRef"
        class="video-element"
        controls
        @timeupdate="handleTimeUpdate"
        @ended="handleEnded"
        @play="handlePlay"
        @pause="handlePause"
        @loadedmetadata="handleMetadataLoaded"
        :src="videoUrl"
        :poster="poster"
      ></video>
      
      <!-- 可选的自定义控制栏 -->
      <div v-if="customControls" class="custom-controls" :class="{ 'hidden': playing && !showControls }">
        <!-- 进度条 -->
        <div class="progress-bar" @click="seekVideo">
          <div class="buffered" :style="{ width: bufferedPercent + '%' }"></div>
          <div class="played" :style="{ width: playedPercent + '%' }"></div>
        </div>
        
        <!-- 控制按钮区 -->
        <div class="controls-container">
          <div class="left-controls">
            <button @click="togglePlay" class="control-btn">
              <icon-pause-circle-fill v-if="playing" />
              <icon-play-circle-fill v-else />
            </button>
            
            <div class="volume-control">
              <button @click="toggleMute" class="control-btn">
                <icon-volume-notice v-if="!muted && volume > 0.5" />
                <icon-volume-small v-else-if="!muted && volume > 0" />
                <icon-mute v-else />
              </button>
              <input 
                type="range" 
                class="volume-slider" 
                min="0" 
                max="1" 
                step="0.01" 
                v-model="volume"
              />
            </div>
            
            <div class="time-display">
              {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
            </div>
          </div>
          
          <div class="right-controls">
            <a-dropdown trigger="click">
              <button class="control-btn playback-rate">{{ playbackRate }}x</button>
              <template #content>
                <a-doption 
                  v-for="rate in [0.5, 0.75, 1, 1.25, 1.5, 2]" 
                  :key="rate" 
                  @click="setPlaybackRate(rate)"
                >
                  {{ rate }}x
                </a-doption>
              </template>
            </a-dropdown>
            
            <button @click="toggleFullscreen" class="control-btn">
              <icon-fullscreen-exit v-if="isFullscreen" />
              <icon-fullscreen v-else />
            </button>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';

const props = defineProps({
  videoUrl: {
    type: String,
    required: true
  },
  poster: {
    type: String,
    default: ''
  },
  autoplay: {
    type: Boolean,
    default: false
  },
  initialTime: {
    type: Number,
    default: 0
  },
  customControls: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['timeUpdate', 'ended', 'play', 'pause']);

// 状态变量
const videoRef = ref(null);
const playing = ref(false);
const currentTime = ref(0);
const duration = ref(0);
const buffered = ref(0);
const volume = ref(1);
const muted = ref(false);
const playbackRate = ref(1);
const isFullscreen = ref(false);
const showControls = ref(true);
const controlsTimeout = ref(null);

// 计算属性
const playedPercent = computed(() => {
  if (!duration.value) return 0;
  // 确保不超过100%
  const percent = (currentTime.value / duration.value) * 100;
  return Math.min(percent, 100);
});

const bufferedPercent = computed(() => {
  return duration.value ? (buffered.value / duration.value) * 100 : 0;
});

// 监听器
watch(() => props.videoUrl, () => {
  if (videoRef.value) {
    playing.value = false;
    currentTime.value = 0;
    duration.value = 0;
    
    // 重新加载视频
    videoRef.value.load();
  }
});

watch(() => volume.value, (newVolume) => {
  if (videoRef.value) {
    videoRef.value.volume = newVolume;
    muted.value = newVolume === 0;
  }
});

watch(() => muted.value, (isMuted) => {
  if (videoRef.value) {
    videoRef.value.muted = isMuted;
  }
});

watch(() => playbackRate.value, (rate) => {
  if (videoRef.value) {
    videoRef.value.playbackRate = rate;
  }
});

// 方法
const handleTimeUpdate = () => {
  if (videoRef.value) {
    currentTime.value = videoRef.value.currentTime;
    
    // 更新缓冲进度
    if (videoRef.value.buffered.length > 0) {
      buffered.value = videoRef.value.buffered.end(videoRef.value.buffered.length - 1);
    }
    
    // 计算进度百分比，确保不超过100%
    const percent = duration.value ? Math.min((currentTime.value / duration.value) * 100, 100) : 0;
    
    emit('timeUpdate', {
      currentTime: currentTime.value,
      duration: duration.value,
      percent: percent
    });
  }
};

const handleMetadataLoaded = () => {
  if (videoRef.value) {
    duration.value = videoRef.value.duration;
    
    // 设置初始播放位置
    if (props.initialTime > 0 && props.initialTime < duration.value) {
      videoRef.value.currentTime = props.initialTime;
    }
    
    // 设置自动播放
    if (props.autoplay) {
      togglePlay();
    }
  }
};

const handlePlay = () => {
  playing.value = true;
  emit('play');
  
  // 自动隐藏控制栏
  if (props.customControls) {
    showControls.value = true;
    resetControlsTimeout();
  }
};

const handlePause = () => {
  playing.value = false;
  emit('pause');
  
  // 显示控制栏
  if (props.customControls) {
    showControls.value = true;
    clearControlsTimeout();
  }
};

const handleEnded = () => {
  playing.value = false;
  emit('ended');
};

const togglePlay = async () => {
  if (videoRef.value) {
    if (playing.value) {
      videoRef.value.pause();
    } else {
      try {
        await videoRef.value.play();
      } catch (error) {
        console.error('播放失败:', error);
      }
    }
  }
};

const seekVideo = (event) => {
  if (videoRef.value && duration.value) {
    const progressBar = event.currentTarget;
    const rect = progressBar.getBoundingClientRect();
    const clickPosition = (event.clientX - rect.left) / rect.width;
    
    videoRef.value.currentTime = clickPosition * duration.value;
    currentTime.value = videoRef.value.currentTime;
  }
};

const toggleMute = () => {
  muted.value = !muted.value;
};

const setPlaybackRate = (rate) => {
  playbackRate.value = rate;
};

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    const element = videoRef.value.parentElement;
    
    if (element.requestFullscreen) {
      element.requestFullscreen();
    } else if (element.webkitRequestFullscreen) {
      element.webkitRequestFullscreen();
    } else if (element.mozRequestFullScreen) {
      element.mozRequestFullScreen();
    } else if (element.msRequestFullscreen) {
      element.msRequestFullscreen();
    }
    
    isFullscreen.value = true;
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen();
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen();
    } else if (document.mozCancelFullScreen) {
      document.mozCancelFullScreen();
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen();
    }
    
    isFullscreen.value = false;
  }
};

const resetControlsTimeout = () => {
  clearControlsTimeout();
  
  if (playing.value) {
    controlsTimeout.value = setTimeout(() => {
      showControls.value = false;
    }, 3000);
  }
};

const clearControlsTimeout = () => {
  if (controlsTimeout.value) {
    clearTimeout(controlsTimeout.value);
    controlsTimeout.value = null;
  }
};

const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00';
  
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = Math.floor(seconds % 60);
  
  if (h > 0) {
    return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  } else {
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
};

// 鼠标移动事件处理
const handleMouseMove = () => {
  if (props.customControls) {
    showControls.value = true;
    resetControlsTimeout();
  }
};

// 键盘事件处理
const handleKeyDown = (e) => {
  if (!videoRef.value) return;
  
  switch (e.key) {
    case ' ': // 空格
      togglePlay();
      e.preventDefault();
      break;
    case 'ArrowRight': // 右箭头
      videoRef.value.currentTime += 10; // 前进10秒
      e.preventDefault();
      break;
    case 'ArrowLeft': // 左箭头
      videoRef.value.currentTime -= 10; // 后退10秒
      e.preventDefault();
      break;
    case 'ArrowUp': // 上箭头
      volume.value = Math.min(1, volume.value + 0.1);
      e.preventDefault();
      break;
    case 'ArrowDown': // 下箭头
      volume.value = Math.max(0, volume.value - 0.1);
      e.preventDefault();
      break;
    case 'f': // F键
      toggleFullscreen();
      e.preventDefault();
      break;
    case 'm': // M键
      toggleMute();
      e.preventDefault();
      break;
  }
};

// 生命周期钩子
onMounted(() => {
  if (props.customControls) {
    // 添加事件监听
    const container = videoRef.value?.parentElement;
    container?.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('keydown', handleKeyDown);
    document.addEventListener('fullscreenchange', () => {
      isFullscreen.value = !!document.fullscreenElement;
    });
  }
});

onUnmounted(() => {
  if (props.customControls) {
    // 移除事件监听
    const container = videoRef.value?.parentElement;
    container?.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('keydown', handleKeyDown);
    document.removeEventListener('fullscreenchange', () => {});
    
    clearControlsTimeout();
  }
});
</script>

<style lang="less" scoped>
.video-player-container {
  position: relative;
  width: 100%;
  height: 0;
  padding-bottom: 56.25%; // 16:9比例
  background-color: #000;
  border-radius: 8px;
  overflow: hidden;
  
  .video-element {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
  
  .custom-controls {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    padding: 10px;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
    transition: opacity 0.3s ease;
    
    &.hidden {
      opacity: 0;
    }
    
    .progress-bar {
      height: 4px;
      background-color: rgba(255, 255, 255, 0.2);
      margin-bottom: 10px;
      position: relative;
      cursor: pointer;
      border-radius: 2px;
      overflow: hidden;
      
      &:hover {
        height: 6px;
      }
      
      .buffered {
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        background-color: rgba(255, 255, 255, 0.4);
        z-index: 1;
      }
      
      .played {
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        background-color: var(--color-primary-6);
        z-index: 2;
      }
    }
    
    .controls-container {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .left-controls, .right-controls {
        display: flex;
        align-items: center;
      }
      
      .control-btn {
        background: transparent;
        border: none;
        color: white;
        font-size: 18px;
        padding: 5px;
        margin: 0 5px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        
        &:hover {
          color: var(--color-primary-5);
        }
        
        &.playback-rate {
          font-size: 14px;
          min-width: 40px;
        }
      }
      
      .volume-control {
        display: flex;
        align-items: center;
        
        .volume-slider {
          width: 60px;
          height: 4px;
          margin: 0 5px;
          appearance: none;
          -webkit-appearance: none;
          background-color: rgba(255, 255, 255, 0.2);
          border-radius: 2px;
          
          &::-webkit-slider-thumb {
            -webkit-appearance: none;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background-color: white;
            cursor: pointer;
          }
          
          &::-moz-range-thumb {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background-color: white;
            cursor: pointer;
          }
        }
      }
      
      .time-display {
        color: white;
        font-size: 14px;
        margin-left: 10px;
      }
    }
  }
}

// 全屏样式调整
:fullscreen .video-player-container {
  padding-bottom: 0;
  height: 100%;
}
</style>