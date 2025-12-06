<template>
  <div class="course-training">
    <!-- 页面仅作重定向用，实际内容在新组件中 -->
    <a-spin tip="跳转中..."></a-spin>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { 
  IconPlus, IconEdit, IconFile, IconImage, IconCode, 
  IconDownload 
} from '@arco-design/web-vue/es/icon';
import useCourseStore from '../../../sotre/course-store';

const router = useRouter();
const route = useRoute();
const courseId = route.params.courseId;

// 组件挂载后立即重定向到新的项目实训页面
onMounted(() => {
  router.replace({
    name: 'CourseProjects',
    params: { courseId }
  });
});
</script>

<style scoped>
.course-training {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}
</style> 