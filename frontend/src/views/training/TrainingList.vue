<template>
  <div class="training-list">
    <div class="search-section">
      <h2>实训项目</h2>
      <div class="search-box">
        <a-input-search
          v-model="filterForm.keyword"
          placeholder="搜索实训项目..."
          search-button
          :style="{ width: '500px' }"
          @search="handleSearch"
        />
      </div>
      <div class="search-tags">
        <span class="tag-label">热门领域：</span>
        <a-space>
          <a-tag v-for="tag in tags" :key="tag" color="blue" checkable @click="handleTagClick(tag)">{{ tag }}</a-tag>
        </a-space>
      </div>
    </div>

    <div class="filter-section">
      <a-form :model="filterForm" layout="inline">
        <a-form-item field="difficulty" label="难度级别">
          <a-select v-model="filterForm.difficulty" placeholder="选择难度" allow-clear style="width: 120px">
            <a-option value="beginner">入门级</a-option>
            <a-option value="intermediate">中级</a-option>
            <a-option value="advanced">高级</a-option>
          </a-select>
        </a-form-item>
        <a-form-item field="duration" label="项目周期">
          <a-select v-model="filterForm.duration" placeholder="选择周期" allow-clear style="width: 120px">
            <a-option value="short">短期 (1-2周)</a-option>
            <a-option value="medium">中期 (3-4周)</a-option>
            <a-option value="long">长期 (5周以上)</a-option>
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
          共找到 <span class="highlight">{{ total }}</span> 个实训项目
        </div>
        <div class="sort-options">
          <a-radio-group v-model="sortBy" type="button" size="small">
            <a-radio value="createTime">最新发布</a-radio>
            <a-radio value="popularity">最受欢迎</a-radio>
            <a-radio value="difficulty">难度排序</a-radio>
          </a-radio-group>
        </div>
      </div>

      <a-spin :loading="loading">
        <a-empty v-if="projects.length === 0" />
        <div v-else class="project-grid">
          <a-card 
            v-for="project in projects" 
            :key="project.id" 
            class="project-card" 
            :bordered="false"
            @click="viewDetail(project.id)"
          >
            <div class="project-cover">
              <img :src="project.coverImage || defaultCover" alt="项目封面">
              <div class="project-difficulty" :class="getDifficultyClass(project.difficulty)">
                {{ getDifficultyText(project.difficulty) }}
              </div>
            </div>
            <div class="project-content">
              <h3 class="project-title">{{ project.title }}</h3>
              <p class="project-description">{{ project.description }}</p>
              <div class="project-meta">
                <span><icon-team /> {{ project.teamCount }} 个团队</span>
                <span><icon-clock-circle /> {{ project.duration }}</span>
              </div>
              <div class="project-tags">
                <a-tag v-for="tag in project.tags" :key="tag" size="small">{{ tag }}</a-tag>
              </div>
              <div class="project-footer">
                <span class="project-author">{{ project.teacher.name }}</span>
                <a-button type="primary" size="small" @click.stop="viewDetail(project.id)">查看详情</a-button>
              </div>
            </div>
          </a-card>
        </div>
        
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
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getTrainingList } from '@/apis/training-api';
import { Message } from '@arco-design/web-vue';
import useUserStore from '@/sotre/user-store';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 筛选表单
const filterForm = reactive({
  keyword: '',
  difficulty: '',
  duration: ''
});

// 标签列表
const tags = ref(['Web开发', '移动应用', '数据分析', '人工智能', '游戏开发', '云计算']);

// 排序方式
const sortBy = ref('createTime');

// 分页相关
const current = ref(1);
const pageSize = ref(9);
const total = ref(0);

// 项目列表
const projects = ref([]);
const loading = ref(false);

// 默认封面图
const defaultCover = ref('https://via.placeholder.com/300x200');

// 监听路由查询参数变化
watch(() => route.query, (query) => {
  if (query.keyword) {
    filterForm.keyword = query.keyword;
    handleSearch();
  }
}, { immediate: true });

// 监听排序方式变化
watch(sortBy, () => {
  fetchProjects();
});

// 获取难度级别文本
const getDifficultyText = (difficulty) => {
  const difficultyMap = {
    'beginner': '入门级',
    'intermediate': '中级',
    'advanced': '高级'
  };
  return difficultyMap[difficulty] || '未知';
};

// 获取难度级别样式类
const getDifficultyClass = (difficulty) => {
  return `difficulty-${difficulty}`;
};

// 处理搜索
const handleSearch = () => {
  current.value = 1;
  fetchProjects();
};

// 处理标签点击
const handleTagClick = (tag) => {
  filterForm.keyword = tag;
  handleSearch();
};

// 重置筛选条件
const resetFilters = () => {
  filterForm.keyword = '';
  filterForm.difficulty = '';
  filterForm.duration = '';
  handleSearch();
};

// 处理页码变化
const handlePageChange = () => {
  fetchProjects();
};

// 查看项目详情
const viewDetail = (id) => {
  router.push({
    name: 'training-detail',
    params: { id }
  });
};

