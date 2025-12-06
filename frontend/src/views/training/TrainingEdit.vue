<template>
  <div class="training-edit">
    <a-page-header
      :title="isEdit ? '编辑项目' : '创建项目'"
      @back="goBack"
    />

    <div class="edit-content">
      <a-form
        ref="formRef"
        :model="formData"
        layout="vertical"
        :style="{ maxWidth: '800px', margin: '0 auto' }"
        @submit="handleSubmit"
      >
        <a-form-item field="title" label="项目标题" :rules="[{ required: true, message: '请输入项目标题' }]">
          <a-input v-model="formData.title" placeholder="请输入项目标题" />
        </a-form-item>

        <a-form-item field="coverImage" label="项目封面">
          <a-upload
            list-type="picture-card"
            :file-list="fileList"
            :limit="1"
            @change="handleCoverChange"
          >
            <template #upload-button>
              <div>
                <icon-plus />
                <div style="margin-top: 10px">上传封面</div>
              </div>
            </template>
          </a-upload>
        </a-form-item>

        <a-form-item field="tags" label="项目标签">
          <a-input-tag
            v-model="formData.tags"
            placeholder="请输入标签，按Enter确认"
            allow-clear
          />
        </a-form-item>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item field="difficulty" label="难度级别" :rules="[{ required: true, message: '请选择难度级别' }]">
              <a-select v-model="formData.difficulty" placeholder="请选择难度级别">
                <a-option value="beginner">入门级</a-option>
                <a-option value="intermediate">中级</a-option>
                <a-option value="advanced">高级</a-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item field="duration" label="项目周期" :rules="[{ required: true, message: '请输入项目周期' }]">
              <a-input v-model="formData.duration" placeholder="例如：4周" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item field="startTime" label="开始时间" :rules="[{ required: true, message: '请选择开始时间' }]">
              <a-date-picker v-model="formData.startTime" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item field="endTime" label="结束时间" :rules="[{ required: true, message: '请选择结束时间' }]">
              <a-date-picker v-model="formData.endTime" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item field="description" label="项目介绍" :rules="[{ required: true, message: '请输入项目介绍' }]">
          <a-textarea
            v-model="formData.description"
            placeholder="请输入项目介绍"
            :auto-size="{ minRows: 4, maxRows: 8 }"
          />
        </a-form-item>

        <a-form-item field="objectives" label="学习目标" :rules="[{ required: true, message: '请添加至少一个学习目标' }]">
          <div class="objectives-list">
            <div v-for="(objective, index) in formData.objectives" :key="index" class="objective-item">
              <a-input v-model="formData.objectives[index]" placeholder="请输入学习目标" />
              <a-button type="text" status="danger" @click="removeObjective(index)">
                <icon-delete />
              </a-button>
            </div>
            <a-button type="dashed" long @click="addObjective">
              <icon-plus />
              添加学习目标
            </a-button>
          </div>
        </a-form-item>

        <a-form-item field="requirements" label="项目要求" :rules="[{ required: true, message: '请输入项目要求' }]">
          <a-textarea
            v-model="formData.requirements"
            placeholder="请输入项目要求"
            :auto-size="{ minRows: 4, maxRows: 8 }"
          />
        </a-form-item>

        <a-form-item field="maxTeamMembers" label="团队人数上限" :rules="[{ required: true, message: '请设置团队人数上限' }]">
          <a-input-number v-model="formData.maxTeamMembers" :min="1" :max="10" style="width: 100%" />
        </a-form-item>

        <a-divider>任务安排</a-divider>

        <div v-if="formData.tasks.length === 0" class="empty-tasks">
          <a-empty description="暂无任务安排" />
          <a-button type="primary" @click="addTask">添加任务</a-button>
        </div>

        <div v-else class="tasks-list">
          <a-collapse>
            <a-collapse-item v-for="(task, index) in formData.tasks" :key="index" :header="`任务 ${index + 1}: ${task.title || '未命名任务'}`">
              <div class="task-form">
                <a-form-item :field="`tasks[${index}].title`" label="任务标题" :rules="[{ required: true, message: '请输入任务标题' }]">
                  <a-input v-model="task.title" placeholder="请输入任务标题" />
                </a-form-item>

                <a-form-item :field="`tasks[${index}].description`" label="任务描述" :rules="[{ required: true, message: '请输入任务描述' }]">
                  <a-textarea
                    v-model="task.description"
                    placeholder="请输入任务描述"
                    :auto-size="{ minRows: 2, maxRows: 4 }"
                  />
                </a-form-item>

                <a-form-item :field="`tasks[${index}].deadline`" label="截止日期" :rules="[{ required: true, message: '请选择截止日期' }]">
                  <a-date-picker v-model="task.deadline" style="width: 100%" />
                </a-form-item>

                <div class="task-actions">
                  <a-button status="danger" @click="removeTask(index)">删除任务</a-button>
                </div>
              </div>
            </a-collapse-item>
          </a-collapse>

          <a-button type="dashed" long style="margin-top: 16px" @click="addTask">
            <icon-plus />
            添加任务
          </a-button>
        </div>

        <div class="form-actions">
          <a-space>
            <a-button @click="goBack">取消</a-button>
            <a-button type="primary" html-type="submit">{{ isEdit ? '保存修改' : '创建项目' }}</a-button>
          </a-space>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { getTrainingDetail, saveTraining } from '@/apis/training-api';
