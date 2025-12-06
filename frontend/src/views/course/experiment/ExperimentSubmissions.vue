<template>
  <div class="experiment-submissions">
    <a-card class="general-card">
      <template #title>
        实验提交列表
        <a-tag style="margin-left: 12px" color="green">已提交: {{ pagination.total || 0 }}</a-tag>
        <a-tag color="orange">总人数: {{ totalStudents }}</a-tag>
      </template>
      
      <!-- 实验信息卡片 -->
      <a-card class="info-card" v-if="experimentInfo">
        <a-descriptions :data="experimentInfoDescData" size="mini" layout="inline-horizontal" />
      </a-card>
      
      <!-- 提交列表 -->
      <a-table
        :data="submissionList"
        :loading="loading"
        :pagination="pagination"
        @page-change="onPageChange"
        style="margin-top: 16px"
      >
        <template #columns>
          <a-table-column title="学生姓名" data-index="studentName" />
          <a-table-column title="提交时间" data-index="createdAt">
            <template #cell="{ record }">
              {{ formatDate(record.createdAt) }}
            </template>
          </a-table-column>
          <a-table-column title="最后更新" data-index="updatedAt">
            <template #cell="{ record }">
              {{ formatDate(record.updatedAt) }}
            </template>
          </a-table-column>
          <a-table-column title="分数" data-index="score">
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
                <a-button type="text" size="small" @click="handleViewDetail(record)">
                  查看详情
                </a-button>
                <a-button type="text" size="small" @click="handleGrade(record)">
                  {{ record.score !== null && record.score !== undefined ? '修改评分' : '评分' }}
                </a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>
    
    <!-- 评分对话框 -->
    <a-modal
      v-model:visible="gradeModalVisible"
      :title="currentSubmission ? `为 ${currentSubmission.studentName} 评分` : '评分'"
      @cancel="gradeModalVisible = false"
      @before-ok="handleGradeSubmit"
    >
      <a-form :model="gradeForm" ref="gradeFormRef">
        <a-form-item field="score" label="分数" extra="请输入0-100之间的整数">
          <a-input-number
            v-model="gradeForm.score"
            :min="0"
            :max="100"
            :precision="0"
            style="width: 120px"
          />
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 提交详情对话框 -->
    <a-modal
      v-model:visible="detailModalVisible"
      :title="currentSubmission ? `${currentSubmission.studentName} 的提交详情` : '提交详情'"
      @cancel="detailModalVisible = false"
      :footer="false"
      :width="700"
    >
      <div v-if="currentSubmission" class="submission-detail">
        <a-descriptions :data="submissionDescData" />
        
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
      </div>
    </a-modal>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { 
  getExperimentDetailRequest,
  getExperimentSubmitListRequest,
  getStudentSubmitDetailForTeacherRequest,
  gradeStudentSubmitRequest
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';
import axios from '../../../utils/http'; // 导入正确的axios实例

export default {
  name: 'ExperimentSubmissions',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const experimentId = computed(() => route.params.experimentId);
    
    const loading = ref(false);
    const submissionList = ref([]);
    const experimentInfo = ref(null);
    const totalStudents = ref(0);
    
    const currentSubmission = ref(null);
    const gradeModalVisible = ref(false);
    const detailModalVisible = ref(false);
    const gradeForm = reactive({
      score: 0
    });
    const gradeFormRef = ref(null);
    
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showTotal: true,
      showPageSize: true,
    });

    // 获取实验详情
    const fetchExperimentDetail = async () => {
      try {
        const res = await getExperimentDetailRequest(experimentId.value);
        if (res.data.code === '00000') {
          experimentInfo.value = res.data.data;
        } else {
          Message.error(res.data.msg || '获取实验详情失败');
        }
      } catch (error) {
        console.error('获取实验详情出错:', error);
        Message.error('获取实验详情失败');
      }
    };

    // 获取提交列表
    const fetchSubmissionList = async () => {
      loading.value = true;
      try {
        const res = await getExperimentSubmitListRequest(
          experimentId.value,
          pagination.current,
          pagination.pageSize
        );
        
        if (res.data.code === '00000') {
          const data = res.data.data;
          submissionList.value = data.records || [];
          pagination.total = data.total || 0;
        } else {
          Message.error(res.data.msg || '获取提交列表失败');
        }
      } catch (error) {
        console.error('获取提交列表出错:', error);
        Message.error('获取提交列表失败');
      } finally {
        loading.value = false;
      }
    };

    // 获取课程学生总数
    const getCourseStudentCount = async () => {
      try {
        const res = await axios.get(`/uapi/courses/getCourseStudentCount`, {
          params: { courseId: courseId.value }
        });
        
        console.log('学生总数API响应:', res);
        
        if (res.data && res.data.code === '00000') {
          totalStudents.value = res.data.data;
        } else {
          console.warn('获取学生总数返回格式不正确:', res.data);
          totalStudents.value = 0;
        }
      } catch (error) {
        console.error('获取学生总数出错:', error);
        totalStudents.value = 0;
      }
    };

    // 页码变化
    const onPageChange = (current) => {
      pagination.current = current;
      fetchSubmissionList();
    };

    // 查看提交详情
    const handleViewDetail = async (record) => {
      try {
        const res = await getStudentSubmitDetailForTeacherRequest(
          experimentId.value,
          record.studentId
        );
        
        if (res.data.code === '00000') {
          currentSubmission.value = res.data.data;
          detailModalVisible.value = true;
        } else {
          Message.error(res.data.msg || '获取提交详情失败');
        }
      } catch (error) {
        console.error('获取提交详情出错:', error);
        Message.error('获取提交详情失败');
      }
    };

    // 评分
    const handleGrade = (record) => {
      currentSubmission.value = record;
      gradeForm.score = record.score !== null && record.score !== undefined ? record.score : 0;
      gradeModalVisible.value = true;
    };

    // 提交评分
    const handleGradeSubmit = async (done) => {
      // 手动验证分数
      if (gradeForm.score === undefined || gradeForm.score === null || gradeForm.score < 0 || gradeForm.score > 100) {
        Message.error('请输入0-100之间的有效分数');
        done(false);
        return;
      }
      
      try {
        const res = await gradeStudentSubmitRequest(
          currentSubmission.value.id,
          gradeForm.score
        );
        
        if (res.data.code === '00000') {
          Message.success('评分成功');
          fetchSubmissionList();
          done();
        } else {
          Message.error(res.data.msg || '评分失败');
          done(false);
        }
      } catch (error) {
        console.error('评分出错:', error);
        Message.error('评分失败');
        done(false);
      }
    };

    // 下载文件
    const downloadFile = (url) => {
      window.open(url, '_blank');
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '无';
      const date = new Date(dateString);
      return date.toLocaleString();
    };

    // 实验信息展示数据
    const experimentInfoDescData = computed(() => {
      if (!experimentInfo.value) return [];
      
      return [
        {
          label: '实验标题',
          value: experimentInfo.value.title || '暂无'
        },
        {
          label: '截止日期',
          value: experimentInfo.value.ddl ? formatDate(experimentInfo.value.ddl) : '无截止日期'
        }
      ];
    });

    // 提交信息展示数据
    const submissionDescData = computed(() => {
      if (!currentSubmission.value) return [];
      
      const data = [
        {
          label: '学生姓名',
          value: currentSubmission.value.studentName || '暂无'
        },
        {
          label: '提交时间',
          value: formatDate(currentSubmission.value.createdAt)
        },
        {
          label: '最后更新',
          value: formatDate(currentSubmission.value.updatedAt)
        },
        {
          label: '分数',
          value: currentSubmission.value.score !== null && currentSubmission.value.score !== undefined 
            ? currentSubmission.value.score 
            : '未评分'
        }
      ];
      
      if (currentSubmission.value.detail) {
        data.push({
          label: '提交说明',
          value: currentSubmission.value.detail
        });
      }
      
      return data;
    });

    onMounted(async () => {
      await fetchExperimentDetail();
      await fetchSubmissionList();
      await getCourseStudentCount();
    });

    return {
      submissionList,
      loading,
      pagination,
      experimentInfo,
      totalStudents,
      currentSubmission,
      gradeModalVisible,
      detailModalVisible,
      gradeForm,
      gradeFormRef,
      experimentInfoDescData,
      submissionDescData,
      onPageChange,
      handleViewDetail,
      handleGrade,
      handleGradeSubmit,
      downloadFile,
      formatDate
    };
  }
};
</script>

<style scoped>
.experiment-submissions {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
.info-card {
  margin-bottom: 16px;
  background-color: #f5f5f5;
}
.submission-files {
  margin-top: 16px;
}
</style> 