// 获取项目列表
const fetchProjects = async () => {
  loading.value = true;
  try {
    // 构建查询参数
    const params = {
      page: current.value,
      pageSize: pageSize.value,
      sort: sortBy.value,
      order: 'desc'
    };

    if (filterForm.keyword) {
      params.keyword = filterForm.keyword;
    }

    if (filterForm.difficulty) {
      params.difficulty = filterForm.difficulty;
    }

    if (filterForm.duration) {
      params.duration = filterForm.duration;
    }

    // 调用API获取项目列表
    const res = await getTrainingList(params);
    
    if (res && res.data && res.data.data) {
      projects.value = res.data.data.list || [];
      total.value = res.data.data.total || 0;
    } else {
      // 模拟数据（开发阶段使用）
      projects.value = [
        {
          id: '1',
          title: '在线教育平台开发',
          description: '开发一个功能完善的在线教育平台，包括课程管理、用户认证、视频播放等功能',
          coverImage: 'https://via.placeholder.com/300x200?text=Education+Platform',
          teacher: { id: '101', name: '张教授' },
          difficulty: 'intermediate',
          duration: '4周',
          teamCount: 8,
          tags: ['Web开发', 'Vue.js', '前端']
        },
        {
          id: '2',
          title: '移动端健康监测App',
          description: '设计并实现一个移动应用，用于健康数据监测和分析，支持多种健康指标记录',
          coverImage: 'https://via.placeholder.com/300x200?text=Health+App',
          teacher: { id: '102', name: '李老师' },
          difficulty: 'advanced',
          duration: '6周',
          teamCount: 5,
          tags: ['移动应用', 'React Native', '健康科技']
        },
        {
          id: '3',
          title: '数据可视化分析工具',
          description: '开发一个数据可视化工具，能够导入各种格式的数据并生成直观的图表和报告',
          coverImage: 'https://via.placeholder.com/300x200?text=Data+Visualization',
          teacher: { id: '103', name: '王讲师' },
          difficulty: 'beginner',
          duration: '3周',
          teamCount: 12,
          tags: ['数据分析', '可视化', 'JavaScript']
        },
        {
          id: '4',
          title: '智能聊天机器人',
          description: '基于自然语言处理技术，开发一个能够理解和回应用户问题的智能聊天机器人',
          coverImage: 'https://via.placeholder.com/300x200?text=Chatbot',
          teacher: { id: '104', name: '赵教授' },
          difficulty: 'advanced',
          duration: '8周',
          teamCount: 6,
          tags: ['人工智能', 'NLP', 'Python']
        },
        {
          id: '5',
          title: '2D横版游戏开发',
          description: '使用Unity引擎开发一个2D横版动作游戏，包括关卡设计、角色动画和游戏机制',
          coverImage: 'https://via.placeholder.com/300x200?text=2D+Game',
          teacher: { id: '105', name: '刘老师' },
          difficulty: 'intermediate',
          duration: '5周',
          teamCount: 9,
          tags: ['游戏开发', 'Unity', 'C#']
        },
        {
          id: '6',
          title: '云端文件存储系统',
          description: '设计并实现一个基于云服务的文件存储系统，支持文件上传、下载和共享功能',
          coverImage: 'https://via.placeholder.com/300x200?text=Cloud+Storage',
          teacher: { id: '106', name: '陈讲师' },
          difficulty: 'intermediate',
          duration: '4周',
          teamCount: 7,
          tags: ['云计算', '后端开发', 'Node.js']
        }
      ];
      total.value = projects.value.length;
    }
  } catch (error) {
    console.error('获取项目列表失败', error);
    Message.error('获取项目列表失败');
    projects.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchProjects();
});
</script>

<style lang="less" scoped>
.training-list {
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
      text-align: center;
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

    .project-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;
      margin-bottom: 20px;
    }

    .project-card {
      cursor: pointer;
      transition: transform 0.3s, box-shadow 0.3s;
      height: 100%;
      
      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
      }
      
      .project-cover {
        position: relative;
        height: 160px;
        overflow: hidden;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        .project-difficulty {
          position: absolute;
          top: 10px;
          right: 10px;
          padding: 2px 8px;
          border-radius: 12px;
          font-size: 12px;
          color: white;
          
          &.difficulty-beginner {
            background-color: #52c41a;
          }
          
          &.difficulty-intermediate {
            background-color: #1890ff;
          }
          
          &.difficulty-advanced {
            background-color: #ff4d4f;
          }
        }
      }
      
      .project-content {
        padding: 16px;
        
        .project-title {
          font-size: 16px;
          font-weight: 600;
          margin-bottom: 8px;
          color: var(--color-text-1);
          display: -webkit-box;
          -webkit-line-clamp: 1;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .project-description {
          font-size: 14px;
          color: var(--color-text-3);
          margin-bottom: 12px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          height: 40px;
        }
        
        .project-meta {
          display: flex;
          justify-content: space-between;
          margin-bottom: 12px;
          font-size: 12px;
          color: var(--color-text-3);
          
          span {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
        
        .project-tags {
          margin-bottom: 12px;
          min-height: 24px;
        }
        
        .project-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .project-author {
            font-size: 14px;
            color: var(--color-text-2);
          }
        }
      }
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}
</style> 