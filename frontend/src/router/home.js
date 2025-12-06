const Home = () => import("../views/home/Home.vue")
const home = [
  {
    path: '/home',
    component: Home,
    name: 'Home',
    meta: {
      title: "首页",
      requiresAuth: false // 不需要登录
    },
  },
]
export default home;