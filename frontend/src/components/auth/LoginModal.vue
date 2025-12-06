<template>
  <a-modal
    :visible="visible"
    :title="title"
    :mask-closable="false"
    :footer="false"
    :width="400"
    @cancel="handleCancel"
    @update:visible="(val) => emit('update:visible', val)"
  >
    <div class="login-modal">
      <!-- 标签选项 -->
      <Tabs style="margin:20px 0" v-model:tag="loginType" :tagList="tagList" @tab-click="taggleTag" />
      
      <!-- 登录表单 -->
      <div class="tag_login" v-if="loginType === 0">
        <a-form :model="loginForm" @submit-success="handleLogin">
          <a-form-item field="username" :hide-label="true" :rules="form_rules.verfiName">
            <a-input
              size="large"
              v-model="loginForm.username"
              placeholder="输入用户名/邮箱地址"
            />
          </a-form-item>
          <a-form-item field="password" :hide-label="true" :rules="form_rules.password">
            <a-input-password
              size="large"
              v-model="loginForm.password"
              placeholder="输入密码（6~18个字符）"
            />
          </a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="loading"
            long
          >{{ tagList[loginType].name }}</a-button>
        </a-form>
      </div>
      
      <!-- 注册表单 -->
      <div class="tag_login" v-if="loginType === 1">
        <a-form :model="registerForm" ref="registerFormRef" @submit-success="handleRegister">
          <a-form-item field="email" :hide-label="true" :rules="form_rules.email">
            <a-input
              size="large"
              v-model="registerForm.email"
              placeholder="输入邮箱地址"
            />
          </a-form-item>
          <a-form-item field="verifiCode" :hide-label="true" :rules="form_rules.verifiCode">
            <a-input
              size="large"
              v-model="registerForm.verifiCode"
              placeholder="输入验证码（4个字符）"
            >
             <template #append>
                <a-button type="primary" :loading="emailLoading" @click="sendEmailCode('registerForm')">{{send_btn_text}}</a-button>
              </template>
            </a-input>
          </a-form-item>
          <a-form-item field="nickname" :hide-label="true">
            <a-input
              size="large"
              v-model="registerForm.nickname"
              placeholder="输入昵称"
            >
            </a-input>
          </a-form-item>
          <a-form-item field="username" :hide-label="true" :rules="form_rules.username">
            <a-input
              size="large"
              v-model="registerForm.username"
              placeholder="输入个人用户名/登录名"
            >
            </a-input>
          </a-form-item>
          <a-form-item field="password" :hide-label="true" :rules="form_rules.password">
            <a-input-password
              size="large"
              v-model="registerForm.password"
              placeholder="输入密码（6~18个字符）"
            />
          </a-form-item>
          <a-form-item field="checkPass" :hide-label="true" :rules="form_rules.checkPass">
            <a-input-password
              size="large"
              v-model="registerForm.checkPass"
              placeholder="确认密码（6~18个字符）"
            />
          </a-form-item>
          
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="loading"
            long
          >{{ tagList[loginType].name }}</a-button>
        </a-form>
      </div>
      
      <!-- 找回密码表单 -->
      <div class="tag_login" v-if="loginType === 2">
        <a-form :model="forgetForm" ref="forgetFormRef" @submit-success="handleForgetPassword">
          <a-form-item field="email" :hide-label="true" :rules="form_rules.email">
            <a-input
              size="large"
              v-model="forgetForm.email"
              placeholder="输入邮箱地址"
            />
          </a-form-item>
          <a-form-item field="verifiCode" :hide-label="true" :rules="form_rules.verifiCode">
            <a-input
              size="large"
              v-model="forgetForm.verifiCode"
              placeholder="输入验证码（4个字符）"
            >
             <template #append>
                <a-button type="primary" :loading="emailLoading" @click="sendEmailCode('forgetForm')">{{send_btn_text}}</a-button>
              </template>
            </a-input>
          </a-form-item>
          <a-form-item field="password" :hide-label="true" :rules="form_rules.password">
            <a-input-password
              size="large"
              v-model="forgetForm.password"
              placeholder="输入密码（6~18个字符）"
            />
          </a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="loading"
            long
          >{{ tagList[loginType].name }}</a-button>
        </a-form>
      </div>
      
      <div v-show="loginType === 0">
        <div class="protocol">
          点击「登录」表示已阅读并同意 <a class="protocol-link">服务条款</a>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { Message } from '@arco-design/web-vue';
import { loginRequest, registerRequest, forgetPassRequest, sendEmailCodeRequest } from '@/apis/auth-api.js';
import useUserStore from '../../sotre/user-store';
import Tabs from '@/components/Tabs.vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  redirectPath: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:visible', 'login-success', 'cancel']);

const app_name = import.meta.env.VITE_APP_NAME || '智慧学堂';

