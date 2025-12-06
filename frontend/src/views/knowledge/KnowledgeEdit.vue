<template>
  <div class="knowledge-edit">
    <div class="edit-header">
      <h1>上传资源</h1>
      <a-button type="text" @click="goBack">
        <template #icon><icon-left /></template>
        返回
      </a-button>
    </div>

    <a-spin :loading="loading">
      <a-form
        ref="formRef"
        :model="form"
        layout="vertical"
        class="edit-form"
      >
        <a-form-item field="title" label="资源标题" required>
          <a-input v-model="form.title" placeholder="请输入资源标题" />
        </a-form-item>

        <a-form-item field="type" label="资源类型" required>
          <a-select v-model="form.type" placeholder="请选择资源类型">
            <a-option value="pdf">PDF文档</a-option>
            <a-option value="document">文档</a-option>
            <a-option value="video">视频</a-option>
            <a-option value="image">图片</a-option>
            <a-option value="other">其他</a-option>
          </a-select>
        </a-form-item>

        <a-form-item field="description" label="资源描述">
          <a-textarea
            v-model="form.description"
            placeholder="请输入资源描述"
            :auto-size="{ minRows: 4, maxRows: 8 }"
          />
        </a-form-item>

        <a-form-item field="tags" label="资源标签">
          <a-input-tag
            v-model="form.tags"
            placeholder="请输入标签，按回车确认"
            :max-tag-count="10"
          />
          <div class="tag-suggestions">
            <span class="suggestion-label">推荐标签：</span>
            <a-space>
              <a-tag
                v-for="tag in suggestedTags"
                :key="tag"
                color="blue"
                checkable
                :checked="form.tags.includes(tag)"
                @click="toggleTag(tag)"
              >
                {{ tag }}
              </a-tag>
            </a-space>
          </div>
        </a-form-item>

        <a-form-item field="file" label="上传文件" required>
          <a-upload
            :file-list="fileList"
            :limit="1"
            :custom-request="customUpload"
            @change="handleFileChange"
          >
            <template #upload-button>
              <a-button type="primary">
                <template #icon><icon-upload /></template>
                选择文件
              </a-button>
            </template>
            <template #extra>
              <div class="upload-tip">
                支持上传PDF、Word、PPT、图片、视频等格式文件，单个文件大小不超过100MB
              </div>
            </template>
          </a-upload>
        </a-form-item>

        <a-form-item field="isPublic" label="资源权限">
          <a-radio-group v-model="form.isPublic">
            <a-radio :value="true">公开资源（所有教师可见）</a-radio>
            <a-radio :value="false">私有资源（仅自己可见）</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-divider />

        <div class="form-actions">
          <a-space>
            <a-button @click="goBack">取消</a-button>
            <a-button type="primary" @click="handleSubmit">上传资源</a-button>
          </a-space>
        </div>
      </a-form>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { uploadKnowledgeFile } from '@/apis/knowledge-api';
import { Message } from '@arco-design/web-vue';

const route = useRoute();
const router = useRouter();
const formRef = ref(null);

// 移除 isEdit 相关逻辑

// 表单数据
const form = reactive({
  title: '',
  type: '',
  description: '',
  tags: [],
  file: null,
  isPublic: true
});

// 移除表单验证规则，改为手动验证

// 文件列表
const fileList = ref([]);
// 推荐标签
const suggestedTags = ref(['课件', 'PPT', '教案', '试题', '视频教程', '实验指导', '高等数学', '线性代数', '计算机科学', '编程语言']);
// 加载状态
const loading = ref(false);

// 返回上一页
const goBack = () => {
  router.back();
};

// 切换标签选择
const toggleTag = (tag) => {
  if (form.tags.includes(tag)) {
    form.tags = form.tags.filter(t => t !== tag);
  } else {
    form.tags.push(tag);
  }
};

// 自定义上传处理
const customUpload = async (options) => {
  const { fileItem, onSuccess, onError } = options;
  
  try {
    // 保存文件到表单中
    form.file = fileItem;
    // 不立即上传，在提交表单时一起上传
    onSuccess();
  } catch (error) {
    console.error('文件处理失败', error);
    onError();
  }
};

// 处理文件变化
const handleFileChange = (fileList) => {
  
};

// 移除 fetchResourceDetail 相关逻辑

// 提交表单
const handleSubmit = async () => {
  // 手动验证
  if (!form.title.trim()) {
    Message.error('请输入资源标题');
    return;
  }
  if (form.title.trim().length > 100) {
    Message.error('标题长度不能超过100个字符');
    return;
  }
  if (!form.type) {
    Message.error('请选择资源类型');
    return;
  }
  if (!form.file) {
    Message.error('请上传文件');
    return;
  }
  if (form.file && !form.file.file) {
    Message.error('文件对象不正确，请重新上传');
    return;
  }
  loading.value = true;
  try {
    const params = {
      name: form.title,
      isPublic: form.isPublic ? '1' : '0',
      tag: form.tags.join(','),
      type: form.type
    };
    const fileToUpload = form.file && form.file.file ? form.file.file : form.file;
    const res = await uploadKnowledgeFile(fileToUpload, params);
    Message.success('资源上传成功');
    router.push({ name: 'my-knowledge' });
  } catch (uploadError) {
    Message.error('文件上传失败: ' + (uploadError.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

onMounted(() => {});
</script>

<style lang="less" scoped>
.knowledge-edit {
  background-color: var(--color-bg-1);
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 24px;

  .edit-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h1 {
      font-size: 24px;
      font-weight: bold;
      color: var(--color-text-1);
      margin: 0;
    }
  }

  .edit-form {
    max-width: 800px;

    // 强化：让已选标签横向自动换行（兼容所有布局）
    :deep(.arco-input-tag-view) {
      display: flex !important;
      flex-wrap: wrap !important;
      flex-direction: row !important;
      align-items: center;
      min-height: 40px;
      gap: 4px;
    }
    :deep(.arco-tag) {
      margin-bottom: 4px;
      margin-right: 4px;
    }

    .tag-suggestions {
      margin-top: 8px;
      display: flex;
      align-items: center;

      .suggestion-label {
        color: var(--color-text-3);
        margin-right: 8px;
        white-space: nowrap;
      }
    }

    .upload-tip {
      color: var(--color-text-3);
      font-size: 12px;
      margin-top: 4px;
    }

    .form-actions {
      display: flex;
      justify-content: center;
      margin-top: 16px;
    }
  }
}
</style> 