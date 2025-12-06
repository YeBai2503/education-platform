<template>
    <div class="my-course">
        <a-tabs v-model:active-key="defaultTag" @change="tagChange">
            <a-tab-pane key="student">
                <template #title>
                    <icon-calendar /> 我学的课
                </template>
            </a-tab-pane>
            <a-tab-pane key="teacher">
                <template #title>
                    <icon-clock-circle /> 我教的课
                </template>
            </a-tab-pane>
        </a-tabs>
        <!-- 操作区 -->
        <div class="course-operation">
            <a-button type="primary" @click="showAddModal(defaultTag=='student'?0:1)" shape="round">{{ defaultTag=='student'?'添加课程':'创建课程' }}</a-button>
            <a-radio-group type="button" @change="statusChange" default-value="0">
                <a-radio value="0">正在学</a-radio>
                <a-radio value="1">已完结</a-radio>
            </a-radio-group>
        </div>
        <!-- 课程列表 -->
        <div class="course-list" v-if="loading">
            <a-row :gutter="[15, 15]" class="course-list">
                <a-col :xs="24" :sm="12" :xl="8" :xxl="6"  v-for="item of 3" :key="item">
                    <a-skeleton class="course-item">
                        <a-space direction="vertical" size="large" style="width:100%">
                            <a-skeleton-shape class="course-picture" style="width:100%"/>
                            <a-skeleton-line :rows="2" />
                        </a-space>
                    </a-skeleton>
                </a-col>
            </a-row>
            
        </div>
        <div v-else-if="courseList.length != 0">
            <a-row :gutter="[15, 15]" class="course-list" :class="{'course-end':isEnd=='1'}">
                <a-col :xs="24" :sm="12" :xl="8" :xxl="6" v-for="item of courseList" :key="item.id"  @click="toCourse(item)">
                    <!-- <router-link :to="'/course/'+item.id"> -->
                        <div class="course-item" @mouseenter="handleCourseHover(item)">
                        <div class="course-picture">
                            <a-image width="100%" style="object-fit: cover;" height="100%" :src="getImageUrl(item.cover)"  show-loader/>
                            <div class="course-opera" v-if="defaultTag=='teacher'">
                                <span>{{item.status==0?"结课":"开课"}}</span>
                                <span @click.stop="showAddModal(2,item)">修改</span>
                                <span>{{item.isPublic==0?"公开":"隐藏"}}</span>
                            </div>
                            <div class="course-opera" v-else>
                                <span @click.stop="showQuitModal(item)">退课</span>
                                <span @click.stop="showRatingModal(item)">打分</span>
                                <!-- 移除这里的评分显示 -->
                            </div>
                            <!-- 添加单独的评分显示区域 -->
                            <div class="course-score-container" v-if="item.scoreData && (item.scoreData.scoreCount > 0)">
                                <div class="course-score">
                                    <icon-star-fill style="color: #FFD700; margin-right: 3px;" />
                                    {{ item.scoreData.sumScore }} ({{ item.scoreData.scoreCount }}人评)
                                </div>
                            </div>
                            <div class="course-score-container" v-else-if="item.loadingScore">
                                <div class="course-score">
                                    <a-spin size="small" />
                                </div>
                            </div>
                            <div class="course-score-container" v-else>
                                <div class="course-score">
                                    暂无评分
                                </div>
                            </div>
                        </div>
                        <div class="course-info">
                            <h3 class="title">{{ item.name }}</h3>
                            <p class="author">{{ item.teacher.nickname }}</p>
                            <!-- 添加评分显示（教师视图） -->
                            <div class="course-rating" v-if="defaultTag=='teacher'">
                                <div v-if="item.scoreData && (item.scoreData.scoreCount > 0)" class="rating-summary">
                                    <span class="rating-label">课程评分:</span>
                                    <span class="rating-stars">
                                        <icon-star-fill v-for="n in Math.floor(item.scoreData.sumScore)" :key="n" style="color: #FFD700;" />
                                    </span>
                                    <span class="rating-value">{{ item.scoreData.sumScore }}</span>
                                    <span class="rating-count">({{ item.scoreData.scoreCount }}人评)</span>
                                </div>
                                <div v-else-if="item.loadingScore" class="rating-summary">
                                    <a-spin size="small" />
                                </div>
                                <div v-else class="rating-summary">
                                    <span class="rating-label">暂无评分</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- </router-link> -->
                </a-col>
            </a-row>
            <a-pagination style="justify-content: center;margin:10px 0"  v-model:current="currPage"  @change="getCourseList"  :total="total" :current="currPage" :page-size="10" />
        </div>
        <a-empty v-else />
        <!-- 邀请码添加课程 -->
        <a-modal simple v-model:visible="addModalVisible" @ok="courseOk" :title="modalTitle">
            <a-form :model="stuAddInfo" v-if="defaultTag=='student'">
                <a-form-item field="name" label="邀请码">
                    <a-input v-model="stuAddInfo.code" placeholder="输入班级邀请码" />
                </a-form-item>
            </a-form>
            <a-form :model="teaAddInfo" v-else>
                <a-form-item field="name" label="课程名称">
                    <a-input v-model="teaAddInfo.name" placeholder="输入课程名称" />
                </a-form-item>
                <a-form-item field="name" label="课程简介">
                    <a-input v-model="teaAddInfo.introduce" placeholder="输入课程简介" />
                </a-form-item>
                <a-form-item field="name" label="课程封面">
                    <a-upload :show-file-list="false" :custom-request="customRequest"  list-type="picture-card">
                        <template #upload-button>
                            <a-avatar class="info-avatar" shape="square">
                                <template #trigger-icon>
                                    <icon-camera />
                                </template>
                                <img style="object-fit: cover;" v-if="teaAddInfo.cover" v-loadImg :src="teaAddInfo.cover" />
                                <icon-plus v-else  style="font-size: 25px;"/>
                            </a-avatar>
                        </template>
                    </a-upload>
                </a-form-item>
                <a-form-item field="name" label="课程公开">
                    <a-radio-group type="button" v-model:model-value="teaAddInfo.isPublic" default-value="0">
                        <a-radio value="0">隐藏</a-radio>
                        <a-radio value="1">公开</a-radio>
                    </a-radio-group>
                </a-form-item>
                <a-form-item field="name" label="课程状态" v-if="modalType!=1">
                    <a-radio-group type="button" v-model:model-value="teaAddInfo.status"  default-value="0">
                        <a-radio value="0">开课</a-radio>
                        <a-radio value="1">结课</a-radio>
                    </a-radio-group>
                </a-form-item>
            </a-form>
        </a-modal>
        
        <!-- 退课确认对话框 -->
        <a-modal simple v-model:visible="quitModalVisible" @ok="handleQuitClass" @cancel="quitModalVisible = false" title="退出课程">
            <div class="quit-course-modal">
                <p>确定要退出课程 <strong>{{ currentCourse.name }}</strong> 吗？</p>
                <p class="warning-text">注意：退出后将无法查看该课程的作业、考试和学习资料，且无法恢复！</p>
            </div>
        </a-modal>
        
        <!-- 课程打分弹窗 -->
        <a-modal simple v-model:visible="ratingModalVisible" @ok="submitRating" title="课程评分">
            <div class="rating-container">
                <p>请为 <strong>{{ currentCourse.name }}</strong> 课程各方面进行评分：</p>
                
                <div class="rating-item">
                    <span class="rating-label">作业评分：</span>
                    <a-rate v-model="courseRatings.homeworkScore" :allow-half="false" />
                    <span class="rating-value">{{ getRatingText(courseRatings.homeworkScore) }}</span>
                </div>
                
                <div class="rating-item">
                    <span class="rating-label">实验评分：</span>
                    <a-rate v-model="courseRatings.experimentScore" :allow-half="false" />
                    <span class="rating-value">{{ getRatingText(courseRatings.experimentScore) }}</span>
                </div>
                
                <div class="rating-item">
                    <span class="rating-label">视频评分：</span>
                    <a-rate v-model="courseRatings.videoScore" :allow-half="false" />
                    <span class="rating-value">{{ getRatingText(courseRatings.videoScore) }}</span>
                </div>
                
                <div class="rating-item">
                    <span class="rating-label">考试评分：</span>
                    <a-rate v-model="courseRatings.examScore" :allow-half="false" />
                    <span class="rating-value">{{ getRatingText(courseRatings.examScore) }}</span>
                </div>
                
                <div class="rating-item">
                    <span class="rating-label">项目评分：</span>
                    <a-rate v-model="courseRatings.projectScore" :allow-half="false" />
                    <span class="rating-value">{{ getRatingText(courseRatings.projectScore) }}</span>
                </div>
                
                <p class="rating-note">注：总体评分将由系统根据各项评分自动计算</p>
            </div>
        </a-modal>
    </div>
