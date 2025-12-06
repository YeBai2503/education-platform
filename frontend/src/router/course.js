const Study=()=>import("../views/study/Study.vue")
const Question=()=>import("../views/course/Question.vue")
const MyClasses=()=>import("../views/course/MyClasses.vue")
const Classroom=()=>import("../views/course/Classroom.vue")
const ClassUser=()=>import("../views/course/ClassUser.vue")
const BatchImportQuestion=()=>import("../views/course/BatchImportQuestion.vue")
const CourseIntro=()=>import("../views/course/CourseIntro.vue")
const CourseVideo=()=>import("../views/course/CourseVideo.vue")
const CourseVideoManage=()=>import("../views/course/CourseVideoManage.vue")

const HomeworkCenter=()=>import("../views/homework/HomeworkCenter.vue")
const HomeworkPaperManger=()=>import("../views/homework/HomeworkPaperManger.vue")
const HomeworkPaper=()=>import("../views/homework/HomeworkPaper.vue")
const HomeworkPaperPreView=()=>import("../views/homework/HomeworkPaperPreView.vue")
const HomeworkStart=()=>import("../views/homework/HomeworkStart.vue")
const HomeworkView=()=>import("../views/homework/HomeworkView.vue")
const HomeworkReviewList=()=>import("../views/homework/HomeworkReviewList.vue")
const HomeworkReview=()=>import("../views/homework/HomeworkReview.vue")

const ExamPaper=()=>import("../views/exam/ExamPaper.vue")
const ExamPaperManger=()=>import("../views/exam/ExamPaperManger.vue")
const AutomaticPaper=()=>import("../views/exam/AutomaticPaper.vue")
const ExamPaperPreView=()=>import("../views/exam/ExamPaperPreView.vue")

const ExamManage=()=>import("../views/exam/ExamManage.vue")

// 实验相关组件
const ExperimentList=()=>import("../views/course/experiment/ExperimentList.vue")
const ExperimentCreate=()=>import("../views/course/experiment/ExperimentCreate.vue")
const ExperimentDetail=()=>import("../views/course/experiment/ExperimentDetail.vue")
const ExperimentSubmit=()=>import("../views/course/experiment/ExperimentSubmit.vue")
const ExperimentSubmissions=()=>import("../views/course/experiment/ExperimentSubmissions.vue")

// 项目实训相关组件
const CourseTraining=()=>import("../views/course/training/CourseTraining.vue")
const ProjectList=()=>import("../views/course/experiment/ProjectList.vue")
const ProjectCreate=()=>import("../views/course/experiment/ProjectCreate.vue")
const ProjectDetail=()=>import("../views/course/experiment/ProjectDetail.vue")
const ProjectSubmit=()=>import("../views/course/experiment/ProjectSubmit.vue")
const ProjectSubmissions=()=>import("../views/course/experiment/ProjectSubmissions.vue")
const ProjectTeamJoin=()=>import("../views/course/experiment/ProjectTeamJoin.vue")
const ProjectTeamManage=()=>import("../views/course/experiment/ProjectTeamManage.vue")

// 重新设计路由结构
import CourseProblemCenter from "../views/course/CourseProblemCenter.vue"

