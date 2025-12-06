<template>
  <div class="my-knowledge">
    <div class="header-actions">
      <a-button type="primary" @click="createResource">
        <template #icon><icon-plus /></template>
        上传资源
      </a-button>
      <a-input-search
        v-model="searchKeyword"
        placeholder="搜索我的资源"
        style="width: 300px"
        @search="handleSearch"
      />
    </div>

    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="all" title="全部资源">
        <div class="resource-list">
          <a-spin :loading="loading" style="width: 100%">
            <a-empty v-if="filteredResources.length === 0" />
            <a-list :bordered="false" v-else>
              <a-list-item v-for="item in filteredResources" :key="item.id">
                <a-list-item-meta
                  :title="item.title"
                  :description="item.description"
                >
                  <template #avatar>
                    <div class="resource-icon" :style="{ backgroundColor: getTypeColor(item.type) }">
                      <icon-file-pdf v-if="item.type === 'pdf'" />
                      <icon-file-image v-else-if="item.type === 'image'" />
                      <icon-file-video v-else-if="item.type === 'video'" />
                      <icon-file v-else />
                    </div>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <div class="resource-info">
                    <span v-if="item.createTime"><icon-clock-circle /> {{ item.createTime }}</span>
                  </div>
                  <div class="resource-tags">
                    <a-tag v-for="tag in item.tags" :key="tag" size="small">{{ tag }}</a-tag>
                  </div>
                  <div class="resource-actions">
                    <a-space>
                      <a-button type="text" size="small" @click="viewResource(item.id)">
                        <icon-eye />查看
                      </a-button>
                      <a-button type="text" status="danger" size="small" @click="deleteResource(item.id)">
                        <icon-delete />删除
                      </a-button>
                    </a-space>
                  </div>
                </template>
              </a-list-item>
            </a-list>
          </a-spin>
        </div>
      </a-tab-pane>
      <a-tab-pane key="document" title="文档">
        <div class="resource-list">
          <a-spin :loading="loading" style="width: 100%">
            <a-empty v-if="filteredDocuments.length === 0" />
            <a-list :bordered="false" v-else>
              <a-list-item v-for="item in filteredDocuments" :key="item.id">
                <a-list-item-meta
                  :title="item.title"
                  :description="item.description"
                >
                  <template #avatar>
                    <div class="resource-icon" :style="{ backgroundColor: getTypeColor(item.type) }">
                      <icon-file-pdf v-if="item.type === 'pdf'" />
                      <icon-file-image v-else-if="item.type === 'image'" />
                      <icon-file-video v-else-if="item.type === 'video'" />
                      <icon-file v-else />
                    </div>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <div class="resource-info">
                    <span v-if="item.createTime"><icon-clock-circle /> {{ item.createTime }}</span>
                  </div>
                  <div class="resource-tags">
                    <a-tag v-for="tag in item.tags" :key="tag" size="small">{{ tag }}</a-tag>
                  </div>
                  <div class="resource-actions">
                    <a-space>
                      <a-button type="text" size="small" @click="viewResource(item.id)">
                        <icon-eye />查看
                      </a-button>
                      <a-button type="text" status="danger" size="small" @click="deleteResource(item.id)">
                        <icon-delete />删除
                      </a-button>
                    </a-space>
                  </div>
                </template>
              </a-list-item>
            </a-list>
          </a-spin>
        </div>
      </a-tab-pane>
      <a-tab-pane key="video" title="视频">
        <div class="resource-list">
          <a-spin :loading="loading" style="width: 100%">
            <a-empty v-if="filteredVideos.length === 0" />
            <a-list :bordered="false" v-else>
              <a-list-item v-for="item in filteredVideos" :key="item.id">
                <a-list-item-meta
                  :title="item.title"
                  :description="item.description"
                >
                  <template #avatar>
                    <div class="resource-icon" :style="{ backgroundColor: getTypeColor(item.type) }">
                      <icon-file-pdf v-if="item.type === 'pdf'" />
                      <icon-file-image v-else-if="item.type === 'image'" />
                      <icon-file-video v-else-if="item.type === 'video'" />
                      <icon-file v-else />
                    </div>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <div class="resource-info">
                    <span v-if="item.createTime"><icon-clock-circle /> {{ item.createTime }}</span>
                  </div>
                  <div class="resource-tags">
                    <a-tag v-for="tag in item.tags" :key="tag" size="small">{{ tag }}</a-tag>
                  </div>
                  <div class="resource-actions">
                    <a-space>
                      <a-button type="text" size="small" @click="viewResource(item.id)">
                        <icon-eye />查看
                      </a-button>
                      <a-button type="text" status="danger" size="small" @click="deleteResource(item.id)">
                        <icon-delete />删除
                      </a-button>
                    </a-space>
                  </div>
                </template>
              </a-list-item>
            </a-list>
          </a-spin>
        </div>
      </a-tab-pane>
      <a-tab-pane key="image" title="图片">
        <div class="resource-list">
          <a-spin :loading="loading" style="width: 100%">
            <a-empty v-if="filteredImages.length === 0" />
            <a-list :bordered="false" v-else>
              <a-list-item v-for="item in filteredImages" :key="item.id">
                <a-list-item-meta
                  :title="item.title"
                  :description="item.description"
                >
                  <template #avatar>
                    <div class="resource-icon" :style="{ backgroundColor: getTypeColor(item.type) }">
                      <icon-file-pdf v-if="item.type === 'pdf'" />
                      <icon-file-image v-else-if="item.type === 'image'" />
                      <icon-file-video v-else-if="item.type === 'video'" />
                      <icon-file v-else />
                    </div>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <div class="resource-info">
                    <span v-if="item.createTime"><icon-clock-circle /> {{ item.createTime }}</span>
                  </div>
                  <div class="resource-tags">
                    <a-tag v-for="tag in item.tags" :key="tag" size="small">{{ tag }}</a-tag>
                  </div>
                  <div class="resource-actions">
                    <a-space>
                      <a-button type="text" size="small" @click="viewResource(item.id)">
                        <icon-eye />查看
                      </a-button>
                      <a-button type="text" status="danger" size="small" @click="deleteResource(item.id)">
                        <icon-delete />删除
                      </a-button>
                    </a-space>
                  </div>
                </template>
              </a-list-item>
            </a-list>
          </a-spin>
        </div>
      </a-tab-pane>
      <a-tab-pane key="other" title="其他">
        <div class="resource-list">
          <a-spin :loading="loading" style="width: 100%">
            <a-empty v-if="filteredOthers.length === 0" />
            <a-list :bordered="false" v-else>
              <a-list-item v-for="item in filteredOthers" :key="item.id">
                <a-list-item-meta
                  :title="item.title"
                  :description="item.description"
                >
                  <template #avatar>
                    <div class="resource-icon" :style="{ backgroundColor: getTypeColor(item.type) }">
                      <icon-file-pdf v-if="item.type === 'pdf'" />
                      <icon-file-image v-else-if="item.type === 'image'" />
                      <icon-file-video v-else-if="item.type === 'video'" />
                      <icon-file v-else />
                    </div>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <div class="resource-info">
                    <span v-if="item.createTime"><icon-clock-circle /> {{ item.createTime }}</span>
                  </div>
                  <div class="resource-tags">
                    <a-tag v-for="tag in item.tags" :key="tag" size="small">{{ tag }}</a-tag>
                  </div>
                  <div class="resource-actions">
                    <a-space>
                      <a-button type="text" size="small" @click="viewResource(item.id)">
                        <icon-eye />查看
                      </a-button>
                      <a-button type="text" status="danger" size="small" @click="deleteResource(item.id)">
                        <icon-delete />删除
                      </a-button>
                    </a-space>
                  </div>
                </template>
              </a-list-item>
            </a-list>
          </a-spin>
        </div>
      </a-tab-pane>
    </a-tabs>
    
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getKnowledgeList, deleteKnowledge } from '@/apis/knowledge-api';
import { getTeacherCoursesRequest } from '@/apis/course-api';
import { Message, Modal } from '@arco-design/web-vue';

