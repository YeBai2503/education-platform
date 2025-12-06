<template>
    <div class="course-notes">
        <div class="notes-header">
            <div class="header-left">
                <a-button type="text" @click="goBack">
                    <icon-left />返回
                </a-button>
                <h2>{{ courseInfo.name || '课程笔记' }}</h2>
            </div>
            <a-button type="primary" @click="showAddNoteModal">
                <template #icon><icon-plus /></template>
                添加笔记
            </a-button>
            <div class="header-right">
                <a-input-search
                    v-model="searchKeyword"
                    placeholder="搜索笔记内容..."
                    search-button
                    @search="handleSearch"
                />
            </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
            <a-spin />
        </div>

        <!-- 笔记列表 -->
        <div v-else-if="notesList.length > 0" class="notes-list">
            <div v-for="note in notesList" :key="note.id" class="note-item">
                <a-card class="note-card" @click="viewNoteDetail(note)">
                    <div class="note-content">
                        <div class="note-text">
                            <md-preview 
                                :modelValue="note.context.length > 150 ? note.context.substring(0, 150) + '...' : note.context" 
                                language="zh-CN" 
                                :preview-theme="'github'"
                                :style="{ maxHeight: '150px', overflow: 'hidden' }" 
                            />
                        </div>
                        <div class="note-footer">
                            <span class="note-time">{{ formatDate(note.createdAt) }}</span>
                            <div class="note-actions">
                                <a-button type="text" status="danger" size="small" @click.stop="confirmDeleteNote(note)">
                                    <icon-delete />删除
                                </a-button>
                            </div>
                        </div>
                        <div class="note-id">ID: {{ note.id }} | 课程ID: {{ note.courseId }}</div>
                    </div>
                </a-card>
            </div>
        </div>

        <!-- 空状态 -->
        <a-empty v-else description="暂无笔记" />

        <!-- 删除确认对话框 -->
        <a-modal
            v-model:visible="deleteModalVisible"
            title="删除笔记"
            @ok="deleteNote"
            @cancel="cancelDelete"
            ok-text="删除"
            cancel-text="取消"
            :ok-button-props="{ status: 'danger' }"
        >
            <p>确定要删除这条笔记吗？此操作不可恢复。</p>
        </a-modal>

        <!-- 添加笔记对话框 -->
        <a-modal
            v-model:visible="addNoteModalVisible"
            title="添加笔记"
            @ok="saveNewNote"
            @cancel="cancelAddNote"
            ok-text="保存"
            cancel-text="取消"
            :ok-button-props="{ status: 'primary' }"
            width="800px"
            :footer="false"
        >
            <md-editor 
                v-model="newNoteContent" 
                language="zh-CN"
                :toolbars="toolbars"
                :preview="true"
                preview-theme="github"
                code-theme="atom"
                :style="{ height: '500px' }"
            />
            <div class="modal-footer">
                <a-button @click="cancelAddNote">取消</a-button>
                <a-button type="primary" @click="saveNewNote" :loading="isAddingNote">保存</a-button>
            </div>
        </a-modal>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';
