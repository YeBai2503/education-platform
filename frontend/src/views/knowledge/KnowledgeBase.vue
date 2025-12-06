<template>
  <div class="knowledge-base-container">
    <div class="knowledge-header">
      <div class="knowledge-title">
        <h1>知识库管理</h1>
        <p>查看、搜索公开资源，管理自己的资源</p>
      </div>
      <div class="knowledge-tabs">
        <a-tabs :default-active-key="activeTab" @change="handleTabChange">
          <a-tab-pane key="list" title="资源列表">
            <template #icon><icon-book /></template>
          </a-tab-pane>
          <a-tab-pane key="my" title="我的资源">
            <template #icon><icon-user /></template>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
    <div class="knowledge-content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

// 根据当前路由计算激活的标签页
const activeTab = computed(() => {
  const path = route.path;
  if (path.includes('/knowledge/my')) {
    return 'my';
  } else {
    return 'list';
  }
});

// 处理标签页切换
const handleTabChange = (key) => {
  switch (key) {
    case 'list':
      router.push({ name: 'knowledge-list' });
      break;
    case 'my':
      router.push({ name: 'my-knowledge' });
      break;
    default:
      router.push({ name: 'knowledge-list' });
  }
};

// 组件挂载时，如果是根路径，默认跳转到资源列表
onMounted(() => {
  if (route.path === '/knowledge') {
    router.push({ name: 'knowledge-list' });
  }
});
</script>

<style lang="less" scoped>
.knowledge-base-container {
  padding-top: 72px;
  min-height: 100vh;
  background-color: var(--color-bg-2);

  .knowledge-header {
    background-color: var(--color-bg-1);
    padding: 20px 30px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);

    .knowledge-title {
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

  .knowledge-content {
    padding: 20px 30px;
  }
}
</style> 