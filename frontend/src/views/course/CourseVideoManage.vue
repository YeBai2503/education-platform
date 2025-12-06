<template>
  <div class="video-manage">
    <a-page-header title="视频管理" @back="$router.back">
      <template #extra>
        <a-button type="primary" @click="showAddChapterModal">添加章节</a-button>
      </template>
    </a-page-header>
    <div class="chapter-grid">
      <!-- 章节列表 -->
      <div class="chapter-item ebutton-hover" v-for="chapter in chapters" :key="chapter.id">
        <div class="chapter-header">
          <div class="chapter-info-wrap">
            <a-avatar class="avatar" :size="40" shape="square" :style="{backgroundColor: 'var(--color-fill-2)'}">
              章
            </a-avatar>
            <div class="chapter-info">
              <p class="title">{{ chapter.title }}</p>
              <p class="description" v-if="chapter.description">{{ chapter.description }}</p>
            </div>
          </div>
          <div class="chapter-actions">
            <a-button @click="showAddSectionModal(chapter)" style="margin-right: 5px;">
              <template #icon>
                <icon-plus />
              </template>
            </a-button>
            <a-button @click="showEditChapterModal(chapter)" style="margin-right: 5px;">
              <template #icon>
                <icon-edit />
              </template>
            </a-button>
            <a-button status="danger" @click="confirmDeleteChapter(chapter.id)">
              <template #icon>
                <icon-delete />
              </template>
            </a-button>
          </div>
        </div>
        
        <!-- 小节列表，折叠在章节下方 -->
        <div class="section-container" v-if="chapter.sections && chapter.sections.length > 0">
          <div class="section-list">
            <div class="section-item" v-for="section in chapter.sections" :key="section.id">
              <div class="section-info-wrap">
                <a-avatar class="avatar" :size="28" shape="square" :style="{backgroundColor: 'var(--color-fill-3)'}">
                  节
                </a-avatar>
                <div class="section-info">
                  <p class="title">{{ section.title }} <a-tag>{{ formatDuration(section.duration) }}</a-tag></p>
                  <p class="description" v-if="section.description">{{ section.description }}</p>
                </div>
              </div>
              <div class="section-actions">
                <a-button type="text" @click="showEditSectionModal(section, chapter.id)" style="margin-right: 5px;">
                  <template #icon>
                    <icon-edit />
                  </template>
                </a-button>
                <a-button type="text" status="danger" @click="confirmDeleteSection(section.id)">
                  <template #icon>
                    <icon-delete />
                  </template>
                </a-button>
              </div>
            </div>
          </div>
        </div>
        <div class="section-container" v-else>
          <a-empty description="暂无小节" :style="{margin: '10px 0', padding: '10px'}" />
        </div>
      </div>
    </div>
    
    <a-empty v-if="chapters.length === 0" description="暂无章节" />

    <!-- 添加/编辑章节模态框 -->
    <a-modal
      v-model:visible="chapterModalVisible"
      :title="isEditingChapter ? '编辑章节' : '添加章节'"
      @ok="submitChapter"
      :ok-loading="submitLoading"
    >
      <a-form :model="chapterForm" ref="chapterFormRef" layout="vertical">
        <a-form-item field="title" label="章节标题" :rules="[{ required: true, message: '请输入章节标题' }]">
          <a-input v-model="chapterForm.title" placeholder="请输入章节标题" />
        </a-form-item>
        <a-form-item field="description" label="章节描述">
          <a-textarea v-model="chapterForm.description" placeholder="请输入章节描述" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 添加/编辑小节模态框 -->
    <a-modal
      v-model:visible="sectionModalVisible"
      :title="isEditingSection ? '编辑小节' : '添加小节'"
      @ok="submitSection"
      :ok-loading="submitLoading"
    >
      <a-form :model="sectionForm" ref="sectionFormRef" layout="vertical">
        <a-form-item field="title" label="小节标题" :rules="[{ required: true, message: '请输入小节标题' }]">
          <a-input v-model="sectionForm.title" placeholder="请输入小节标题" />
        </a-form-item>
        <a-form-item field="description" label="小节描述">
          <a-textarea v-model="sectionForm.description" placeholder="请输入小节描述" />
        </a-form-item>
        <a-form-item field="rank" label="排序号">
          <a-input-number v-model="sectionForm.rank" placeholder="请输入排序号" :min="1" />
        </a-form-item>
        <a-form-item field="video" label="视频文件">
          <a-upload
            :file-list="videoFileList"
            :limit="1"
            :custom-request="customVideoUpload"
            @change="handleVideoChange"
            accept="video/*"
          >
            <template #upload-button>
              <a-button>上传视频</a-button>
            </template>
          </a-upload>
        </a-form-item>
        <a-form-item field="cover" label="封面图片">
          <a-upload
            :file-list="coverFileList"
            :limit="1"
            :custom-request="customCoverUpload"
            @change="handleCoverChange"
            accept="image/*"
          >
            <template #upload-button>
              <a-button>上传封面</a-button>
            </template>
          </a-upload>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message, Modal } from '@arco-design/web-vue';
