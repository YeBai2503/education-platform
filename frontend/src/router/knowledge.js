export default [
  {
    path: "/knowledge",
    name: "knowledge",
    component: () => import("../views/knowledge/KnowledgeBase.vue"),
    meta: {
      title: "知识库",
      requiresAuth: true
    },
    children: [
      {
        path: "",
        name: "knowledge-list",
        component: () => import("../views/knowledge/KnowledgeList.vue"),
        meta: {
          title: "知识库资源列表",
          requiresAuth: true
        }
      },
      {
        path: "my",
        name: "my-knowledge",
        component: () => import("../views/knowledge/MyKnowledge.vue"),
        meta: {
          title: "我的知识库",
          requiresAuth: true
        }
      },
      {
        path: "detail/:id",
        name: "knowledge-detail",
        component: () => import("../views/knowledge/KnowledgeDetail.vue"),
        meta: {
          title: "资源详情",
          requiresAuth: true
        }
      },
      {
        path: "edit/:id?",
        name: "knowledge-edit",
        component: () => import("../views/knowledge/KnowledgeEdit.vue"),
        meta: {
          title: "编辑资源",
          requiresAuth: true
        }
      }
    ]
  }
]; 