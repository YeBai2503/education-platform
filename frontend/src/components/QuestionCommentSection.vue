<template>
  <div class="comment-section">
    <a-divider>评论区</a-divider>
    
    <!-- 评论列表 -->
    <div v-if="loading" class="comment-list">
      <a-skeleton :animation="true" :loading="loading" v-for="i in 2" :key="i">
        <a-space direction="vertical" style="width: 100%">
          <a-skeleton-line :rows="1" :widths="['30%']" />
          <a-skeleton-line :rows="1" :widths="['70%']" />
        </a-space>
      </a-skeleton>
    </div>
    <div v-else-if="comments.length > 0" class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-id">#{{ comment.id }}</div>
        <div class="comment-author">
          <a-avatar size="small">
            <img v-if="comment.userAvatar" :src="comment.userAvatar" />
            <template v-else>{{ comment.username?.substring(0, 1) }}</template>
          </a-avatar>
          <span class="author-name">{{ comment.username }}</span>
          <span class="comment-time">{{ comment.createdAt }}</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        
        <!-- 子评论 -->
        <div v-if="comment.children && comment.children.length > 0" class="sub-comments">
          <div v-for="subComment in comment.children" :key="subComment.id" class="sub-comment-item">
            <div class="comment-author">
              <a-avatar size="mini">
                <img v-if="subComment.userAvatar" :src="subComment.userAvatar" />
                <template v-else>{{ subComment.username?.substring(0, 1) }}</template>
              </a-avatar>
              <span class="author-name">{{ subComment.username }}</span>
              <span class="comment-time">{{ subComment.createdAt }}</span>
            </div>
            <div class="comment-content">{{ subComment.content }}</div>
            <div class="comment-actions">
              <a-space>
                <!-- 添加子评论的回复按钮 -->
                <a-button type="text" size="mini" @click="showReplyInput(comment.id, subComment)">
                  回复
                </a-button>
                <!-- 当前用户的子评论才显示删除按钮 -->
                <a-button v-if="isCurrentUser(subComment.userId)" type="text" status="danger" size="mini" @click="confirmDelete(subComment.id)">
                  <template #icon><icon-delete /></template>
                  删除
                </a-button>
              </a-space>
            </div>
          </div>
        </div>
        
        <!-- 评论操作 -->
        <div class="comment-actions">
          <a-space>
            <a-button type="text" size="mini" @click="showReplyInput(comment.id)">
              回复
            </a-button>
            <!-- 当前用户的评论才显示删除按钮 -->
            <a-button v-if="isCurrentUser(comment.userId)" type="text" status="danger" size="mini" @click="confirmDelete(comment.id)">
              <template #icon><icon-delete /></template>
              删除
            </a-button>
          </a-space>
        </div>
        
        <!-- 回复输入框 -->
        <div v-if="replyingTo === comment.id" class="reply-input">
          <a-input-search
            v-model="replyContent"
            :placeholder="replyToUser ? `回复 @${replyToUser.username}` : '回复评论...'"
            button-text="回复"
            search-button
            @search="submitReply(comment.id)"
          />
        </div>
      </div>
    </div>
    <a-empty v-else description="暂无评论" />
    
    <!-- 添加评论 -->
    <div class="add-comment">
      <a-textarea 
        v-model="newComment" 
        placeholder="添加评论..." 
        :auto-size="{ minRows: 2, maxRows: 4 }" 
        :max-length="500"
        show-word-limit
        allow-clear
      />
      <div class="comment-actions">
        <a-button type="primary" @click="handleSubmitComment" :disabled="!newComment.trim()">
          <template #icon><icon-send /></template>
          提交评论
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, onMounted, watch } from 'vue';
import { Message, Modal } from '@arco-design/web-vue';
import { IconSend, IconDelete } from '@arco-design/web-vue/es/icon';
import { submitComment as postComment, deleteComment } from '../apis/problem-api';
import { getUserInfoById } from '../apis/user-api';
import useUserStore from '../sotre/user-store';

const userStore = useUserStore();
const userInfo = userStore.userInfo;