import { IconPlus, IconEdit, IconDelete } from '@arco-design/web-vue/es/icon';
import {
  getCourseChaptersRequest,
  addChapterRequest,
  updateChapterRequest,
  deleteChapterRequest,
  addOrUpdateSectionRequest,
  deleteSectionRequest
} from '../../apis/video-api';

const route = useRoute();
const router = useRouter();
const courseId = route.params.courseId;

// 状态变量
const loading = ref(false);
const chapters = ref([]);
const chapterModalVisible = ref(false);
const sectionModalVisible = ref(false);
const submitLoading = ref(false);
const isEditingChapter = ref(false);
const isEditingSection = ref(false);
const videoFileList = ref([]);
const coverFileList = ref([]);

// 表单数据
const chapterForm = reactive({
  id: null,
  title: '',
  description: '',
  courseId: parseInt(courseId)
});

const sectionForm = reactive({
  id: null,
  chapterId: null,
  title: '',
  description: '',
  rank: 1,
  video: null,
  cover: null
});

const chapterFormRef = ref();
const sectionFormRef = ref();

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const response = await getCourseChaptersRequest(courseId);
    const data = response.data.data || response.data;
    
    if (Array.isArray(data)) {
      chapters.value = data;
      console.log('章节数据:', chapters.value);
    } else {
      console.error('获取章节数据格式错误:', data);
      Message.error('获取章节数据失败');
    }
  } catch (error) {
    console.error('加载数据失败', error);
    Message.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

// 章节相关方法
const showAddChapterModal = () => {
  isEditingChapter.value = false;
  resetChapterForm();
  chapterModalVisible.value = true;
};

const showEditChapterModal = (chapter) => {
  isEditingChapter.value = true;
  Object.assign(chapterForm, {
    id: chapter.id,
    title: chapter.title,
    description: chapter.description,
    courseId: parseInt(courseId)
  });
  chapterModalVisible.value = true;
};

const resetChapterForm = () => {
  Object.assign(chapterForm, {
    id: null,
    title: '',
    description: '',
    courseId: parseInt(courseId)
  });
};

const submitChapter = async () => {
  try {
    await chapterFormRef.value.validate();
    submitLoading.value = true;

    if (isEditingChapter.value) {
      // 更新章节
      await updateChapterRequest(chapterForm.id, chapterForm);
      Message.success('章节更新成功');
    } else {
      // 添加章节
      await addChapterRequest(chapterForm);
      Message.success('章节添加成功');
    }

    chapterModalVisible.value = false;
    loadData(); // 重新加载数据
  } catch (error) {
    console.error('提交章节失败', error);
    Message.error('提交失败: ' + (error.message || '未知错误'));
  } finally {
    submitLoading.value = false;
  }
};

const confirmDeleteChapter = (chapterId) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除此章节吗？删除后将无法恢复，且章节下的所有小节也将被删除。',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        await deleteChapterRequest(chapterId);
        Message.success('章节删除成功');
        loadData(); // 重新加载数据
      } catch (error) {
        console.error('删除章节失败', error);
        Message.error('删除失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

// 小节相关方法
const showAddSectionModal = (chapter) => {
  isEditingSection.value = false;
  resetSectionForm();
  sectionForm.chapterId = chapter.id;
  sectionModalVisible.value = true;
};

const showEditSectionModal = (section, chapterId) => {
  isEditingSection.value = true;
  Object.assign(sectionForm, {
    id: section.id,
    chapterId: chapterId,
    title: section.title,
    description: section.description,
    rank: section.rank || 1
  });
  
  // 如果有视频URL，显示在文件列表中
  if (section.videoUrl) {
    videoFileList.value = [{
      uid: 'video',
      name: '当前视频',
      url: section.videoUrl,
      status: 'done'
    }];
  } else {
    videoFileList.value = [];
  }
  
  // 如果有封面URL，显示在文件列表中
  if (section.coverUrl) {
    coverFileList.value = [{
      uid: 'cover',
      name: '当前封面',
      url: section.coverUrl,
      status: 'done'
    }];
  } else {
    coverFileList.value = [];
  }
  
  sectionModalVisible.value = true;
};

const resetSectionForm = () => {
  Object.assign(sectionForm, {
    id: null,
    chapterId: null,
    title: '',
    description: '',
    rank: 1,
    video: null,
    cover: null
  });
  videoFileList.value = [];
  coverFileList.value = [];
};

const submitSection = async () => {
  try {
    await sectionFormRef.value.validate();
    submitLoading.value = true;

    // 检查文件是否存在
    console.log('提交前检查文件:', {
      videoFile: sectionForm.video ? (sectionForm.video instanceof File ? sectionForm.video.name : '非文件对象') : null,
      coverFile: sectionForm.cover ? (sectionForm.cover instanceof File ? sectionForm.cover.name : '非文件对象') : null,
      videoFileList: videoFileList.value,
      coverFileList: coverFileList.value
    });

    // 确保文件对象正确传递
    const formData = { ...sectionForm };
    
    // 从文件列表中获取最新的文件对象
    if (videoFileList.value.length > 0 && videoFileList.value[0].file) {
      formData.video = videoFileList.value[0].file;
      console.log('从文件列表获取视频文件:', videoFileList.value[0].file.name);
    }
    
    if (coverFileList.value.length > 0 && coverFileList.value[0].file) {
      formData.cover = coverFileList.value[0].file;
      console.log('从文件列表获取封面文件:', coverFileList.value[0].file.name);
    }

    console.log('提交到API的数据:', {
      ...formData,
      video: formData.video ? (formData.video instanceof File ? formData.video.name : '非文件对象') : null,
      cover: formData.cover ? (formData.cover instanceof File ? formData.cover.name : '非文件对象') : null
    });
    
    await addOrUpdateSectionRequest(formData);
    
    Message.success(isEditingSection.value ? '小节更新成功' : '小节添加成功');
    sectionModalVisible.value = false;
    loadData(); // 重新加载数据
  } catch (error) {
    console.error('提交小节失败', error);
    Message.error('提交失败: ' + (error.message || '未知错误'));
  } finally {
    submitLoading.value = false;
  }
};

const confirmDeleteSection = (sectionId) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除此小节吗？删除后将无法恢复。',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        await deleteSectionRequest(sectionId);
        Message.success('小节删除成功');
        loadData(); // 重新加载数据
      } catch (error) {
        console.error('删除小节失败', error);
        Message.error('删除失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

// 文件上传相关
const customVideoUpload = (options) => {
  // Arco Design's custom request options structure
  console.log('视频文件上传选项:', options);
  
  // 获取文件对象 - 可能在不同的位置
  const file = options.file || (options.fileItem && options.fileItem.file);
  
  if (file && file instanceof File) {
    console.log('获取到视频文件:', file.name);
    sectionForm.video = file;
  } else {
    console.error('无法获取有效的视频文件对象');
  }
  
  // 手动更新上传状态
  if (typeof options.onSuccess === 'function') {
    options.onSuccess();
  }
};

const customCoverUpload = (options) => {
  // Arco Design's custom request options structure
  console.log('封面图片上传选项:', options);
  
  // 获取文件对象 - 可能在不同的位置
  const file = options.file || (options.fileItem && options.fileItem.file);
  
  if (file && file instanceof File) {
    console.log('获取到封面文件:', file.name);
    sectionForm.cover = file;
  } else {
    console.error('无法获取有效的封面文件对象');
  }
  
  // 手动更新上传状态
  if (typeof options.onSuccess === 'function') {
    options.onSuccess();
  }
};

const handleVideoChange = (fileList) => {
  console.log('视频文件列表变化:', fileList);
  videoFileList.value = fileList;
  
  // 如果清空了文件列表，也清空表单中的文件
  if (fileList.length === 0) {
    sectionForm.video = null;
  } else if (fileList.length > 0 && fileList[0].file) {
    // 确保文件对象被正确设置
    sectionForm.video = fileList[0].file;
    console.log('从文件列表更新视频文件:', fileList[0].file.name);
  }
};

const handleCoverChange = (fileList) => {
  console.log('封面文件列表变化:', fileList);
  coverFileList.value = fileList;
  
  // 如果清空了文件列表，也清空表单中的文件
  if (fileList.length === 0) {
    sectionForm.cover = null;
  } else if (fileList.length > 0 && fileList[0].file) {
    // 确保文件对象被正确设置
    sectionForm.cover = fileList[0].file;
    console.log('从文件列表更新封面文件:', fileList[0].file.name);
  }
};

// 辅助函数
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

// 组件挂载时加载数据
onMounted(loadData);
</script>

<style lang="less" scoped>
.video-manage {
  padding: 20px;
  
  .chapter-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* 固定为两列布局 */
    gap: 20px; /* 章节之间的间距 */
    margin-top: 20px;
  }

  .chapter-item {
    display: flex;
    flex-direction: column; /* 改为垂直布局 */
    padding: 16px;
    background-color: var(--color-bg-2);
    border-radius: 4px;
    transition: all 0.2s;
    min-height: 150px; /* 设置最小高度，使卡片更加协调 */
    
    &:hover {
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    }

    .chapter-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start; /* 对齐顶部，避免当标题很长时出现问题 */
      width: 100%;
      margin-bottom: 10px; /* 章节标题和小节列表之间的间距 */
    }

    .chapter-info-wrap {
      display: flex;
      align-items: flex-start;
      flex: 1;
      min-width: 0; /* 避免溢出 */
      margin-right: 10px; /* 与操作按钮的间距 */

      .avatar {
        flex-shrink: 0;
        margin-right: 10px;
      }

      .chapter-info {
        display: flex;
        flex-direction: column;
        justify-content: center;
        flex: 1;
        min-width: 0;

        .title {
          color: var(--color-text-1);
          font-weight: bold;
          margin: 0;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .description {
          color: var(--color-text-3);
          font-size: 14px;
          margin-top: 6px;
          max-width: 100%;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }
    
    .chapter-actions {
      display: flex;
      flex-shrink: 0;
      white-space: nowrap;
      
      button {
        padding: 0 4px;
        height: 28px;
        line-height: 28px;
      }
    }
    
    .section-container {
      width: 100%;
      margin-top: 15px;
      border-top: 1px dashed var(--color-border);
      padding-top: 10px;
      flex: 1; /* 使小节列表区域自动填充剩余空间 */
      overflow: auto; /* 如果小节太多，允许滚动 */
      max-height: 300px; /* 限制高度，避免卡片过长 */
      
      .section-list {
        width: 100%;
        
        .section-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 8px 0;
          margin-bottom: 5px;
          border-radius: 4px;
          transition: all 0.2s;
          
          &:hover {
            background-color: var(--color-fill-1);
            padding-left: 8px;
            padding-right: 8px;
            margin-left: -8px;
            margin-right: -8px;
          }
          
          .section-info-wrap {
            display: flex;
            align-items: center;
            flex: 1;
            min-width: 0;
            
            .avatar {
              flex-shrink: 0;
              margin-right: 8px;
              width: 28px;
              height: 28px;
              line-height: 28px;
              text-align: center;
              font-size: 12px;
            }
            
            .section-info {
              flex: 1;
              min-width: 0;
              
              .title {
                font-size: 14px;
                font-weight: normal;
                display: flex;
                align-items: center;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                
                :deep(.arco-tag) {
                  flex-shrink: 0;
                  margin-left: 8px;
                  font-size: 12px;
                  height: 20px;
                  line-height: 18px;
                }
              }
              
              .description {
                font-size: 12px;
                margin-top: 4px;
              }
            }
          }
          
          .section-actions {
            flex-shrink: 0;
            display: flex;
            
            button {
              padding: 0 4px;
              height: 24px;
              line-height: 24px;
            }
          }
        }
      }
    }
  }
}

/* 响应式布局，在小屏幕上改为单列 */
@media (max-width: 768px) {
  .video-manage .chapter-grid {
    grid-template-columns: 1fr; /* 小屏幕上改为单列 */
  }
}

.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  margin-top: 20px;
  background-color: var(--color-bg-2);
  border-radius: 4px;
}
</style> 