const courses = [
    // 课程详情页面 - 不需要登录，公开可见
    {
        path: '/course/:courseId(\\d+)',
        component: CourseIntro,
        name: 'CourseDetail',
        meta: {
            title: '课程详情',
            requiresAuth: false,
            header: true
        }
    },
    // 课程学习中心 - 需要登录，包含课程的各个功能模块
    {
        path: '/study/course/:courseId(\\d+)',
        component: Study,
        name: 'CourseStudy',
        meta: {
            title: '学习中心',
            requiresAuth: true
        },
        children: [
            {
                path:"video",
                component:CourseVideo,
                name:'CourseVideo',
                props: true,
                meta:{
                    title:'课程视频'
                }
            },
            {
                path:"video/manage",
                component:CourseVideoManage,
                name:'CourseVideoManage',
                props: true,
                meta:{
                    title:'视频管理',
                    requireTeacher: true
                }
            },
            {
                path:"homework",
                component:HomeworkCenter,
                name:'Homework',
                props: true,
                meta:{
                    title:'作业中心'
                }
            },            
            {
                path:"homework/start/:homeworkId",
                component:HomeworkStart,
                name:'CourseHomeworkStart',
                props: true,
                meta:{
                    title:'开始作业'
                }
            },
            {
                path:"homework/view/:homeworkId",
                component:HomeworkView,
                name:'CourseHomeworkView',
                props: true,
                meta:{
                    title:'查看作业'
                }
            },
            {
                path:"homework/paper-manager",
                component:HomeworkPaperManger,
                name:'CourseHomeworkPaperManger',
                props: true,
                meta:{
                    title:'作业管理',
                    requireTeacher: true
                }
            },
            {
                path:"homework/paper/:homeworkId?",
                component:HomeworkPaper,
                name:'CourseHomeworkPaper',
                props: true,
                meta:{
                    title:'作业组题',
                    requireTeacher: true
                }
            },
            {
                path:"homework/paper-preview/:homeworkId",
                component:HomeworkPaperPreView,
                name:'CourseHomeworkPaperPreView',
                props: true,
                meta:{
                    title:'作业预览',
                    requireTeacher: true
                }
            },
            {
                path:"homework/review-list/:homeworkId",
                component:HomeworkReviewList,
                name:'CourseHomeworkReviewList',
                props: true,
                meta:{
                    title:'作业批阅',
                    requireTeacher: true
                }
            },
            {
                path:"homework/review/:homeworkId/:studentId",
                component:HomeworkReview,
                name:'CourseHomeworkReview',
                props: true,
                meta:{
                    title:'作业批阅',
                    requireTeacher: true
                }
            },
            // 默认页面 - 课堂学习
            {
                path: "",
                component: Classroom,
                name: 'CourseClassroom',
                meta: {
                    title: '课程学习'
                }
            },
            // 班级相关
            {
                path: "classes",
                component: MyClasses,
                name: 'CourseClasses',
                meta: {
                    title: '班级管理'
                }
            },
            {
                path: "class/:classId(\\d+)/users",
                component: ClassUser,
                name: 'ClassUsers',
                meta: {
                    title: '班级成员'
                }
            },
            // 题库相关
            {
                path: "questions",
                component: Question,
                name: 'CourseQuestions',
                meta: {
                    title: '课程题库'
                }
            },
            {
                path: "questions/import",
                component: BatchImportQuestion,
                name: 'ImportQuestions',
                meta: {
                    title: '批量导入题库'
                }
            },
            // 考试相关
            {
                path: "exams",
                component: ExamManage,
                name: 'CourseExams',
                meta: {
                    title: '考试中心'
                }
            },
            {
                path: "exams/papers",
                component: ExamPaperManger,
                name: 'ExamPapers',
                meta: {
                    title: '试卷管理'
                }
            },
            {
                path: "exams/paper/create/:examId(\\d+)?",
                component: ExamPaper,
                name: 'CreateExamPaper',
                meta: {
                    title: '创建试卷'
                }
            },
            {
                path: "exams/paper/preview/:examId(\\d+)",
                component: ExamPaperPreView,
                name: 'PreviewExamPaper',
                meta: {
                    title: '试卷预览'
                }
            },
            {
                path: "exams/paper/auto-generate",
                component: AutomaticPaper,
                name: 'AutoGeneratePaper',
                meta: {
                    title: '自动组卷'
                }
            },
            // 实验相关
            {
                path: "experiments",
                component: ExperimentList,
                name: 'CourseExperiments',
                meta: {
                    title: '实验管理'
                }
            },
            {
                path: "experiments/create",
                component: ExperimentCreate,
                name: 'ExperimentCreate',
                meta: {
                    title: '创建实验'
                }
            },
            {
                path: "experiments/:experimentId(\\d+)",
                component: ExperimentDetail,
                name: 'ExperimentDetail',
                meta: {
                    title: '实验详情'
                }
            },
            {
                path: "experiments/edit/:experimentId(\\d+)",
                component: ExperimentCreate,
                name: 'ExperimentEdit',
                meta: {
                    title: '编辑实验'
                }
            },
            {
                path: "experiments/:experimentId(\\d+)/submit",
                component: ExperimentSubmit,
                name: 'ExperimentSubmit',
                meta: {
                    title: '提交实验'
                }
            },
            {
                path: "experiments/:experimentId(\\d+)/submissions",
                component: ExperimentSubmissions,
                name: 'ExperimentSubmissions',
                meta: {
                    title: '实验提交列表'
                }
            },
            // 项目实训
            {
                path: "projects",
                component: ProjectList,
                name: 'CourseProjects',
                meta: {
                    title: '项目实训'
                }
            },
            {
                path: "projects/create",
                component: ProjectCreate,
                name: 'ProjectCreate',
                meta: {
                    title: '创建项目实训'
                }
            },
            {
                path: "projects/:experimentId(\\d+)",
                component: ProjectDetail,
                name: 'ProjectDetail',
                meta: {
                    title: '项目实训详情'
                }
            },
            {
                path: "projects/edit/:experimentId(\\d+)",
                component: ProjectCreate,
                name: 'ProjectEdit',
                meta: {
                    title: '编辑项目实训'
                }
            },
            {
                path: "projects/:experimentId(\\d+)/submit",
                component: ProjectSubmit,
                name: 'ProjectSubmit',
                meta: {
                    title: '提交项目'
                }
            },
            {
                path: "projects/:experimentId(\\d+)/submissions",
                component: ProjectSubmissions,
                name: 'ProjectSubmissions',
                meta: {
                    title: '项目提交列表'
                }
            },
            {
                path: "projects/:experimentId(\\d+)/team/join",
                component: ProjectTeamJoin,
                name: 'ProjectTeamJoin',
                meta: {
                    title: '加入团队'
                }
            },
            {
                path: "projects/:experimentId(\\d+)/team/:teamId(\\d+)",
                component: ProjectTeamManage,
                name: 'ProjectTeamManage',
                meta: {
                    title: '团队管理'
                }
            },
            // 问题中心
            {
                path: "problem-center",
                component: CourseProblemCenter,
                name: 'CourseProblemCenter',
                meta: {
                    title: '问题中心',
                    requiresAuth: true
                }
            }
        ]
    }
]

export default courses