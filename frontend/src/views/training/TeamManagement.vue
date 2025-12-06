<template>
  <div class="team-management">
    <a-page-header
      title="团队管理"
      @back="goBack"
    />

    <div class="team-content">
      <a-tabs default-active-key="teams">
        <a-tab-pane key="teams" title="团队列表">
          <a-spin :loading="loadingTeams">
            <div class="actions-bar" v-if="isCreator">
              <a-button type="primary" @click="showCreateTeamModal">
                <template #icon><icon-plus /></template>
                创建团队
              </a-button>
            </div>

            <a-empty v-if="teams.length === 0" description="暂无团队" />
            <div v-else class="teams-list">
              <a-card
                v-for="team in teams"
                :key="team.id"
                class="team-card"
                :bordered="false"
              >
                <div class="team-header">
                  <h3>{{ team.name }}</h3>
                  <a-tag color="blue">{{ team.members.length }}/{{ team.maxMembers }} 人</a-tag>
                </div>
                <p class="team-description">{{ team.description || '暂无描述' }}</p>
                <a-divider style="margin: 12px 0" />
                <div class="team-members">
                  <h4>团队成员</h4>
                  <div class="members-list">
                    <a-avatar-group :size="32" :max-count="5">
                      <a-tooltip
                        v-for="member in team.members"
                        :key="member.id"
                        :content="member.name + (member.id === team.leaderId ? ' (队长)' : '')"
                      >
                        <a-avatar>{{ member.name.charAt(0) }}</a-avatar>
                      </a-tooltip>
                    </a-avatar-group>
                  </div>
                </div>
                <div class="team-actions">
                  <a-space>
                    <a-button size="small" @click="viewTeamDetail(team.id)">查看详情</a-button>
                    <a-button
                      v-if="isCreator || userStore.userInfo?.id === team.leaderId"
                      type="primary"
                      size="small"
                      @click="editTeam(team)"
                    >
                      管理团队
                    </a-button>
                  </a-space>
                </div>
              </a-card>
            </div>
          </a-spin>
        </a-tab-pane>

        <a-tab-pane key="applications" title="加入申请" v-if="isCreator">
          <a-spin :loading="loadingApplications">
            <a-empty v-if="applications.length === 0" description="暂无加入申请" />
            <a-table
              v-else
              :columns="applicationColumns"
              :data="applications"
              :pagination="{ pageSize: 10 }"
            >
              <template #user="{ record }">
                <a-space>
                  <a-avatar size="small">{{ record.user.name.charAt(0) }}</a-avatar>
                  <span>{{ record.user.name }}</span>
                </a-space>
              </template>
              <template #team="{ record }">
                {{ record.team.name }}
              </template>
              <template #status="{ record }">
                <a-tag :color="getApplicationStatusColor(record.status)">
                  {{ getApplicationStatusText(record.status) }}
                </a-tag>
              </template>
              <template #createTime="{ record }">
                {{ record.createTime }}
              </template>
              <template #operations="{ record }">
                <a-space v-if="record.status === 'pending'">
                  <a-button type="text" status="success" size="small" @click="handleApplication(record.id, 'approve')">
                    批准
                  </a-button>
                  <a-button type="text" status="danger" size="small" @click="handleApplication(record.id, 'reject')">
                    拒绝
                  </a-button>
                </a-space>
                <span v-else>-</span>
              </template>
            </a-table>
          </a-spin>
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- 创建/编辑团队弹窗 -->
    <a-modal
      v-model:visible="teamModalVisible"
      :title="editingTeam ? '编辑团队' : '创建团队'"
      @ok="handleTeamSubmit"
      @cancel="teamModalVisible = false"
    >
      <a-form :model="teamForm" layout="vertical">
        <a-form-item field="name" label="团队名称" :rules="[{ required: true, message: '请输入团队名称' }]">
          <a-input v-model="teamForm.name" placeholder="请输入团队名称" />
        </a-form-item>

        <a-form-item field="description" label="团队描述">
          <a-textarea
            v-model="teamForm.description"
            placeholder="请输入团队描述"
            :auto-size="{ minRows: 2, maxRows: 5 }"
          />
        </a-form-item>

        <a-form-item field="maxMembers" label="成员上限" :rules="[{ required: true, message: '请设置成员上限' }]">
          <a-input-number v-model="teamForm.maxMembers" :min="1" :max="10" style="width: 100%" />
        </a-form-item>

        <a-form-item v-if="editingTeam" field="leaderId" label="团队队长">
          <a-select v-model="teamForm.leaderId" placeholder="请选择团队队长">
            <a-option
              v-for="member in editingTeam.members"
              :key="member.id"
              :value="member.id"
            >
              {{ member.name }}
            </a-option>
          </a-select>
        </a-form-item>

        <a-divider v-if="editingTeam">团队成员管理</a-divider>

        <div v-if="editingTeam" class="members-management">
          <div v-for="member in editingTeam.members" :key="member.id" class="member-item">
            <a-space>
              <a-avatar>{{ member.name.charAt(0) }}</a-avatar>
              <span>{{ member.name }}</span>
              <a-tag v-if="member.id === teamForm.leaderId" color="blue">队长</a-tag>
            </a-space>
            <a-button
              v-if="member.id !== teamForm.leaderId"
              type="text"
              status="danger"
              size="small"
              @click="removeMember(member.id)"
            >
              移除
            </a-button>
          </div>
        </div>
      </a-form>
    </a-modal>

    <!-- 团队详情弹窗 -->
    <a-modal
      v-model:visible="detailModalVisible"
      title="团队详情"
      @cancel="detailModalVisible = false"
      :footer="false"
    >
      <div v-if="currentTeam" class="team-detail">
        <h3>{{ currentTeam.name }}</h3>
        <p class="description">{{ currentTeam.description || '暂无描述' }}</p>
        
        <a-divider />
        
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="info-item">
            <span class="label">创建时间</span>
            <span>{{ currentTeam.createTime || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">成员数量</span>
            <span>{{ currentTeam.members.length }}/{{ currentTeam.maxMembers }}</span>
          </div>
        </div>
        
        <a-divider />
        
        <div class="detail-section">
          <h4>团队成员</h4>
          <a-list size="small">
            <a-list-item v-for="member in currentTeam.members" :key="member.id">
              <div class="member-info">
                <a-space>
                  <a-avatar>{{ member.name.charAt(0) }}</a-avatar>
                  <span>{{ member.name }}</span>
                </a-space>
                <a-tag v-if="member.id === currentTeam.leaderId" color="blue">队长</a-tag>
              </div>
            </a-list-item>
          </a-list>
        </div>
        
        <a-divider />
        
        <div class="detail-section">
          <h4>团队任务</h4>
          <a-empty v-if="!currentTeam.tasks || currentTeam.tasks.length === 0" description="暂无任务" />
          <a-list v-else size="small">
            <a-list-item v-for="task in currentTeam.tasks" :key="task.id">
              <div class="task-info">
                <div>
                  <div class="task-title">{{ task.title }}</div>
                  <div class="task-deadline">截止日期: {{ task.deadline }}</div>
                </div>
                <a-tag :color="getTaskStatusColor(task.status)">
                  {{ getTaskStatusText(task.status) }}
                </a-tag>
              </div>
            </a-list-item>
          </a-list>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Message, Modal } from '@arco-design/web-vue';
import { getTeamList, manageTeam } from '@/apis/training-api';
import { getTrainingDetail } from '@/apis/training-api';
import useUserStore from '@/sotre/user-store';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 项目ID
const projectId = computed(() => route.params.id);

// 项目信息
const project = ref({});

// 是否为项目创建者
const isCreator = computed(() => {
  if (!userStore.userInfo || !project.value) return false;
  return project.value.createdBy === userStore.userInfo.id;
});

// 团队列表
const teams = ref([]);
const loadingTeams = ref(false);

// 加入申请列表
const applications = ref([]);
const loadingApplications = ref(false);

// 申请列表列定义
const applicationColumns = [
  {
    title: '申请人',
    slotName: 'user',
  },
  {
    title: '申请团队',
    slotName: 'team',
  },
  {
    title: '申请状态',
    slotName: 'status',
    width: 100,
  },
  {
    title: '申请时间',
    slotName: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    slotName: 'operations',
    width: 150,
    align: 'center',
  }
];

// 团队弹窗
const teamModalVisible = ref(false);
const editingTeam = ref(null);
const teamForm = reactive({
  name: '',
  description: '',
  maxMembers: 5,
  leaderId: ''
});

// 团队详情弹窗
const detailModalVisible = ref(false);
const currentTeam = ref(null);

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 获取申请状态文本
const getApplicationStatusText = (status) => {
  const statusMap = {
    'pending': '待审核',
    'approved': '已批准',
    'rejected': '已拒绝'
  };
  return statusMap[status] || '未知';
};

// 获取申请状态颜色
const getApplicationStatusColor = (status) => {
  const colorMap = {
    'pending': 'orange',
    'approved': 'green',
    'rejected': 'red'
  };
  return colorMap[status] || 'default';
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

// 显示创建团队弹窗
const showCreateTeamModal = () => {
  editingTeam.value = null;
  teamForm.name = '';
  teamForm.description = '';
  teamForm.maxMembers = 5;
  teamForm.leaderId = '';
  teamModalVisible.value = true;
};

// 编辑团队
const editTeam = (team) => {
  editingTeam.value = team;
  teamForm.name = team.name;
  teamForm.description = team.description || '';
  teamForm.maxMembers = team.maxMembers;
  teamForm.leaderId = team.leaderId;
  teamModalVisible.value = true;
};

// 查看团队详情
const viewTeamDetail = (teamId) => {
  const team = teams.value.find(t => t.id === teamId);
  if (team) {
    currentTeam.value = team;
    detailModalVisible.value = true;
  }
};

// 移除成员
const removeMember = (memberId) => {
  Modal.warning({
    title: '确认移除',
    content: '确定要移除该成员吗？',
    okText: '确定',
    cancelText: '取消',
    onOk: () => {
      // 在实际API实现中，这里应该调用API移除成员
      // 这里只是模拟移除
      if (editingTeam.value) {
        editingTeam.value.members = editingTeam.value.members.filter(m => m.id !== memberId);
      }
    }
  });
};

// 处理团队表单提交
const handleTeamSubmit = async () => {
  try {
    const data = {
      name: teamForm.name,
      description: teamForm.description,
      maxMembers: teamForm.maxMembers
    };
    
    if (editingTeam.value) {
      data.id = editingTeam.value.id;
      data.leaderId = teamForm.leaderId;
    }
    
    await manageTeam(projectId.value, data);
    
    Message.success(editingTeam.value ? '团队更新成功' : '团队创建成功');
    teamModalVisible.value = false;
    
    // 重新加载团队列表
    fetchTeams();
  } catch (error) {
    console.error('保存团队失败', error);
    Message.error('操作失败');
  }
};

// 处理加入申请
const handleApplication = async (applicationId, action) => {
  try {
    // 这里应该调用API处理申请
    // await handleTeamApplication(applicationId, action);
    
    Message.success(action === 'approve' ? '已批准申请' : '已拒绝申请');
    
    // 更新申请状态（模拟）
    applications.value = applications.value.map(app => {
      if (app.id === applicationId) {
        return {
          ...app,
          status: action === 'approve' ? 'approved' : 'rejected'
        };
      }
      return app;
    });
  } catch (error) {
    console.error('处理申请失败', error);
    Message.error('操作失败');
  }
};

// 获取项目信息
const fetchProjectDetail = async () => {
  try {
    const res = await getTrainingDetail(projectId.value);
    
    if (res && res.data && res.data.data) {
      project.value = res.data.data;
    }
  } catch (error) {
    console.error('获取项目详情失败', error);
    Message.error('获取项目详情失败');
  }
};

// 获取团队列表
const fetchTeams = async () => {
  loadingTeams.value = true;
  try {
    const res = await getTeamList(projectId.value);
    
    if (res && res.data && res.data.data) {
      teams.value = res.data.data;
    } else {
      // 模拟数据（开发阶段使用）
      teams.value = [
        {
          id: '201',
          name: '代码先锋队',
          description: '专注于前端开发的团队',
          maxMembers: 5,
          createTime: '2023-05-25',
          leaderId: '1001',
          members: [
            { id: '1001', name: '张三' },
            { id: '1002', name: '李四' },
            { id: '1003', name: '王五' },
            { id: '1004', name: '赵六' }
          ],
          tasks: [
            {
              id: '101',
              title: '需求分析与原型设计',
              deadline: '2023-06-01',
              status: 'completed'
            },
            {
              id: '102',
              title: '项目架构搭建',
              deadline: '2023-06-10',
              status: 'completed'
            },
            {
              id: '103',
              title: '用户认证模块开发',
              deadline: '2023-06-20',
              status: 'in_progress'
            }
          ]
        },
        {
          id: '202',
          name: 'Web精英',
          description: '全栈开发团队',
          maxMembers: 5,
          createTime: '2023-05-28',
          leaderId: '1005',
          members: [
            { id: '1005', name: '钱七' },
            { id: '1006', name: '孙八' },
            { id: '1007', name: '周九' }
          ],
          tasks: [
            {
              id: '101',
              title: '需求分析与原型设计',
              deadline: '2023-06-01',
              status: 'completed'
            },
            {
              id: '102',
              title: '项目架构搭建',
              deadline: '2023-06-10',
              status: 'in_progress'
            }
          ]
        }
      ];
    }
  } catch (error) {
    console.error('获取团队列表失败', error);
    Message.error('获取团队列表失败');
    teams.value = [];
  } finally {
    loadingTeams.value = false;
  }
};

// 获取加入申请列表
const fetchApplications = async () => {
  if (!isCreator.value) return;
  
  loadingApplications.value = true;
  try {
    // 这里应该调用API获取申请列表
    // const res = await getTeamApplications(projectId.value);
    
    // 模拟数据（开发阶段使用）
    applications.value = [
      {
        id: '301',
        user: { id: '1008', name: '吴十' },
        team: { id: '201', name: '代码先锋队' },
        status: 'pending',
        createTime: '2023-06-05 10:30:25'
      },
      {
        id: '302',
        user: { id: '1009', name: '郑十一' },
        team: { id: '202', name: 'Web精英' },
        status: 'pending',
        createTime: '2023-06-06 14:15:36'
      },
      {
        id: '303',
        user: { id: '1010', name: '王十二' },
        team: { id: '201', name: '代码先锋队' },
        status: 'approved',
        createTime: '2023-06-04 09:20:15'
      }
    ];
  } catch (error) {
    console.error('获取申请列表失败', error);
    Message.error('获取申请列表失败');
    applications.value = [];
  } finally {
    loadingApplications.value = false;
  }
};

onMounted(async () => {
  // 确保用户已登录
  if (!userStore.userInfo) {
    await userStore.getUserInfo();
  }
  
  // 获取项目详情
  await fetchProjectDetail();
  
  // 获取团队列表
  fetchTeams();
  
  // 获取加入申请列表
  fetchApplications();
});
</script>

<style lang="less" scoped>
.team-management {
  .team-content {
    margin-top: 24px;
    background-color: var(--color-bg-1);
    padding: 24px;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
  
  .actions-bar {
    margin-bottom: 16px;
    display: flex;
    justify-content: flex-end;
  }
  
  .teams-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 16px;
    
    .team-card {
      height: 100%;
      
      .team-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        
        h3 {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
        }
      }
      
      .team-description {
        color: var(--color-text-3);
        margin-bottom: 8px;
        min-height: 40px;
      }
      
      .team-members {
        margin-bottom: 16px;
        
        h4 {
          font-size: 14px;
          margin-bottom: 8px;
        }
      }
      
      .team-actions {
        display: flex;
        justify-content: flex-end;
      }
    }
  }
  
  .members-management {
    .member-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px solid var(--color-border);
      
      &:last-child {
        border-bottom: none;
      }
    }
  }
  
  .team-detail {
    h3 {
      font-size: 18px;
      margin-bottom: 8px;
    }
    
    .description {
      color: var(--color-text-2);
    }
    
    .detail-section {
      h4 {
        font-size: 16px;
        margin-bottom: 16px;
      }
      
      .info-item {
        display: flex;
        margin-bottom: 8px;
        
        .label {
          width: 100px;
          color: var(--color-text-3);
        }
      }
      
      .member-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
      }
      
      .task-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
        
        .task-title {
          font-weight: 500;
        }
        
        .task-deadline {
          font-size: 12px;
          color: var(--color-text-3);
        }
      }
    }
  }
}
</style> 