const router = useRouter();
const activeTab = ref('all');
const searchKeyword = ref('');
const myResources = ref([]);
const loading = ref(false);

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

// 根据搜索关键词过滤资源
const filteredResources = computed(() => {
  console.log('过滤前的资源数量:', myResources.value.length);
  if (!searchKeyword.value) return myResources.value;
  const filtered = myResources.value.filter(item => 
    item.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) || 
    (item.description && item.description.toLowerCase().includes(searchKeyword.value.toLowerCase()))
  );
  console.log('过滤后的资源数量:', filtered.length);
  return filtered;
});

// 按类型过滤资源
const filteredDocuments = computed(() => {
  const filtered = filteredResources.value.filter(item => item.type === 'pdf' || item.type === 'document');
  console.log('文档类型资源数量:', filtered.length);
  return filtered;
});
const filteredVideos = computed(() => {
  const filtered = filteredResources.value.filter(item => item.type === 'video');
  console.log('视频类型资源数量:', filtered.length);
  return filtered;
});
const filteredImages = computed(() => {
  const filtered = filteredResources.value.filter(item => item.type === 'image');
  console.log('图片类型资源数量:', filtered.length);
  return filtered;
});
const filteredOthers = computed(() => {
  const filtered = filteredResources.value.filter(item => 
    item.type !== 'pdf' && item.type !== 'document' && item.type !== 'video' && item.type !== 'image'
  );
  console.log('其他类型资源数量:', filtered.length);
  return filtered;
});

