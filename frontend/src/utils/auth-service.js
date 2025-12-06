import { ref } from 'vue';
import useUserStore from '../sotre/user-store';
import { useRouter } from 'vue-router';

// 创建一个单例模式的登录服务
class AuthService {
  constructor() {
    if (AuthService.instance) {
      return AuthService.instance;
    }
    
    AuthService.instance = this;
    
    // 登录弹窗状态
    this.loginModalVisible = ref(false);
    
    // 登录成功后的回调队列
    this.successCallbacks = [];
    
    // 登录取消后的回调队列
    this.cancelCallbacks = [];
    
    // 重定向路径
    this.redirectPath = ref('');
    
    // 路由实例
    this.router = null;
  }
  
  // 初始化路由
  initRouter() {
    if (!this.router) {
      this.router = useRouter();
    }
  }
  
  // 检查是否已登录
  isLoggedIn() {
    const userStore = useUserStore();
    return userStore.isLogin;
  }
  
  // 显示登录弹窗
  showLoginModal(redirectPath = '') {
    this.redirectPath.value = redirectPath;
    this.loginModalVisible.value = true;
    
    // 返回一个Promise，用于处理登录成功或取消的回调
    return new Promise((resolve, reject) => {
      this.successCallbacks.push(() => {
        resolve(true);
      });
      
      this.cancelCallbacks.push(() => {
        reject(new Error('用户取消登录'));
      });
    });
  }
  
  // 隐藏登录弹窗
  hideLoginModal() {
    this.loginModalVisible.value = false;
  }
  
  // 处理登录成功
  handleLoginSuccess() {
    // 执行所有成功回调
    if (this.successCallbacks && this.successCallbacks.length > 0) {
      this.successCallbacks.forEach(callback => callback());
    }
    
    // 清空回调队列
    this.successCallbacks = [];
    this.cancelCallbacks = [];
    
    // 处理重定向
    if (this.redirectPath.value) {
      // 初始化路由
      this.initRouter();
      
      // 如果有重定向路径，则进行跳转
      if (this.router) {
        this.router.push(this.redirectPath.value);
      } else {
        // 如果路由实例不可用，使用window.location进行跳转
        window.location.href = this.redirectPath.value;
      }
      
      // 清空重定向路径
      this.redirectPath.value = '';
    }
    
    // 隐藏弹窗
    this.hideLoginModal();
  }
  
  // 处理登录取消
  handleLoginCancel() {
    // 执行所有取消回调
    if (this.cancelCallbacks && this.cancelCallbacks.length > 0) {
      this.cancelCallbacks.forEach(callback => callback());
    }
    
    // 清空回调队列
    this.successCallbacks = [];
    this.cancelCallbacks = [];
    
    // 清空重定向路径
    this.redirectPath.value = '';
    
    // 隐藏弹窗
    this.hideLoginModal();
  }
  
  // 退出登录
  logout() {
    const userStore = useUserStore();
    userStore.logOut();
  }
}

// 创建单例并导出
const authServiceInstance = new AuthService();

// 导出isLogin函数，用于路由守卫
export const isLogin = () => authServiceInstance.isLoggedIn();

export default authServiceInstance;