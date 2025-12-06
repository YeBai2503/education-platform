<template>
  <div class="experiment-detail">
    <a-card class="general-card" v-if="experimentInfo">
      <template #title>
        实验详情
        <a-tag v-if="submissionInfo && submissionInfo.score !== null && submissionInfo.score !== undefined" 
              color="green" style="margin-left: 12px">
          得分: {{ submissionInfo.score }}
        </a-tag>
      </template>
      
      <!-- 实验信息 -->
      <a-descriptions :data="experimentInfoDescData" />
      
      <!-- 实验附件 -->
      <div v-if="experimentInfo.fileUrls && experimentInfo.fileUrls.length > 0" class="experiment-files">
        <a-divider>实验附件</a-divider>
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
      
      <!-- 操作区域 -->
      <div class="experiment-actions">
        <a-space>
          <a-button @click="goBack">返回</a-button>
          
          <template v-if="isTeacher">
            <a-button type="primary" @click="handleEdit">
              <template #icon><icon-edit /></template>
              编辑实验
            </a-button>
            <a-button type="primary" @click="handleViewSubmissions">
              <template #icon><icon-user-group /></template>
              查看提交 ({{ submitCount || 0 }}/{{ totalStudents || 0 }})
            </a-button>
            <a-popconfirm
              content="确定要删除该实验吗？删除后将无法恢复，且会删除所有相关的提交记录。"
              @ok="handleDelete"
            >
              <a-button status="danger">
                <template #icon><icon-delete /></template>
                删除实验
              </a-button>
            </a-popconfirm>
          </template>
          
          <template v-else>
            <a-button 
              type="primary" 
              @click="handleSubmit"
              :disabled="isExpired"
            >
              <template #icon><icon-send /></template>
              {{ hasSubmitted ? '修改提交' : '提交实验' }}
            </a-button>
          </template>
        </a-space>
      </div>
      
      <!-- 已提交的内容信息 -->
      <a-card class="inner-card" v-if="submissionInfo" style="margin-top: 20px">
        <template #title>我的提交</template>
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
  deleteExperimentRequest
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';
import axios from '../../../utils/http'; // 导入axios实例

export default {
  name: 'ExperimentDetail',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const experimentId = computed(() => route.params.experimentId);
    const isTeacher = computed(() => courseStore.isTeacher);
    
    const experimentInfo = ref(null);
    const submissionInfo = ref(null);
    const submitCount = ref(0);
    const totalStudents = ref(0);
    const hasSubmitted = ref(false);
    
    // 判断是否过期
    const isExpired = computed(() => {
      if (!experimentInfo.value || !experimentInfo.value.ddl) return false;
      const now = new Date();
      const deadline = new Date(experimentInfo.value.ddl);
      return now > deadline;
    });
    
    // 获取实验详情
    const fetchExperimentDetail = async () => {
      try {
        const res = await getExperimentDetailRequest(experimentId.value);
        if (res.data.code === '00000') {
          experimentInfo.value = res.data.data;
        } else {
          Message.error(res.data.msg || '获取实验详情失败');
          goBack();
        }
      } catch (error) {
        console.error('获取实验详情出错:', error);
        Message.error('获取实验详情失败');
        goBack();
      }
    };

    // 检查提交状态
    const checkSubmissionStatus = async () => {
      if (isTeacher.value) {
        // 如果是老师，获取提交数量
        await getSubmitCount();
        await getCourseStudentCount();
      } else {
        // 如果是学生，检查是否已提交
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
      }
    };

    // 获取提交数量
    const getSubmitCount = async () => {
      try {
        const res = await getExperimentSubmitCountRequest(experimentId.value);
        if (res.data.code === '00000') {
          submitCount.value = res.data.data;
        }
      } catch (error) {
        console.error('获取提交数量出错:', error);
      }
    };

    // 获取课程学生总数
    const getCourseStudentCount = async () => {
      try {
        // 使用axios发送请求
        const res = await axios.get('/uapi/courses/getCourseStudentCount', {
          params: { courseId: courseId.value }
        });
        
        console.log('学生总数API响应:', res);
        
        if (res.data && res.data.code === '00000') {
          totalStudents.value = res.data.data || 0;
        } else {
          console.warn('获取学生总数返回格式不正确:', res.data);
          // 使用一个默认值
          totalStudents.value = 0;
        }
      } catch (error) {
        console.error('获取学生总数出错:', error);
        totalStudents.value = 0;
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

    // 编辑实验
    const handleEdit = () => {
      router.push({
        name: 'ExperimentEdit',
        params: { 
          courseId: courseId.value,
          experimentId: experimentId.value
        }
      });
    };

    // 查看提交列表
    const handleViewSubmissions = () => {
      router.push({
        name: 'ExperimentSubmissions',
        params: { 
          courseId: courseId.value,
          experimentId: experimentId.value
        }
      });
    };

    // 删除实验
    const handleDelete = async () => {
      try {
        const res = await deleteExperimentRequest(experimentId.value);
        if (res.data.code === '00000') {
          Message.success('删除实验成功');
          goBack();
        } else {
          Message.error(res.data.msg || '删除实验失败');
        }
      } catch (error) {
        console.error('删除实验出错:', error);
        Message.error('删除实验失败');
      }
    };

    // 提交实验
    const handleSubmit = () => {
      router.push({
        name: 'ExperimentSubmit',
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
        name: 'CourseExperiments',
        params: { courseId: courseId.value }
      });
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
      
      const data = [
        {
          label: '实验标题',
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
          label: '实验详情',
          value: experimentInfo.value.detail
        });
      }
      
      return data;
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
      
      return data;
    });

    onMounted(async () => {
      await fetchExperimentDetail();
      await checkSubmissionStatus();
    });

    return {
      experimentInfo,
      submissionInfo,
      submitCount,
      totalStudents,
      hasSubmitted,
      isTeacher,
      isExpired,
      experimentInfoDescData,
      submissionDescData,
      handleEdit,
      handleViewSubmissions,
      handleDelete,
      handleSubmit,
      downloadFile,
      goBack,
      formatDate
    };
  }
};
</script>

<style scoped>
.experiment-detail {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
.inner-card {
  border: 1px solid #e5e6eb;
  border-radius: 4px;
}
.experiment-files, .submission-files {
  margin-top: 16px;
}
.experiment-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style> 