<template>
  <div class="experiment-submit">
    <a-card class="general-card" :title="isEdit ? '修改实验提交' : '提交实验'">
      <a-form
        ref="formRef"
        :model="formData"
        label-align="left"
        :style="{ width: '600px' }"
      >
        <a-form-item field="detail" label="实验详情说明" extra="请输入详细的实验说明，此项为必填">
          <a-textarea
            v-model="formData.detail"
            placeholder="请输入实验详情说明"
            allow-clear
            :auto-size="{ minRows: 5, maxRows: 10 }"
          />
        </a-form-item>
        <a-form-item label="附件" extra="最多可上传5个文件">
          <div>
            <a-upload
              :file-list="fileList"
              :custom-request="customUploadRequest"
              @change="handleUploadChange"
              multiple
              :limit="5"
            >
              <template #upload-button>
                <a-button>
                  <template #icon><icon-upload /></template>
                  上传附件
                </a-button>
              </template>
            </a-upload>
            <div v-if="isEdit && filesToDelete && filesToDelete.length > 0" class="file-operations">
              <a-tag color="red">已标记 {{ filesToDelete.length }} 个文件待删除</a-tag>
            </div>
          </div>
        </a-form-item>

        <a-space>
          <a-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '更新提交' : '提交实验' }}
          </a-button>
          <a-button @click="goBack">取消</a-button>
        </a-space>
      </a-form>
    </a-card>

    <!-- 实验信息卡片 -->
    <a-card class="general-card" v-if="experimentInfo" style="margin-top: 20px">
      <template #title>实验详情</template>
      <a-descriptions :data="experimentInfoDescData" layout="inline-vertical" />
      
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
      
      <!-- 已提交的内容信息 -->
      <a-card class="inner-card" v-if="submissionInfo" style="margin-top: 20px">
        <template #title>
          已提交信息
          <span v-if="submissionInfo.score !== null && submissionInfo.score !== undefined" class="score-tag">
            得分: {{ submissionInfo.score }}
          </span>
        </template>
        <a-descriptions :data="submissionDescData" layout="inline-vertical" />
        
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
  studentSubmitExperimentRequest,
  updateStudentSubmitRequest
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';

