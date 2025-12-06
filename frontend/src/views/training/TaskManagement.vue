<template>
  <div class="task-management">
    <a-page-header
      title="任务管理"
      @back="goBack"
    />

    <div class="task-content">
      <div class="actions-bar" v-if="isCreator">
        <a-button type="primary" @click="showCreateTaskModal">
          <template #icon><icon-plus /></template>
          创建任务
        </a-button>
      </div>

      <a-spin :loading="loading">
        <a-empty v-if="tasks.length === 0" description="暂无任务" />
        <div v-else class="tasks-list">
          <a-collapse accordion>
            <a-collapse-item
              v-for="task in tasks"
              :key="task.id"
              :header="task.title"
            >
              <template #extra>
                <a-tag :color="getTaskStatusColor(task.status)">{{ getTaskStatusText(task.status) }}</a-tag>
              </template>
              <div class="task-detail">
                <div class="task-info">
                  <div class="info-item">
                    <span class="label">任务描述</span>
                    <span>{{ task.description }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">截止日期</span>
                    <span>{{ task.deadline }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">任务状态</span>
                    <a-tag :color="getTaskStatusColor(task.status)">{{ getTaskStatusText(task.status) }}</a-tag>
                  </div>
                </div>

                <a-divider />

                <div class="task-progress">
                  <h4>完成进度</h4>
                  <div class="progress-stats">
                    <div class="stat-item">
                      <span>{{ task.completedTeams || 0 }}/{{ task.totalTeams || teams.length }} 个团队已提交</span>
                    </div>
                    <a-progress
                      :percent="calculateProgress(task)"
                      :stroke-color="{ from: '#108ee9', to: '#87d068' }"
                    />
                  </div>
                </div>

                <a-divider />

                <div class="task-submissions">
                  <h4>团队提交</h4>
                  <a-empty v-if="!task.submissions || task.submissions.length === 0" description="暂无提交" />
                  <a-list v-else size="small">
                    <a-list-item v-for="submission in task.submissions" :key="submission.id">
                      <div class="submission-item">
                        <div class="submission-info">
                          <div class="team-name">{{ submission.team.name }}</div>
                          <div class="submission-time">提交时间: {{ submission.submitTime }}</div>
                        </div>
                        <div class="submission-actions">
                          <a-space>
                            <a-button size="small" @click="viewSubmission(submission)">查看</a-button>
                            <a-button
                              v-if="isCreator && !submission.grade"
                              type="primary"
                              size="small"
                              @click="gradeSubmission(submission)"
                            >
                              评分
                            </a-button>
                            <a-tag v-if="submission.grade" color="green">{{ submission.grade }} 分</a-tag>
                          </a-space>
                        </div>
                      </div>
                    </a-list-item>
                  </a-list>
                </div>

                <div class="task-actions" v-if="isCreator">
                  <a-space>
                    <a-button type="primary" @click="editTask(task)">编辑任务</a-button>
                    <a-button status="danger" @click="deleteTask(task.id)">删除任务</a-button>
                  </a-space>
                </div>
              </div>
            </a-collapse-item>
          </a-collapse>
        </div>
      </a-spin>
    </div>

    <!-- 创建/编辑任务弹窗 -->
    <a-modal
      v-model:visible="taskModalVisible"
      :title="editingTask ? '编辑任务' : '创建任务'"
      @ok="handleTaskSubmit"
      @cancel="taskModalVisible = false"
    >
      <a-form ref="taskFormRef" :model="taskForm" layout="vertical">
        <a-form-item field="title" label="任务标题" :rules="[{ required: true, message: '请输入任务标题' }]">
          <a-input v-model="taskForm.title" placeholder="请输入任务标题" />
        </a-form-item>

        <a-form-item field="description" label="任务描述" :rules="[{ required: true, message: '请输入任务描述' }]">
          <a-textarea
            v-model="taskForm.description"
            placeholder="请输入任务描述"
            :auto-size="{ minRows: 3, maxRows: 6 }"
          />
        </a-form-item>

        <a-form-item field="deadline" label="截止日期" :rules="[{ required: true, message: '请选择截止日期' }]">
          <a-date-picker v-model="taskForm.deadline" style="width: 100%" />
        </a-form-item>

        <a-form-item field="status" label="任务状态">
          <a-select v-model="taskForm.status" placeholder="请选择任务状态">
            <a-option value="pending">未开始</a-option>
            <a-option value="in_progress">进行中</a-option>
            <a-option value="completed">已完成</a-option>
          </a-select>
        </a-form-item>

        <a-form-item field="teams" label="分配团队">
          <a-select
            v-model="taskForm.teams"
            placeholder="请选择团队"
            multiple
          >
            <a-option
              v-for="team in teams"
              :key="team.id"
              :value="team.id"
            >
              {{ team.name }}
            </a-option>
          </a-select>
          <div class="form-tip">不选择团队则默认分配给所有团队</div>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 提交详情弹窗 -->
    <a-modal
      v-model:visible="submissionModalVisible"
      title="提交详情"
      :footer="false"
      @cancel="submissionModalVisible = false"
    >
      <div v-if="currentSubmission" class="submission-detail">
        <div class="submission-header">
          <h3>{{ currentSubmission.team.name }}</h3>
          <div class="submission-meta">
            <span>提交时间: {{ currentSubmission.submitTime }}</span>
            <a-tag v-if="currentSubmission.grade" color="green">{{ currentSubmission.grade }} 分</a-tag>
          </div>
        </div>

        <a-divider />

        <div class="submission-content">
          <h4>提交内容</h4>
          <div v-html="currentSubmission.content"></div>
        </div>

        <div v-if="currentSubmission.attachments && currentSubmission.attachments.length > 0" class="submission-attachments">
          <h4>附件</h4>
          <a-list size="small">
            <a-list-item v-for="attachment in currentSubmission.attachments" :key="attachment.id">
              <a-link :href="attachment.url" target="_blank">
                {{ attachment.name }}
              </a-link>
            </a-list-item>
          </a-list>
        </div>

        <a-divider />

        <div class="submission-comment">
          <h4>评语</h4>
          <p v-if="currentSubmission.comment">{{ currentSubmission.comment }}</p>
          <a-empty v-else description="暂无评语" />
        </div>

        <div v-if="isCreator && !currentSubmission.grade" class="submission-actions">
          <a-button type="primary" @click="gradeSubmission(currentSubmission)">评分</a-button>
        </div>
      </div>
    </a-modal>

    <!-- 评分弹窗 -->
    <a-modal
      v-model:visible="gradeModalVisible"
      title="评分"
      @ok="handleGradeSubmit"
      @cancel="gradeModalVisible = false"
    >
      <a-form :model="gradeForm" layout="vertical">
        <a-form-item field="grade" label="分数" :rules="[{ required: true, message: '请输入分数' }]">
          <a-input-number v-model="gradeForm.grade" :min="0" :max="100" style="width: 100%" />
        </a-form-item>

        <a-form-item field="comment" label="评语">
          <a-textarea
            v-model="gradeForm.comment"
            placeholder="请输入评语"
            :auto-size="{ minRows: 3, maxRows: 6 }"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Message, Modal } from '@arco-design/web-vue';
import { getTaskList, saveTask } from '@/apis/training-api';
import { getTrainingDetail } from '@/apis/training-api';
import { getTeamList } from '@/apis/training-api';
import { gradeSubmission as gradeSubmissionApi } from '@/apis/training-api';
import useUserStore from '@/sotre/user-store';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const taskFormRef = ref(null);

// 项目ID
const projectId = computed(() => route.params.id);

// 项目信息
const project = ref({});

// 是否为项目创建者
const isCreator = computed(() => {
  if (!userStore.userInfo || !project.value) return false;
  return project.value.createdBy === userStore.userInfo.id;
});

// 任务列表
const tasks = ref([]);
const loading = ref(false);

// 团队列表
const teams = ref([]);

// 任务弹窗
const taskModalVisible = ref(false);
const editingTask = ref(null);
const taskForm = reactive({
  title: '',
  description: '',
  deadline: '',
  status: 'pending',
  teams: []
});

// 提交详情弹窗
const submissionModalVisible = ref(false);
const currentSubmission = ref(null);

// 评分弹窗
const gradeModalVisible = ref(false);
const gradeForm = reactive({
  submissionId: '',
  grade: 0,
  comment: ''
});

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

// 计算任务进度
const calculateProgress = (task) => {
  if (!task.completedTeams) return 0;
  const total = task.totalTeams || teams.value.length;
  if (total === 0) return 0;
  return Math.round((task.completedTeams / total) * 100);
};

// 显示创建任务弹窗
const showCreateTaskModal = () => {
  editingTask.value = null;
  taskForm.title = '';
  taskForm.description = '';
  taskForm.deadline = '';
  taskForm.status = 'pending';
  taskForm.teams = [];
  taskModalVisible.value = true;
};

// 编辑任务
const editTask = (task) => {
  editingTask.value = task;
  taskForm.title = task.title;
  taskForm.description = task.description;
  taskForm.deadline = task.deadline;
  taskForm.status = task.status;
  taskForm.teams = task.teams ? task.teams.map(team => team.id) : [];
  taskModalVisible.value = true;
};

// 删除任务
const deleteTask = (taskId) => {
  Modal.warning({
    title: '确认删除',
    content: '确定要删除此任务吗？删除后将无法恢复。',
    okText: '确定删除',
    cancelText: '取消',
    onOk: async () => {
      try {
        // 这里应该调用API删除任务
        // await deleteTask(taskId);
        
        Message.success('删除成功');
        
        // 从列表中移除（模拟）
        tasks.value = tasks.value.filter(task => task.id !== taskId);
      } catch (error) {
        console.error('删除任务失败', error);
        Message.error('删除任务失败');
      }
    }
  });
};

// 查看提交详情
const viewSubmission = (submission) => {
  currentSubmission.value = submission;
  submissionModalVisible.value = true;
};

// 评分
const gradeSubmission = (submission) => {
  currentSubmission.value = submission;
  gradeForm.submissionId = submission.id;
  gradeForm.grade = submission.grade || 0;
  gradeForm.comment = submission.comment || '';
  gradeModalVisible.value = true;
};

// 处理任务表单提交
const handleTaskSubmit = async () => {
  const { validate } = taskFormRef.value;
  try {
    await validate();
    
    const data = {
      title: taskForm.title,
      description: taskForm.description,
      status: taskForm.status,
      teams: taskForm.teams
    };
    
    // 处理日期格式
    if (taskForm.deadline) {
      data.deadline = new Date(taskForm.deadline).toISOString().split('T')[0];
    }
    
    if (editingTask.value) {
      data.id = editingTask.value.id;
    }
    
    await saveTask(projectId.value, data);
    
    Message.success(editingTask.value ? '任务更新成功' : '任务创建成功');
    taskModalVisible.value = false;
    
    // 重新加载任务列表
    fetchTasks();
  } catch (error) {
    console.error('保存任务失败', error);
    Message.error('操作失败');
  }
};

// 处理评分提交
const handleGradeSubmit = async () => {
  try {
    await gradeSubmissionApi(gradeForm.submissionId, {
      grade: gradeForm.grade,
      comment: gradeForm.comment
    });
    
    Message.success('评分成功');
    gradeModalVisible.value = false;
    
    // 更新当前提交的评分（模拟）
    if (currentSubmission.value) {
      currentSubmission.value.grade = gradeForm.grade;
      currentSubmission.value.comment = gradeForm.comment;
    }
    
    // 更新任务列表中的提交评分（模拟）
    tasks.value = tasks.value.map(task => {
      if (task.submissions) {
        task.submissions = task.submissions.map(sub => {
          if (sub.id === gradeForm.submissionId) {
            return {
              ...sub,
              grade: gradeForm.grade,
              comment: gradeForm.comment
            };
          }
          return sub;
        });
      }
      return task;
    });
  } catch (error) {
    console.error('评分失败', error);
    Message.error('评分失败');
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

// 获取任务列表
const fetchTasks = async () => {
  loading.value = true;
  try {
    const res = await getTaskList(projectId.value);
    
    if (res && res.data && res.data.data) {
      tasks.value = res.data.data;
    } else {
      // 模拟数据（开发阶段使用）
      tasks.value = [
        {
          id: '101',
          title: '需求分析与原型设计',
          description: '分析项目需求，设计用户界面原型和功能流程',
          deadline: '2023-06-01',
          status: 'completed',
          completedTeams: 2,
          totalTeams: 2,
          teams: [
            { id: '201', name: '代码先锋队' },
            { id: '202', name: 'Web精英' }
          ],
          submissions: [
            {
              id: '401',
              team: { id: '201', name: '代码先锋队' },
              submitTime: '2023-05-30 15:20:36',
              content: '<p>我们完成了需求分析文档和原型设计，包括以下内容：</p><ul><li>用户需求分析</li><li>功能需求列表</li><li>界面原型设计</li></ul>',
              attachments: [
                { id: '501', name: '需求分析文档.pdf', url: '#' },
                { id: '502', name: '原型设计.zip', url: '#' }
              ],
              grade: 95,
              comment: '分析全面，原型设计清晰，很好地理解了项目需求。'
            },
            {
              id: '402',
              team: { id: '202', name: 'Web精英' },
              submitTime: '2023-05-31 09:45:12',
              content: '<p>我们的需求分析和原型设计已完成：</p><ul><li>用户角色定义</li><li>核心功能流程图</li><li>界面原型</li></ul>',
              attachments: [
                { id: '503', name: '需求文档.docx', url: '#' }
              ],
              grade: 88,
              comment: '分析基本到位，但原型设计细节有待完善。'
            }
          ]
        },
        {
          id: '102',
          title: '项目架构搭建',
          description: '搭建Vue项目框架，配置路由、状态管理和基础组件',
          deadline: '2023-06-10',
          status: 'in_progress',
          completedTeams: 1,
          totalTeams: 2,
          teams: [
            { id: '201', name: '代码先锋队' },
            { id: '202', name: 'Web精英' }
          ],
          submissions: [
            {
              id: '403',
              team: { id: '201', name: '代码先锋队' },
              submitTime: '2023-06-08 16:30:45',
              content: '<p>我们已完成项目架构搭建：</p><ul><li>Vue 3 + Vite项目初始化</li><li>路由配置</li><li>Pinia状态管理</li><li>基础组件库集成</li></ul>',
              attachments: [
                { id: '504', name: '项目代码.zip', url: '#' }
              ],
              grade: 92,
              comment: '架构设计合理，代码组织清晰，组件封装得当。'
            }
          ]
        },
        {
          id: '103',
          title: '用户认证模块开发',
          description: '实现用户注册、登录和个人中心功能',
          deadline: '2023-06-20',
          status: 'pending',
          completedTeams: 0,
          totalTeams: 2,
          teams: [
            { id: '201', name: '代码先锋队' },
            { id: '202', name: 'Web精英' }
          ],
          submissions: []
        }
      ];
    }
  } catch (error) {
    console.error('获取任务列表失败', error);
    Message.error('获取任务列表失败');
    tasks.value = [];
  } finally {
    loading.value = false;
  }
};

// 获取团队列表
const fetchTeams = async () => {
  try {
    const res = await getTeamList(projectId.value);
    
    if (res && res.data && res.data.data) {
      teams.value = res.data.data;
    } else {
      // 模拟数据（开发阶段使用）
      teams.value = [
        {
          id: '201',
          name: '代码先锋队'
        },
        {
          id: '202',
          name: 'Web精英'
        }
      ];
    }
  } catch (error) {
    console.error('获取团队列表失败', error);
    teams.value = [];
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
  await fetchTeams();
  
  // 获取任务列表
  fetchTasks();
});
</script>

<style lang="less" scoped>
.task-management {
  .task-content {
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
  
  .tasks-list {
    .task-detail {
      padding: 8px 0;
      
      .task-info {
        .info-item {
          margin-bottom: 12px;
          
          .label {
            display: inline-block;
            width: 80px;
            color: var(--color-text-3);
            margin-right: 8px;
          }
        }
      }
      
      .task-progress {
        margin-bottom: 16px;
        
        h4 {
          font-size: 14px;
          margin-bottom: 8px;
        }
        
        .progress-stats {
          .stat-item {
            margin-bottom: 8px;
            text-align: right;
            color: var(--color-text-2);
          }
        }
      }
      
      .task-submissions {
        margin-bottom: 16px;
        
        h4 {
          font-size: 14px;
          margin-bottom: 8px;
        }
        
        .submission-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          width: 100%;
          
          .submission-info {
            .team-name {
              font-weight: 500;
            }
            
            .submission-time {
              font-size: 12px;
              color: var(--color-text-3);
            }
          }
        }
      }
      
      .task-actions {
        margin-top: 16px;
        display: flex;
        justify-content: flex-end;
      }
    }
  }
  
  .form-tip {
    font-size: 12px;
    color: var(--color-text-3);
    margin-top: 4px;
  }
  
  .submission-detail {
    .submission-header {
      h3 {
        font-size: 18px;
        margin-bottom: 8px;
      }
      
      .submission-meta {
        display: flex;
        justify-content: space-between;
        color: var(--color-text-3);
      }
    }
    
    .submission-content,
    .submission-attachments,
    .submission-comment {
      margin-bottom: 16px;
      
      h4 {
        font-size: 16px;
        margin-bottom: 8px;
      }
    }
    
    .submission-actions {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style> 