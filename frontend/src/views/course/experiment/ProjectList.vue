<template>
  <div class="project-list">
    <a-card class="general-card">
      <template #title>
        项目实训列表
        <a-button
          v-if="isTeacher"
          type="primary"
          @click="handleCreateProject"
          style="margin-left: 16px"
        >
          创建项目实训
        </a-button>
      </template>
      
      <!-- 项目实训列表 -->
      <a-table
        :data="projectList"
        :loading="loading"
        :pagination="pagination"
        @page-change="onPageChange"
      >
        <template #columns>
          <a-table-column title="标题" data-index="title" />
          <a-table-column title="创建时间" data-index="createdAt">
            <template #cell="{ record }">
              {{ formatDate(record.createdAt) }}
            </template>
          </a-table-column>
          <a-table-column title="截止日期" data-index="ddl">
            <template #cell="{ record }">
              {{ record.ddl ? formatDate(record.ddl) : '无截止日期' }}
            </template>
          </a-table-column>
          <a-table-column title="团队情况" v-if="isTeacher">
            <template #cell="{ record }">
              <a-space>
                <a-tag>{{ record.submitCount || 0 }}</a-tag>
                /
                <a-tag>{{ totalTeams[record.id] || 0 }}</a-tag>
              </a-space>
            </template>
          </a-table-column>
          <a-table-column title="团队状态" v-if="!isTeacher">
            <template #cell="{ record }">
              <a-tag v-if="record.teamInfo" color="green">已加入团队 - {{ record.teamInfo.name }}</a-tag>
              <a-tag v-else-if="isExpired(record.ddl)" color="red">已过期</a-tag>
              <a-tag v-else color="orange">未加入团队</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="操作" align="center">
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" size="small" @click="handleViewDetail(record)">
                  详情
                </a-button>
                
                <template v-if="isTeacher">
                  <a-button type="text" size="small" @click="handleEditProject(record)">
                    编辑
                  </a-button>
                  <a-button type="text" size="small" @click="handleViewSubmissions(record)">
                    查看提交
                  </a-button>
                  <a-popconfirm
                    content="确定要删除该项目实训吗？删除后将无法恢复，且会删除所有相关的团队和提交记录。"
                    @ok="handleDeleteProject(record)"
                  >
                    <a-button type="text" status="danger" size="small">
                      删除
                    </a-button>
                  </a-popconfirm>
                </template>
                
                <template v-else>
                  <a-button 
                    type="text" 
                    size="small" 
                    @click="handleTeamManagement(record)"
                    :disabled="isExpired(record.ddl)"
                  >
                    {{ record.teamInfo ? '查看我的团队' : '加入/创建团队' }}
                  </a-button>
                  <a-button 
                    v-if="record.teamInfo && record.isTeamHeader" 
                    type="text" 
                    size="small" 
                    @click="handleSubmitProject(record)"
                    :disabled="isExpired(record.ddl)"
                  >
                    {{ record.submitted ? '修改提交' : '提交项目' }}
                  </a-button>
                </template>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message, Modal } from '@arco-design/web-vue';