export default {
  name: 'ExperimentSubmit',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const formRef = ref(null);
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const experimentId = computed(() => route.params.experimentId);
    const submissionId = ref(null);
    const isEdit = ref(false);
    const experimentInfo = ref(null);
    const submissionInfo = ref(null);
    
    const submitting = ref(false);
    const fileList = ref([]);
    const filesToDelete = ref([]);
    
    const formData = reactive({
      detail: '',
      files: []
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
        Message.error('获取实验详情失败');
        goBack();
      }
    };

    // 检查是否已提交过实验
    const checkSubmissionStatus = async () => {
      try {
        const res = await checkStudentSubmitRequest(experimentId.value);
        if (res.data.code === '00000') {
          const hasSubmitted = res.data.data;
          
          if (hasSubmitted) {
            // 如果已提交，获取提交详情
            fetchSubmissionDetail();
          }
        } else {
          Message.error(res.data.msg || '检查提交状态失败');
        }
      } catch (error) {
        Message.error('检查提交状态失败');
      }
    };

    // 获取提交详情
    const fetchSubmissionDetail = async () => {
      try {
        const res = await getStudentSubmitDetailRequest(experimentId.value);
        if (res.data.code === '00000') {
          submissionInfo.value = res.data.data;
          submissionId.value = submissionInfo.value.id;
          isEdit.value = true;
          
          // 填充表单数据
          formData.detail = submissionInfo.value.detail || '';
          
          // 处理已有的附件
          if (submissionInfo.value.fileUrls && submissionInfo.value.fileUrls.length > 0) {
            fileList.value = submissionInfo.value.fileUrls.map((url, index) => {
              const fileName = url.split('/').pop();
              return {
                uid: `existing-${index}`,
                name: fileName,
                url: url,
                status: 'done',
                isExisting: true
              };
            });
          }
        } else {
          Message.error(res.data.msg || '获取提交详情失败');
        }
      } catch (error) {
        Message.error('获取提交详情失败');
      }
    };

    // 自定义上传请求
    const customUploadRequest = (option) => {
      console.log('customUploadRequest');
      const { fileItem, onSuccess, onProgress } = option;
      
      // 检查是否已达到最大文件数
      if (formData.files.length >= 5) {
        Message.warning('最多只能上传5个文件');
        onProgress({ percent: 0 });
        return;
      }
      
      // 直接获取原始File对象
      const originalFile = fileItem.file || fileItem;
      
      const newFile = {
        uid: `new-${Date.now()}-${Math.random().toString(36).substr(2, 5)}`,
        name: originalFile.name,
        status: 'done',
        percent: 100,
        size: originalFile.size,
        file: originalFile,
        isNew: true
      };
      
      // 更新文件列表的显示
      fileList.value = [...fileList.value, newFile];
      
      // 保存原始File对象，确保二进制内容完整
      formData.files.push(originalFile);
      
      console.log('文件上传后 - fileList:', fileList.value.map(f => f.name));
      console.log('文件上传后 - formData.files:', formData.files.map(f => f.name));
      
      onProgress({ percent: 100 });
      onSuccess();
    };

    // 处理上传文件变化
    const handleUploadChange = (event) => {
      console.log('handleUploadChange', event);
      if (!event) return;
      
      // 由于event实际上是一个文件数组的代理，我们需要直接处理数组
      if (event.length >= 0) {
        // 当前文件列表
        const currentFiles = [...event];
        console.log('当前文件列表:', currentFiles.map(f => f.name || '未命名'));
        
        // 比较前后文件列表，找出被删除的文件
        const previousFiles = [...fileList.value];
        const deletedFiles = previousFiles.filter(prevFile => 
          !currentFiles.some(currFile => currFile.uid === prevFile.uid)
        );
        console.log('deletedFiles', deletedFiles);
        
        if (deletedFiles.length > 0) {
          console.log('检测到删除的文件:', deletedFiles.map(f => f.name || '未命名'));
        }
        
        // 处理删除的文件
        for (const removedFile of deletedFiles) {
          // 如果是已有文件被删除
          if (removedFile.isExisting && removedFile.url) {
            console.log('已有文件被删除，完整URL:', removedFile.url);
            // 添加到待删除列表
            filesToDelete.value.push(removedFile.url);
          }
          
          // 如果是新上传的文件被删除
          else if (removedFile.isNew || removedFile.file) {
            console.log('新上传的文件被删除:', removedFile.name || '未命名');
            
            // 从formData.files中删除对应文件
            const fileIndex = formData.files.findIndex(f => 
              f && f.name === removedFile.name
            );
            
            if (fileIndex !== -1) {
              console.log(`找到匹配文件，从formData.files中移除索引: ${fileIndex}`);
              formData.files.splice(fileIndex, 1);
            }
          }
        }
        
        // 同步更新文件列表
        fileList.value = currentFiles;
        
        // 确保formData.files与fileList一致 - 只保留与fileList对应的新文件
        const newFileList = fileList.value.filter(f => f.isNew || f.file);
        
        if (newFileList.length === 0) {
          // 如果没有新文件，清空formData.files
          formData.files = [];
        } else {
          // 根据fileList重构formData.files
          formData.files = newFileList.map(file => file.file || file).filter(Boolean);
        }
        
        console.log('同步后 - fileList:', fileList.value.map(f => f.name || '未命名'));
        console.log('同步后 - formData.files:', formData.files.map(f => f.name || '未命名'));
      } else {
        // 如果event不是数组，可能是清空了所有文件
        console.log('文件列表可能已清空');
        
        // 如果fileList之前有内容，检查是否需要标记删除
        if (fileList.value.length > 0) {
          fileList.value.forEach(file => {
            if (file.isExisting && file.url) {
              console.log('已有文件被删除，完整URL:', file.url);
              filesToDelete.value.push(file.url);
            }
          });
        }
        
        // 清空文件列表和formData.files
        fileList.value = [];
        formData.files = [];
        
        console.log('文件列表已清空');
        console.log('formData.files已清空');
      }
    };
    
    // 提交表单
    const handleSubmit = async () => {
      console.log('表单引用:', formRef.value);
      console.log('表单数据:', formData);
      
      // 手动验证表单
      if (!formData.detail || formData.detail.trim() === '') {
        Message.error('请输入实验详情说明');
        return;
      }
      
      await submitForm();
    };
    
    // 提交表单
    const submitForm = async () => {
      submitting.value = true;
      try {
        let res;
        
        if (isEdit.value) {
          // 更新实验提交
          res = await updateStudentSubmitRequest(
            submissionId.value,
            formData.detail,
            filesToDelete.value,
            formData.files
          );
        } else {
          // 创建实验提交
          res = await studentSubmitExperimentRequest(
            experimentId.value,
            formData.detail,
            formData.files
          );
        }
        
        if (res.data.code === '00000') {
          Message.success(isEdit.value ? '更新实验提交成功' : '提交实验成功');
          // 刷新提交信息
          await fetchSubmissionDetail();
          // 重置表单状态
          filesToDelete.value = [];
          formData.files = [];
          
          if (!isEdit.value) {
            isEdit.value = true;
          }
        } else {
          Message.error(res.data.msg || (isEdit.value ? '更新实验提交失败' : '提交实验失败'));
        }
      } catch (error) {
        console.error('提交出错:', error);
        Message.error(isEdit.value ? '更新实验提交失败' : '提交实验失败');
      } finally {
        submitting.value = false;
      }
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

    // 实验信息展示数据
    const experimentInfoDescData = computed(() => {
      if (!experimentInfo.value) return [];
      
      const data = [
        {
          label: '实验标题',
          value: experimentInfo.value.title || '暂无'
        },
        {
          label: '截止日期',
          value: experimentInfo.value.ddl ? new Date(experimentInfo.value.ddl).toLocaleString() : '无截止日期'
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
          value: submissionInfo.value.createdAt ? new Date(submissionInfo.value.createdAt).toLocaleString() : '暂无'
        },
        {
          label: '最后更新',
          value: submissionInfo.value.updatedAt ? new Date(submissionInfo.value.updatedAt).toLocaleString() : '暂无'
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
      // 获取实验详情
      await fetchExperimentDetail();
      
      // 检查是否已提交过实验
      await checkSubmissionStatus();
    });

    return {
      formRef,
      formData,
      fileList,
      filesToDelete,
      submitting,
      isEdit,
      experimentInfo,
      submissionInfo,
      experimentInfoDescData,
      submissionDescData,
      handleSubmit,
      customUploadRequest,
      handleUploadChange,
      downloadFile,
      goBack
    };
  }
};
</script>

<style scoped>
.experiment-submit {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
.inner-card {
  border: 1px solid #e5e6eb;
  border-radius: 4px;
}
.file-operations {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.experiment-files, .submission-files {
  margin-top: 16px;
}
.score-tag {
  margin-left: 16px;
  font-size: 14px;
  color: #ff7d00;
  font-weight: bold;
}
</style> 