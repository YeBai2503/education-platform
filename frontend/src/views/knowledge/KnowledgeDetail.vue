<template>
  <div class="knowledge-detail">
    <a-spin :loading="loading" style="width: 100%">
      <div v-if="resource" class="detail-container">
        <div class="detail-header">
          <div class="back-link">
            <a-button type="text" @click="goBack">
              <template #icon><icon-left /></template>
              返回
            </a-button>
          </div>
          <h1 class="detail-title">{{ resource.title }}</h1>
          <div class="detail-meta">
            <a-space>
              <a-tag :color="getTypeColor(resource.type)">{{ resource.type }}</a-tag>
              <span v-if="resource.author"><icon-user /> {{ resource.author }}</span>
              <span v-if="resource.createTime"><icon-clock-circle /> {{ resource.createTime }}</span>
            </a-space>
          </div>
          <div class="detail-actions">
            <a-space>
              <a-button type="primary" @click="downloadResource">
                <template #icon><icon-download /></template>
                下载资源
              </a-button>
              <a-button v-if="isOwner" type="outline" @click="editResource">
                <template #icon><icon-edit /></template>
                编辑资源
              </a-button>
              <a-button v-if="isOwner" status="danger" @click="deleteResource">
                <template #icon><icon-delete /></template>
                删除资源
              </a-button>
            </a-space>
          </div>
        </div>

        <a-divider />

        <div class="detail-content">
          <div class="content-section">
            <h2>资源描述</h2>
            <div class="description">{{ resource.description }}</div>
          </div>

          <div class="content-section">
            <h2>资源预览</h2>
            <div class="preview-container">
              <!-- PDF预览 -->
              <div v-if="resource.type === 'pdf'" class="pdf-preview">
                <div class="pdf-placeholder">
                  <icon-file-pdf style="font-size: 48px; color: #ff4d4f;" />
                  <p>PDF文档预览</p>
                </div>
              </div>
              
              <!-- 图片预览 -->
              <div v-else-if="resource.type === 'image'" class="image-preview">
                <img :src="resource.previewUrl || 'https://via.placeholder.com/800x500'" alt="资源预览" />
              </div>
              
              <!-- 视频预览 -->
              <div v-else-if="resource.type === 'video'" class="video-preview">
                <div class="video-placeholder">
                  <icon-video-camera style="font-size: 48px; color: #1890ff;" />
                  <p>视频资源预览</p>
                </div>
              </div>
              
              <!-- 其他类型资源 -->
              <div v-else class="other-preview">
                <div class="other-placeholder">
                  <icon-file style="font-size: 48px; color: #52c41a;" />
                  <p>{{ resource.title }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="content-section">
            <h2>标签</h2>
            <div class="tags">
              <a-space>
                <a-tag v-for="tag in resource.tags" :key="tag" color="blue">{{ tag }}</a-tag>
              </a-space>
            </div>
          </div>
        </div>
      </div>
      <a-empty v-else description="未找到资源信息" />
    </a-spin>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getKnowledgeDetail, deleteKnowledge } from '@/apis/knowledge-api';
import { Message, Modal } from '@arco-design/web-vue';
import useUserStore from '@/sotre/user-store';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const loading = ref(true);
const resource = ref(null);
const relatedResources = ref([]);

// 判断当前用户是否为资源所有者
const isOwner = computed(() => {
  if (!resource.value || !userStore.userInfo) return false;
  return resource.value.userId === userStore.userInfo.id;
});

