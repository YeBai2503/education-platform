<template>
  <div class="team-manage">
    <a-card class="general-card" v-if="teamInfo">
      <template #title>
        团队管理
        <a-tag color="orange" style="margin-left: 8px">队长权限</a-tag>
      </template>
      
      <!-- 团队信息 -->
      <a-form 
        ref="formRef" 
        :model="formData" 
        label-align="left" 
        :style="{ width: '600px' }"
      >
        <a-form-item field="name" label="团队名称" required extra="修改团队名称">
          <a-input
            v-model="formData.name"
            placeholder="请输入团队名称"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="项目实训">
          <a-input
            :model-value="experimentInfo ? experimentInfo.title : '加载中...'"
            disabled
          />
        </a-form-item>
        <a-form-item label="创建时间">
          <a-input
            :model-value="teamInfo ? formatDate(teamInfo.createdAt) : ''"
            disabled
          />
        </a-form-item>
        <a-space>
          <a-button type="primary" @click="handleUpdateTeam" :loading="updating">
            保存团队信息
          </a-button>
          <a-popconfirm
            content="确定要删除该团队吗？删除后所有成员将退出团队，且无法恢复。"
            @ok="handleDeleteTeam"
          >
            <a-button status="danger">
              <template #icon><icon-delete /></template>
              解散团队
            </a-button>
          </a-popconfirm>
          <a-button @click="goBack">返回</a-button>
        </a-space>
      </a-form>
      
      <!-- 团队成员管理 -->
      <a-divider>团队成员管理 ({{ teamInfo.memberCount || 0 }}/5)</a-divider>
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
          <a-table-column title="操作" align="center">
            <template #cell="{ record }">
              <span>--</span>
            </template>
          </a-table-column>
        </template>
      </a-table>
      
      <!-- 项目提交状态 -->
      <a-divider>项目提交状态</a-divider>
      <div v-if="submissionInfo" class="submission-info">
        <a-descriptions :data="submissionDescData" layout="inline-vertical" :column="3" />
        
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
        
        <div class="submission-actions">
          <a-button 
            type="primary" 
            @click="handleSubmit"
            :disabled="isExpired"
          >
            <template #icon><icon-edit /></template>
            {{ submissionInfo ? '修改提交' : '提交项目' }}
          </a-button>
        </div>
      </div>
      <div v-else class="no-submission">
        <a-empty description="尚未提交项目">
          <template #extra>
            <a-button 
              type="primary" 
              @click="handleSubmit"
              :disabled="isExpired"
            >
              提交项目
            </a-button>
          </template>
        </a-empty>
      </div>
    </a-card>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message, Modal } from '@arco-design/web-vue';
import { 
  getExperimentDetailRequest,
  getTeamDetailRequest,
  updateTeamRequest,
  deleteTeamRequest,
  getStudentSubmitDetailRequest,
  checkStudentSubmitRequest,
  checkUserIsHeaderRequest
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';

export default {
  name: 'ProjectTeamManage',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const experimentId = computed(() => route.params.experimentId);
    const teamId = computed(() => route.params.teamId);
    
    const updating = ref(false);
    const teamInfo = ref(null);
    const experimentInfo = ref(null);
    const submissionInfo = ref(null);
    const isTeamHeader = ref(false);
    
    const formData = reactive({
      name: '',
      experimentId: '',
    });
    
    // 判断是否过期
    const isExpired = computed(() => {
      if (!experimentInfo.value || !experimentInfo.value.ddl) return false;
      const now = new Date();
      const deadline = new Date(experimentInfo.value.ddl);
      return now > deadline;
    });
    
    // 检查是否为队长
    const checkIsTeamHeader = async () => {
      try {
        const res = await checkUserIsHeaderRequest(teamId.value);
        if (res.data.code === '00000') {
          isTeamHeader.value = res.data.data;
          
          if (!isTeamHeader.value) {
            Message.error('您不是队长，无权管理团队');
            goBack();
          }
        } else {
          Message.error(res.data.msg || '验证队长权限失败');
          goBack();
        }
      } catch (error) {
        console.error('验证队长权限出错:', error);
        Message.error('验证队长权限失败');
        goBack();
      }
    };
    
    // 获取团队信息
    const fetchTeamDetail = async () => {
      try {
        const res = await getTeamDetailRequest(teamId.value);
        if (res.data.code === '00000') {
          teamInfo.value = res.data.data;
          
          // 填充表单数据
          formData.name = teamInfo.value.name;
          formData.experimentId = teamInfo.value.experimentId;
        } else {
          Message.error(res.data.msg || '获取团队信息失败');
          goBack();
        }
      } catch (error) {
        console.error('获取团队信息出错:', error);
        Message.error('获取团队信息失败');
        goBack();
      }
    };
    
    // 获取项目详情
    const fetchExperimentDetail = async () => {
      try {
        const res = await getExperimentDetailRequest(experimentId.value);
        if (res.data.code === '00000') {
          experimentInfo.value = res.data.data;
        } else {
          Message.error(res.data.msg || '获取项目详情失败');
        }
      } catch (error) {
        console.error('获取项目详情出错:', error);
        Message.error('获取项目详情失败');
      }
    };
    
    // 检查提交状态
    const checkSubmissionStatus = async () => {
      try {
        const res = await checkStudentSubmitRequest(experimentId.value);
        if (res.data.code === '00000' && res.data.data) {
          // 如果已提交，获取提交详情
          fetchSubmissionDetail();
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
    
    // 更新团队信息
    const handleUpdateTeam = async () => {
      if (!formData.name || formData.name.trim() === '') {
        Message.error('团队名称不能为空');
        return;
      }
      
      updating.value = true;
      try {
        const data = {
          experimentId: formData.experimentId,
          name: formData.name
        };
        
        const res = await updateTeamRequest(teamId.value, data);
        if (res.data.code === '00000') {
          Message.success('更新团队成功');
          // 刷新团队信息
          await fetchTeamDetail();
        } else {
          Message.error(res.data.msg || '更新团队失败');
        }
      } catch (error) {
        console.error('更新团队出错:', error);
        Message.error('更新团队失败');
      } finally {
        updating.value = false;
      }
    };
    
    // 删除团队
    const handleDeleteTeam = async () => {
      try {
        const res = await deleteTeamRequest(teamId.value);
        if (res.data.code === '00000') {
          Message.success('解散团队成功');
          router.push({
            name: 'ProjectDetail',
            params: { 
              courseId: courseId.value,
              experimentId: experimentId.value
            }
          });
        } else {
          Message.error(res.data.msg || '解散团队失败');
        }
      } catch (error) {
        console.error('解散团队出错:', error);
        Message.error('解散团队失败');
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
      await checkIsTeamHeader();
      await fetchTeamDetail();
      await fetchExperimentDetail();
      await checkSubmissionStatus();
    });
    
    return {
      teamInfo,
      experimentInfo,
      submissionInfo,
      submissionDescData,
      updating,
      isExpired,
      formData,
      handleUpdateTeam,
      handleDeleteTeam,
      handleSubmit,
      downloadFile,
      formatDate,
      goBack
    };
  }
};
</script>

<style scoped>
.team-manage {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
.submission-files {
  margin-top: 16px;
}
.submission-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
.no-submission {
  margin-top: 24px;
  padding: 32px 0;
}
</style> 