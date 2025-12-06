import { defineAsyncComponent } from 'vue'

const TrainingBase = defineAsyncComponent(() => import('../views/training/TrainingBase.vue'))
const TrainingList = defineAsyncComponent(() => import('../views/training/TrainingList.vue'))
const MyTraining = defineAsyncComponent(() => import('../views/training/MyTraining.vue'))
const TrainingDetail = defineAsyncComponent(() => import('../views/training/TrainingDetail.vue'))
const TrainingEdit = defineAsyncComponent(() => import('../views/training/TrainingEdit.vue'))
const TeamManagement = defineAsyncComponent(() => import('../views/training/TeamManagement.vue'))
const TaskManagement = defineAsyncComponent(() => import('../views/training/TaskManagement.vue'))
const SubmitWork = defineAsyncComponent(() => import('../views/training/SubmitWork.vue'))

export default [
  {
    path: '/training',
    name: 'training',
    component: TrainingBase,
    meta: {
      title: "项目实训",
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'training-list',
        component: TrainingList,
        meta: {
          title: "实训项目列表",
          requiresAuth: true
        }
      },
      {
        path: 'my',
        name: 'my-training',
        component: MyTraining,
        meta: {
          title: "我的实训项目",
          requiresAuth: true
        }
      },
      {
        path: 'detail/:id',
        name: 'training-detail',
        component: TrainingDetail,
        meta: {
          title: "项目详情",
          requiresAuth: true
        }
      },
      {
        path: 'edit',
        name: 'training-create',
        component: TrainingEdit,
        meta: {
          title: "创建项目",
          requiresAuth: true
        }
      },
      {
        path: 'edit/:id',
        name: 'training-edit',
        component: TrainingEdit,
        meta: {
          title: "编辑项目",
          requiresAuth: true
        }
      },
      {
        path: ':id/team',
        name: 'team-management',
        component: TeamManagement,
        meta: {
          title: "团队管理",
          requiresAuth: true
        }
      },
      {
        path: ':id/task',
        name: 'task-management',
        component: TaskManagement,
        meta: {
          title: "任务管理",
          requiresAuth: true
        }
      },
      {
        path: ':projectId/task/:taskId/submit',
        name: 'submit-work',
        component: SubmitWork,
        meta: {
          title: "提交成果",
          requiresAuth: true
        }
      }
    ]
  }
]; 