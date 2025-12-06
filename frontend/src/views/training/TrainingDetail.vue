<template>
  <div class="training-detail">
    <a-spin :loading="loading">
      <a-page-header
        :title="project.title"
        @back="goBack"
      >
        <template #extra>
          <a-space>
            <a-button v-if="isCreator" type="primary" @click="editProject">
              <template #icon><icon-edit /></template>
              编辑项目
            </a-button>
            <a-button v-if="isCreator" @click="manageTeams">
              <template #icon><icon-user-group /></template>
              团队管理
            </a-button>
            <a-button v-if="isCreator" @click="manageTasks">
              <template #icon><icon-calendar /></template>
              任务管理
            </a-button>
            <a-button v-if="!isJoined && project.status === 'active'" type="primary" @click="joinProject">
              <template #icon><icon-plus /></template>
              加入项目
            </a-button>
          </a-space>
        </template>
      </a-page-header>

      <div class="detail-content">
        <div class="detail-main">
          <div class="project-info">
            <div class="project-header">
              <div class="project-cover">
                <img :src="project.coverImage || defaultCover" alt="项目封面">
              </div>
              <div class="project-meta">
                <div class="meta-item">
                  <span class="label">项目状态</span>
                  <a-tag :color="getStatusColor(project.status)">{{ getStatusText(project.status) }}</a-tag>
                </div>
                <div class="meta-item">
                  <span class="label">难度级别</span>
                  <a-tag :color="getDifficultyColor(project.difficulty)">{{ getDifficultyText(project.difficulty) }}</a-tag>
                </div>
                <div class="meta-item">
                  <span class="label">项目周期</span>
                  <span>{{ project.duration }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">创建时间</span>
                  <span>{{ project.createTime }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">创建者</span>
                  <span>{{ project.creator?.name }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">参与团队</span>
                  <span>{{ project.teamCount }} 个团队</span>
                </div>
                <div class="meta-item tags">
                  <span class="label">项目标签</span>
                  <div>
                    <a-tag v-for="tag in project.tags" :key="tag" size="small">{{ tag }}</a-tag>
                  </div>
                </div>
              </div>
            </div>

            <a-divider />

            <div class="project-description">
              <h3>项目介绍</h3>
              <div class="description-content" v-html="project.description"></div>
            </div>

            <a-divider />

            <div class="project-objectives">
              <h3>学习目标</h3>
              <a-list>
                <a-list-item v-for="(objective, index) in project.objectives" :key="index">
                  {{ objective }}
                </a-list-item>
              </a-list>
            </div>

            <a-divider />

            <div class="project-requirements">
              <h3>项目要求</h3>
              <div class="requirements-content" v-html="project.requirements"></div>
            </div>
          </div>

          <a-divider />

          <div class="project-tasks">
            <h3>任务安排</h3>
            <a-empty v-if="!project.tasks || project.tasks.length === 0" description="暂无任务安排" />
            <a-timeline v-else>
              <a-timeline-item 
                v-for="task in project.tasks" 
                :key="task.id"
                :dot-color="getTaskStatusColor(task.status)"
              >
                <div class="task-item">
                  <div class="task-header">
                    <h4>{{ task.title }}</h4>
                    <a-tag :color="getTaskStatusColor(task.status)">{{ getTaskStatusText(task.status) }}</a-tag>
                  </div>
                  <p class="task-time">截止时间：{{ task.deadline }}</p>
                  <p class="task-description">{{ task.description }}</p>
                  <div class="task-actions" v-if="isJoined && task.status !== 'completed'">
                    <a-button type="primary" size="small" @click="submitTask(task.id)">提交成果</a-button>
                  </div>
                </div>
              </a-timeline-item>
            </a-timeline>
          </div>
        </div>

        <div class="detail-sidebar">
          <div class="sidebar-section">
            <h3>我的团队</h3>
            <div v-if="isJoined">
              <a-card :bordered="false" class="team-card">
                <h4>{{ myTeam.name }}</h4>
                <p>{{ myTeam.members.length }} 名成员</p>
                <a-divider />
                <div class="team-members">
                  <a-avatar-group :size="32" :max-count="5">
                    <a-avatar v-for="member in myTeam.members" :key="member.id">
                      {{ member.name.charAt(0) }}
                    </a-avatar>
                  </a-avatar-group>
                </div>
                <a-button type="primary" long style="margin-top: 16px" @click="goToTeamSpace">
                  进入团队空间
                </a-button>
              </a-card>
            </div>
            <a-empty v-else description="您尚未加入任何团队" />
          </div>

          <div class="sidebar-section">
            <h3>项目进度</h3>
            <div class="progress-wrapper">
              <a-progress
                :percent="project.progress || 0"
                :stroke-color="{ from: '#108ee9', to: '#87d068' }"
                style="margin-bottom: 16px"
              />
              <div class="progress-stats">
                <div class="stat-item">
                  <span class="label">已完成任务</span>
                  <span class="value">{{ completedTasksCount }}/{{ totalTasksCount }}</span>
                </div>
                <div class="stat-item">
                  <span class="label">剩余天数</span>
                  <span class="value">{{ remainingDays }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="sidebar-section">
            <h3>最近动态</h3>
            <a-empty v-if="!project.activities || project.activities.length === 0" description="暂无动态" />
            <a-list v-else size="small">
              <a-list-item v-for="activity in project.activities" :key="activity.id">
                <div class="activity-item">
                  <span class="activity-time">{{ activity.time }}</span>
                  <span class="activity-content">{{ activity.content }}</span>
                </div>
              </a-list-item>
            </a-list>
          </div>
        </div>
      </div>
    </a-spin>

    <!-- 加入项目弹窗 -->
    <a-modal
      v-model:visible="joinModalVisible"
      title="加入项目"
      @ok="handleJoin"
      @cancel="joinModalVisible = false"
      :ok-button-props="{ disabled: !selectedTeam && !createTeamName }"
    >
      <a-form :model="joinForm" layout="vertical">
        <a-form-item field="joinType" label="加入方式">
          <a-radio-group v-model="joinForm.joinType">
            <a-radio value="existing">加入已有团队</a-radio>
            <a-radio value="create">创建新团队</a-radio>
          </a-radio-group>
        </a-form-item>

        <template v-if="joinForm.joinType === 'existing'">
          <a-form-item field="teamId" label="选择团队">
            <a-select
              v-model="joinForm.teamId"
              placeholder="请选择要加入的团队"
              allow-clear
            >
              <a-option
                v-for="team in availableTeams"
                :key="team.id"
                :value="team.id"
              >
                {{ team.name }} ({{ team.memberCount }}/{{ team.maxMembers }} 人)
              </a-option>
            </a-select>
          </a-form-item>
        </template>

        <template v-else>
          <a-form-item field="teamName" label="团队名称">
            <a-input v-model="joinForm.teamName" placeholder="请输入团队名称" />
          </a-form-item>
          <a-form-item field="teamDescription" label="团队简介">
            <a-textarea v-model="joinForm.teamDescription" placeholder="请简要介绍团队" />
          </a-form-item>
        </template>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getTrainingDetail, joinTraining } from '@/apis/training-api';
import { Message } from '@arco-design/web-vue';
import useUserStore from '@/sotre/user-store';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo);

// 判断用户是否为项目创建者
const isCreator = computed(() => {
  if (!userStore.userInfo || !project.value) return false;
  return project.value.createdBy === userStore.userInfo.id;
});

// 默认封面图
const defaultCover = ref('https://via.placeholder.com/800x400');

// 项目数据
const project = ref({
  title: '',
  status: '',
  difficulty: '',
  duration: '',
  createTime: '',
  teacher: {},
  teamCount: 0,
  tags: [],
  description: '',
  objectives: [],
  requirements: '',
  tasks: [],
  progress: 0,
  activities: []
});

// 加载状态
const loading = ref(true);

// 是否已加入项目
const isJoined = ref(false);

// 我的团队
const myTeam = ref({
  id: '',
  name: '',
  members: []
});

// 可加入的团队
const availableTeams = ref([]);

// 加入项目弹窗
const joinModalVisible = ref(false);
const joinForm = ref({
  joinType: 'existing',
  teamId: '',
  teamName: '',
  teamDescription: ''
});

// 已完成任务数量
const completedTasksCount = computed(() => {
  if (!project.value.tasks) return 0;
  return project.value.tasks.filter(task => task.status === 'completed').length;
});

// 总任务数量
const totalTasksCount = computed(() => {
  if (!project.value.tasks) return 0;
  return project.value.tasks.length;
});

// 剩余天数
const remainingDays = computed(() => {
  if (!project.value.endTime) return 0;
  const endDate = new Date(project.value.endTime);
  const today = new Date();
  const diffTime = endDate - today;
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays > 0 ? diffDays : 0;
});

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

// 获取难度文本
const getDifficultyText = (difficulty) => {
  const difficultyMap = {
    'beginner': '入门级',
    'intermediate': '中级',
    'advanced': '高级'
  };
  return difficultyMap[difficulty] || '未知';
};

// 获取难度颜色
const getDifficultyColor = (difficulty) => {
  const colorMap = {
    'beginner': 'green',
    'intermediate': 'blue',
    'advanced': 'red'
  };
  return colorMap[difficulty] || 'default';
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

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 编辑项目
const editProject = () => {
  router.push({
    name: 'training-edit',
    params: { id: route.params.id }
  });
};

// 团队管理
const manageTeams = () => {
  router.push({
    name: 'team-management',
    params: { id: route.params.id }
  });
};

// 任务管理
const manageTasks = () => {
  router.push({
    name: 'task-management',
    params: { id: route.params.id }
  });
};

// 打开加入项目弹窗
const joinProject = () => {
  joinModalVisible.value = true;
};

// 处理加入项目
const handleJoin = async () => {
  try {
    const data = {
      joinType: joinForm.value.joinType
    };

    if (joinForm.value.joinType === 'existing') {
      data.teamId = joinForm.value.teamId;
    } else {
      data.teamName = joinForm.value.teamName;
      data.teamDescription = joinForm.value.teamDescription;
    }

    await joinTraining(route.params.id, data);
    Message.success('加入项目成功');
    joinModalVisible.value = false;
    
    // 重新加载项目详情
    fetchProjectDetail();
  } catch (error) {
    console.error('加入项目失败', error);
    Message.error('加入项目失败');
  }
};

// 进入团队空间
const goToTeamSpace = () => {
  router.push({
    name: 'team-management',
    params: { id: myTeam.value.id }
  });
};

// 提交任务
const submitTask = (taskId) => {
  router.push({
    name: 'submission-management',
    params: { id: taskId }
  });
};

// 获取项目详情
const fetchProjectDetail = async () => {
  loading.value = true;
  try {
    const res = await getTrainingDetail(route.params.id);
    
    if (res && res.data && res.data.data) {
      project.value = res.data.data;
      
      // 检查用户是否已加入项目
      if (project.value.myTeam) {
        isJoined.value = true;
        myTeam.value = project.value.myTeam;
      }
      
      // 获取可加入的团队
      availableTeams.value = project.value.availableTeams || [];
    } else {
      // 模拟数据（开发阶段使用）
      project.value = {
        id: route.params.id,
        title: '在线教育平台开发',
        status: 'active',
        difficulty: 'intermediate',
        duration: '4周',
        createTime: '2023-05-20',
        endTime: '2023-07-15',
        createdBy: '101',
        creator: { id: '101', name: '张教授' },
        teamCount: 8,
        tags: ['Web开发', 'Vue.js', '前端'],
        coverImage: 'https://via.placeholder.com/800x400?text=Education+Platform',
        description: `<p>本项目旨在开发一个功能完善的在线教育平台，包括课程管理、用户认证、视频播放等功能。学生将在实际项目中应用所学的前端开发技术，提升实践能力。</p>
        <p>平台需要实现响应式设计，确保在不同设备上都有良好的用户体验。同时，需要考虑性能优化和安全性问题。</p>`,
        objectives: [
          '掌握Vue.js框架的实际应用',
          '学习前后端交互和API设计',
          '理解用户认证和权限管理',
          '掌握组件化开发和状态管理',
          '学习项目部署和性能优化'
        ],
        requirements: `<p><strong>技术栈要求：</strong></p>
        <ul>
          <li>前端框架：Vue.js 3.x</li>
          <li>UI组件库：Element Plus或Arco Design</li>
          <li>状态管理：Pinia</li>
          <li>路由管理：Vue Router</li>
          <li>HTTP客户端：Axios</li>
        </ul>
        <p><strong>功能要求：</strong></p>
        <ul>
          <li>用户注册、登录和个人中心</li>
          <li>课程浏览、搜索和详情页</li>
          <li>视频播放器和进度保存</li>
          <li>作业提交和评分系统</li>
          <li>消息通知和互动功能</li>
        </ul>`,
        tasks: [
          {
            id: '101',
            title: '需求分析与原型设计',
            description: '分析项目需求，设计用户界面原型和功能流程',
            deadline: '2023-06-01',
            status: 'completed'
          },
          {
            id: '102',
            title: '项目架构搭建',
            description: '搭建Vue项目框架，配置路由、状态管理和基础组件',
            deadline: '2023-06-10',
            status: 'completed'
          },
          {
            id: '103',
            title: '用户认证模块开发',
            description: '实现用户注册、登录和个人中心功能',
            deadline: '2023-06-20',
            status: 'in_progress'
          },
          {
            id: '104',
            title: '课程管理模块开发',
            description: '实现课程列表、详情和搜索功能',
            deadline: '2023-06-30',
            status: 'pending'
          },
          {
            id: '105',
            title: '视频播放模块开发',
            description: '实现视频播放器和进度保存功能',
            deadline: '2023-07-10',
            status: 'pending'
          }
        ],
        progress: 40,
        activities: [
          {
            id: '1',
            time: '2023-06-10 14:30',
            content: '团队"代码先锋队"完成了"项目架构搭建"任务'
          },
          {
            id: '2',
            time: '2023-06-05 09:15',
            content: '团队"Web精英"加入了项目'
          },
          {
            id: '3',
            time: '2023-06-01 16:45',
            content: '团队"代码先锋队"完成了"需求分析与原型设计"任务'
          }
        ],
        myTeam: {
          id: '201',
          name: '代码先锋队',
          members: [
            { id: '1001', name: '张三' },
            { id: '1002', name: '李四' },
            { id: '1003', name: '王五' },
            { id: '1004', name: '赵六' }
          ]
        },
        availableTeams: [
          { id: '202', name: 'Web精英', memberCount: 3, maxMembers: 5 },
          { id: '203', name: '前端战队', memberCount: 2, maxMembers: 5 }
        ]
      };
      
      // 检查用户是否已加入项目
      if (project.value.myTeam) {
        isJoined.value = true;
        myTeam.value = project.value.myTeam;
      }
      
      // 获取可加入的团队
      availableTeams.value = project.value.availableTeams || [];
    }
  } catch (error) {
    console.error('获取项目详情失败', error);
    Message.error('获取项目详情失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  // 确保用户信息已加载
  if (!userStore.userInfo) {
    userStore.getUserInfo().then(() => {
      fetchProjectDetail();
    });
  } else {
    fetchProjectDetail();
  }
});
</script>

<style lang="less" scoped>
.training-detail {
  .detail-content {
    display: flex;
    gap: 24px;
    margin-top: 24px;
    
    .detail-main {
      flex: 1;
      min-width: 0;
      
      .project-header {
        display: flex;
        gap: 24px;
        margin-bottom: 24px;
        
        .project-cover {
          width: 300px;
          height: 200px;
          overflow: hidden;
          border-radius: 8px;
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
        
        .project-meta {
          flex: 1;
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 16px;
          
          .meta-item {
            display: flex;
            flex-direction: column;
            
            .label {
              color: var(--color-text-3);
              margin-bottom: 4px;
              font-size: 14px;
            }
            
            &.tags {
              grid-column: span 2;
            }
          }
        }
      }
      
      h3 {
        font-size: 18px;
        font-weight: 600;
        margin-bottom: 16px;
        color: var(--color-text-1);
      }
      
      .project-description,
      .project-objectives,
      .project-requirements {
        margin-bottom: 24px;
        
        .description-content,
        .requirements-content {
          color: var(--color-text-2);
          line-height: 1.6;
          
          :deep(p) {
            margin-bottom: 12px;
          }
          
          :deep(ul) {
            padding-left: 24px;
            margin-bottom: 12px;
          }
        }
      }
      
      .project-tasks {
        .task-item {
          margin-bottom: 16px;
          
          .task-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
            
            h4 {
              font-weight: 500;
              color: var(--color-text-1);
              margin: 0;
            }
          }
          
          .task-time {
            font-size: 12px;
            color: var(--color-text-3);
            margin-bottom: 8px;
          }
          
          .task-description {
            color: var(--color-text-2);
            margin-bottom: 8px;
          }
          
          .task-actions {
            margin-top: 8px;
          }
        }
      }
    }
    
    .detail-sidebar {
      width: 300px;
      flex-shrink: 0;
      
      .sidebar-section {
        background-color: var(--color-bg-1);
        padding: 16px;
        border-radius: 8px;
        margin-bottom: 24px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        
        h3 {
          font-size: 16px;
          font-weight: 600;
          margin-bottom: 16px;
          color: var(--color-text-1);
        }
        
        .team-card {
          h4 {
            font-weight: 500;
            color: var(--color-text-1);
            margin-bottom: 8px;
          }
          
          .team-members {
            margin-bottom: 16px;
          }
        }
        
        .progress-wrapper {
          .progress-stats {
            display: flex;
            justify-content: space-between;
            
            .stat-item {
              display: flex;
              flex-direction: column;
              
              .label {
                font-size: 12px;
                color: var(--color-text-3);
                margin-bottom: 4px;
              }
              
              .value {
                font-size: 16px;
                font-weight: 600;
                color: var(--color-text-1);
              }
            }
          }
        }
        
        .activity-item {
          display: flex;
          flex-direction: column;
          
          .activity-time {
            font-size: 12px;
            color: var(--color-text-3);
            margin-bottom: 4px;
          }
          
          .activity-content {
            color: var(--color-text-2);
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .training-detail {
    .detail-content {
      flex-direction: column;
      
      .detail-main {
        .project-header {
          flex-direction: column;
          
          .project-cover {
            width: 100%;
            height: auto;
            aspect-ratio: 16/9;
          }
        }
      }
      
      .detail-sidebar {
        width: 100%;
      }
    }
  }
}
</style>