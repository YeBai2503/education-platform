<template>
  <div class="my-training">
    <div class="section-header">
      <h2>我的实训项目</h2>
    </div>

    <a-tabs default-active-key="participating">
      <a-tab-pane key="participating" title="参与中的项目">
        <a-spin :loading="loadingParticipating">
          <a-empty v-if="participatingProjects.length === 0" description="暂无参与中的项目" />
          <div v-else class="project-grid">
            <a-card 
              v-for="project in participatingProjects" 
              :key="project.id" 
              class="project-card" 
              :bordered="false"
              @click="viewDetail(project.id)"
            >
              <div class="project-cover">
                <img :src="project.coverImage || defaultCover" alt="项目封面">
                <div class="project-progress">
                  <a-progress :percent="project.progress" size="small" />
                </div>
              </div>
              <div class="project-content">
                <h3 class="project-title">{{ project.title }}</h3>
                <div class="project-meta">
                  <span><icon-user /> {{ project.creator.name }}</span>
                  <span><icon-calendar /> {{ project.endTime }} 截止</span>
                </div>
                <div class="project-team">
                  <span class="team-label">我的团队：</span>
                  <span class="team-name">{{ project.team.name }}</span>
                </div>
                <div class="project-actions">
                  <a-button type="primary" size="small" @click.stop="viewTeam(project.team.id)">团队空间</a-button>
                  <a-button type="outline" size="small" @click.stop="viewDetail(project.id)">项目详情</a-button>
                </div>
              </div>
            </a-card>
          </div>
        </a-spin>
      </a-tab-pane>
      
      <a-tab-pane key="created" title="我创建的项目">
        <a-spin :loading="loadingCreated">
          <div class="actions-bar">
            <a-button type="primary" @click="createProject">
              <template #icon><icon-plus /></template>
              创建新项目
            </a-button>
          </div>
          
          <a-empty v-if="createdProjects.length === 0" description="暂无创建的项目" />
          <a-table 
            v-else
            :columns="createdColumns" 
            :data="createdProjects" 
            :pagination="{ pageSize: 10 }"
          >
            <template #title="{ record }">
              <a @click="viewDetail(record.id)">{{ record.title }}</a>
            </template>
            <template #status="{ record }">
              <a-tag :color="getStatusColor(record.status)">{{ getStatusText(record.status) }}</a-tag>
            </template>
            <template #teamCount="{ record }">
              {{ record.teamCount }} 个团队
            </template>
            <template #operations="{ record }">
              <a-space>
                <a-button type="text" size="small" @click="viewDetail(record.id)">查看</a-button>
                <a-button type="text" size="small" @click="editProject(record.id)">编辑</a-button>
                <a-button type="text" size="small" status="danger" @click="deleteProject(record.id)">删除</a-button>
              </a-space>
            </template>
          </a-table>
        </a-spin>
      </a-tab-pane>
      
      <a-tab-pane key="completed" title="已完成的项目">
        <a-spin :loading="loadingCompleted">
          <a-empty v-if="completedProjects.length === 0" description="暂无已完成的项目" />
          <div v-else class="project-grid">
            <a-card 
              v-for="project in completedProjects" 
              :key="project.id" 
              class="project-card completed-card" 
              :bordered="false"
              @click="viewDetail(project.id)"
            >
              <div class="project-cover">
                <img :src="project.coverImage || defaultCover" alt="项目封面">
                <div class="project-status">已完成</div>
              </div>
              <div class="project-content">
                <h3 class="project-title">{{ project.title }}</h3>
                <div class="project-meta">
                  <span><icon-user /> {{ project.creator.name }}</span>
                  <span><icon-calendar /> {{ project.completedTime }} 完成</span>
                </div>
                <div class="project-team">
                  <span class="team-label">我的团队：</span>
                  <span class="team-name">{{ project.team.name }}</span>
                </div>
                <div class="project-grade" v-if="project.grade">
                  <span class="grade-label">最终评分：</span>
                  <span class="grade-value">{{ project.grade }}</span>
                </div>
                <div class="project-actions">
                  <a-button type="outline" size="small" @click.stop="viewDetail(project.id)">查看详情</a-button>
                </div>
              </div>
            </a-card>
          </div>
        </a-spin>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getMyTrainingList } from '@/apis/training-api';
import { Message, Modal } from '@arco-design/web-vue';
import useUserStore from '@/sotre/user-store';

const router = useRouter();
const userStore = useUserStore();

// 默认封面图
const defaultCover = ref('https://via.placeholder.com/300x200');

// 参与中的项目
const participatingProjects = ref([]);
const loadingParticipating = ref(false);

// 我创建的项目
const createdProjects = ref([]);
const loadingCreated = ref(false);

// 已完成的项目
const completedProjects = ref([]);
const loadingCompleted = ref(false);

// 创建的项目表格列定义
const createdColumns = [
  {
    title: '项目名称',
    dataIndex: 'title',
    slotName: 'title',
  },
  {
    title: '状态',
    dataIndex: 'status',
    slotName: 'status',
    width: 100,
  },
  {
    title: '团队数',
    slotName: 'teamCount',
    width: 100,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    slotName: 'operations',
    width: 200,
    align: 'center',
  }
];

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'draft': '草稿',
    'active': '进行中',
    'completed': '已结束'
  };
  return statusMap[status] || '未知';
};

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    'draft': 'gray',
    'active': 'green',
    'completed': 'blue'
  };
  return colorMap[status] || 'default';
};

// 查看项目详情
const viewDetail = (id) => {
  router.push({
    name: 'training-detail',
    params: { id }
  });
};

// 查看团队
const viewTeam = (teamId) => {
  router.push({
    name: 'team-management',
    params: { id: teamId }
  });
};

