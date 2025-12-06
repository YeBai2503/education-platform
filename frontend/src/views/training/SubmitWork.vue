<template>
  <div class="submit-work">
    <a-page-header
      title="提交成果"
      @back="goBack"
    />

    <div class="submit-content">
      <a-spin :loading="loading">
        <a-alert v-if="task && isOverdue" type="warning" class="deadline-alert">
          <template #message>
            <div class="alert-content">
              <icon-exclamation-circle-fill /> 任务已过截止日期，但您仍可提交成果
            </div>
          </template>
        </a-alert>

        <div v-if="task" class="task-info">
          <h3>{{ task.title }}</h3>
          <div class="task-meta">
            <a-tag :color="getTaskStatusColor(task.status)">{{ getTaskStatusText(task.status) }}</a-tag>
            <span class="deadline">截止日期: {{ task.deadline }}</span>
          </div>
          <div class="task-description">
            {{ task.description }}
          </div>
        </div>

        <a-divider />

        <div v-if="previousSubmission" class="previous-submission">
          <a-alert type="info" class="submission-alert">
            <template #message>
              <div class="alert-content">
                <icon-info-circle-fill /> 您已于 {{ previousSubmission.submitTime }} 提交过成果
                <span v-if="previousSubmission.grade">
                  (评分: <span class="grade">{{ previousSubmission.grade }}</span> 分)
                </span>
              </div>
            </template>
          </a-alert>

          <div class="submission-preview">
            <h4>上次提交内容</h4>
            <div v-html="previousSubmission.content"></div>
            
            <div v-if="previousSubmission.attachments && previousSubmission.attachments.length > 0" class="attachments">
              <h4>附件</h4>
              <a-space>
                <a-tag
                  v-for="attachment in previousSubmission.attachments"
                  :key="attachment.id"
                  size="medium"
                  closable
                  @close="() => {}"
                >
                  <a-link :href="attachment.url" target="_blank">
                    {{ attachment.name }}
                  </a-link>
                </a-tag>
              </a-space>
            </div>
            
            <div v-if="previousSubmission.comment" class="comment">
              <h4>教师评语</h4>
              <div class="comment-content">{{ previousSubmission.comment }}</div>
            </div>
          </div>
        </div>

        <a-form
          ref="formRef"
          :model="formData"
          layout="vertical"
          @submit="handleSubmit"
        >
          <a-form-item field="content" label="提交内容" :rules="[{ required: true, message: '请输入提交内容' }]">
            <div class="editor-wrapper">
              <base-text-editor v-model="formData.content" />
            </div>
          </a-form-item>

          <a-form-item field="attachments" label="附件">
            <a-upload
              :file-list="fileList"
              :limit="5"
              :custom-request="customUploadRequest"
              @change="handleFileChange"
            >
              <template #upload-button>
                <a-button>
                  <template #icon><icon-upload /></template>
                  上传附件
                </a-button>
              </template>
            </a-upload>
            <div class="upload-tip">支持上传文档、图片、压缩包等文件，单个文件不超过10MB，最多5个附件</div>
          </a-form-item>

          <a-form-item field="teamNote" label="团队备注">
            <a-textarea
              v-model="formData.teamNote"
              placeholder="可以添加一些团队内部备注，仅团队成员可见"
              :auto-size="{ minRows: 2, maxRows: 4 }"
            />
          </a-form-item>

          <div class="form-actions">
            <a-space>
              <a-button @click="goBack">取消</a-button>
              <a-button type="primary" html-type="submit" :loading="submitting">
                {{ previousSubmission ? '重新提交' : '提交成果' }}
              </a-button>
            </a-space>
          </div>
        </a-form>
      </a-spin>
    </div>

    <a-modal
      v-model:visible="confirmModalVisible"
      title="确认提交"
      @ok="confirmSubmit"
      @cancel="confirmModalVisible = false"
      :ok-button-props="{ loading: submitting }"
    >
      <p>您确定要{{ previousSubmission ? '重新' : '' }}提交此任务成果吗？</p>
      <p v-if="previousSubmission">重新提交将覆盖之前的提交内容。</p>
      <p v-if="isOverdue" style="color: var(--color-warning);">注意：任务已过截止日期，提交可能会被扣分。</p>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { getTrainingDetail } from '@/apis/training-api';
import { getTaskDetail, submitTask } from '@/apis/training-api';
import { uploadFile } from '@/apis/file-api';
import BaseTextEditor from '@/components/BaseTextEditor.vue';
import useUserStore from '@/sotre/user-store';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const formRef = ref(null);

// 项目ID和任务ID
const projectId = computed(() => route.params.projectId);
const taskId = computed(() => route.params.taskId);

// 加载状态
const loading = ref(false);
const submitting = ref(false);

// 任务信息
const task = ref(null);
const previousSubmission = ref(null);

// 是否过期
const isOverdue = computed(() => {
  if (!task.value || !task.value.deadline) return false;
  const deadline = new Date(task.value.deadline);
  return deadline < new Date();
});

// 表单数据
const formData = reactive({
  content: '',
  teamNote: '',
  attachments: []
});

// 文件列表
const fileList = ref([]);

// 确认弹窗
const confirmModalVisible = ref(false);

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 获取任务状态文本
const getTaskStatusText = (status) => {
  const statusMap = {
    'pending': '未开始',
    'in_progress': '进行中',
    'completed': '已完成',
    'overdue': '已逾期'
  };
  return statusMap[status] || '未知';
};

