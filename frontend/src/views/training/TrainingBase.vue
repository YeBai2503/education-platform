<template>
  <div class="training-base-container">
    <div class="training-header">
      <div class="training-title">
        <h1>项目实训</h1>
        <p>通过实际项目巩固所学知识，培养团队协作能力</p>
      </div>
      <div class="training-tabs">
        <a-tabs :default-active-key="activeTab" @change="handleTabChange">
          <a-tab-pane key="list" title="实训项目">
            <template #icon><icon-apps /></template>
          </a-tab-pane>
          <a-tab-pane key="my" title="我的项目">
            <template #icon><icon-user /></template>
          </a-tab-pane>
          <a-tab-pane key="create" title="创建项目">
            <template #icon><icon-plus /></template>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
    <div class="training-content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import useUserStore from '@/sotre/user-store';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 根据当前路由计算激活的标签页
const activeTab = computed(() => {
  const path = route.path;
  if (path.includes('/training/my')) {
    return 'my';
  } else if (path.includes('/training/edit') && !route.params.id) {
    return 'create';
  } else {
    return 'list';
  }
});

// 处理标签页切换
const handleTabChange = (key) => {
  switch (key) {
    case 'list':
      router.push({ name: 'training-list' });
      break;
    case 'my':
      router.push({ name: 'my-training' });
      break;
    case 'create':
      router.push({ name: 'training-edit' });
      break;
    default:
      router.push({ name: 'training-list' });
  }
};

// 组件挂载时，如果是根路径，默认跳转到实训项目列表
onMounted(() => {
  if (route.path === '/training') {
    router.push({ name: 'training-list' });
  }
  
  // 确保用户信息已加载
  if (!userStore.userInfo) {
    userStore.getUserInfo();
  }
});
</script>

<style lang="less" scoped>
.training-base-container {
  padding-top: 72px;
  min-height: 100vh;
  background-color: var(--color-bg-2);

  .training-header {
    background-color: var(--color-bg-1);
    padding: 20px 30px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);

    .training-title {
      margin-bottom: 20px;

      h1 {
        font-size: 24px;
        font-weight: bold;
        color: var(--color-text-1);
        margin-bottom: 8px;
      }

      p {
        color: var(--color-text-3);
        font-size: 14px;
      }
    }
  }

  .training-content {
    padding: 20px 30px;
  }
}
</style> 