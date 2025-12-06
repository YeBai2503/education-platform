<template>
  <div class="project-detail">
    <a-card class="general-card" v-if="experimentInfo">
      <template #title>
        项目实训详情
        <a-tag v-if="teamInfo && teamInfo.score !== null && teamInfo.score !== undefined" 
              color="green" style="margin-left: 12px">
          团队得分: {{ teamInfo.score }}
        </a-tag>
      </template>
      
      <!-- 项目信息 -->
      <a-descriptions :data="experimentInfoDescData" />
      
      <!-- 项目附件 -->
      <div v-if="experimentInfo.fileUrls && experimentInfo.fileUrls.length > 0" class="project-files">
        <a-divider>项目资料</a-divider>
        <a-space wrap>
          <a-tag
            v-for="(fileUrl, index) in experimentInfo.fileUrls"
            :key="index"
            color="arcoblue"
            hoverable
            @click="downloadFile(fileUrl)"
          >
            <template #icon><icon-file /></template>
            {{ fileUrl.split('/').pop() }}
          </a-tag>
        </a-space>
      </div>
      
      <!-- 团队信息 -->
      <a-card class="inner-card" v-if="teamInfo" style="margin-top: 20px">
        <template #title>
          团队信息
          <span v-if="isTeamHeader" class="team-header-badge">（队长）</span>
        </template>
        <a-descriptions :data="teamInfoDescData" />
        
        <!-- 团队成员列表 -->
        <a-divider>团队成员 ({{ teamInfo.memberCount || 0 }}人)</a-divider>
        <a-table :data="teamInfo.members" :pagination="false">
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
        
        <!-- 团队管理按钮 -->
        <div class="team-actions" v-if="!isTeacher">
          <a-button
            v-if="isTeamHeader"
            type="primary"
            @click="handleManageTeam"
            :disabled="isExpired"
          >
            <template #icon><icon-settings /></template>
            管理团队
          </a-button>
          <a-popconfirm
            v-else
            content="确定要退出当前团队吗？退出后将无法访问团队提交的内容。"
            @ok="handleLeaveTeam"
          >
            <a-button status="danger" :disabled="isExpired">
              <template #icon><icon-export /></template>
              退出团队
            </a-button>
          </a-popconfirm>
        </div>
      </a-card>
      
      <!-- 提交信息 -->
      <a-card class="inner-card" v-if="submissionInfo" style="margin-top: 20px">
        <template #title>团队提交</template>
        <a-descriptions :data="submissionDescData" />
        
        <!-- 提交的附件 -->
        <div v-if="submissionInfo.fileUrls && submissionInfo.fileUrls.length > 0" class="submission-files">
          <a-divider>提交的附件</a-divider>
          <a-space wrap>
            <a-tag
              v-for="(fileUrl, index) in submissionInfo.fileUrls"
              :key="index"
              color="green"
              hoverable
              @click="downloadFile(fileUrl)"
            >
              <template #icon><icon-file /></template>
              {{ fileUrl.split('/').pop() }}
            </a-tag>
          </a-space>
        </div>
      </a-card>
      
      <!-- 操作区域 -->
      <div class="project-actions">
        <a-space>
          <a-button @click="goBack">返回</a-button>
          
          <template v-if="isTeacher">
            <a-button type="primary" @click="handleEdit">
              <template #icon><icon-edit /></template>
              编辑项目
            </a-button>
            <a-button type="primary" @click="handleManageTeams">
              <template #icon><icon-user-group /></template>
              团队管理
            </a-button>
            <a-button type="primary" @click="handleViewSubmissions">
              <template #icon><icon-file /></template>
              查看提交 ({{ submitCount || 0 }}/{{ totalTeams || 0 }})
            </a-button>
            <a-popconfirm
              content="确定要删除该项目实训吗？删除后将无法恢复，且会删除所有相关的团队和提交记录。"
              @ok="handleDelete"
            >
              <a-button status="danger">
                <template #icon><icon-delete /></template>
                删除项目
              </a-button>
            </a-popconfirm>
          </template>
          
          <template v-else-if="!teamInfo">
            <a-button 
              type="primary" 
              @click="handleJoinTeam"
              :disabled="isExpired"
            >
              <template #icon><icon-plus /></template>
              加入/创建团队
            </a-button>
          </template>
          
          <template v-else-if="isTeamHeader">
            <a-button 
              type="primary" 
              @click="handleSubmit"
              :disabled="isExpired"
            >
              <template #icon><icon-send /></template>
              {{ hasSubmitted ? '修改提交' : '提交项目' }}
            </a-button>
          </template>
        </a-space>
      </div>
    </a-card>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { 
  getExperimentDetailRequest,
  getStudentSubmitDetailRequest,
  checkStudentSubmitRequest,
  getExperimentSubmitCountRequest,
  deleteExperimentRequest,
  getUserTeamRequest,
  checkUserJoinedTeamRequest,
  checkUserIsHeaderRequest,
  leaveTeamRequest,
  getTeamListRequest
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';

export default {
  name: 'ProjectDetail',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const experimentId = computed(() => route.params.experimentId);
    const isTeacher = computed(() => courseStore.isTeacher);
    
    const experimentInfo = ref(null);
    const submissionInfo = ref(null);
    const teamInfo = ref(null);
    const isTeamHeader = ref(false);
    const submitCount = ref(0);
    const totalTeams = ref(0);
    const hasSubmitted = ref(false);
    
    // 判断是否过期
    const isExpired = computed(() => {
      if (!experimentInfo.value || !experimentInfo.value.ddl) return false;
      const now = new Date();
      const deadline = new Date(experimentInfo.value.ddl);
      return now > deadline;
    });
    
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

    // 检查团队状态
    const checkTeamStatus = async () => {
      if (isTeacher.value) {
        // 如果是老师，获取团队数量
        try {
          const teamsRes = await getTeamListRequest(experimentId.value);
          if (teamsRes.data.code === '00000') {
            totalTeams.value = teamsRes.data.data.length || 0;
          }
          
          // 获取提交数量
          const submitRes = await getExperimentSubmitCountRequest(experimentId.value);
          if (submitRes.data.code === '00000') {
            submitCount.value = submitRes.data.data;
          }
        } catch (error) {
          console.error('获取团队信息出错:', error);
        }
      } else {
        // 如果是学生，检查是否已加入团队
        try {
          const joinedRes = await checkUserJoinedTeamRequest(experimentId.value);
          if (joinedRes.data.code === '00000' && joinedRes.data.data) {
            // 如果已加入团队，获取团队信息
            const teamRes = await getUserTeamRequest(experimentId.value);
            if (teamRes.data.code === '00000') {
              teamInfo.value = teamRes.data.data;
              
              // 检查是否为队长
              if (teamInfo.value) {
                const headerRes = await checkUserIsHeaderRequest(teamInfo.value.id);
                if (headerRes.data.code === '00000') {
                  isTeamHeader.value = headerRes.data.data;
                  
                  // 如果是队长，检查是否已提交
                  if (isTeamHeader.value) {
                    await checkSubmissionStatus();
                  }
                }
              }
            }
          }
        } catch (error) {
          console.error('检查团队状态出错:', error);
        }
      }
    };

    // 检查提交状态
    const checkSubmissionStatus = async () => {
      try {
        const res = await checkStudentSubmitRequest(experimentId.value);
        if (res.data.code === '00000') {
          hasSubmitted.value = res.data.data;
          
          if (hasSubmitted.value) {
            // 如果已提交，获取提交详情
            await fetchSubmissionDetail();
          }
        }
      } catch (error) {
        console.error('检查提交状态出错:', error);
      }
    };

    // 获取提交详情
    const fetchSubmissionDetail = async () => {
      try {
        const res = await getStudentSubmitDetailRequest(experimentId.value);
        if (res.data.code === '00000') {
          submissionInfo.value = res.data.data;
        }
      } catch (error) {
        console.error('获取提交详情出错:', error);
      }
    };

    // 编辑项目
    const handleEdit = () => {
      router.push({
        name: 'ProjectEdit',
        params: { 
          courseId: courseId.value,
          experimentId: experimentId.value
        }
      });
    };

    // 管理团队（老师）
    const handleManageTeams = () => {
      router.push({
        name: 'ProjectSubmissions',
        params: { 
          courseId: courseId.value,
          experimentId: experimentId.value
        }
      });
    };

    // 查看提交列表
    const handleViewSubmissions = () => {
      router.push({
        name: 'ProjectSubmissions',
        params: { 
          courseId: courseId.value,
          experimentId: experimentId.value
        }
      });
    };

    // 删除项目
    const handleDelete = async () => {
      try {
        const res = await deleteExperimentRequest(experimentId.value);
        if (res.data.code === '00000') {
          Message.success('删除项目实训成功');
          goBack();
        } else {
          Message.error(res.data.msg || '删除项目实训失败');
        }
      } catch (error) {
        console.error('删除项目出错:', error);
        Message.error('删除项目实训失败');
      }
    };

    // 加入团队
    const handleJoinTeam = () => {
      router.push({
        name: 'ProjectTeamJoin',
        params: { 
          courseId: courseId.value,
          experimentId: experimentId.value
        }
      });
    };
    
    // 管理团队（学生队长）
    const handleManageTeam = () => {
      router.push({
        name: 'ProjectTeamManage',
        params: { 
          courseId: courseId.value,
          experimentId: experimentId.value,
          teamId: teamInfo.value.id
        }
      });
    };
    
    // 退出团队
    const handleLeaveTeam = async () => {
      if (!teamInfo.value) return;
      
      try {
        const res = await leaveTeamRequest(teamInfo.value.id);
        if (res.data.code === '00000') {
          Message.success('退出团队成功');
          // 刷新页面信息
          teamInfo.value = null;
          isTeamHeader.value = false;
          submissionInfo.value = null;
        } else {
          Message.error(res.data.msg || '退出团队失败');
        }
      } catch (error) {
        console.error('退出团队出错:', error);
        Message.error('退出团队失败');
      }
    };

    // 提交项目
    const handleSubmit = () => {
      router.push({
        name: 'ProjectSubmit',
        params: { 
          courseId: courseId.value,
          experimentId: experimentId.value
        }
      });
    };

    // 下载文件
    const downloadFile = (url) => {
      window.open(url, '_blank');
    };

    // 返回上一页
    const goBack = () => {
      router.push({
        name: 'CourseProjects',
        params: { courseId: courseId.value }
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
          label: '创建时间',
          value: formatDate(experimentInfo.value.createdAt)
        },
        {
          label: '截止日期',
          value: experimentInfo.value.ddl ? formatDate(experimentInfo.value.ddl) : '无截止日期'
        }
      ];
      
      if (experimentInfo.value.detail) {
        data.push({
          label: '项目详情',
          value: experimentInfo.value.detail
        });
      }
      
      return data;
    });

    // 团队信息展示数据
    const teamInfoDescData = computed(() => {
      if (!teamInfo.value) return [];
      
      return [
        {
          label: '团队名称',
          value: teamInfo.value.name
        },
        {
          label: '队长',
          value: teamInfo.value.headerName
        },
        {
          label: '创建时间',
          value: formatDate(teamInfo.value.createdAt)
        }
      ];
    });

    // 提交信息展示数据
    const submissionDescData = computed(() => {
      if (!submissionInfo.value) return [];
      
      const data = [
        {
          label: '提交时间',
          value: formatDate(submissionInfo.value.createdAt)
        },
        {
          label: '最后更新',
          value: formatDate(submissionInfo.value.updatedAt)
        }
      ];
      
      if (submissionInfo.value.detail) {
        data.push({
          label: '提交说明',
          value: submissionInfo.value.detail
        });
      }
      
      if (submissionInfo.value.score !== null && submissionInfo.value.score !== undefined) {
        data.push({
          label: '得分',
          value: submissionInfo.value.score
        });
      }
      
      return data;
    });

    onMounted(async () => {
      await fetchExperimentDetail();
      await checkTeamStatus();
    });

    return {
      experimentInfo,
      submissionInfo,
      teamInfo,
      isTeamHeader,
      submitCount,
      totalTeams,
      hasSubmitted,
      isTeacher,
      isExpired,
      experimentInfoDescData,
      teamInfoDescData,
      submissionDescData,
      handleEdit,
      handleManageTeams,
      handleViewSubmissions,
      handleDelete,
      handleJoinTeam,
      handleManageTeam,
      handleLeaveTeam,
      handleSubmit,
      downloadFile,
      goBack,
      formatDate
    };
  }
};
</script>

<style scoped>
.project-detail {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
.inner-card {
  border: 1px solid #e5e6eb;
  border-radius: 4px;
  margin-top: 16px;
}
.project-files, .submission-files {
  margin-top: 16px;
}
.project-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
.team-header-badge {
  color: #ff7d00;
  margin-left: 8px;
  font-size: 14px;
}
.team-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style> 