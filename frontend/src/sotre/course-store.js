import { defineStore } from "pinia";
import { getCourseInfoRequest, getClassListRequest } from "../apis/course-api";
import useUserStore from "./user-store";
import {IconApps, IconSelectAll, IconAt, IconBookmark, IconCommand, IconExperiment, IconCode, IconQuestionCircle,IconVideoCamera,IconRobot} from "@arco-design/web-vue/es/icon";


const useCourseStore = defineStore('course',{
  state: () => ({
    courseInfo: {},
    classList:[],
    userRole: 'student' // 默认为学生身份
  }),
  getters: {
    isTeacher(state) {
      const userStore = useUserStore();
      return userStore.userInfo?.userId == state.courseInfo?.userId;
    },
    menu() {
      const id = this.courseInfo["id"];
      const params = {
        courseId: id,
      };
      return [
        {
          name: "资源",
          icon: IconVideoCamera,
          key: "CourseVideo",
          params: params,
          visble: true,
        },
        {
          name: "课堂",
          icon: IconApps,
          key: "CourseClassroom",
          params: params,
          visble: true,
        },
        {
          name: "作业",
          icon: IconSelectAll,
          key: "Homework",
          params: {
            courseId: id
          },
          visble: true,
        },
        {
          name: "考试",
          icon: IconExperiment,
          key: "CourseExams",
          params: params,
          visble: true,
        },
        {
          name: "实验",
          icon: IconCode,
          key: "CourseExperiments",
          params: params,
          visble: true,
        },
        {
          name: "项目实训",
          icon: IconRobot,
          key: "CourseProjects",
          params: params,
          visble: true,
        },
        {
          name: "试卷",
          icon: IconAt,
          key: "ExamPapers",
          params: params,
          visble: this.isTeacher,
        },
        {
          name: "题库",
          icon: IconBookmark,
          key: "CourseQuestions",
          params: params,
          visble: this.isTeacher,
        },
        {
          name: "问题中心",
          icon: IconQuestionCircle,
          key: "CourseProblemCenter",
          params: params,
          visble: true,
        },
        {
          name: "班级",
          icon: IconCommand,
          key: "CourseClasses",
          params: params,
          visble: true,
        },
      ];
    },
  },
  actions: {
    async getCourseInfo(courseId) {
      const resp = await getCourseInfoRequest(courseId);
        this.courseInfo = resp.data.data;
    },
    async getClassList(courseId) {
      const resp = await getClassListRequest(courseId);
      this.classList = resp.data.data;
    },
    setUserRole(role) {
      this.userRole = role;
    }
  },
});
export default useCourseStore;
