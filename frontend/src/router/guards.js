import useCourseStore from "../sotre/course-store"
import authService from "../utils/auth-service"
import { Message } from '@arco-design/web-vue'
import useUserStore from "../sotre/user-store"

export const courseGuard = async (to, from) => {
  // 获取课程信息
  const courseId = to.params["courseId"]
  if (courseId) {
    const courseStore = useCourseStore()
    const courseInfo = courseStore.courseInfo
    
    // 如果课程ID变化或没有课程信息，则获取课程信息
    if (courseInfo.id != courseId) {
      await courseStore.getCourseInfo(courseId)
      
      // 如果是学习中心路由，还需要获取班级列表
      if (to.path.startsWith("/study/course/")) {
        await courseStore.getClassList(courseId)
      }
    }
  }
}

// 登录守卫
export const authGuard = async (to, from) => {
  // 如果路由明确设置为不需要登录，直接放行
  if (to.meta.requiresAuth === false) {
    return true;
  }
  
  // 检查是否已登录
  if (!authService.isLoggedIn()) {
    try {
      // 显示登录弹窗，等待用户登录
      await authService.showLoginModal(to.fullPath);
      // 登录成功，继续导航
      return true;
    } catch (error) {
      // 用户取消登录，返回上一页或首页
      Message.info('请先登录');
      if (from.name) {
        return false; // 返回上一页
      } else {
        return { path: '/' }; // 返回首页
      }
    }
  }
  
  // 用户已登录，继续导航
  return true;
}

// 检查用户角色权限
export const roleGuard = async (to) => {
  // 如果路由不需要角色检查，直接通过
  if (!to.meta.roles) {
    return true;
  }

  return true;
};