// 定义组件属性
const props = defineProps({
  // 问题ID
  relatedId: {
    type: Number,
    required: true
  },
  // 评论列表
  commentList: {
    type: Array,
    default: () => []
  },
  // 是否正在加载
  isLoading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['refresh']);

// 组件状态
const loading = ref(props.isLoading);
const comments = ref(props.commentList || []);
const newComment = ref('');
const replyContent = ref('');
const replyingTo = ref(null);
const replyToUser = ref(null); // 添加被回复用户对象

// 更新评论和子评论中的用户信息
const updateUserInfoForComments = async (commentArr) => {
  for (const comment of commentArr) {
    if (comment.userId) {
      try {
        const userRes = await getUserInfoById(comment.userId);
        const user = userRes.data;
        comment.userAvatar = user.picture;
        comment.username = user.nickname || user.username || '匿名用户';
      } catch (e) {
        comment.userAvatar = '';
        comment.username = '匿名用户';
      }
    }
    // 递归处理子评论
    if (comment.children && comment.children.length > 0) {
      await updateUserInfoForComments(comment.children);
    }
  }
};

const updateFromProps = async () => {
  loading.value = props.isLoading;
  comments.value = props.commentList || [];
  await updateUserInfoForComments(comments.value);
};

onMounted(() => {
  updateFromProps();
});

// 监听props变化
watch(() => props.commentList, async (newVal) => {
  comments.value = newVal || [];
  await updateUserInfoForComments(comments.value);
}, { deep: true });

watch(() => props.isLoading, (newVal) => {
  loading.value = newVal;
});

// 当问题ID改变时，重置回复状态
watch(() => props.relatedId, () => {
  replyingTo.value = null;
  replyContent.value = '';
  replyToUser.value = null;
});

// 显示回复输入框
const showReplyInput = (commentId, subComment = null) => {
  // 设置被回复用户
  if (subComment) {
    replyToUser.value = {
      id: subComment.id,
      username: subComment.username
    };
  } else {
    replyToUser.value = null;
  }
  
  // 如果点击的是当前已经打开的回复框，则关闭它
  if (replyingTo.value === commentId && 
      (!subComment || 
       (replyToUser.value && replyToUser.value.id === subComment.id))) {
    replyingTo.value = null;
    replyContent.value = '';
    replyToUser.value = null;
  } else {
    // 否则打开回复框
    replyingTo.value = commentId;
    replyContent.value = '';
  }
};

// 提交评论
const handleSubmitComment = () => {
  if (!newComment.value.trim()) {
    Message.warning('评论内容不能为空');
    return;
  }
  
  // 检查用户是否登录
  if (!userInfo || !userInfo.userId) {
    Message.warning('请先登录后再发表评论');
    return;
  }
  
  const commentData = {
    content: newComment.value,
    relatedId: props.relatedId,
    parentId: 0,  // 一级评论
    type: 1       // 添加评论类型字段
  };
  
  // 显示提交中状态
  const loadingMsg = Message.loading({
    content: '正在提交评论...',
    duration: 0
  });
  
  postComment(commentData)
    .then(({ data }) => {
      loadingMsg.close();
      
      if (data && data.code === '00000') {
        Message.success('评论提交成功');
        newComment.value = '';
        
        // 通知父组件刷新评论列表
        emit('refresh');
        
        // 如果返回了评论数据，直接添加到评论列表
        if (data.data) {
          comments.value.unshift(data.data);
        }
      } else {
        Message.error('提交失败: ' + (data?.msg || '未知错误'));
      }
    })
    .catch(error => {
      loadingMsg.close();
      console.error('提交评论失败:', error);
      Message.error('评论提交失败: ' + (error.message || '网络错误'));
    });
};

// 提交回复
const submitReply = (parentId) => {
  if (!replyContent.value.trim()) {
    Message.warning('回复内容不能为空');
    return;
  }
  
  // 检查用户是否登录
  if (!userInfo || !userInfo.userId) {
    Message.warning('请先登录后再发表回复');
    return;
  }
  
  // 准备回复内容，如果是回复子评论，添加@用户名
  let content = replyContent.value;
  if (replyToUser.value) {
    content = `@${replyToUser.value.username} ${content}`;
  }
  
  const replyData = {
    content: content,
    relatedId: props.relatedId,
    parentId: parentId,  // 父评论ID始终是一级评论的ID
    type: 1              // 添加评论类型字段
  };
  
  // 显示提交中状态
  const loadingMsg = Message.loading({
    content: '正在提交回复...',
    duration: 0
  });
  
  postComment(replyData)
    .then(({ data }) => {
      loadingMsg.close();
      
      if (data && data.code === '00000') {
        Message.success('回复提交成功');
        replyContent.value = '';
        replyingTo.value = null;
        replyToUser.value = null;
        
        // 通知父组件刷新评论列表
        emit('refresh');
        
        // 如果返回了回复数据，直接添加到评论列表
        if (data.data) {
          // 找到父评论
          const parentComment = comments.value.find(c => c.id === parentId);
          if (parentComment) {
            // 如果父评论没有子评论数组，创建一个
            if (!parentComment.children) {
              parentComment.children = [];
            }
            // 添加新回复到子评论数组
            parentComment.children.push(data.data);
          }
        }
      } else {
        Message.error('提交失败: ' + (data?.msg || '未知错误'));
      }
    })
    .catch(error => {
      loadingMsg.close();
      console.error('提交回复失败:', error);
      Message.error('回复提交失败: ' + (error.message || '网络错误'));
    });
};

// 检查是否是当前登录用户的评论
const isCurrentUser = (userId) => {
  return userInfo && userInfo.userId === userId;
};

// 确认删除评论
const confirmDelete = (commentId) => {
  Modal.confirm({
    title: '删除评论',
    content: '确定要删除这条评论吗？此操作不可恢复。',
    okText: '删除',
    cancelText: '取消',
    okButtonProps: { status: 'danger' },
    onOk: () => handleDeleteComment(commentId)
  });
};

// 删除评论
const handleDeleteComment = (commentId) => {
  const loadingMsg = Message.loading({
    content: '正在删除评论...',
    duration: 0
  });
  
  deleteComment(commentId)
    .then(({ data }) => {
      loadingMsg.close();
      
      if (data && data.code === '00000') {
        Message.success('评论删除成功');
        
        // 通知父组件刷新评论列表
        emit('refresh');
        
        // 在本地更新评论列表
        // 1. 先检查是否为一级评论
        const commentIndex = comments.value.findIndex(c => c.id === commentId);
        if (commentIndex !== -1) {
          // 是一级评论，直接从数组中移除
          comments.value.splice(commentIndex, 1);
          return;
        }
        
        // 2. 不是一级评论，可能是子评论，遍历查找
        for (let i = 0; i < comments.value.length; i++) {
          const comment = comments.value[i];
          if (comment.children && comment.children.length > 0) {
            const subIndex = comment.children.findIndex(sub => sub.id === commentId);
            if (subIndex !== -1) {
              // 找到子评论，从数组中移除
              comment.children.splice(subIndex, 1);
              break;
            }
          }
        }
      } else {
        Message.error('删除失败: ' + (data?.msg || '未知错误'));
      }
    })
    .catch(error => {
      loadingMsg.close();
      console.error('删除评论失败:', error);
      Message.error('删除失败: ' + (error.message || '网络错误'));
    });
};
</script>

<style lang="less" scoped>
.comment-section {
  margin-top: 20px;
  
  .comment-list {
    margin-bottom: 20px;
    
    .comment-item {
      padding: 12px;
      border-bottom: 1px dashed var(--color-border-2);
      position: relative;
      
      &:last-child {
        border-bottom: none;
      }
      
      .comment-id {
        position: absolute;
        top: 10px;
        right: 10px;
        font-size: 12px;
        color: var(--color-text-3);
        background-color: var(--color-fill-2);
        padding: 2px 6px;
        border-radius: 10px;
      }
      
      .comment-author {
        display: flex;
        align-items: center;
        margin-bottom: 5px;
        
        .author-name {
          margin-left: 8px;
          font-weight: 500;
          font-size: 14px;
        }
        
        .comment-time {
          margin-left: 10px;
          color: var(--color-text-3);
          font-size: 12px;
        }
      }
      
      .comment-content {
        margin-left: 28px;
        font-size: 14px;
        line-height: 1.6;
        white-space: pre-line;
      }
      
      .comment-actions {
        margin-top: 5px;
        display: flex;
        justify-content: flex-end;
      }
      
      .reply-input {
        margin-top: 10px;
        padding-left: 28px;
      }
      
      .sub-comments {
        margin-top: 10px;
        margin-left: 28px;
        padding: 5px 0;
        background-color: var(--color-fill-1);
        border-radius: 4px;
        
        .sub-comment-item {
          padding: 8px 12px;
          border-bottom: 1px dotted var(--color-border-2);
          
          &:last-child {
            border-bottom: none;
          }
          
          .comment-content {
            margin-left: 24px;
            font-size: 13px;
          }
          
          .comment-actions {
            margin-left: 24px;
            margin-top: 3px;
            
            .arco-btn {
              font-size: 12px;
              padding: 0 4px;
              height: 24px;
            }
          }
        }
      }
    }
  }
  
  .add-comment {
    margin-top: 10px;
    
    .comment-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 10px;
    }
  }
}
</style> 