import { 
  getExperimentListRequest, 
  deleteExperimentRequest,
  getExperimentSubmitCountRequest,
  checkStudentSubmitRequest,
  checkUserJoinedTeamRequest,
  getUserTeamRequest,
  checkUserIsHeaderRequest,
  getTeamListRequest,
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';
import axios from '../../../utils/http'; // 导入axios实例

export default {
  name: 'ProjectList',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const isTeacher = computed(() => courseStore.isTeacher);
    
    const loading = ref(false);
    const projectList = ref([]);
    const totalTeams = ref({});
    
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showTotal: true,
      showPageSize: true,
    });

    // 获取项目实训列表
    const fetchProjectList = async () => {
      loading.value = true;
      try {
        // 使用getExperimentListRequest并传递type参数
        const res = await getExperimentListRequest(
          courseId.value,
          pagination.current,
          pagination.pageSize,
          '1' // 使用type=1查询项目实训
        );
        
        if (res.data.code === '00000') {
          const data = res.data.data;
          projectList.value = data.records || [];
          pagination.total = data.total || 0;
          
          // 如果是学生，查询每个项目的团队状态和提交状态
          if (!isTeacher.value) {
            await checkTeamStatus();
            await checkSubmissionStatus();
          } else {
            // 如果是老师，获取每个项目的提交数量和团队数量
            await getSubmissionCounts();
            await getTeamCounts();
          }
        } else {
          Message.error(res.data.msg || '获取项目实训列表失败');
        }
      } catch (error) {
        console.error('获取项目实训列表出错:', error);
        Message.error('获取项目实训列表失败');
      } finally {
        loading.value = false;
      }
    };

    // 检查学生的团队状态
    const checkTeamStatus = async () => {
      if (isTeacher.value || projectList.value.length === 0) return;
      
      try {
        for (const project of projectList.value) {
          // 检查是否加入了团队
          const joinedRes = await checkUserJoinedTeamRequest(project.id);
          if (joinedRes.data.code === '00000' && joinedRes.data.data) {
            // 如果已加入团队，获取团队信息
            const teamRes = await getUserTeamRequest(project.id);
            if (teamRes.data.code === '00000') {
              project.teamInfo = teamRes.data.data;
              
              // 检查是否为队长
              if (project.teamInfo) {
                const headerRes = await checkUserIsHeaderRequest(project.teamInfo.id);
                if (headerRes.data.code === '00000') {
                  project.isTeamHeader = headerRes.data.data;
                }
              }
            }
          }
        }
      } catch (error) {
        console.error('检查团队状态出错:', error);
      }
    };

    // 检查学生的提交状态
    const checkSubmissionStatus = async () => {
      if (isTeacher.value || projectList.value.length === 0) return;
      
      try {
        for (const project of projectList.value) {
          // 只有队长才能提交，所以只有队长需要检查提交状态
          if (project.isTeamHeader) {
            const res = await checkStudentSubmitRequest(project.id);
            if (res.data.code === '00000') {
              project.submitted = res.data.data;
            }
          }
        }
      } catch (error) {
        console.error('检查提交状态出错:', error);
      }
    };

    // 获取各项目的提交数量
    const getSubmissionCounts = async () => {
      if (!isTeacher.value || projectList.value.length === 0) return;
      
      try {
        for (const project of projectList.value) {
          const res = await getExperimentSubmitCountRequest(project.id);
          if (res.data.code === '00000') {
            project.submitCount = res.data.data;
          }
        }
      } catch (error) {
        console.error('获取提交数量出错:', error);
      }
    };

    // 获取各项目的团队数量
    const getTeamCounts = async () => {
      if (!isTeacher.value || projectList.value.length === 0) return;
      
      try {
        for (const project of projectList.value) {
          const res = await getTeamListRequest(project.id);
          if (res.data.code === '00000') {
            totalTeams.value[project.id] = res.data.data.length || 0;
          }
        }
      } catch (error) {
        console.error('获取团队数量出错:', error);
      }
    };

    // 页码变化
    const onPageChange = (current) => {
      pagination.current = current;
      fetchProjectList();
    };

    // 创建项目实训
    const handleCreateProject = () => {
      router.push({
        name: 'ProjectCreate',
        params: { courseId: courseId.value }
      });
    };

    // 查看项目实训详情
    const handleViewDetail = (record) => {
      router.push({
        name: 'ProjectDetail',
        params: { 
          courseId: courseId.value,
          experimentId: record.id
        }
      });
    };

    // 编辑项目实训
    const handleEditProject = (record) => {
      router.push({
        name: 'ProjectEdit',
        params: { 
          courseId: courseId.value,
          experimentId: record.id
        }
      });
    };

    // 团队管理（老师）
    const handleManageTeams = (record) => {
      router.push({
        name: 'ProjectTeams',
        params: { 
          courseId: courseId.value,
          experimentId: record.id
        }
      });
    };

    // 查看提交列表（老师）
    const handleViewSubmissions = (record) => {
      router.push({
        name: 'ProjectSubmissions',
        params: { 
          courseId: courseId.value,
          experimentId: record.id
        }
      });
    };

    // 删除项目实训
    const handleDeleteProject = async (record) => {
      try {
        const res = await deleteExperimentRequest(record.id);
        if (res.data.code === '00000') {
          Message.success('删除项目实训成功');
          fetchProjectList();
        } else {
          Message.error(res.data.msg || '删除项目实训失败');
        }
      } catch (error) {
        console.error('删除项目实训出错:', error);
        Message.error('删除项目实训失败');
      }
    };

    // 团队管理（学生）
    const handleTeamManagement = (record) => {
      router.push({
        name: 'ProjectTeamJoin',
        params: { 
          courseId: courseId.value,
          experimentId: record.id
        }
      });
    };

    // 提交项目实训
    const handleSubmitProject = (record) => {
      router.push({
        name: 'ProjectSubmit',
        params: { 
          courseId: courseId.value,
          experimentId: record.id
        }
      });
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '无';
      const date = new Date(dateString);
      return date.toLocaleString();
    };

    // 判断是否过期
    const isExpired = (ddl) => {
      if (!ddl) return false;
      const now = new Date();
      const deadline = new Date(ddl);
      return now > deadline;
    };

    onMounted(() => {
      fetchProjectList();
    });

    return {
      projectList,
      loading,
      pagination,
      totalTeams,
      isTeacher,
      onPageChange,
      handleCreateProject,
      handleViewDetail,
      handleEditProject,
      handleManageTeams,
      handleViewSubmissions,
      handleDeleteProject,
      handleTeamManagement,
      handleSubmitProject,
      formatDate,
      isExpired
    };
  }
};
</script>

<style scoped>
.project-list {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
</style> 