</template>
<script setup>
import { reactive, ref, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { courseListRequest, stuAddCourseRequest, teaCreateCourseRequest, rateCourse, getCourseScore, quitClassRequest } from '@/apis/course-api.js'
import useCourseStore from '../../sotre/course-store';
import {uploadCourseCover} from '../../apis/file-api'
import {imageUploadHandle,getImageUrl} from '../../utils/image'
import { Message } from '@arco-design/web-vue';
import useUserStore from '../../sotre/user-store';
import { IconStarFill } from '@arco-design/web-vue/es/icon';
const route = useRoute()
const router = useRouter()
const courseStore=useCourseStore()
const userStore = useUserStore();


const modalTitle=ref('');
//0 加入、1：创建、2：修改
const modalType=ref(0);
const addModalVisible = ref(false)
const ratingModalVisible = ref(false)
const quitModalVisible = ref(false)
const currentCourse = ref({})

// 使用对象来存储多维度评分
const courseRatings = reactive({
    homeworkScore: 0,
    experimentScore: 0,
    videoScore: 0,
    examScore: 0,
    projectScore: 0,
    courseId: 0,
    studentId: 0
})

const stuAddInfo = reactive({
    code: ""
})
const teaAddInfoState={
        cover:"",
        id:0,
        introduce:"",
        isPublic:"0",
        name:"",
        status:"0"
}
const teaAddInfo = reactive({...teaAddInfoState})
//选择标签
const defaultTag = ref(route.params.role)
// 是否结课
const isEnd = ref(0)
const currPage = ref(1)
const total = ref(0)
const loading = ref(false)
//课程列表
const courseList = ref([])
// 确定角色内容
const checkRole = () => {
    switch (defaultTag.value) {
        case 'teacher':
            defaultTag.value = 'teacher'
            break;
        case 'student':
        default:
            defaultTag.value = 'student'
    }
}
const customRequest = (options) => {
    return imageUploadHandle(options,uploadCourseCover,(res)=>{
      teaAddInfo.cover=res.data.data;
    })
};
//标签改变
const tagChange = () => {
    checkRole();
    router.push({
        name: "MyCourse",
        params: {
            role: defaultTag.value
        }
    })
    getCourseList()
    console.log(defaultTag.value)
}
//筛选改变
const statusChange = (value) => {
    isEnd.value = value;
    getCourseList();
}
const showAddModal = (type,data) => {
    //重置状态
    if(type==0){
        modalTitle.value="添加课程"
        modalType.value=0
        stuAddInfo.code=""
    }else if(type==1){
        modalTitle.value="创建课程"
        modalType.value=1
        Object.assign(teaAddInfo,teaAddInfoState)
    }else{
        if (defaultTag.value === 'student') {
            // 学生点击退课，不显示修改课程对话框
            return;
        }
        modalTitle.value="修改课程"
        modalType.value=2
        Object.assign(teaAddInfo,data)
    }
    addModalVisible.value = !addModalVisible.value
}

// 显示退课确认对话框
const showQuitModal = (course) => {
    event.stopPropagation(); // 阻止事件冒泡，避免触发课程详情
    currentCourse.value = course;
    quitModalVisible.value = true;
}

// 处理退课
const handleQuitClass = () => {
    // 显示加载提示
    const loadingMsg = Message.loading({
        content: '正在退出课程...',
        duration: 0
    });
    
    // 调用退课API，使用课程ID
    quitClassRequest(currentCourse.value.id)
        .then(({ data }) => {
            loadingMsg.close();
            
            if (data && data.code === '00001') {
                Message.success('退出课程成功');
                // 刷新课程列表
                getCourseList();
            } else {
                Message.error('退出课程失败: ' + (data?.msg || '未知错误'));
            }
        })
        .catch(error => {
            loadingMsg.close();
            console.error('退出课程失败:', error);
            Message.error('退出课程失败: ' + (error.message || '网络错误'));
        });
    
    // 关闭对话框
    quitModalVisible.value = false;
}

const getCourseList = () => {
    loading.value = true
    courseListRequest(defaultTag.value, currPage.value, isEnd.value)
        .then(({ data }) => {
            console.log(data)
            const result=data.data;
            courseList.value =result.list
            currPage.value = result.current
            total.value = result.total
            loading.value = false
        }).catch(err => {
            loading.value = false
        })
}
const courseOk=()=>{
    if(modalType.value==0){
        stuAddCourseRequest(stuAddInfo.code).then(()=>{
            getCourseList()
        })
        return
    }else if(modalType.value==1){
        delete teaAddInfo.id
    }
    teaCreateCourseRequest(teaAddInfo).then(()=>{
        getCourseList()
    })
}
const toCourse=(data)=>{
    courseStore.courseInfo=data
    // 设置用户角色
    courseStore.setUserRole(defaultTag.value)
    router.push({
        name:'CourseClassroom',
        params:{
            courseId:data.id
        }
    })
}
const showRatingModal = (course) => {
    currentCourse.value = course
    
    // 如果已经有评分数据，加载已有评分
    if (course.userRating) {
        // 这里模拟从课程对象中获取评分数据
        // 实际应用中可能需要单独API获取详细评分数据
        courseRatings.homeworkScore = course.homeworkScore || 0
        courseRatings.experimentScore = course.experimentScore || 0
        courseRatings.videoScore = course.videoScore || 0
        courseRatings.examScore = course.examScore || 0
        courseRatings.projectScore = course.projectScore || 0
    } else {
        // 重置评分
        courseRatings.homeworkScore = 0
        courseRatings.experimentScore = 0
        courseRatings.videoScore = 0
        courseRatings.examScore = 0
        courseRatings.projectScore = 0
    }
    
    // 设置课程ID
    courseRatings.courseId = course.id
    // 从用户存储中获取学生ID
    courseRatings.studentId = userStore.userInfo?.userId
    
    ratingModalVisible.value = true
}

const getRatingText = (rating) => {
    if (rating === 0) return '请选择评分'
    if (rating === 1) return '非常差'
    if (rating === 2) return '差'
    if (rating === 3) return '一般'
    if (rating === 4) return '好'
    return '非常好'
}

// 删除calculateAverageRating函数，因为总评分由后端计算

const submitRating = () => {
    // 检查是否至少有一项评分
    if (courseRatings.homeworkScore === 0 && 
        courseRatings.experimentScore === 0 && 
        courseRatings.videoScore === 0 && 
        courseRatings.examScore === 0 && 
        courseRatings.projectScore === 0) {
        Message.warning('请至少为课程的一个方面提供评分')
        return
    }
    
    // 显示加载提示
    const loadingMsg = Message.loading({
        content: '正在提交评分...',
        duration: 0
    })
    
    // 记录发送的数据
    console.log('提交评分数据:', courseRatings)
    
    // 调用API提交评分
    rateCourse(courseRatings)
        .then(({ data }) => {
            loadingMsg.close()
            
            // 记录API响应
            console.log('评分API响应:', data)
            
            if (data && data.code === '00001') {
                // 成功状态码，直接显示成功消息
                Message.success('评分提交成功')
                
                // 更新本地数据
                const courseIndex = courseList.value.findIndex(c => c.id === currentCourse.value.id)
                if (courseIndex !== -1) {
                    // 如果后端返回了总评分则使用，否则标记为已评分
                    courseList.value[courseIndex].userRating = data.sumScore || 5
                }
                
                // 关闭弹窗
                ratingModalVisible.value = false
            } else {
                // 后端返回错误状态码
                Message.error('评分提交失败: ' + (data?.msg || '未知错误'))
            }
        })
        .catch(error => {
            loadingMsg.close()
            console.error('提交评分失败:', error)
            Message.error('评分提交失败: ' + (error.message || '网络错误'))
        })
}

// 监听评分变化，在修改单项评分后可以选择是否自动更新总评分
// watch(
//     () => [
//         courseRatings.homeworkScore,
//         courseRatings.experimentScore,
//         courseRatings.videoScore,
//         courseRatings.examScore,
//         courseRatings.projectScore
//     ],
//     () => {
//         calculateAverageRating()
//     },
//     { deep: true }
// )
checkRole()
getCourseList()

// 处理课程卡片悬停，获取课程评分
const handleCourseHover = (course) => {
    // 如果已经有评分数据或正在加载，则不重复请求
    if (course.scoreData || course.loadingScore) {
        return;
    }
    
    // 标记为正在加载
    course.loadingScore = true;
    
    // 获取课程评分
    getCourseScore(course.id)
        .then(({ data }) => {
            // 移除加载标记
            course.loadingScore = false;
            
            console.log('课程评分响应:', data);
            
            // 检查是否有数据，不再严格检查状态码
            if (data && data.data) {
                // 保存评分数据到课程对象
                course.scoreData = data.data;
            } else if (data && data.msg === '一切ok') {
                // 特殊情况：后端返回"一切ok"但没有数据，可能表示暂无评分
                course.scoreData = { sumScore: 0, scoreCount: 0 };
            } else {
                console.warn('获取课程评分失败:', data?.msg);
                // 设置空评分数据，避免重复请求
                course.scoreData = { sumScore: 0, scoreCount: 0 };
            }
        })
        .catch(error => {
            // 移除加载标记
            course.loadingScore = false;
            console.error('获取课程评分失败:', error);
            // 设置空评分数据，避免重复请求
            course.scoreData = { sumScore: 0, scoreCount: 0 };
        });
};

</script>
<style lang="less">
.my-course {

    .course-operation {
        display: flex;
        justify-content: space-between;
        padding-bottom: 20px;
    }

    .course-list {
        .course-item {
            border-radius: 6px;
            overflow: hidden;
            cursor: pointer;
            box-sizing: border-box;
            border: 1px solid var(--color-border-2);
            transition: all .3s;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
            .course-picture{
                img{
                    transition: all .3s;
                    width: 100%;
                    height: 100%;
                    aspect-ratio: 2 / 1; 
                }
            }
            &:hover {
                box-shadow:0 0 20px rgba(0, 0, 0, 0.1);
                .course-picture {
                    .course-opera {
                        height: 100%;
                    }
                    img{
                        transform: scale(1.05);
                        filter: blur(2px);
                    }
                }

            }

            .course-picture {
                height: 160px;
                position: relative;
                .course-opera {
                    position: absolute;
                    top: 0px;
                    right: 0px;
                    height: 0;
                    left: 0;
                    bottom: 0;
                    overflow: hidden;
                    transition: height .3s;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    box-sizing: border-box;
                    span{
                        font-size: 14px;
                        display: inline-block;
                        height: 50px;
                        width: 50px;
                        text-align: center;
                        line-height: 50px;
                        border-radius: 30px;
                        color: var(--color-text-1);
                        background-color: var(--color-bg-1);
                        font-weight: bold;
                        margin: 0 5px;
                        &:hover{
                            background-color: var(--color-fill-2);
                        }
                    }
                }
                
                .course-score-container {
                    position: absolute;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    display: flex;
                    justify-content: center;
                    background-color: rgba(0, 0, 0, 0.6);
                    padding: 5px 0;
                    margin-top: 5px;
                    
                    .course-score {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        color: #fff;
                        font-size: 12px;
                    }
                }
            }
            .course-info{
                padding: 10px;
                .title {
                    margin: 10px 0;
                    color: var(--color-text-1);
                }

                .author {
                    color: var(--color-text-2);
                }
                .course-rating {
                    margin-top: 5px;
                    display: flex;
                    align-items: center;
                    
                    .rating-summary {
                        display: flex;
                        align-items: center;
                        font-size: 12px;
                        color: var(--color-text-2);
                        
                        .rating-label {
                            margin-right: 5px;
                        }
                        
                        .rating-stars {
                            display: flex;
                            margin-right: 5px;
                        }
                        
                        .rating-value {
                            font-weight: bold;
                            margin-right: 3px;
                        }
                        
                        .rating-count {
                            color: var(--color-text-3);
                        }
                    }
                }
            }

           
        }
    }
    .course-end{
        .course-item{
            filter: grayscale(1);
        }
    }
}
.info-avatar{
        height:100px;
        width:150px;
        .arco-avatar-text{
            overflow: hidden;
            border-radius: var(--border-radius-medium);
            width: 100%;
            height: 100%;
            position: relative;
            text-align: center;
            line-height: 100%;
            transform:none!important;
            left: 0;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        img{
            width: 100%;
            height: 100%;
        }
    }

.rating-container {
    padding: 10px 0;
    
    .rating-item {
        display: flex;
        align-items: center;
        margin: 15px 0;
        
        .rating-label {
            width: 100px;
            text-align: right;
            padding-right: 15px;
            flex-shrink: 0;
        }
        
        .rating-value {
            margin-left: 10px;
            min-width: 60px;
            color: var(--color-text-2);
        }
    }
    
    .rating-note {
        margin-top: 15px;
        text-align: center;
        color: var(--color-text-3);
        font-style: italic;
        font-size: 13px;
    }
}

.quit-course-modal {
    padding: 10px 0;
    text-align: center;
    
    p {
        margin: 10px 0;
    }
    
    .warning-text {
        color: var(--color-danger-light-4);
        font-weight: bold;
        margin-top: 15px;
    }
}
</style>