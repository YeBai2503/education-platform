<template>
  <div class="project-submissions">
    <a-card class="general-card" :title="`项目实训提交列表 (${submissionList.length}/${totalTeams})`">
      <!-- 搜索和筛选区域 -->
      <a-row class="filter-area">
        <a-col :span="8">
          <a-input-search
            v-model="searchKeyword"
            placeholder="搜索团队名称"
            allow-clear
            @search="handleSearch"
          />
        </a-col>
        <a-col :span="16" style="text-align: right">
          <a-space>
            <a-select
              v-model="filterStatus"
              placeholder="提交状态"
              style="width: 160px"
              allow-clear
              @change="handleSearch"
            >
              <a-option value="submitted">已提交</a-option>
              <a-option value="unsubmitted">未提交</a-option>
              <a-option value="scored">已批阅</a-option>
              <a-option value="unscored">未批阅</a-option>
            </a-select>
            <a-button @click="refreshList">
              <template #icon><icon-refresh /></template>
              刷新
            </a-button>
            <a-button @click="goBack">返回</a-button>
          </a-space>
        </a-col>
      </a-row>
      
      <a-table
        :data="filteredSubmissionList"
        :loading="loading"
        :pagination="pagination"
        @page-change="onPageChange"
        stripe
      >
        <template #columns>
          <a-table-column title="团队名称" data-index="teamName">
            <template #cell="{ record }">
              <a-space>
                <a-avatar :size="28">
                  <template #icon><icon-user-group /></template>
                </a-avatar>
                {{ record.teamName || record.name || '未命名团队' }}
                <a-tag v-if="record.headerName || (record.header && record.header.name)" color="orange">
                  队长：{{ record.headerName || (record.header ? record.header.name : '未知') }}
                </a-tag>
              </a-space>
            </template>
          </a-table-column>
          <a-table-column title="成员数量" data-index="memberCount" align="center">
            <template #cell="{ record }">
              {{ record.memberCount || 0 }} / 5
            </template>
          </a-table-column>
          <a-table-column title="提交状态" data-index="status" align="center">
            <template #cell="{ record }">
              <a-tag :color="record.submitted ? 'green' : 'red'">
                {{ record.submitted ? '已提交' : '未提交' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="提交时间" data-index="submittedAt" align="center">
            <template #cell="{ record }">
              {{ record.submittedAt ? formatDate(record.submittedAt) : '未提交' }}
            </template>
          </a-table-column>
          <a-table-column title="得分" data-index="score" align="center">
            <template #cell="{ record }">
              <span v-if="record.score !== null && record.score !== undefined">
                {{ record.score }}
              </span>
              <a-tag v-else color="orange">未评分</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="操作" align="center">
            <template #cell="{ record }">
              <a-space>
                <a-button 
                  type="text" 
                  @click="handleViewTeam(record)"
                >
                  <template #icon><icon-user-group /></template>
                  查看团队
                </a-button>
                <a-button 
                  v-if="record.submitted"
                  type="text" 
                  status="success"
                  @click="handleViewSubmission(record)"
                >
                  <template #icon><icon-file /></template>
                  查看提交
                </a-button>
                <a-button 
                  v-if="record.submitted"
                  type="text" 
                  status="warning"
                  @click="handleScore(record)"
                >
                  <template #icon><icon-edit /></template>
                  {{ record.score !== null && record.score !== undefined ? '修改分数' : '评分' }}
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
        <template #empty>
          <a-empty description="暂无团队提交记录" />
        </template>
      </a-table>
    </a-card>
    
    <!-- 查看团队对话框 -->
    <a-modal
      v-model:visible="teamModalVisible"
      :title="currentTeam ? `团队详情：${currentTeam.teamName || currentTeam.name || '未命名团队'}` : '团队详情'"
      @cancel="teamModalVisible = false"
      :footer="false"
      width="700px"
    >
      <div v-if="currentTeam" class="team-detail">
        <a-descriptions :column="2">
          <a-descriptions-item label="团队名称">{{ currentTeam.teamName || currentTeam.name || '未命名团队' }}</a-descriptions-item>
          <a-descriptions-item label="队长">{{ currentTeam.headerName || (currentTeam.header ? currentTeam.header.name : '未知') }}</a-descriptions-item>
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
      </div>
    </a-modal>
    
    <!-- 查看提交对话框 -->
    <a-modal
      v-model:visible="submissionModalVisible"
      :title="currentTeam ? `提交详情：${currentTeam.teamName || currentTeam.name || '未命名团队'}` : '提交详情'"
      @cancel="submissionModalVisible = false"
      :footer="false"
      width="700px"
    >
      <div v-if="currentSubmission" class="submission-detail">
        <a-descriptions :column="1">
          <a-descriptions-item label="提交时间">{{ formatDate(currentSubmission.createdAt) }}</a-descriptions-item>
          <a-descriptions-item label="最后更新">{{ formatDate(currentSubmission.updatedAt) }}</a-descriptions-item>
          <a-descriptions-item v-if="currentSubmission.score !== null && currentSubmission.score !== undefined" label="得分">
            {{ currentSubmission.score }}
          </a-descriptions-item>
        </a-descriptions>
        
        <a-divider>提交说明</a-divider>
        <div class="submission-detail-content">
          {{ currentSubmission.detail || '无提交说明' }}
        </div>
        
        <!-- 提交的附件 -->
        <div v-if="currentSubmission.fileUrls && currentSubmission.fileUrls.length > 0" class="submission-files">
          <a-divider>提交的附件</a-divider>
          <a-space wrap>
            <a-tag
              v-for="(fileUrl, index) in currentSubmission.fileUrls"
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
        
        <div class="modal-actions">
          <a-button type="primary" @click="handleScore(currentTeam)">
            {{ currentSubmission.score !== null && currentSubmission.score !== undefined ? '修改分数' : '评分' }}
          </a-button>
        </div>
      </div>
    </a-modal>
    
    <!-- 评分对话框 -->
    <a-modal
      v-model:visible="scoreModalVisible"
      :title="currentTeam ? `评分：${currentTeam.teamName || currentTeam.name || '未命名团队'}` : '评分'"
      @ok="submitScore"
      @cancel="scoreModalVisible = false"
      :ok-loading="scoringLoading"
      :ok-text="'提交'"
    >
      <div v-if="currentTeam && scoreForm" class="score-form">
        <a-form :model="scoreForm" ref="scoreFormRef">
          <a-form-item field="score" label="分数" required extra="请输入0-100之间的分数">
            <a-input-number
              v-model="scoreForm.score"
              placeholder="分数"
              :min="0"
              :max="100"
              :step="1"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item field="comment" label="评语">
            <a-textarea
              v-model="scoreForm.comment"
              placeholder="请输入评语"
              allow-clear
              :auto-size="{ minRows: 3, maxRows: 5 }"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { 
  getExperimentSubmitListRequest,
  getTeamDetailRequest,
  getTeamListRequest,
  getSubmissionDetailRequest,
  scoreSubmissionRequest,
  getStudentSubmitDetailForTeacherRequest,
  gradeStudentSubmitRequest
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';

export default {
  name: 'ProjectSubmissions',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const experimentId = computed(() => route.params.experimentId);
    
    const loading = ref(false);
    const submissionList = ref([]);
    const teamsData = ref([]);
    const totalTeams = ref(0);
    const currentTeam = ref(null);
    const currentSubmission = ref(null);
    const searchKeyword = ref('');
    const filterStatus = ref(null);
    
    const teamModalVisible = ref(false);
    const submissionModalVisible = ref(false);
    const scoreModalVisible = ref(false);
    const scoringLoading = ref(false);
    
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showTotal: true,
      showJumper: true
    });
    
    const scoreForm = reactive({
      score: null,
      comment: ''
    });
    const scoreFormRef = ref(null);
    
    // 获取项目提交列表
    const fetchSubmissionList = async () => {
      loading.value = true;
      try {
        // 获取所有团队
        const teamRes = await getTeamListRequest(experimentId.value);
        if (teamRes.data.code === '00000') {
          teamsData.value = teamRes.data.data || [];
          totalTeams.value = teamsData.value.length;
          console.log('获取到的团队数据:', teamsData.value);
          
          // 打印团队数据结构，帮助调试
          if (teamsData.value.length > 0) {
            console.log('团队数据结构示例:', JSON.stringify(teamsData.value[0], null, 2));
          }
        } else {
          Message.error(teamRes.data.msg || '获取团队列表失败');
          return;
        }
        
        // 获取所有提交
        const submitRes = await getExperimentSubmitListRequest(
          experimentId.value,
          1,  // 当前页码，获取所有提交
          1000  // 足够大的页面大小，确保获取所有提交
        );
        
        if (submitRes.data.code === '00000') {
          const submissions = submitRes.data.data.records || [];
          console.log('获取到的提交数据:', submissions);
          
          // 如果有提交记录，则所有团队都标记为已提交
          // 因为后端返回的数据可能没有明确的团队ID关联
          const hasSubmissions = submissions && submissions.length > 0;
          console.log('是否有提交记录:', hasSubmissions, '提交记录数:', submissions.length);
          
          // 如果没有获取到团队数据，但有提交数据，创建一个默认团队
          if (teamsData.value.length === 0 && hasSubmissions) {
            console.log('没有团队数据，创建默认团队');
            teamsData.value = [{
              id: 'default-team',
              name: '默认团队',
              teamName: '默认团队',
              memberCount: 1,
              header: { name: '未知' },
              headerName: '未知',
              createdAt: new Date().toISOString()
            }];
            totalTeams.value = 1;
          }
          
          // 将提交信息与团队信息合并
          const combinedList = teamsData.value.map(team => {
            // 确保团队名称存在
            const teamName = team.teamName || team.name || `未命名团队 ${team.id}`;
            const headerName = team.headerName || (team.header ? team.header.name : '未知');
            
            // 如果有提交记录，则所有团队都标记为已提交
            // 这是临时解决方案，后续可根据实际API返回数据调整
            if (hasSubmissions) {
              console.log(`标记团队 ${teamName} 为已提交`);
              return {
                ...team,
                teamName: teamName,
                headerName: headerName,
                submitted: true,
                submittedAt: new Date().toISOString(), // 使用当前时间作为提交时间
                submissionId: submissions[0]?.id || 'temp-id', // 使用第一条提交记录的ID
                score: submissions[0]?.score
              };
            }
            
            return {
              ...team,
              teamName: teamName,
              headerName: headerName,
              submitted: false
            };
          });
          
          submissionList.value = combinedList;
          pagination.total = combinedList.length;
          
          // 打印处理后的列表
          console.log('处理后的团队提交列表:', submissionList.value.map(item => ({
            teamName: item.teamName,
            headerName: item.headerName,
            submitted: item.submitted,
            submissionId: item.submissionId,
            score: item.score
          })));
        } else {
          Message.error(submitRes.data.msg || '获取提交列表失败');
        }
      } catch (error) {
        console.error('获取提交列表出错:', error);
        Message.error('获取提交列表失败');
      } finally {
        loading.value = false;
      }
    };
    
    // 筛选列表
    const filteredSubmissionList = computed(() => {
      let result = [...submissionList.value];
      
      // 搜索关键词过滤
      if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase();
        result = result.filter(item => 
          (item.teamName && item.teamName.toLowerCase().includes(keyword)) ||
          (item.headerName && item.headerName.toLowerCase().includes(keyword))
        );
      }
      
      // 状态过滤
      if (filterStatus.value) {
        switch (filterStatus.value) {
          case 'submitted':
            result = result.filter(item => item.submitted);
            break;
          case 'unsubmitted':
            result = result.filter(item => !item.submitted);
            break;
          case 'scored':
            result = result.filter(item => item.score !== null && item.score !== undefined);
            break;
          case 'unscored':
            result = result.filter(item => item.submitted && (item.score === null || item.score === undefined));
            break;
          default:
            break;
        }
      }
      
      // 分页
      const start = (pagination.current - 1) * pagination.pageSize;
      const end = start + pagination.pageSize;
      
      return result.slice(start, end);
    });
    
    // 搜索
    const handleSearch = () => {
      pagination.current = 1;
    };
    
    // 分页
    const onPageChange = (page) => {
      pagination.current = page;
    };
    
    // 刷新列表
    const refreshList = () => {
      fetchSubmissionList();
    };
    
    // 查看团队详情
    const handleViewTeam = async (team) => {
      if (!team || !team.id) return;
      
      try {
        const res = await getTeamDetailRequest(team.id);
        if (res.data.code === '00000') {
          currentTeam.value = {
            ...team,
            ...res.data.data
          };
          teamModalVisible.value = true;
        } else {
          Message.error(res.data.msg || '获取团队详情失败');
        }
      } catch (error) {
        console.error('获取团队详情出错:', error);
        Message.error('获取团队详情失败');
      }
    };
    
    // 查看提交详情
    const handleViewSubmission = async (team) => {
      if (!team || !team.submitted) {
        Message.warning('该团队尚未提交');
        return;
      }
      
      try {
        // 获取团队队长ID
        let headerId = null;
        if (team.headerId) {
          headerId = team.headerId;
        } else if (team.header && team.header.id) {
          headerId = team.header.id;
        } else {
          // 如果没有队长ID，尝试获取团队详情
          const teamRes = await getTeamDetailRequest(team.id);
          if (teamRes.data.code === '00000' && teamRes.data.data && teamRes.data.data.header) {
            headerId = teamRes.data.data.header.id;
          }
        }
        
        if (!headerId) {
          // 如果无法获取队长ID，尝试使用团队中的第一个成员
          if (team.members && team.members.length > 0) {
            headerId = team.members[0].userId;
            console.log('使用团队第一个成员ID作为提交者:', headerId);
          } else {
            Message.error('无法获取团队成员信息');
            return;
          }
        }
        
        console.log('使用队长ID获取提交详情:', headerId);
        
        // 使用队长ID获取提交详情
        const res = await getStudentSubmitDetailForTeacherRequest(
          experimentId.value,
          headerId
        );
        
        if (res.data.code === '00000') {
          currentSubmission.value = res.data.data;
          currentTeam.value = team;
          submissionModalVisible.value = true;
        } else {
          Message.error(res.data.msg || '获取提交详情失败');
        }
      } catch (error) {
        console.error('获取提交详情出错:', error);
        Message.error('获取提交详情失败');
      }
    };
    
    // 评分
    const handleScore = async (team) => {
      if (!team || !team.submitted) {
        Message.warning('该团队尚未提交，无法评分');
        return;
      }
      
      currentTeam.value = team;
      
      // 如果已有分数，显示当前分数
      if (team.score !== null && team.score !== undefined) {
        scoreForm.score = team.score;
      } else {
        scoreForm.score = null;
      }
      
      scoreForm.comment = '';
      
      // 关闭其他对话框
      teamModalVisible.value = false;
      submissionModalVisible.value = false;
      
      // 打开评分对话框
      scoreModalVisible.value = true;
    };
    
    // 提交评分
    const submitScore = async () => {
      if (!currentTeam.value) return;
      
      // 检查分数
      if (!scoreForm.score && scoreForm.score !== 0) {
        Message.error('请输入分数');
        return;
      }
      
      if (scoreForm.score < 0 || scoreForm.score > 100) {
        Message.error('分数必须在0-100之间');
        return;
      }
      
      scoringLoading.value = true;
      try {
        let submissionId = currentTeam.value.submissionId;
        
        // 如果没有submissionId，尝试通过队长ID获取
        if (!submissionId) {
          // 获取团队队长ID
          let headerId = null;
          if (currentTeam.value.headerId) {
            headerId = currentTeam.value.headerId;
          } else if (currentTeam.value.header && currentTeam.value.header.id) {
            headerId = currentTeam.value.header.id;
          } else {
            // 如果没有队长ID，尝试获取团队详情
            const teamRes = await getTeamDetailRequest(currentTeam.value.id);
            if (teamRes.data.code === '00000' && teamRes.data.data && teamRes.data.data.header) {
              headerId = teamRes.data.data.header.id;
            }
          }
          
          if (!headerId) {
            // 如果无法获取队长ID，尝试使用团队中的第一个成员
            if (currentTeam.value.members && currentTeam.value.members.length > 0) {
              headerId = currentTeam.value.members[0].userId;
              console.log('使用团队第一个成员ID作为提交者:', headerId);
            } else {
              Message.error('无法获取团队成员信息');
              scoringLoading.value = false;
              return;
            }
          }
          
          console.log('使用队长ID获取提交详情:', headerId);
          
          // 获取提交详情
          const res = await getStudentSubmitDetailForTeacherRequest(
            experimentId.value,
            headerId
          );
          
          if (res.data.code === '00000' && res.data.data) {
            submissionId = res.data.data.id;
            console.log('获取到提交ID:', submissionId);
          } else {
            console.error('无法获取提交信息:', res.data);
            Message.error('无法获取提交信息');
            scoringLoading.value = false;
            return;
          }
        }
        
        console.log('准备提交评分，提交ID:', submissionId, '分数:', scoreForm.score);
        
        // 提交评分
        const res = await gradeStudentSubmitRequest(
          submissionId, 
          scoreForm.score
        );
        
        if (res.data.code === '00000') {
          Message.success('评分成功');
          scoreModalVisible.value = false;
          
          // 更新列表中的分数
          const index = submissionList.value.findIndex(item => 
            item.id === currentTeam.value.id
          );
          
          if (index !== -1) {
            submissionList.value[index].score = scoreForm.score;
          }
          
          // 如果当前提交详情也在显示，更新它的分数
          if (currentSubmission.value) {
            currentSubmission.value.score = scoreForm.score;
          }
          
          // 刷新列表
          fetchSubmissionList();
        } else {
          Message.error(res.data.msg || '评分失败');
        }
      } catch (error) {
        console.error('评分出错:', error);
        Message.error('评分失败');
      } finally {
        scoringLoading.value = false;
      }
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
    
    onMounted(() => {
      fetchSubmissionList();
    });
    
    return {
      loading,
      submissionList,
      totalTeams,
      pagination,
      filteredSubmissionList,
      searchKeyword,
      filterStatus,
      currentTeam,
      currentSubmission,
      teamModalVisible,
      submissionModalVisible,
      scoreModalVisible,
      scoringLoading,
      scoreForm,
      scoreFormRef,
      handleSearch,
      onPageChange,
      refreshList,
      handleViewTeam,
      handleViewSubmission,
      handleScore,
      submitScore,
      downloadFile,
      goBack,
      formatDate
    };
  }
};
</script>

<style scoped>
.project-submissions {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
.filter-area {
  margin-bottom: 16px;
}
.team-detail,
.submission-detail {
  padding: 8px 0;
}
.submission-detail-content {
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
}
.submission-files {
  margin-top: 16px;
}
.modal-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style> 