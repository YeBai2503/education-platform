import axios from '../utils/http'
// 查询课程列表
export const courseListRequest=(role,page,status)=>{
    return axios.get(`/uapi/courses/list/${role}?&currentPage=${page}&status=${status}`)
}
/// 添加课程
export const stuAddCourseRequest=(code)=>{
    return axios.post(`/uapi/classes/join/${code}`)
}
/// 教师添加/更新课程信息
export const teaCreateCourseRequest=(params)=>{
    return axios.post(`/uapi/courses/update`,params)
}
///获取课程班级列表
export const getClassListRequest=(courseId)=>{
    return axios.get(`/uapi/classes/${courseId}/list`)
}

// 退出课程
export const quitClassRequest=(courseId)=>{
    return axios.delete(`/uapi/join-class/quit-course/${courseId}`)
}

// 添加/修改编辑
export const teaAddClassRequest=(params)=>{
    return axios.post(`/uapi/classes/update`,params)
}
export const teaDelClassRequest=(classId)=>{
    return axios.post(`/uapi/classes/delete/${classId}`)
}
export const teaGetClassCodeRequest=(classId,anew=false)=>{
    return axios.get(`/uapi/classes/classCode/${classId}?anew=${anew}`)
}
export const getClassUsersRequest=(classId,currentPage,pageSize=10)=>{
    return axios.get(`/uapi/join-class/${classId}/student/list?currentPage=${currentPage}&pageSize=${pageSize}`)
}
export const getBatchClassUsersRequest=(classIds,currentPage,pageSize=10)=>{
    return axios.post(`/uapi/join-class/student/batch/list?currentPage=${currentPage}&pageSize=${pageSize}`,classIds)
}
export const getCourseInfoRequest=(courseId)=>{
    return axios.get(`/uapi/courses/getInfo?courseId=${courseId}`)
}

// 获取课程章节和视频列表
export const getCourseChaptersRequest = (courseId) => {
    return axios.get(`/vapi/video/public/course/${courseId}/chapters`)
}

// 获取教师的课程列表（用于知识库资源复用）
export const getTeacherCoursesRequest = () => {
    return axios.get('/uapi/courses/teacher/list')
}

// 搜索课程
export const searchCoursesRequest = (keyword, page = 1, pageSize = 9) => {
  return axios.get(`/uapi/courses/search?keyword=${encodeURIComponent(keyword)}&currentPage=${page}&pageSize=${pageSize}`)
}

// 获取课程分类
export const getCourseCategoriesRequest = () => {
    return axios.get('/uapi/courses/categories')
}

// 实验相关API
// 分页查询课程下的实验列表
export const getExperimentListRequest = (courseId, current, size, type) => {
    const url = `/exam-experiment/experiment/publish/course/${courseId}/list?current=${current}&size=${size}${type !== undefined ? '&type=' + type : ''}`;
    return axios.get(url);
}

// 获取实验详情
export const getExperimentDetailRequest = (experimentId) => {
    return axios.get(`/exam-experiment/experiment/publish/detail/${experimentId}`)
}

// 创建实验
export const createExperimentRequest = (courseId, title, detail, ddl, files, type = '0') => {
    console.log('API - 创建实验，参数:', { courseId, title, detail, ddl, type });
    console.log('API - 上传文件数量:', files ? files.length : 0);
    // 创建FormData对象
    const formData = new FormData();
    
    // 添加文件到formData - 根据API文档，文件参数名应为file1, file2...file5
    if (files && files.length > 0) {
        console.log('API - 添加文件到FormData');
        files.forEach((file, index) => {
            if (index < 5) { // 最多支持5个文件
                const fileKey = `file${index + 1}`;
                // 确保我们传递的是原始File对象
                const fileObject = file instanceof File ? file : (file.file || file);
                console.log(`API - 添加文件 ${index + 1}:`, fileObject.name, '参数名:', fileKey);
                formData.append(fileKey, fileObject);
            }
        });
    }
    
    // API文档显示这些参数应作为query参数传递，而不是放在formData中
    // 构建URL查询参数
    const params = new URLSearchParams();
    params.append('courseId', courseId);
    params.append('title', title);
    if (detail) params.append('detail', detail);
    if (ddl) params.append('ddl', ddl);
    if (type) params.append('type', type); // 添加type参数
    
    const url = `/exam-experiment/experiment/publish/create?${params.toString()}`;
    console.log('API - 发送请求URL:', url);
    
    // 发送请求
    return axios.post(url, formData);
}

