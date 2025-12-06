const Land = () => import("../views/land/Land.vue")
const land = [
  {
    path:'/',
    component: Land,
    name: 'Land',
    meta: {
      title: "在线教育",
      header: false,
      requiresAuth: false // 不需要登录
    },
  },
]
export default land;