// 根据资源类型获取颜色
const getTypeColor = (type) => {
  const colorMap = {
    'pdf': '#ff7875',
    'image': '#52c41a',
    'video': '#1890ff',
    'document': '#fa8c16',
    'other': '#8c8c8c'
  };
  return colorMap[type] || '#8c8c8c';
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 获取资源详情
const fetchResourceDetail = async () => {
  loading.value = true;
  try {
    const id = route.params.id;
    const res = await getKnowledgeDetail(id);
    
    console.log('资源详情API响应:', res);
    
    // 处理API返回的数据格式
    let data = null;
    
    if (res && res.data && res.data.data) {
      data = res.data.data;
    }
    
    console.log('资源详情数据:', data);
    
    if (data) {
      // 将API返回的数据转换为组件需要的格式
      // 只保留后端实际返回的数据
      resource.value = {
        id: data.id,
        title: data.name || data.title || '未命名资源',
        description: data.description || '',
        author: data.uploader?.nickname || data.author || '',
        userId: data.userId || data.uploaderId,
        createTime: data.createdAt || data.createTime || '',
        type: data.type || 'other',
        tags: data.tag ? data.tag.split(',') : (data.tags || []),
        previewUrl: data.url || data.fileUrl || ''
      };
      
      // 重新构建描述字段
      let desc = '';
      if (data.type) {
        desc += `类型: ${data.type}`;
      }
      if (data.tag) {
        desc += desc ? `\n标签: ${data.tag}` : `标签: ${data.tag}`;
      }
      if (data.description) {
        desc += desc ? `\n\n${data.description}` : data.description;
      }
      
      resource.value.description = desc;
      
      console.log('获取资源详情成功', resource.value);
    } else {
      console.error('API返回的数据格式不符合预期或无数据', res);
      resource.value = null;
    }
    
    // 获取相关资源
    fetchRelatedResources();
  } catch (error) {
    console.error('获取资源详情失败', error);
    Message.error('获取资源详情失败');
  } finally {
    loading.value = false;
  }
};

// 获取相关资源
const fetchRelatedResources = async () => {
  try {
    // 模拟数据
    relatedResources.value = [
      {
        id: '2',
        title: '高等数学习题集',
        author: '李老师',
        views: 986,
        type: 'pdf'
      },
      {
        id: '3',
        title: '微积分视频教程',
        author: '王讲师',
        views: 2341,
        type: 'video'
      },
      {
        id: '4',
        title: '线性代数图解',
        author: '赵老师',
        views: 1542,
        type: 'image'
      }
    ];
  } catch (error) {
    console.error('获取相关资源失败', error);
  }
};

// 下载资源
const downloadResource = () => {
  if (!resource.value || !resource.value.previewUrl) {
    Message.error('资源链接不可用');
    return;
  }
  
  try {
    // 创建下载链接
    const link = document.createElement('a');
    link.href = resource.value.previewUrl;
    link.target = '_blank';
    link.download = resource.value.title || 'download';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    Message.success('资源下载中...');
  } catch (error) {
    console.error('下载失败', error);
    Message.error('下载失败，请稍后重试');
  }
};

// 编辑资源
const editResource = () => {
  router.push({
    name: 'knowledge-edit',
    params: { id: resource.value.id }
  });
};

// 删除资源
const deleteResource = () => {
  Modal.warning({
    title: '确认删除',
    content: '确定要删除此资源吗？删除后将无法恢复。',
    okText: '确定删除',
    cancelText: '取消',
    onOk: async () => {
      try {
        await deleteKnowledge(resource.value.id);
        Message.success('删除成功');
        router.push({ name: 'my-knowledge' });
      } catch (error) {
        console.error('删除资源失败', error);
        Message.error('删除资源失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

// 查看相关资源
const viewRelatedResource = (id) => {
  router.push({
    name: 'knowledge-detail',
    params: { id }
  });
};

onMounted(() => {
  fetchResourceDetail();
});
</script>

<style lang="less" scoped>
.knowledge-detail {
  .detail-container {
    background-color: var(--color-bg-1);
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    padding: 24px;

    .detail-header {
      .back-link {
        margin-bottom: 16px;
      }

      .detail-title {
        font-size: 24px;
        font-weight: bold;
        color: var(--color-text-1);
        margin-bottom: 16px;
      }

      .detail-meta {
        margin-bottom: 16px;
        color: var(--color-text-3);

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }

      .detail-actions {
        margin-top: 16px;
      }
    }

    .detail-content {
      .content-section {
        margin-bottom: 24px;

        h2 {
          font-size: 18px;
          font-weight: bold;
          color: var(--color-text-1);
          margin-bottom: 16px;
        }

        .description {
          color: var(--color-text-2);
          line-height: 1.6;
          white-space: pre-line;
        }

        .preview-container {
          border: 1px solid var(--color-border);
          border-radius: 4px;
          min-height: 300px;
          display: flex;
          align-items: center;
          justify-content: center;

          .pdf-placeholder,
          .video-placeholder,
          .other-placeholder {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 40px;
            text-align: center;
            color: var(--color-text-3);

            p {
              margin-top: 16px;
            }
          }

          .image-preview {
            width: 100%;
            text-align: center;

            img {
              max-width: 100%;
              max-height: 500px;
              object-fit: contain;
            }
          }
        }
      }
    }
  }
}
</style> 