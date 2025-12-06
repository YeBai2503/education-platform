import axios from '../utils/http'

// 老师发布/修改作业信息
export const updateHomeworkInfoRequest = (homeworkData) => {
    return axios.post('/hapi/homework-info/update', homeworkData)
}

// 获取发布的作业详细信息
export const getHomeworkInfoDetailRequest = (homeworkInfoId) => {
    return axios.get(`/hapi/homework-info/detail/${homeworkInfoId}`)
}

// 老师删除发布的作业
export const deleteHomeworkInfoRequest = (homeworkInfoId) => {
    return axios.post(`/hapi/homework-info/delete/${homeworkInfoId}`)
}

// 学生获取发布的作业列表
export const getHomeworkInfoListRequest = (courseId, status = 0, page = 1, pageSize = 10) => {
    return axios.get(`/hapi/homework-info/list/${courseId}?status=${status}&page=${page}&pageSize=${pageSize}`)
}

// 获取作业发布信息
export const getHomeworkInfoRequest = (homeworkInfoId) => {
    return axios.get(`/hapi/homework-info/info/${homeworkInfoId}`)
}

// 删除自动组卷API

// 老师更新作业题目
export const updateHomeworkPaperRequest = (homeworkPaper, questions) => {
    return axios.post('/hapi/homework-paper/update', {
        homeworkPaper,
        questions
    })
}

// 老师删除组题作业
export const deleteHomeworkPaperRequest = (homeworkId) => {
    return axios.post(`/hapi/homework-paper/delete/${homeworkId}`)
}

// 获取作业组题信息
export const getHomeworkPaperDetailRequest = (homeworkId) => {
    return axios.get(`/hapi/homework-paper/detail/${homeworkId}`)
}

// 获取作业信息
export const getHomeworkPaperInfoRequest = (homeworkId) => {
    return axios.get(`/hapi/homework-paper/info/${homeworkId}`)
}

// 获取组题的作业列表
export const getHomeworkPaperListRequest = (courseId, page = 1, pageSize = 10) => {
    return axios.get(`/hapi/homework-paper/list/${courseId}?page=${page}&pageSize=${pageSize}`)
}

// 作业数据统计
export const getHomeworkStatisticsRequest = (homeworkId) => {
    return axios.get(`/hapi/homework-paper/statistics/${homeworkId}`)
}

// 学生获取作业列表
export const getHomeworkListRequest = (status = 0, page = 1, pageSize = 10) => {
    return axios.get(`/hapi/homework-info/list?status=${status}&page=${page}&pageSize=${pageSize}`)
}

// 获取作业提交状态
export const getHomeworkSubmitStatusRequest = (homeworkIds) => {
    return axios.post('/hapi/homework-info/submit-status', { homeworkIds })
}

// 获取作业详情（学生）
export const getHomeworkDetailRequest = (homeworkId) => {
    return axios.get(`/hapi/homework-answer/detail/${homeworkId}`)
}

// 学生开始作答
export const startHomeworkRequest = (homeworkInfoId) => {
    return axios.get(`/hapi/homework-answer/start/${homeworkInfoId}`)
}

// 学生提交作业
export const submitHomeworkRequest = (homeworkInfoId, answers) => {
    return axios.post(`/hapi/homework-answer/submit/${homeworkInfoId}`, answers)
}

// 获取待批阅学生列表
export const getHomeworkReviewStudentListRequest = (homeworkInfoId, page = 1, pageSize = 10, classId) => {
    // 确保classId是有效值时才添加到URL参数中
    let url = `/hapi/homework-review/student-list/${homeworkInfoId}?page=${page}&pageSize=${pageSize}`;
    
    // 只有当classId有值且不为空字符串时，才添加classId参数
    if (classId !== undefined && classId !== null && classId !== '') {
        url += `&classId=${classId}`;
    }
    
    console.log('发送学生列表请求URL:', url);
    return axios.get(url);
}

// 获取学生作业详情（教师批阅）
export const getHomeworkStudentDetailRequest = (homeworkInfoId, studentId) => {
    return axios.get(`/hapi/homework-review/student-detail/${homeworkInfoId}/${studentId}`)
}

// 批阅作业
export const reviewHomeworkRequest = (homeworkInfoId, studentId, reviewData) => {
    return axios.post(`/hapi/homework-review/review/${homeworkInfoId}/${studentId}`, reviewData)
}
 