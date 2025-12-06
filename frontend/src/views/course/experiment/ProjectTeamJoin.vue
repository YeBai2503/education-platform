<template>
  <div class="team-join">
    <a-row :gutter="16">
      <!-- 项目信息 -->
      <a-col :span="24">
        <a-card class="general-card" v-if="experimentInfo">
          <template #title>项目实训信息</template>
          <a-descriptions :data="experimentInfoDescData" layout="inline-vertical" :column="3" />
        </a-card>
      </a-col>

      <!-- 创建团队 -->
      <a-col :span="8">
        <a-card class="general-card">
          <template #title>创建新团队</template>
          <a-form ref="createFormRef" :model="createForm">
            <a-form-item field="name" label="团队名称" required extra="请输入团队名称">
              <a-input
                v-model="createForm.name"
                placeholder="请输入团队名称"
                allow-clear
              />
            </a-form-item>
            <a-button type="primary" @click="handleCreateTeam" :loading="submitting">
              创建团队
            </a-button>
          </a-form>
        </a-card>
      </a-col>

      <!-- 现有团队列表 -->
      <a-col :span="16">
        <a-card class="general-card" :title="`已有团队 (${teamList.length}个)`">
          <div v-if="loading" class="loading-container">
            <a-spin />
          </div>
          <a-empty v-else-if="teamList.length === 0" description="暂无团队，您可以创建一个新团队">
          </a-empty>
          <div v-else>
            <a-row :gutter="16">
              <a-col :span="12" v-for="team in teamList" :key="team.id">
                <a-card class="team-card" :hoverable="true">
                  <template #title>
                    <span>{{ team.name }}</span>
                    <a-tag v-if="team.memberCount >= 5" color="red" style="margin-left: 8px">已满</a-tag>
                  </template>
                  <template #extra>
                    <a-tooltip content="查看团队详情">
                      <a-button type="text" shape="circle" @click="handleViewTeam(team)">
                        <template #icon><icon-info-circle /></template>
                      </a-button>
                    </a-tooltip>
                  </template>
                  <a-descriptions :column="1" style="margin-bottom: 16px">
                    <a-descriptions-item label="队长">{{ team.headerName }}</a-descriptions-item>
                    <a-descriptions-item label="成员数">{{ team.memberCount }} / 5</a-descriptions-item>
                    <a-descriptions-item label="创建时间">{{ formatDate(team.createdAt) }}</a-descriptions-item>
                  </a-descriptions>
                  <div class="team-actions">
                    <a-button 
                      type="primary" 
                      @click="handleJoinTeam(team)" 
                      :disabled="team.memberCount >= 5"
                      style="width: 100%"
                    >
                      加入团队
                    </a-button>
                  </div>
                </a-card>
              </a-col>
            </a-row>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 查看团队详情对话框 -->
    <a-modal
      v-model:visible="teamDetailModalVisible"
      :title="currentTeam ? currentTeam.name : '团队详情'"
      @cancel="teamDetailModalVisible = false"
      :footer="false"
    >
      <div v-if="currentTeam">
        <a-descriptions :column="1" layout="inline-vertical">
          <a-descriptions-item label="团队名称">{{ currentTeam.name }}</a-descriptions-item>
          <a-descriptions-item label="队长">{{ currentTeam.headerName }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ formatDate(currentTeam.createdAt) }}</a-descriptions-item>
          <a-descriptions-item label="最后更新">{{ formatDate(currentTeam.updatedAt) }}</a-descriptions-item>
        </a-descriptions>

        <a-divider>团队成员 ({{ currentTeam.memberCount || 0 }}人)</a-divider>
        <a-table :data="currentTeam.members" :pagination="false">
          <template #columns>
            <a-table-column title="成员" data-index="userName">
              <template #cell="{ record }">
                <a-space>
                  <a-avatar :size="28" :imageUrl="record.userPicture">
                    <template #icon><icon-user /></template>
                  </a-avatar>
                  {{ record.userName }}
                  <a-tag v-if="record.isHeader" color="orange">队长</a-tag>
                </a-space>
              </template>
            </a-table-column>
            <a-table-column title="加入时间" data-index="joinTime">
              <template #cell="{ record }">
                {{ formatDate(record.joinTime) }}
              </template>
            </a-table-column>
          </template>
        </a-table>

        <div class="modal-actions">
          <a-button 
            type="primary" 
            @click="handleJoinTeam(currentTeam); teamDetailModalVisible = false" 
            :disabled="currentTeam.memberCount >= 5"
          >
            加入团队
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message, Modal } from '@arco-design/web-vue';
import { 
  getExperimentDetailRequest,
  getTeamListRequest,
  createTeamRequest,
  joinTeamRequest,
  checkUserJoinedTeamRequest,
  getUserTeamRequest
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';

export default {
  name: 'ProjectTeamJoin',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const experimentId = computed(() => route.params.experimentId);
    
    const loading = ref(false);
    const submitting = ref(false);
    const teamList = ref([]);
    const experimentInfo = ref(null);
    
    const currentTeam = ref(null);
    const teamDetailModalVisible = ref(false);
    
    const createForm = reactive({
      name: ''
    });
    const createFormRef = ref(null);
    
    // 获取项目详情
    const fetchExperimentDetail = async () => {
      try {
        const res = await getExperimentDetailRequest(experimentId.value);
        if (res.data.code === '00000') {
          experimentInfo.value = res.data.data;
        } else {
          Message.error(res.data.msg || '获取项目详情失败');
          goBack();
        }
      } catch (error) {
        console.error('获取项目详情出错:', error);
        Message.error('获取项目详情失败');
        goBack();
      }
    };

    // 获取团队列表
    const fetchTeamList = async () => {
      loading.value = true;
      try {
        const res = await getTeamListRequest(experimentId.value);
        if (res.data.code === '00000') {
          teamList.value = res.data.data || [];
        } else {
          Message.error(res.data.msg || '获取团队列表失败');
        }
      } catch (error) {
        console.error('获取团队列表出错:', error);
        Message.error('获取团队列表失败');
      } finally {
        loading.value = false;
      }
    };

    // 检查用户是否已加入团队
    const checkUserTeamStatus = async () => {
      try {
        const joinedRes = await checkUserJoinedTeamRequest(experimentId.value);
        if (joinedRes.data.code === '00000' && joinedRes.data.data) {
          // 如果已加入团队，获取团队信息并跳转到团队详情页面
          const teamRes = await getUserTeamRequest(experimentId.value);
          if (teamRes.data.code === '00000') {
            const userTeam = teamRes.data.data;
            if (userTeam) {
              Message.info('您已加入团队，正在跳转到团队详情页面');
              router.replace({
                name: 'ProjectDetail',
                params: { 
                  courseId: courseId.value,
                  experimentId: experimentId.value
                }
              });
              return true;
            }
          }
        }
        return false;
      } catch (error) {
        console.error('检查用户团队状态出错:', error);
        return false;
      }
    };

    // 创建团队
    const handleCreateTeam = async () => {
      if (!createForm.name || createForm.name.trim() === '') {
        Message.error('请输入团队名称');
        return;
      }
      
      submitting.value = true;
      try {
        const res = await createTeamRequest(experimentId.value, createForm.name);
        if (res.data.code === '00000') {
          Message.success('创建团队成功');
          router.replace({
            name: 'ProjectDetail',
            params: { 
              courseId: courseId.value,
              experimentId: experimentId.value
            }
          });
        } else {
          Message.error(res.data.msg || '创建团队失败');
        }
      } catch (error) {
        console.error('创建团队出错:', error);
        Message.error('创建团队失败');
      } finally {
        submitting.value = false;
      }
    };

    // 加入团队
    const handleJoinTeam = async (team) => {
      if (!team || !team.id) return;
      
      if (team.memberCount >= 5) {
        Message.error('该团队成员已满，无法加入');
        return;
      }
      
      try {
        const res = await joinTeamRequest(team.id);
        if (res.data.code === '00000') {
          Message.success('加入团队成功');
          router.replace({
            name: 'ProjectDetail',
            params: { 
              courseId: courseId.value,
              experimentId: experimentId.value
            }
          });
        } else {
          Message.error(res.data.msg || '加入团队失败');
        }
      } catch (error) {
        console.error('加入团队出错:', error);
        Message.error('加入团队失败');
      }
    };

    // 查看团队详情
    const handleViewTeam = (team) => {
      currentTeam.value = team;
      teamDetailModalVisible.value = true;
    };

    // 返回上一页
    const goBack = () => {
      router.push({
        name: 'ProjectDetail',
        params: { 
          courseId: courseId.value,
          experimentId: experimentId.value
        }
      });
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '无';
      const date = new Date(dateString);
      return date.toLocaleString();
    };

    // 项目信息展示数据
    const experimentInfoDescData = computed(() => {
      if (!experimentInfo.value) return [];
      
      const data = [
        {
          label: '项目标题',
          value: experimentInfo.value.title || '暂无'
        },
        {
          label: '截止日期',
          value: experimentInfo.value.ddl ? formatDate(experimentInfo.value.ddl) : '无截止日期'
        }
      ];
      
      return data;
    });

    onMounted(async () => {
      const hasTeam = await checkUserTeamStatus();
      if (!hasTeam) {
        await fetchExperimentDetail();
        await fetchTeamList();
      }
    });

    return {
      loading,
      submitting,
      teamList,
      experimentInfo,
      experimentInfoDescData,
      currentTeam,
      teamDetailModalVisible,
      createForm,
      createFormRef,
      handleCreateTeam,
      handleJoinTeam,
      handleViewTeam,
      formatDate,
      goBack
    };
  }
};
</script>

<style scoped>
.team-join {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
.team-card {
  margin-bottom: 16px;
}
.team-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}
.modal-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style> 