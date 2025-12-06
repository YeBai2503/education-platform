const Study=()=>import("../views/study/Study.vue")
const MyCourse=()=>import("../views/study/MyCourse.vue")
const MyHomework=()=>import("../views/study/MyHomework.vue")
const MyExams=()=>import("../views/study/MyExams.vue")
const MyNotes=()=>import("../views/study/MyNotes.vue")
const CourseNotes=()=>import("../views/study/CourseNotes.vue")  //课程笔记
const NoteDetail=()=>import("../views/study/NoteDetail.vue")  //笔记详情
const study = [
  {
    path: '/study',
    component: Study,
    name:'Study',
    children: [
      {
          path:"student",
          component:MyCourse,
          name:'MyCourse',
          meta:{
              title:'我的课程'
          }
      },
      {
          path:"homework",
          component:MyHomework,
          name:'MyHomework',
          meta:{
              title:'我的作业'
          }
      },
      {
          path:"exams",
          component:MyExams,
          name:'MyExams',
          meta:{
              title:'我的考试'
          }
      },
      {
          path:"notes",
          component:MyNotes,
          name:'MyNotes',
          meta:{
              title:'我的笔记'
          }
      },
      {
          path:"notes/:courseId",
          component:CourseNotes,
          name:'CourseNotes',
          meta:{
              title:'课程笔记'
          }
      },
      {
          path:"notes/:courseId/:noteId",
          component:NoteDetail,
          name:'NoteDetail',
          meta:{
              title:'笔记详情'
          }
      },
    ]
  },
]
export default study;