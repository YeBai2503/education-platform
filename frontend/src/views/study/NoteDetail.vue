<template>
    <div class="note-detail">
        <div class="note-header">
            <div class="header-left">
                <a-button type="text" @click="goBack">
                    <icon-left />返回
                </a-button>
                <h2>{{ courseName || '课程笔记' }}</h2>
            </div>
            <div class="header-right">
                <span class="note-id-badge">笔记ID: {{ noteId }} | 课程ID: {{ courseId }}</span>
                <a-button 
                    :type="isEditing ? 'primary' : 'outline'" 
                    @click="toggleEditMode"
                    :loading="isEditing && isSaving">
                    <icon-edit v-if="!isEditing" />
                    <icon-check v-else />
                    {{ isEditing ? '保存' : '编辑' }}
                </a-button>
            </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
            <a-spin />
        </div>

        <!-- 笔记内容 -->
        <div v-else class="note-content">
            <!-- 编辑模式 -->
            <div v-if="isEditing" class="edit-mode">
                <md-editor 
                    v-model="editContent" 
                    language="zh-CN" 
                    :toolbars="toolbars"
                    :preview="true"
                    preview-theme="github"
                    code-theme="atom"
                    :style="{ height: 'calc(100vh - 230px)', maxHeight: 'none' }"
                />
                <div class="edit-toolbar">
                    <a-button @click="cancelEdit">取消</a-button>
                    <a-button type="primary" @click="saveNote" :loading="isSaving">保存</a-button>
                </div>
            </div>
            
            <!-- 查看模式 -->
            <div v-else class="view-mode">
                <div class="note-info">
                    <span class="note-time">创建时间：{{ formatDate(noteData.createdAt) }}</span>
                    <span class="note-time">更新时间：{{ formatDate(noteData.updatedAt) }}</span>
                </div>
                <a-divider />
                <div class="markdown-content">
                    <md-preview 
                        :modelValue="noteData.context" 
                        language="zh-CN"
                        preview-theme="github"
                        code-theme="atom"
                        :style="{ height: '100%', overflow: 'auto' }"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { getCourseNotesRequest, updateNoteRequest } from '../../apis/note-api';
import { MdEditor, MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

const route = useRoute();
const router = useRouter();
const courseId = ref(route.params.courseId);
const noteId = ref(route.params.noteId);
const courseName = ref(route.query.courseName);

// 笔记数据
const noteData = ref({
    id: null,
    userId: null,
    courseId: null,
    context: '',
    createdAt: '',
    updatedAt: ''
});

// 编辑内容
const editContent = ref('');

// 是否处于编辑模式
const isEditing = ref(false);

// 是否正在保存
const isSaving = ref(false);

// 加载状态
const loading = ref(true);

// Markdown编辑器工具栏配置
const toolbars = [
    'bold', 'italic', 'strikethrough', 'title', 'sub', 'sup', 'quote', 'unordered-list',
    'ordered-list', 'task-list', '-', 'code', 'inline-code', 'link', 'image', 'table',
    'mermaid', 'katex', '-', 'revoke', 'next', 'fullscreen'
];

// 获取笔记详情
const fetchNoteDetail = async () => {
    loading.value = true;
    try {
        const response = await getCourseNotesRequest(courseId.value);
        
        if (response.data.code === "00000") {
            const notes = response.data.data;
            const note = notes.find(n => n.id == noteId.value);
            
            if (note) {
                noteData.value = note;
                editContent.value = note.context;
            } else {
                Message.error('未找到该笔记');
                router.back();
            }
        } else {
            Message.error('获取笔记详情失败');
        }
    } catch (error) {
        console.error('获取笔记详情失败:', error);
        Message.error('获取笔记详情失败');
    } finally {
        loading.value = false;
    }
};

// 格式化日期
const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 返回上一页
const goBack = () => {
    router.back();
};

// 切换编辑模式
const toggleEditMode = () => {
    if (isEditing.value) {
        // 保存编辑
        saveNote();
    } else {
        // 进入编辑模式
        editContent.value = noteData.value.context;
        isEditing.value = true;
    }
};

// 取消编辑
const cancelEdit = () => {
    editContent.value = noteData.value.context;
    isEditing.value = false;
};

// 保存笔记
const saveNote = async () => {
    if (isSaving.value) return; // 防止重复提交
    
    try {
        isSaving.value = true;
        
        // 显示加载状态
        const loadingMessage = Message.loading({
            content: '正在保存笔记...',
            duration: 0
        });
        
        const noteDataToUpdate = {
            courseId: courseId.value,
            context: editContent.value
        };
        
        console.log('保存笔记数据:', {
            id: noteId.value,
            courseId: courseId.value,
            context: editContent.value
        });
        
        const response = await updateNoteRequest(noteId.value, noteDataToUpdate);
        
        // 关闭加载提示
        loadingMessage.close();
        
        if (response.data.code === "00000") {
            noteData.value.context = editContent.value;
            noteData.value.updatedAt = new Date().toISOString();
            isEditing.value = false;
            Message.success('笔记保存成功');
        } else {
            Message.error(`保存笔记失败: ${response.data.msg || '未知错误'}`);
            console.error('API返回错误:', response.data);
        }
    } catch (error) {
        console.error('保存笔记失败:', error);
        
        // 显示更详细的错误信息
        if (error.response) {
            // 服务器返回了错误状态码
            console.error('HTTP状态码:', error.response.status);
            console.error('响应数据:', error.response.data);
            Message.error(`保存笔记失败: 服务器返回 ${error.response.status} 错误`);
        } else if (error.request) {
            // 请求已发送但没有收到响应
            Message.error('保存笔记失败: 服务器未响应');
        } else {
            // 请求设置时发生错误
            Message.error(`保存笔记失败: ${error.message || '未知错误'}`);
        }
    } finally {
        isSaving.value = false;
    }
};

onMounted(() => {
    fetchNoteDetail();
});
</script>

<style lang="less" scoped>
.note-detail {
    padding: 16px;
    height: calc(100vh - 120px);
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .note-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;
        flex-shrink: 0;

        .header-left {
            display: flex;
            align-items: center;
            
            h2 {
                margin: 0 0 0 12px;
            }
        }

        .header-right {
            display: flex;
            align-items: center;
            
            .note-id-badge {
                margin-right: 12px;
                font-size: 12px;
                color: var(--color-text-3);
                background-color: var(--color-fill-2);
                padding: 2px 8px;
                border-radius: 10px;
                white-space: nowrap;
                max-width: 300px;
                overflow: hidden;
                text-overflow: ellipsis;
            }
        }
    }

    .loading-container {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 200px;
        flex-grow: 1;
    }

    .note-content {
        background-color: var(--color-bg-2);
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        overflow: hidden;

        .note-info {
            display: flex;
            justify-content: space-between;
            color: var(--color-text-3);
            font-size: 12px;
            margin-bottom: 16px;
            flex-shrink: 0;
        }

        .edit-mode {
            flex-grow: 1;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            
            :deep(.md-editor) {
                border-radius: 8px;
                flex-grow: 1;
                overflow: auto;
            }
            
            .edit-toolbar {
                display: flex;
                justify-content: flex-end;
                gap: 8px;
                margin-top: 16px;
                flex-shrink: 0;
            }
        }

        .view-mode {
            flex-grow: 1;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            
            .markdown-content {
                flex-grow: 1;
                overflow-y: auto;
                
                :deep(.md-preview) {
                    padding: 16px;
                    background-color: var(--color-bg-1);
                    border-radius: 8px;
                    box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.05);
                }
            }
        }
    }
}
</style> 