// 创建新项目
const createProject = () => {
  router.push({
    name: 'training-edit'
  });
};

// 编辑项目
const editProject = (id) => {
  router.push({
    name: 'training-edit',
    params: { id }
  });
};

// 删除项目
const deleteProject = (id) => {
  Modal.warning({
    title: '确认删除',
    content: '确定要删除此项目吗？删除后将无法恢复。',
    okText: '确定删除',
    cancelText: '取消',
    onOk: async () => {
      try {
        // await deleteTraining(id);
        Message.success('删除成功');
        // 重新加载项目列表
        fetchCreatedProjects();
      } catch (error) {
        console.error('删除项目失败', error);
        Message.error('删除项目失败');
      }
    }
  });
};

// 获取参与中的项目
const fetchParticipatingProjects = async () => {
  loadingParticipating.value = true;
  try {
    const res = await getMyTrainingList({ status: 'active' });
    
    if (res && res.data && res.data.data) {
      participatingProjects.value = res.data.data || [];
    } else {
      // 模拟数据（开发阶段使用）
      participatingProjects.value = [
        {
          id: '1',
          title: '在线教育平台开发',
          coverImage: 'https://via.placeholder.com/300x200?text=Education+Platform',
          creator: { id: '101', name: '张教授' },
          endTime: '2023-07-15',
          progress: 65,
          team: { id: '201', name: '代码先锋队' }
        },
        {
          id: '2',
          title: '移动端健康监测App',
          coverImage: 'https://via.placeholder.com/300x200?text=Health+App',
          creator: { id: '102', name: '李老师' },
          endTime: '2023-08-20',
          progress: 30,
          team: { id: '202', name: '健康科技小组' }
        }
      ];
    }
  } catch (error) {
    console.error('获取参与中的项目失败', error);
    Message.error('获取参与中的项目失败');
    participatingProjects.value = [];
  } finally {
    loadingParticipating.value = false;
  }
};

// 获取我创建的项目
const fetchCreatedProjects = async () => {
  loadingCreated.value = true;
  try {
    const res = await getMyTrainingList({ created: true });
    
    if (res && res.data && res.data.data) {
      createdProjects.value = res.data.data || [];
    } else {
      // 模拟数据（开发阶段使用）
      createdProjects.value = [
        {
          id: '1',
          title: '在线教育平台开发',
          status: 'active',
          teamCount: 8,
          createTime: '2023-05-20 14:30:25'
        },
        {
          id: '3',
          title: '数据可视化分析工具',
          status: 'draft',
          teamCount: 0,
          createTime: '2023-06-10 09:15:36'
        },
        {
          id: '5',
          title: '2D横版游戏开发',
          status: 'completed',
          teamCount: 9,
          createTime: '2023-03-05 11:20:48'
        }
      ];
    }
  } catch (error) {
    console.error('获取创建的项目失败', error);
    Message.error('获取创建的项目失败');
    createdProjects.value = [];
  } finally {
    loadingCreated.value = false;
  }
};

// 获取已完成的项目
const fetchCompletedProjects = async () => {
  loadingCompleted.value = true;
  try {
    const res = await getMyTrainingList({ status: 'completed' });
    
    if (res && res.data && res.data.data) {
      completedProjects.value = res.data.data || [];
    } else {
      // 模拟数据（开发阶段使用）
      completedProjects.value = [
        {
          id: '4',
          title: '智能聊天机器人',
          coverImage: 'https://via.placeholder.com/300x200?text=Chatbot',
          creator: { id: '104', name: '赵教授' },
          completedTime: '2023-04-10',
          team: { id: '204', name: 'AI先锋队' },
          grade: 92
        }
      ];
    }
  } catch (error) {
    console.error('获取已完成的项目失败', error);
    Message.error('获取已完成的项目失败');
    completedProjects.value = [];
  } finally {
    loadingCompleted.value = false;
  }
};

onMounted(() => {
  // 确保用户信息已加载
  if (!userStore.userInfo) {
    userStore.getUserInfo().then(() => {
      fetchParticipatingProjects();
      fetchCreatedProjects();
      fetchCompletedProjects();
    });
  } else {
    fetchParticipatingProjects();
    fetchCreatedProjects();
    fetchCompletedProjects();
  }
});
</script>

<style lang="less" scoped>
.my-training {
  .section-header {
    margin-bottom: 24px;
    
    h2 {
      font-size: 20px;
      font-weight: 600;
      color: var(--color-text-1);
    }
  }
  
  .actions-bar {
    margin-bottom: 16px;
    display: flex;
    justify-content: flex-end;
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
    
    &.completed-card {
      opacity: 0.8;
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
      
      .project-progress {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        padding: 8px;
        background-color: rgba(0, 0, 0, 0.6);
      }
      
      .project-status {
        position: absolute;
        top: 10px;
        right: 10px;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        color: white;
        background-color: var(--color-success-6);
      }
    }
    
    .project-content {
      padding: 16px;
      
      .project-title {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 12px;
        color: var(--color-text-1);
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
      
      .project-team {
        margin-bottom: 12px;
        font-size: 14px;
        
        .team-label {
          color: var(--color-text-3);
        }
        
        .team-name {
          color: var(--color-text-1);
          font-weight: 500;
        }
      }
      
      .project-grade {
        margin-bottom: 12px;
        font-size: 14px;
        
        .grade-label {
          color: var(--color-text-3);
        }
        
        .grade-value {
          color: var(--color-success-6);
          font-weight: 600;
          font-size: 16px;
        }
      }
      
      .project-actions {
        display: flex;
        justify-content: space-between;
        gap: 8px;
      }
    }
  }
}
</style> 