// 处理搜索
const handleSearch = () => {
  // 本地过滤，无需额外操作，computed会自动处理
};

// 创建新资源
const createResource = () => {
  router.push({ name: 'knowledge-edit' });
};

// 查看资源
const viewResource = (id) => {
  router.push({ name: 'knowledge-detail', params: { id } });
};

// 删除资源
const deleteResource = (id) => {
  Modal.warning({
    title: '确认删除',
    content: '确定要删除此资源吗？删除后将无法恢复。',
    okText: '确定删除',
    cancelText: '取消',
    onOk: async () => {
      try {
        await deleteKnowledge(id);
        myResources.value = myResources.value.filter(item => item.id !== id);
        Message.success('删除成功');
      } catch (error) {
        console.error('删除资源失败', error);
        Message.error('删除资源失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

// 获取我的资源列表
const fetchMyResources = async () => {
  loading.value = true;
  try {
    // 调用API获取我的资源列表
    const params = {
      currentPage: 1,
      pageSize: 50 // 获取较多数据
    };
    
    const res = await getKnowledgeList(params);
    
    // 处理API返回的数据结构
    let dataList = [];
    
    if (res && res.data) {
      dataList = res.data.data.list;
    }
    console.log('dataList',dataList);
    
    // 转换API返回的数据格式
    if (dataList && dataList.length >= 0) {
      myResources.value = dataList.map(item => {
        // 只保留API实际返回的数据
        let desc = '';
        if (item.type) {
          desc += `类型: ${item.type}`;
        }
        if (item.tag) {
          desc += desc ? ` | 标签: ${item.tag}` : `标签: ${item.tag}`;
        }
        if (item.description) {
          desc += desc ? ` | ${item.description}` : item.description;
        }
        
        const formattedItem = {
          id: item.id,
          title: item.name || item.title || '未命名资源',
          description: desc,
          type: item.type || 'other',
          createTime: item.createdAt || item.createTime || '',
          tags: item.tag ? item.tag.split(',') : []
        };
        
        return formattedItem;
      });
      
      console.log('处理后的资源列表:', myResources.value);
    } else {
      // 如果返回数据格式不符合预期，使用空数组
      console.error('API返回的数据格式不符合预期或无数据', res);
      myResources.value = [];
    }
  } catch (error) {
    console.error('获取我的资源列表失败', error);
    Message.error('获取我的资源列表失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchMyResources();
});
</script>

<style lang="less" scoped>
.my-knowledge {
  .header-actions {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
  }

  .resource-list {
    background-color: var(--color-bg-1);
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    
    .resource-icon {
      width: 40px;
      height: 40px;
      border-radius: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 20px;
    }

    .resource-info {
      display: flex;
      gap: 16px;
      color: var(--color-text-3);
      font-size: 12px;

      span {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }

    .resource-tags {
      margin-top: 8px;
    }

    .resource-actions {
      margin-top: 8px;
      display: flex;
      gap: 8px;
    }
  }
}
</style> 