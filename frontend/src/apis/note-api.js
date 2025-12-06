import axios from '../utils/http'

/**
 * 获取用户课程列表
 * @param {string} role - 用户角色 (student/teacher)
 * @param {number} currentPage - 当前页码
 * @param {string} status - 课程状态 (0:开课/1:结课)
 * @returns {Promise} - 返回API请求Promise
 */
export const getCourseListRequest = (role, currentPage, status) => {
    return axios.get(`/uapi/courses/list/${role}?currentPage=${currentPage}&status=${status}`)
}

/**
 * 获取课程笔记列表
 * @param {string|number} courseId - 课程ID
 * @returns {Promise} - 返回API请求Promise
 */
export const getCourseNotesRequest = (courseId) => {
    return axios.get(`/napi/note/list/course/${courseId}`)
}

/**
 * 删除笔记
 * @param {string|number} noteId - 笔记ID
 * @returns {Promise} - 返回API请求Promise
 */
export const deleteNoteRequest = (noteId) => {
    return axios.delete(`/napi/note/delete/${noteId}`)
}

/**
 * 更新笔记内容
 * @param {string|number} noteId - 笔记ID
 * @param {Object} noteData - 笔记数据
 * @param {string|number} noteData.courseId - 课程ID
 * @param {string} noteData.context - 笔记内容
 * @returns {Promise} - 返回API请求Promise
 */
export const updateNoteRequest = (noteId, noteData) => {
    // 构建完整的请求数据
    const requestData = {
        id: noteId,
        courseId: noteData.courseId,
        context: noteData.context
    };
    return axios.put(`/napi/note/update`, requestData)
}

/**
 * 获取课程的笔记数量
 * 由于后端未实现直接获取数量的接口，这个函数会获取所有笔记然后计算数量
 * @param {string|number} courseId - 课程ID
 * @returns {Promise<number>} - 返回包含笔记数量的Promise
 */
export const getNotesCount = async (courseId) => {
    try {
        const response = await getCourseNotesRequest(courseId);
        if (response.data.code === "00000" && Array.isArray(response.data.data)) {
            return response.data.data.length;
        }
        return 0;
    } catch (error) {
        console.error(`获取课程${courseId}笔记数量失败:`, error);
        return 0;
    }
}

/**
 * 创建新笔记
 * @param {Object} noteData - 笔记数据
 * @param {string|number} noteData.courseId - 课程ID
 * @param {string} noteData.context - 笔记内容
 * @returns {Promise} - 返回API请求Promise
 */
export const createNoteRequest = (noteData) => {
    return axios.post('/napi/note/add', noteData)
} 