// 更新实验
export const updateExperimentRequest = (experimentId, title, detail, ddl, filesToDelete, files, type) => {
    console.log('API - 更新实验，参数:', { experimentId, title, detail, ddl, type });
    console.log('API - 要删除的文件:', filesToDelete);
    console.log('API - 上传文件数量:', files ? files.length : 0);
    
    // 创建FormData对象
    const formData = new FormData();
    
    // 添加文件到formData - 根据API文档，文件参数名应为file1, file2...file5
    if (files && files.length > 0) {
        console.log('API - 添加文件到FormData');
        files.forEach((file, index) => {
            if (index < 5) { // 最多支持5个文件
                const fileKey = `file${index + 1}`;
                // 确保我们传递的是原始File对象
                const fileObject = file instanceof File ? file : (file.file || file);
                console.log(`API - 添加文件 ${index + 1}:`, fileObject.name, '参数名:', fileKey);
                formData.append(fileKey, fileObject);
            }
        });
    }
    
    // 构建URL查询参数
    const params = new URLSearchParams();
    if (title) params.append('title', title);
    if (detail) params.append('detail', detail);
    if (ddl) params.append('ddl', ddl);
    if (type) params.append('type', type); // 添加type参数
    
    // 添加要删除的文件列表 - 直接传递完整URL
    if (filesToDelete && filesToDelete.length > 0) {
        // 方法1: 直接将完整URL添加到查询参数
        filesToDelete.forEach(fileUrl => {
            console.log('要删除的文件URL:', fileUrl);
            params.append('filesToDelete', fileUrl);
        });
        
        // 方法2: 将完整URL列表作为JSON字符串添加到表单数据中
        // 这是一个备选方案，如果方法1由于URL编码问题而失败
        formData.append('filesToDeleteJson', JSON.stringify(filesToDelete));
    }
    
    const url = `/exam-experiment/experiment/publish/update/${experimentId}?${params.toString()}`;
    console.log('API - 发送请求URL:', url);
    
    // 发送请求
    return axios.put(url, formData);
}

// 删除实验
export const deleteExperimentRequest = (experimentId) => {
    return axios.delete(`/exam-experiment/experiment/publish/delete/${experimentId}`)
}

// 统计课程下的实验数量
export const getExperimentCountRequest = (courseId) => {
    return axios.get(`/exam-experiment/experiment/publish/course/${courseId}/count`)
}

// 实验提交相关API
// 学生查看自己的实验提交详情
export const getStudentSubmitDetailRequest = (experimentId) => {
    return axios.get(`/exam-experiment/experiment/submit/student/experiment/${experimentId}/detail`)
}

// 检查学生是否已提交实验
export const checkStudentSubmitRequest = (experimentId) => {
    return axios.get(`/exam-experiment/experiment/submit/student/experiment/${experimentId}/check`)
}

// 学生提交实验
export const studentSubmitExperimentRequest = (experimentId, detail, files) => {
    console.log('API - 学生提交实验，参数:', { experimentId, detail });
    console.log('API - 上传文件数量:', files ? files.length : 0);
    
    // 创建FormData对象
    const formData = new FormData();
    
    // 添加基本参数
    formData.append('experimentId', experimentId);
    if (detail) formData.append('detail', detail);
    
    // 添加文件到formData - 根据API文档，文件参数名应为file1, file2...file5
    if (files && files.length > 0) {
        console.log('API - 添加文件到FormData');
        files.forEach((file, index) => {
            if (index < 5) { // 最多支持5个文件
                const fileKey = `file${index + 1}`;
                // 确保我们传递的是原始File对象
                const fileObject = file instanceof File ? file : (file.file || file);
                console.log(`API - 添加文件 ${index + 1}:`, fileObject.name, '参数名:', fileKey);
                formData.append(fileKey, fileObject);
            }
        });
    }
    
    // 发送请求
    return axios.post('/exam-experiment/experiment/submit/student/create', formData);
}

// 学生更新实验提交
export const updateStudentSubmitRequest = (submitId, detail, filesToDelete, files) => {
    console.log('API - 学生更新实验提交，参数:', { submitId, detail });
    console.log('API - 要删除的文件:', filesToDelete);
    console.log('API - 上传文件数量:', files ? files.length : 0);
    
    // 创建FormData对象
    const formData = new FormData();
    
    // 添加基本参数
    if (detail) formData.append('detail', detail);
    
    // 添加文件到formData
    if (files && files.length > 0) {
        console.log('API - 添加文件到FormData');
        files.forEach((file, index) => {
            if (index < 5) { // 最多支持5个文件
                const fileKey = `file${index + 1}`;
                // 确保我们传递的是原始File对象
                const fileObject = file instanceof File ? file : (file.file || file);
                console.log(`API - 添加文件 ${index + 1}:`, fileObject.name, '参数名:', fileKey);
                formData.append(fileKey, fileObject);
            }
        });
    }
    
    // 构建URL查询参数
    const params = new URLSearchParams();
    
    // 添加要删除的文件列表
    if (filesToDelete && filesToDelete.length > 0) {
        filesToDelete.forEach(fileUrl => {
            console.log('要删除的文件URL:', fileUrl);
            params.append('filesToDelete', fileUrl);
        });
    }
    
    const url = `/exam-experiment/experiment/submit/student/update/${submitId}?${params.toString()}`;
    console.log('API - 发送请求URL:', url);
    
    // 发送请求
    return axios.put(url, formData);
}

// 教师获取实验提交列表
export const getExperimentSubmitListRequest = (experimentId, current, size) => {
    return axios.get(`/exam-experiment/experiment/submit/teacher/experiment/${experimentId}/list?current=${current}&size=${size}`);
}