import useUserStore from '@/sotre/user-store';

const router = useRouter();
const route = useRoute();
const formRef = ref(null);
const userStore = useUserStore();

// 判断是编辑还是创建
const isEdit = computed(() => !!route.params.id);

// 表单数据
const formData = reactive({
  title: '',
  coverImage: '',
  tags: [],
  difficulty: '',
  duration: '',
  startTime: '',
  endTime: '',
  description: '',
  objectives: [''],
  requirements: '',
  maxTeamMembers: 5,
  tasks: []
});

// 文件列表（用于封面上传）
const fileList = ref([]);

// 加载状态
const loading = ref(false);

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 添加学习目标
const addObjective = () => {
  formData.objectives.push('');
};

// 移除学习目标
const removeObjective = (index) => {
  formData.objectives.splice(index, 1);
  if (formData.objectives.length === 0) {
    formData.objectives.push('');
  }
};

// 添加任务
const addTask = () => {
  formData.tasks.push({
    title: '',
    description: '',
    deadline: ''
  });
};

// 移除任务
const removeTask = (index) => {
  formData.tasks.splice(index, 1);
};

// 处理封面变更
const handleCoverChange = (files) => {
  fileList.value = files;
  if (files.length > 0 && files[0].url) {
    formData.coverImage = files[0].url;
  } else {
    formData.coverImage = '';
  }
};

// 处理表单提交
const handleSubmit = async () => {
  const { validate } = formRef.value;
  try {
    await validate();
    loading.value = true;
    
    // 处理表单数据
    const submitData = {
      ...formData
    };
    
    if (isEdit.value) {
      submitData.id = route.params.id;
    }
    
    // 转换日期格式
    if (submitData.startTime) {
      submitData.startTime = new Date(submitData.startTime).toISOString().split('T')[0];
    }
    if (submitData.endTime) {
      submitData.endTime = new Date(submitData.endTime).toISOString().split('T')[0];
    }
    
    // 处理任务日期格式
    submitData.tasks = submitData.tasks.map(task => {
      if (task.deadline) {
        return {
          ...task,
          deadline: new Date(task.deadline).toISOString().split('T')[0]
        };
      }
      return task;
    });
    
    // 调用API保存项目
    await saveTraining(submitData);
    
    Message.success(isEdit.value ? '项目更新成功' : '项目创建成功');
    router.push({ name: 'my-training' });
  } catch (error) {
    console.error('表单验证失败或保存失败', error);
    if (error.response) {
      Message.error(error.response.data.message || '操作失败');
    } else {
      Message.error('操作失败，请检查表单内容');
    }
  } finally {
    loading.value = false;
  }
};

// 获取项目详情（编辑模式）
const fetchProjectDetail = async () => {
  if (!isEdit.value) return;
  
  loading.value = true;
  try {
    const res = await getTrainingDetail(route.params.id);
    
    if (res && res.data && res.data.data) {
      const projectData = res.data.data;
      
      // 填充表单数据
      formData.title = projectData.title;
      formData.coverImage = projectData.coverImage;
      formData.tags = projectData.tags || [];
      formData.difficulty = projectData.difficulty;
      formData.duration = projectData.duration;
      formData.startTime = projectData.startTime;
      formData.endTime = projectData.endTime;
      formData.description = projectData.description;
      formData.objectives = projectData.objectives || [''];
      formData.requirements = projectData.requirements;
      formData.maxTeamMembers = projectData.maxTeamMembers || 5;
      formData.tasks = projectData.tasks || [];
      
      // 设置封面预览
      if (projectData.coverImage) {
        fileList.value = [
          {
            uid: '1',
            name: 'cover.jpg',
            url: projectData.coverImage
          }
        ];
      }
    }
  } catch (error) {
    console.error('获取项目详情失败', error);
    Message.error('获取项目详情失败');
    router.push({ name: 'my-training' });
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  // 确保用户已登录
  if (!userStore.userInfo) {
    userStore.getUserInfo();
  }
  
  // 如果是编辑模式，获取项目详情
  if (isEdit.value) {
    fetchProjectDetail();
  }
});
</script>

<style lang="less" scoped>
.training-edit {
  .edit-content {
    padding: 24px;
    background-color: var(--color-bg-1);
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
  
  .objectives-list {
    .objective-item {
      display: flex;
      align-items: center;
      margin-bottom: 8px;
      
      .arco-input {
        margin-right: 8px;
      }
    }
  }
  
  .empty-tasks {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 24px 0;
    
    .arco-btn {
      margin-top: 16px;
    }
  }
  
  .tasks-list {
    margin-bottom: 24px;
    
    .task-form {
      padding: 8px 0;
    }
    
    .task-actions {
      display: flex;
      justify-content: flex-end;
    }
  }
  
  .form-actions {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style> 