import { getCourseNotesRequest, deleteNoteRequest, createNoteRequest } from '../../apis/note-api';
import { MdEditor, MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import { IconPlus, IconLeft, IconDelete } from '@arco-design/web-vue/es/icon';

const route = useRoute();
const router = useRouter();
const courseId = ref(route.params.courseId);

// 课程信息
const courseInfo = ref({});

// 笔记列表
const notesList = ref([]);

// 加载状态
const loading = ref(true);

// 删除确认对话框
const deleteModalVisible = ref(false);
const noteToDelete = ref(null);

// 添加笔记对话框
const addNoteModalVisible = ref(false);
const newNoteContent = ref('');
const isAddingNote = ref(false);

// Markdown编辑器工具栏配置
const toolbars = [
    'bold', 'italic', 'strikethrough', 'title', 'sub', 'sup', 'quote', 'unordered-list',
    'ordered-list', 'task-list', '-', 'code', 'inline-code', 'link', 'image', 'table',
    'mermaid', 'katex', '-', 'revoke', 'next', 'save', 'preview', 'fullscreen'
];

// 获取课程笔记
const fetchCourseNotes = async () => {
    loading.value = true;
    try {
        const response = await getCourseNotesRequest(courseId.value);
        
        if (response.data.code === "00000") {
            notesList.value = response.data.data;
            
            // 设置课程信息
            courseInfo.value = {
                id: courseId.value,
                name: route.query.courseName || '课程笔记'
            };
        } else {
            Message.error('获取笔记列表失败');
        }
    } catch (error) {
        console.error('获取笔记列表失败:', error);
        Message.error('获取笔记列表失败');
    } finally {
        loading.value = false;
    }
};

// 格式化日期
const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 返回上一页
const goBack = () => {
    router.back();
};

// 显示添加笔记对话框
const showAddNoteModal = () => {
    newNoteContent.value = '';
    addNoteModalVisible.value = true;
};

// 取消添加笔记
const cancelAddNote = () => {
    addNoteModalVisible.value = false;
    newNoteContent.value = '';
};

// 保存新笔记
const saveNewNote = async () => {
    if (isAddingNote.value) return; // 防止重复提交
    
    if (!newNoteContent.value.trim()) {
        Message.warning('笔记内容不能为空');
        return;
    }
    
    try {
        isAddingNote.value = true;
        
        // 显示加载状态
        const loadingMessage = Message.loading({
            content: '正在添加笔记...',
            duration: 0
        });
        
        const noteData = {
            courseId: courseId.value,
            context: newNoteContent.value
        };
        
        console.log('添加笔记数据:', noteData);
        
        const response = await createNoteRequest(noteData);
        
        // 关闭加载提示
        loadingMessage.close();
        
        if (response.data.code === "00000") {
            // 重新获取笔记列表
            fetchCourseNotes();
            Message.success('添加笔记成功');
            addNoteModalVisible.value = false;
            newNoteContent.value = ''; // 清空内容
        } else {
            Message.error(`添加笔记失败: ${response.data.msg || '未知错误'}`);
            console.error('API返回错误:', response.data);
        }
    } catch (error) {
        console.error('添加笔记失败:', error);
        
        // 显示更详细的错误信息
        if (error.response) {
            // 服务器返回了错误状态码
            console.error('HTTP状态码:', error.response.status);
            console.error('响应数据:', error.response.data);
            Message.error(`添加笔记失败: 服务器返回 ${error.response.status} 错误`);
        } else if (error.request) {
            // 请求已发送但没有收到响应
            Message.error('添加笔记失败: 服务器未响应');
        } else {
            // 请求设置时发生错误
            Message.error(`添加笔记失败: ${error.message || '未知错误'}`);
        }
    } finally {
        isAddingNote.value = false;
    }
};

// 查看笔记详情
const viewNoteDetail = (note) => {
    console.log('查看笔记详情:', note);
    router.push({
        name: 'NoteDetail',
        params: { 
            courseId: courseId.value,
            noteId: note.id 
        },
        query: { 
            courseName: courseInfo.value.name
        }
    });
};

// 确认删除笔记
const confirmDeleteNote = (note) => {
    noteToDelete.value = note;
    deleteModalVisible.value = true;
};

// 取消删除
const cancelDelete = () => {
    deleteModalVisible.value = false;
    noteToDelete.value = null;
};

// 删除笔记
const deleteNote = async () => {
    if (!noteToDelete.value) return;
    
    try {
        const response = await deleteNoteRequest(noteToDelete.value.id);
        
        if (response.data.code === "00000") {
            // 从列表中移除笔记
            notesList.value = notesList.value.filter(note => note.id !== noteToDelete.value.id);
            Message.success('删除笔记成功');
        } else {
            Message.error('删除笔记失败');
        }
    } catch (error) {
        console.error('删除笔记失败:', error);
        Message.error('删除笔记失败');
    } finally {
        deleteModalVisible.value = false;
        noteToDelete.value = null;
    }
};

onMounted(() => {
    fetchCourseNotes();
});
</script>

<style lang="less" scoped>
.course-notes {
    padding: 16px;

    .notes-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;

        .header-left {
            display: flex;
            align-items: center;
            
            h2 {
                margin: 0 0 0 12px;
            }
        }
    }

    .loading-container {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 200px;
    }

    .notes-list {
        .note-item {
            margin-bottom: 16px;
        }

        .note-card {
            border-radius: 8px;
            transition: all 0.3s;

            &:hover {
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                transform: translateY(-2px);
            }

            .note-content {
                .note-text {
                    font-size: 14px;
                    line-height: 1.6;
                    margin-bottom: 16px;
                    color: var(--color-text-1);
                    word-break: break-word;
                    max-height: 150px;
                    overflow: hidden;
                    
                    :deep(.md-preview) {
                        padding: 0;
                        border: none;
                        background: transparent;
                        
                        img {
                            max-height: 100px;
                        }
                        
                        h1, h2, h3 {
                            margin-top: 0;
                        }
                        
                        p {
                            margin: 0.5em 0;
                        }
                    }
                }

                .note-footer {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    
                    .note-time {
                        font-size: 12px;
                        color: var(--color-text-3);
                    }
                    
                    .note-actions {
                        display: flex;
                        gap: 8px;
                    }
                }
            }
        }
    }
}

.note-id {
    text-align: right;
    font-size: 10px;
    color: var(--color-text-4);
    margin-top: 4px;
    opacity: 0.6;
}

.modal-footer {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
    gap: 8px;
}
</style> 