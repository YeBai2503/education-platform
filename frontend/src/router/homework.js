export default [
    {
        path: "/homework/:homeworkId/start",
        name: "HomeworkStart",
        component: () => import("../views/homework/HomeworkStart.vue"),
        props: true,
        meta: {
            title: "开始作业",
            requireAuth: true,
        }
    },
    {
        path: "/homework/:homeworkId/view/:studentId?",
        name: "HomeworkView",
        component: () => import("../views/homework/HomeworkView.vue"),
        props: true,
        meta: {
            title: "查看作业",
            requireAuth: true,
        }
    },
    {
        path: "/homework/:homeworkId/review-list",
        name: "HomeworkReviewList",
        component: () => import("../views/homework/HomeworkReviewList.vue"),
        props: true,
        meta: {
            title: "作业批阅列表",
            requireAuth: true,
            requireTeacher: true,
        }
    },
    {
        path: "/homework/:homeworkId/review/:studentId",
        name: "HomeworkReview",
        component: () => import("../views/homework/HomeworkReview.vue"),
        props: true,
        meta: {
            title: "作业批阅",
            requireAuth: true,
            requireTeacher: true,
        }
    },
    {
        path: "/homework/paper-manager/:courseId",
        name: "HomeworkPaperManger",
        component: () => import("../views/homework/HomeworkPaperManger.vue"),
        props: true,
        meta: {
            title: "作业管理",
            requireAuth: true,
        }
    },
    {
        path: "/homework/paper/:courseId/:homeworkId?",
        name: "HomeworkPaper",
        component: () => import("../views/homework/HomeworkPaper.vue"),
        props: true,
        meta: {
            title: "作业组题",
            requireAuth: true,
        }
    },
    {
        path: "/homework/paper-preview/:homeworkId",
        name: "HomeworkPaperPreView",
        component: () => import("../views/homework/HomeworkPaperPreView.vue"),
        props: true,
        meta: {
            title: "作业预览",
            requireAuth: true,
        }
    },
    {
        path: "/homework/paper/automatic",
        name: "AutomaticHomeworkPaper",
        component: () => import("../views/homework/AutomaticHomeworkPaper.vue"),
        props: true,
        meta: {
            title: "自动组卷",
            requireAuth: true,
        }
    }
] 