// 登录类型：0：登录，1：注册，2：找回密码
const loginType = ref(0);
const loading = ref(false);
const emailLoading = ref(false);
const registerFormRef = ref(null);
const forgetFormRef = ref(null);

const tagList = [
  {
    name: '登录',
    loginType: 0,
    title: `登录你的${app_name}账户`
  },
  {
    name: '注册',
    loginType: 1,
    title: `注册你的${app_name}账户`
  },
  {
    name: '找回密码',
    loginType: 2,
    title: `找回你的${app_name}账户`
  }
];

const title = computed(() => tagList[loginType.value].title);

const loginForm = reactive({
  username: '',
  password: '',
});

const registerForm = reactive({
  email: '',
  username: '',
  password: '',
  checkPass: '',
  verifiCode: '',
  nickname: ''
});

const forgetForm = reactive({
  email: '',
  password: '',
  verifiCode: ''
});

// 表单验证规则
const form_rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { minLength: 4, message: '用户名长度不能小于4个字符' }
  ],
  verfiName: [
    { required: true, message: '请输入用户名/邮箱' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { minLength: 6, message: '密码长度不能小于6个字符' },
    { maxLength: 18, message: '密码长度不能大于18个字符' }
  ],
  checkPass: [
    { required: true, message: '请确认密码' },
    {
      validator: (value, callback) => {
        if (value !== registerForm.password) {
          return callback('两次输入的密码不一致');
        }
        callback();
      }
    }
  ],
  email: [
    { required: true, message: '请输入邮箱地址' },
    {
      match: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
      message: '请输入正确的邮箱地址'
    }
  ],
  verifiCode: [
    { required: true, message: '请输入验证码' },
    { minLength: 4, message: '验证码长度不能小于4个字符' }
  ]
};

const userStore = useUserStore();

// 切换标签
const taggleTag = (val) => {
  loginType.value = val;
};

// 处理登录
const handleLogin = () => {
  loading.value = true;
  loginRequest(loginForm.username, loginForm.password)
    .then(res => {
      userStore.token = "Bearer " + res.data.access_token;
      return userStore.getUserInfo();
    })
    .then(() => {
      Message.success("登录成功");
      userStore.getBaseUserInfo();
      emit('login-success');
      emit('update:visible', false);
    })
    .catch(err => {
      console.error('登录失败:', err);
    })
    .finally(() => {
      loading.value = false;
    });
};

// 处理注册
const handleRegister = () => {
  loading.value = true;
  registerRequest(registerForm, registerForm.verifiCode)
    .then(res => {
      Message.success("注册成功，请登录");
      loginType.value = 0; // 切换到登录标签
    })
    .catch(e => {
      console.error('注册失败:', e);
    })
    .finally(() => {
      loading.value = false;
    });
};

// 处理找回密码
const handleForgetPassword = () => {
  loading.value = true;
  forgetPassRequest(forgetForm.email, forgetForm.verifiCode, forgetForm.password)
    .then(res => {
      Message.success("密码重置成功，请登录");
      loginType.value = 0; // 切换到登录标签
    })
    .catch(e => {
      console.error('密码重置失败:', e);
    })
    .finally(() => {
      loading.value = false;
    });
};

// 发送邮箱验证码
const send_btn_text = ref('获取验证码');
let interval_time = 60;
let timer = null;

const sendEmailCode = (formName) => {
  let email = '';
  if (formName === 'registerForm') {
    email = registerForm.email;
  } else if (formName === 'forgetForm') {
    email = forgetForm.email;
  }
  
  if (!email) {
    Message.error('请输入邮箱地址');
    return;
  }
  
  if (!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(email)) {
    Message.error('请输入正确的邮箱地址');
    return;
  }
  
  emailLoading.value = true;
  const type = formName === 'registerForm' ? 'register' : 'forget';
  
  sendEmailCodeRequest(type, email)
    .then(res => {
      Message.success('验证码已发送，请查收邮件');
      interval_time = 60;
      send_btn_text.value = `${interval_time}s`;
      
      if (timer) {
        clearInterval(timer);
      }
      
      timer = setInterval(() => {
        interval_time--;
        send_btn_text.value = `${interval_time}s`;
        if (interval_time <= 0) {
          clearInterval(timer);
          send_btn_text.value = '获取验证码';
        }
      }, 1000);
    })
    .catch(e => {
      console.error('发送验证码失败:', e);
    })
    .finally(() => {
      emailLoading.value = false;
    });
};

// 处理取消
const handleCancel = () => {
  emit('cancel');
  emit('update:visible', false);
};
</script>

<style lang="less" scoped>
.login-modal {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.protocol {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin: 20px 0;
  
  .protocol-link {
    color: #667eea;
    cursor: pointer;
  }
}
</style> 