<template>
    <div class="my-notes">
        <div class="notes-header">
            <h2>我的笔记</h2>
            <a-radio-group type="button" @change="statusChange" default-value="0">
                <a-radio value="0">开课</a-radio>
                <a-radio value="1">结课</a-radio>
            </a-radio-group>
        </div>

        <!-- 教师提示 -->
        <a-result v-if="userRole === 'teacher'" status="info" title="教师账号无需创建笔记" />

        <!-- 学生笔记内容 -->
        <div v-else>
            <!-- 加载状态 -->
            <div v-if="loading">
                <a-skeleton :animation="true" :loading="loading">
                    <a-space direction="vertical" style="width: 100%" :size="16">
                        <a-card v-for="i in 3" :key="i" :style="{ marginBottom: '16px' }">
                            <a-skeleton-line :rows="3" />
                        </a-card>
                    </a-space>
                </a-skeleton>
            </div>

            <!-- 课程列表 -->
            <div v-else-if="courseList.length > 0" class="course-list">
                <a-card 
                    v-for="course in courseList" 
                    :key="course.id" 
                    class="course-card"
                    @click="viewNotes(course)">
                    <div class="course-header">
                        <div class="course-info">
                            <a-avatar :size="50" class="course-avatar">
                                <img v-if="course.cover" v-loadImg :src="getImageUrl(course.cover)" />
                                <icon-book v-else />
                            </a-avatar>
                            <div class="course-details">
                                <h3>{{ course.name }}</h3>
                                <p>{{ course.teacher.nickname }}</p>
                            </div>
                        </div>
                        <a-badge :count="course.notesCount || 0" :dot="false">
                            <a-button type="primary" shape="circle">
                                <icon-file />
                            </a-button>
                        </a-badge>
                    </div>
                </a-card>

                <!-- 分页 -->
                <div class="pagination">
                    <a-pagination 
                        v-model:current="currentPage" 
                        :total="total" 
                        :page-size="10" 
                        @change="handlePageChange" />
                </div>
            </div>

            <!-- 空状态 -->
            <a-empty v-else description="暂无笔记" />
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import useUserStore from '../../sotre/user-store';
import { getImageUrl } from '../../utils/image';
import { Message } from '@arco-design/web-vue';
import { getCourseListRequest, getNotesCount } from '../../apis/note-api';

const router = useRouter();
const userStore = useUserStore();

// 用户角色
const userRole = ref('student'); // 默认为学生，实际应从用户信息中获取

// 课程状态（开课/结课）
const courseStatus = ref('0');

// 分页信息
const currentPage = ref(1);
const total = ref(0);

// 加载状态
const loading = ref(true);

// 课程列表
const courseList = ref([]);



// 获取课程列表
const fetchCourseList = async () => {
    loading.value = true;
    try {
        // 获取课程列表
        const role = userRole.value === 'teacher' ? 'teacher' : 'student';
        const response = await getCourseListRequest(role, currentPage.value, courseStatus.value);
        
        if (response.data.code === "00000") {
            const courseData = response.data.data;
            const courses = courseData.list;
            
            // 获取每个课程的笔记数量
            const coursesWithNoteCount = await Promise.all(
                courses.map(async (course) => {
                    try {
                        // 如果是学生角色，获取笔记数量
                        if (userRole.value === 'student') {
                            // 获取课程笔记数量
                            const notesCount = await getNotesCount(course.id);
                            return {
                                ...course,
                                notesCount: notesCount
                            };
                        }
                        return {
                            ...course,
                            notesCount: 0
                        };
                    } catch (error) {
                        console.error(`获取课程${course.id}笔记数量失败:`, error);
                        return {
                            ...course,
                            notesCount: 0
                        };
                    }
                })
            );
            
            courseList.value = coursesWithNoteCount;
            total.value = courseData.total;
            currentPage.value = courseData.current;
        } else {
            Message.error('获取课程列表失败');
        }
    } catch (error) {
        console.error('获取课程列表失败:', error);
        Message.error('获取课程列表失败');
    } finally {
        loading.value = false;
    }
};

// 状态改变
const statusChange = (value) => {
    courseStatus.value = value;
    currentPage.value = 1;
    fetchCourseList();
};

// 页码改变
const handlePageChange = (page) => {
    currentPage.value = page;
    fetchCourseList();
};

// 查看课程笔记
const viewNotes = (course) => {
    console.log('查看课程笔记:', course);
    router.push({
        name: 'CourseNotes',
        params: { courseId: course.id },
        query: { courseName: course.name }
    });
};

// 检查用户角色
const checkUserRole = () => {
    // 从用户信息中获取角色
    // 实际应用中应该从token或用户信息中获取
    if (userStore.userInfo && userStore.userInfo.role) {
        userRole.value = userStore.userInfo.role === 'teacher' ? 'teacher' : 'student';
    }
};

onMounted(() => {
    checkUserRole();
    fetchCourseList();
});
</script>

<style lang="less" scoped>
.my-notes {
    padding: 16px;

    .notes-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;

        h2 {
            margin: 0;
        }
    }

    .course-list {
        .course-card {
            margin-bottom: 16px;
            transition: all 0.3s;
            cursor: pointer;

            &:hover {
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                transform: translateY(-2px);
            }

            .course-header {
                display: flex;
                justify-content: space-between;
                align-items: center;

                .course-info {
                    display: flex;
                    align-items: center;

                    .course-avatar {
                        margin-right: 12px;
                        background-color: var(--color-fill-2);
                    }

                    .course-details {
                        h3 {
                            margin: 0 0 4px 0;
                            font-size: 16px;
                        }

                        p {
                            margin: 0;
                            color: var(--color-text-3);
                            font-size: 14px;
                        }
                    }
                }
            }
        }
    }

    .pagination {
        display: flex;
        justify-content: center;
        margin-top: 24px;
    }
}
</style> 