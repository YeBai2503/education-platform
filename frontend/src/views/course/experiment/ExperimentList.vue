<template>
  <div class="experiment-list">
    <a-card class="general-card">
      <template #title>
        实验列表
        <a-button
          v-if="isTeacher"
          type="primary"
          @click="handleCreateExperiment"
          style="margin-left: 16px"
        >
          创建实验
        </a-button>
      </template>
      
      <!-- 实验列表 -->
      <a-table
        :data="experimentList"
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
          <a-table-column title="提交情况" v-if="isTeacher">
            <template #cell="{ record }">
              <a-space>
                <a-tag>{{ record.submitCount || 0 }}</a-tag>
                /
                <a-tag>{{ totalStudents }}</a-tag>
              </a-space>
            </template>
          </a-table-column>
          <a-table-column title="状态" v-if="!isTeacher">
            <template #cell="{ record }">
              <a-tag v-if="record.submitted" color="green">已提交</a-tag>
              <a-tag v-else-if="isExpired(record.ddl)" color="red">已过期</a-tag>
              <a-tag v-else color="orange">未提交</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="操作" align="center">
            <template #cell="{ record }">
              <a-space>
                <a-button type="text" size="small" @click="handleViewDetail(record)">
                  详情
                </a-button>
                
                <template v-if="isTeacher">
                  <a-button type="text" size="small" @click="handleEditExperiment(record)">
                    编辑
                  </a-button>
                  <a-button type="text" size="small" @click="handleViewSubmissions(record)">
                    查看提交
                  </a-button>
                  <a-popconfirm
                    content="确定要删除该实验吗？删除后将无法恢复，且会删除所有相关的提交记录。"
                    @ok="handleDeleteExperiment(record)"
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
                    @click="handleSubmitExperiment(record)"
                    :disabled="isExpired(record.ddl)"
                  >
                    {{ record.submitted ? '修改提交' : '提交实验' }}
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
  getExperimentDetailRequest,
  deleteExperimentRequest,
  checkStudentSubmitRequest,
  getExperimentSubmitCountRequest
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';
import axios from '../../../utils/http'; // 导入axios实例

export default {
  name: 'ExperimentList',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const isTeacher = computed(() => courseStore.isTeacher);
    
    const loading = ref(false);
    const experimentList = ref([]);
    const totalStudents = ref(0); // 总学生数
    
    const pagination = reactive({
      current: 1,
      pageSize: 10,
      total: 0,
      showTotal: true,
      showPageSize: true,
    });

    // 获取实验列表
    const fetchExperimentList = async () => {
      loading.value = true;
      try {
        const res = await getExperimentListRequest(
          courseId.value,
          pagination.current,
          pagination.pageSize,
          '0'
        );
        console.log('实验列表',res)
        if (res.data.code === '00000') {
          const data = res.data.data;
          experimentList.value = data.records || [];
          pagination.total = data.total || 0;
          
          // 如果是学生，查询每个实验的提交状态
          if (!isTeacher.value) {
            await checkSubmissionStatus();
          } else {
            // 如果是老师，获取每个实验的提交数量
            await getSubmissionCounts();
            // 获取班级总学生数
            await getCourseStudentCount();
          }
        } else {
          Message.error(res.data.msg || '获取实验列表失败');
        }
      } catch (error) {
        console.error('获取实验列表出错:', error);
        Message.error('获取实验列表失败');
      } finally {
        loading.value = false;
      }
    };

    // 检查学生的提交状态
    const checkSubmissionStatus = async () => {
      if (isTeacher.value || experimentList.value.length === 0) return;
      
      try {
        for (const experiment of experimentList.value) {
          const res = await checkStudentSubmitRequest(experiment.id);
          if (res.data.code === '00000') {
            experiment.submitted = res.data.data;
          }
        }
      } catch (error) {
        console.error('检查提交状态出错:', error);
      }
    };

    // 获取各实验的提交数量
    const getSubmissionCounts = async () => {
      if (!isTeacher.value || experimentList.value.length === 0) return;
      
      try {
        for (const experiment of experimentList.value) {
          const res = await getExperimentSubmitCountRequest(experiment.id);
          if (res.data.code === '00000') {
            experiment.submitCount = res.data.data;
          }
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

    // 页码变化
    const onPageChange = (current) => {
      pagination.current = current;
      fetchExperimentList();
    };

    // 创建实验
    const handleCreateExperiment = () => {
      router.push({
        name: 'ExperimentCreate',
        params: { courseId: courseId.value }
      });
    };

    // 查看实验详情
    const handleViewDetail = (record) => {
      router.push({
        name: 'ExperimentDetail',
        params: { 
          courseId: courseId.value,
          experimentId: record.id
        }
      });
    };

    // 编辑实验
    const handleEditExperiment = (record) => {
      router.push({
        name: 'ExperimentEdit',
        params: { 
          courseId: courseId.value,
          experimentId: record.id
        }
      });
    };

    // 查看提交列表
    const handleViewSubmissions = (record) => {
      router.push({
        name: 'ExperimentSubmissions',
        params: { 
          courseId: courseId.value,
          experimentId: record.id
        }
      });
    };

    // 删除实验
    const handleDeleteExperiment = async (record) => {
      try {
        const res = await deleteExperimentRequest(record.id);
        if (res.data.code === '00000') {
          Message.success('删除实验成功');
          fetchExperimentList();
        } else {
          Message.error(res.data.msg || '删除实验失败');
        }
      } catch (error) {
        console.error('删除实验出错:', error);
        Message.error('删除实验失败');
      }
    };

    // 提交实验
    const handleSubmitExperiment = (record) => {
      router.push({
        name: 'ExperimentSubmit',
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
      fetchExperimentList();
    });

    return {
      experimentList,
      loading,
      pagination,
      totalStudents,
      isTeacher,
      onPageChange,
      handleCreateExperiment,
      handleViewDetail,
      handleEditExperiment,
      handleViewSubmissions,
      handleDeleteExperiment,
      handleSubmitExperiment,
      formatDate,
      isExpired
    };
  }
};
</script>

<style scoped>
.experiment-list {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
</style> 