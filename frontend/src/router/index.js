import { createRouter, createWebHistory } from "vue-router";
import { courseGuard, authGuard, roleGuard } from "./guards";
import landRoutes from './land'
import homeRoutes from './home'
import studyRoutes from './study'
import courseRoutes from './course'
import examRoutes from './exam'
import userRoutes from './users'
import knowledgeRoutes from './knowledge'
import trainingRoutes from './training'

const base = import.meta.env.VITE_BASE;
const routes = [
  ...landRoutes,
  ...homeRoutes,
  ...studyRoutes,
  ...courseRoutes,
  ...examRoutes,
  ...userRoutes,
  ...knowledgeRoutes,
  ...trainingRoutes,
  {
    //404页面
    path: "/:pathMatch(.*)*",
    name: '404',
    component: () => import('../views/land/Land.vue'),
    meta: {
      requiresAuth: false
    }
  },
];
const router = createRouter({
  // 4. 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
  history: createWebHistory(base),
  routes, // `routes: routes` 的缩写
});

// 添加全局前置守卫
router.beforeEach(async (to, from) => {
  // 应用课程守卫
  if (to.path.startsWith("/course/") || to.path.startsWith("/study/course/")) {
    await courseGuard(to, from);
  }
  
  // 应用登录守卫
  const authResult = await authGuard(to, from);
  if (authResult !== true) {
    return authResult;
  }
  
  // 应用角色守卫
  const roleResult = await roleGuard(to);
  if (roleResult !== true) {
    return { name: 'home' }; // 如果角色检查失败，重定向到首页
  }
  
  return true;
});

export default router;
