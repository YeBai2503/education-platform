<template>
  <div class="knowledge-list">
    <div class="search-section">
      <h2>资源搜索</h2>
      <div class="search-box">
        <a-input-search
          v-model="filterForm.keyword"
          placeholder="输入关键词搜索知识库资源"
          search-button
          :style="{ width: '500px' }"
          @search="handleSearch"
        />
      </div>
      <div class="search-tags">
        <span class="tag-label">热门标签：</span>
        <a-space>
          <a-tag v-for="tag in tags" :key="tag" color="blue" checkable @click="handleTagClick(tag)">{{ tag }}</a-tag>
        </a-space>
      </div>
    </div>

    <div class="filter-section">
      <a-form :model="filterForm" layout="inline">
        <a-form-item field="type" label="资源类型">
          <a-select v-model="filterForm.type" placeholder="选择资源类型" allow-clear style="width: 120px">
            <a-option value="pdf">PDF文档</a-option>
            <a-option value="document">文档</a-option>
            <a-option value="video">视频</a-option>
            <a-option value="image">图片</a-option>
            <a-option value="other">其他</a-option>
          </a-select>
        </a-form-item>
        <a-form-item field="tag" label="标签">
          <a-select v-model="filterForm.tag" placeholder="选择标签" allow-clear style="width: 120px">
            <a-option v-for="tag in tags" :key="tag" :value="tag">{{ tag }}</a-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">搜索</a-button>
          <a-button style="margin-left: 8px" @click="resetFilters">重置</a-button>
        </a-form-item>
      </a-form>
    </div>

    <div class="list-section">
      <div class="list-header">
        <div class="total-info">
          共找到 <span class="highlight">{{ total }}</span> 个资源
        </div>
      </div>

      <div class="resource-list">
        <a-spin :loading="loading" style="width: 100%">
          <a-empty v-if="resources.length === 0" />
          <a-list :bordered="false" v-else>
            <a-list-item v-for="item in resources" :key="item.id">
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
                    <span v-if="item.author"><icon-user /> {{ item.author }}</span>
                    <span v-if="item.createTime"><icon-clock-circle /> {{ item.createTime }}</span>
                  </div>
                <div class="resource-tags">
                  <a-tag v-for="tag in item.tags" :key="tag" size="small">{{ tag }}</a-tag>
                </div>
                <div class="resource-actions">
                  <a-button type="primary" size="small" @click="viewDetail(item.id)">查看详情</a-button>
                </div>
              </template>
            </a-list-item>
          </a-list>
          <div class="pagination">
            <a-pagination
              v-model:current="current"
              v-model:pageSize="pageSize"
              :total="total"
              show-total
              show-jumper
              @change="handlePageChange"
            />
          </div>
        </a-spin>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { searchKnowledge } from '@/apis/knowledge-api';
import { Message } from '@arco-design/web-vue';

const route = useRoute();
const router = useRouter();

// 筛选表单
const filterForm = reactive({
  keyword: '',
  type: '',
  tag: ''
});

// 标签列表
const tags = ref(['课件', 'PPT', '教案', '试题', '视频教程', '实验指导']);

// 分页相关
const current = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 资源列表
const resources = ref([]);
const loading = ref(false);

// 监听路由查询参数变化
watch(() => route.query, (query) => {
  if (query.keyword) {
    filterForm.keyword = query.keyword;
    handleSearch();
  }
}, { immediate: true });



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

// 处理搜索
const handleSearch = () => {
  // 手动验证搜索关键词
  if (filterForm.keyword && filterForm.keyword.trim().length > 100) {
    Message.warning('搜索关键词长度不能超过100个字符');
    return;
  }
  
  current.value = 1;
  fetchResources();
};

// 处理标签点击
const handleTagClick = (tag) => {
  filterForm.tag = tag;
  handleSearch();
};

// 重置筛选条件
const resetFilters = () => {
  filterForm.keyword = '';
  filterForm.type = '';
  filterForm.tag = '';
  handleSearch();
};

// 处理页码变化
const handlePageChange = () => {
  fetchResources();
};

// 查看资源详情
const viewDetail = (id) => {
  router.push({
    name: 'knowledge-detail',
    params: { id }
  });
};

// 获取资源列表
const fetchResources = async () => {
  loading.value = true;
  try {
    // 构建查询参数
    const params = {
      currentPage: current.value,
      pageSize: pageSize.value
    };
    params.keyword = filterForm.keyword || '';
    params.type = filterForm.type || '';
    params.tag = filterForm.tag || '';

    

    // 调用API获取资源列表
    let res;
    

    res = await searchKnowledge(params);
    
    console.log('资源列表API响应:', res);
    
    // 检查不同的返回数据结构
    let dataList = [];
    let totalCount = 0;
    
    if (res && res.data) {
      dataList = res.data.data.list;
      totalCount = res.data.data.total;
    }
    console.log('dataList:', dataList);
    console.log('totalCount:', totalCount);
    // 转换API返回的数据格式
    if (dataList.length >= 0) {
      // 格式化资源数据
      resources.value = dataList.map(item => {
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
        
        return {
          id: item.id,
          title: item.name || item.title || '未命名资源',
          description: desc,
          type: item.type || 'other',
          tags: item.tag ? item.tag.split(',') : [],
          author: item.uploader?.nickname || item.author || '',
          createTime: item.createdAt || item.createTime || '',
          url: item.url || ''
        };
      });
      total.value = totalCount;
    } else {
      // 如果返回数据格式不符合预期，使用空数组
      console.error('API返回的数据格式不符合预期或无数据', res);
      resources.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取资源列表失败', error);
    Message.error('获取资源列表失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchResources();
});
</script>

<style lang="less" scoped>
.knowledge-list {
  .search-section {
    background-color: var(--color-bg-1);
    padding: 30px;
    border-radius: 4px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

    h2 {
      font-size: 18px;
      margin-bottom: 16px;
      color: var(--color-text-1);
    }

    .search-box {
      margin-bottom: 16px;
      display: flex;
      justify-content: center;
    }

    .search-tags {
      display: flex;
      align-items: center;
      justify-content: center;

      .tag-label {
        color: var(--color-text-3);
        margin-right: 8px;
      }
    }
  }

  .filter-section {
    background-color: var(--color-bg-1);
    padding: 20px;
    border-radius: 4px;
    margin-bottom: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .list-section {
    background-color: var(--color-bg-1);
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

    .list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 16px;
      border-bottom: 1px solid var(--color-border);

      .total-info {
        font-size: 14px;
        color: var(--color-text-2);

        .highlight {
          font-weight: bold;
          color: var(--color-primary);
        }
      }
    }

    .resource-list {
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

    .pagination {
      margin-top: 16px;
      display: flex;
      justify-content: center;
    }
  }
}
</style> 