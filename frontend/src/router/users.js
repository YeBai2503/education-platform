const SettingIndex=()=>import("../views/user/Index.vue")
const Study=()=>import("../views/study/Study.vue")
const users = [
    {
        path: '/user',
        component: Study,
        name:'User',
        children: [
            {
                path:"setting",
                component:SettingIndex,
                name:'SettingIndex',
                meta:{
                    title:'个人信息'
                }
            },
        ]
    },
]
export default users;