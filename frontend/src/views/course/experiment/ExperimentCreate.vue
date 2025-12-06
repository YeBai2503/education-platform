<template>
  <div class="experiment-create">
    <a-card class="general-card" :title="isEdit ? '编辑实验' : '创建实验'">
      <a-form
        ref="formRef"
        :model="formData"
        label-align="left"
        :style="{ width: '600px' }"
      >
        <a-form-item field="title" label="实验标题" required extra="实验标题为必填项">
          <a-input
            v-model="formData.title"
            placeholder="请输入实验标题"
            allow-clear
          />
        </a-form-item>
        <a-form-item field="detail" label="实验详情" extra="请输入实验的详细描述">
          <a-textarea
            v-model="formData.detail"
            placeholder="请输入实验详情"
            allow-clear
            :auto-size="{ minRows: 5, maxRows: 10 }"
          />
        </a-form-item>
        <a-form-item field="ddl" label="截止日期" extra="设置学生提交实验的截止日期">
          <a-date-picker
            v-model="formData.ddl"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择截止日期"
            style="width: 100%"
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
            {{ isEdit ? '保存修改' : '创建实验' }}
          </a-button>
          <a-button @click="goBack">取消</a-button>
        </a-space>
      </a-form>
    </a-card>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { 
  createExperimentRequest, 
  updateExperimentRequest, 
  getExperimentDetailRequest 
} from '../../../apis/course-api';
import useCourseStore from '../../../sotre/course-store';

export default {
  name: 'ExperimentCreate',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const formRef = ref(null);
    const courseStore = useCourseStore();
    
    const courseId = computed(() => route.params.courseId);
    const experimentId = computed(() => route.params.experimentId);
    const isEdit = computed(() => !!experimentId.value);
    const isTeacher = computed(() => courseStore.isTeacher);
    
    const submitting = ref(false);
    const fileList = ref([]);
    const filesToDelete = ref([]);  // 使用const确保ref对象本身不会被重新赋值
    
    const formData = reactive({
      title: '',
      detail: '',
      ddl: '',
      files: []
    });
    
    // 获取实验详情（编辑模式）
    const fetchExperimentDetail = async () => {
      if (!isEdit.value) return;
      
      try {
        const res = await getExperimentDetailRequest(experimentId.value);
        if (res.data.code === '00000') {
          const experiment = res.data.data;
          formData.title = experiment.title;
          formData.detail = experiment.detail;
          formData.ddl = experiment.ddl ? new Date(experiment.ddl) : '';
          
          // 处理已有的附件
          if (experiment.fileUrls && experiment.fileUrls.length > 0) {
            fileList.value = experiment.fileUrls.map((url, index) => {
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
          Message.error(res.data.msg || '获取实验详情失败');
          goBack();
        }
      } catch (error) {
        Message.error('获取实验详情失败');
        goBack();
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
      console.log('handleUploadChange',event);
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
        console.log('deletedFiles',deletedFiles);
        console.log('检测到删除的文件:', deletedFiles.map(f => f.name || '未命名'));
        
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
      console.log('标题值:', formData.title);
      
      // 手动验证标题是否为空
      if (!formData.title || formData.title.trim() === '') {
        Message.error('实验标题不能为空');
        return;
      }
      
      // 直接提交表单，跳过组件验证
      submitForm();
    };
    
    // 提交表单
    const submitForm = async () => {
      submitting.value = true;
      try {
        // 记录要删除的文件，用于调试
        if (filesToDelete && filesToDelete.value && filesToDelete.value.length > 0) {
          console.log('提交前的待删除文件列表:', filesToDelete.value);
        }
        
        let res;
        // 处理日期格式
        const formattedDdl = formData.ddl ? formData.ddl.toString() : null;
        
        if (isEdit.value) {
          // 更新实验
          res = await updateExperimentRequest(
            experimentId.value,
            formData.title,
            formData.detail,
            formattedDdl,
            filesToDelete ? filesToDelete.value : [],
            formData.files
          );
        } else {
          // 创建实验
          res = await createExperimentRequest(
            courseId.value,
            formData.title,
            formData.detail,
            formattedDdl,
            formData.files
          );
        }
        console.log(res);
        if (res.data.code === '00000') {
          Message.success(isEdit.value ? '更新实验成功' : '创建实验成功');
          goBack();
        } else {
          Message.error(res.data.msg || (isEdit.value ? '更新实验失败' : '创建实验失败'));
        }
      } catch (error) {
        console.error('提交出错:', error);
        Message.error(isEdit.value ? '更新实验失败' : '创建实验失败');
      } finally {
        submitting.value = false;
      }
    };

    // 返回上一页
    const goBack = () => {
      router.push({
        name: 'CourseExperiments',
        params: { courseId: courseId.value }
      });
    };

    onMounted(async () => {
      // 先获取课程信息，确保isTeacher计算属性能够正确计算
      await courseStore.getCourseInfo(courseId.value);
      
      // 如果不是教师，跳转回实验列表页
      if (!isTeacher.value) {
        Message.error('您没有权限创建或编辑实验');
        goBack();
        return;
      }
      
      // 如果是编辑模式，获取实验详情
      if (isEdit.value) {
        fetchExperimentDetail();
      }
    });

    return {
      formRef,
      formData,
      fileList,
      filesToDelete,  
      submitting,
      isEdit,
      handleSubmit,
      customUploadRequest,
      handleUploadChange,
      goBack
    };
  }
};
</script>

<style scoped>
.experiment-create {
  padding: 20px;
}
.general-card {
  margin-bottom: 20px;
}
.file-operations {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style> 