// 教师查看学生提交详情
export const getStudentSubmitDetailForTeacherRequest = (experimentId, studentId) => {
    return axios.get(`/exam-experiment/experiment/submit/teacher/experiment/${experimentId}/student/${studentId}/detail`);
}

// 教师评分
export const gradeStudentSubmitRequest = (submitId, score) => {
    return axios.put(`/exam-experiment/experiment/submit/teacher/grade/${submitId}?score=${score}`);
}

// 统计实验提交数量
export const getExperimentSubmitCountRequest = (experimentId) => {
    return axios.get(`/exam-experiment/experiment/submit/experiment/${experimentId}/count`);
}

// 检查用户是否已加入课程班级
export const checkUserInClassRequest = (courseId) => {
    return axios.get(`/uapi/classes/check/${courseId}`)
}

/**
 * 学生评分课程
 * 接口: /exam-user/course-score/rate
 * @param {Object} ratingData - 评分数据对象，包含各项评分
 * @returns {Promise<{code: string, data: boolean, msg: string}>} - 返回评分结果
 */
export const rateCourse = (ratingData) => {
    // 使用正确的API路径
    return axios.post('/uapi/course-score/rate', ratingData)
}

/**
 * 获取课程平均评分
 * 接口: /exam-user/course-score/course/{courseId}
 * @param {number} courseId - 课程ID
 * @returns {Promise<{code: string, data: CourseScoreVo, msg: string}>} - 返回课程评分数据
 */
export const getCourseScore = (courseId) => {
    return axios.get(`/uapi/course-score/course/${courseId}`)
}

/**
 * 获取课程评价列表（带用户信息）
 * 接口: /exam-user/course-score/list-with-user/{courseId}
 * @param {number} courseId - 课程ID
 * @param {number} page - 页码
 * @param {number} size - 每页大小
 * @returns {Promise<{code: string, data: Array, msg: string}>} - 返回评价列表
 */
export const getCourseReviewsRequest = (courseId, page = 1, size = 10) => {
    return axios.get(`/uapi/course-score/list-with-user/${courseId}?page=${page}&size=${size}`)
}

/**
 * 学生直接加入课程班级
 * 接口: /uapi/classes/join/course/{courseId}
 * @param {number} courseId - 课程ID
 * @returns {Promise<{code: string, data: boolean, msg: string}>} - 返回加入结果
 */
export const joinCourseDirectRequest = (courseId) => {
    return axios.post(`/uapi/courses/join/${courseId}`)
}
// 项目实训团队相关API
// 创建团队
export const createTeamRequest = (experimentId, name) => {
    const formData = new FormData();
    formData.append('experimentId', experimentId);
    formData.append('name', name);
    return axios.post('/exam-experiment/experiment/team/create', formData);
}

// 更新团队信息
export const updateTeamRequest = (teamId, data) => {
    return axios.put(`/exam-experiment/experiment/team/${teamId}`, data);
}

// 删除团队
export const deleteTeamRequest = (teamId) => {
    return axios.delete(`/exam-experiment/experiment/team/${teamId}`);
}

// 获取团队详情
export const getTeamDetailRequest = (teamId) => {
    return axios.get(`/exam-experiment/experiment/team/${teamId}`);
}

// 加入团队
export const joinTeamRequest = (teamId) => {
    return axios.post(`/exam-experiment/experiment/team/join/${teamId}`);
}

// 退出团队
export const leaveTeamRequest = (teamId) => {
    return axios.post(`/exam-experiment/experiment/team/leave/${teamId}`);
}

// 获取项目实训的所有团队
export const getTeamListRequest = (experimentId) => {
    return axios.get(`/exam-experiment/experiment/team/experiment/${experimentId}/list`);
}

// 获取用户在项目实训中的团队
export const getUserTeamRequest = (experimentId) => {
    return axios.get(`/exam-experiment/experiment/team/experiment/${experimentId}/my-team`);
}

// 检查用户是否已加入团队
export const checkUserJoinedTeamRequest = (experimentId) => {
    return axios.get(`/exam-experiment/experiment/team/experiment/${experimentId}/check-joined`);
}

// 检查用户是否为队长
export const checkUserIsHeaderRequest = (teamId) => {
    return axios.get(`/exam-experiment/experiment/team/${teamId}/check-header`);
}

// 获取实验提交列表
export const getExperimentSubmissionsRequest = (experimentId) => {
    return axios.get(`/exam-experiment/experiment/publish/submissions/${experimentId}`);
}

// 获取提交详情
export const getSubmissionDetailRequest = (submissionId) => {
    return axios.get(`/exam-experiment/experiment/submission/${submissionId}`);
}

// 给提交评分
export const scoreSubmissionRequest = (submissionId, score, comment) => {
    const data = {
        score: score
    };
    if (comment) {
        data.comment = comment;
    }
    return axios.post(`/exam-experiment/experiment/submission/${submissionId}/score`, data);
}

// 获取课程总人数
export const getCourseStudentCountRequest = (courseId) => {
    return axios.get(`/uapi/courses/getCourseStudentCount`, { params: { courseId } });
}