// 获取任务状态颜色
const getTaskStatusColor = (status) => {
  const colorMap = {
    'pending': 'gray',
    'in_progress': 'blue',
    'completed': 'green',
    'overdue': 'red'
  };
  return colorMap[status] || 'default';
};

// 自定义上传请求
const customUploadRequest = async (options) => {
  const { file, onProgress, onSuccess, onError } = options;
  
  try {
    // 创建FormData对象
    const formData = new FormData();
    formData.append('file', file);
    
    // 上传进度回调
    const progressCallback = (event) => {
      if (event.lengthComputable) {
        const percent = Math.floor((event.loaded / event.total) * 100);
        onProgress(percent);
      }
    };
    
    // 调用上传API
    const res = await uploadFile(formData, progressCallback);
    
    if (res && res.data && res.data.data) {
      onSuccess(res.data.data);
    } else {
      onError(new Error('上传失败'));
    }
  } catch (error) {
    console.error('文件上传失败', error);
    onError(error);
  }
};

// 处理文件变更
const handleFileChange = (fileList) => {
  formData.attachments = fileList.map(file => {
    if (file.response) {
      return {
        id: file.response.id || file.uid,
        name: file.name,
        url: file.response.url
      };
    }
    return {
      id: file.id || file.uid,
      name: file.name,
      url: file.url
    };
  });
};

// 处理表单提交
const handleSubmit = async () => {
  const { validate } = formRef.value;
  try {
    await validate();
    confirmModalVisible.value = true;
  } catch (error) {
    console.error('表单验证失败', error);
  }
};

// 确认提交
const confirmSubmit = async () => {
  submitting.value = true;
  try {
    const submitData = {
      content: formData.content,
      teamNote: formData.teamNote,
      attachments: formData.attachments
    };
    
    await submitTask(projectId.value, taskId.value, submitData);
    
    Message.success('成果提交成功');
    confirmModalVisible.value = false;
    
    // 返回任务详情页
    router.push({
      name: 'training-detail',
      params: { id: projectId.value }
    });
  } catch (error) {
    console.error('提交失败', error);
    Message.error('提交失败，请重试');
  } finally {
    submitting.value = false;
  }
};

// 获取任务详情
const fetchTaskDetail = async () => {
  loading.value = true;
  try {
    const res = await getTaskDetail(projectId.value, taskId.value);
    
    if (res && res.data && res.data.data) {
      task.value = res.data.data;
      
      // 获取之前的提交
      if (task.value.submission) {
        previousSubmission.value = task.value.submission;
        
        // 填充表单
        formData.content = previousSubmission.value.content || '';
        formData.teamNote = previousSubmission.value.teamNote || '';
        
        // 填充附件列表
        if (previousSubmission.value.attachments) {
          fileList.value = previousSubmission.value.attachments.map(attachment => ({
            uid: attachment.id,
            id: attachment.id,
            name: attachment.name,
            url: attachment.url,
            status: 'done'
          }));
          formData.attachments = previousSubmission.value.attachments;
        }
      }
    }
  } catch (error) {
    console.error('获取任务详情失败', error);
    Message.error('获取任务详情失败');
    router.push({
      name: 'training-detail',
      params: { id: projectId.value }
    });
  } finally {
    loading.value = false;
  }
};

// 获取项目详情
const fetchProjectDetail = async () => {
  try {
    const res = await getTrainingDetail(projectId.value);
    
    if (!res || !res.data || !res.data.data) {
      Message.error('获取项目详情失败');
      router.push({ name: 'my-training' });
    }
  } catch (error) {
    console.error('获取项目详情失败', error);
    Message.error('获取项目详情失败');
    router.push({ name: 'my-training' });
  }
};

onMounted(async () => {
  // 确保用户已登录
  if (!userStore.userInfo) {
    await userStore.getUserInfo();
  }
  
  // 获取项目详情
  await fetchProjectDetail();
  
  // 获取任务详情
  fetchTaskDetail();
});
</script>

<style lang="less" scoped>
.submit-work {
  .submit-content {
    margin-top: 24px;
    background-color: var(--color-bg-1);
    padding: 24px;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
  
  .deadline-alert {
    margin-bottom: 16px;
    
    .alert-content {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }
  
  .task-info {
    margin-bottom: 16px;
    
    h3 {
      font-size: 18px;
      margin-bottom: 8px;
    }
    
    .task-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 12px;
      
      .deadline {
        color: var(--color-text-3);
      }
    }
    
    .task-description {
      color: var(--color-text-2);
      line-height: 1.6;
    }
  }
  
  .previous-submission {
    margin-bottom: 24px;
    
    .submission-alert {
      margin-bottom: 16px;
      
      .alert-content {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .grade {
          color: var(--color-success-6);
          font-weight: bold;
        }
      }
    }
    
    .submission-preview {
      padding: 16px;
      background-color: var(--color-fill-2);
      border-radius: 4px;
      
      h4 {
        font-size: 14px;
        margin-bottom: 8px;
        color: var(--color-text-1);
      }
      
      .attachments,
      .comment {
        margin-top: 16px;
      }
      
      .comment-content {
        color: var(--color-text-2);
        font-style: italic;
      }
    }
  }
  
  .editor-wrapper {
    border: 1px solid var(--color-border);
    border-radius: 4px;
  }
  
  .upload-tip {
    margin-top: 8px;
    font-size: 12px;
    color: var(--color-text-3);
  }
  
  .form-actions {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style> 