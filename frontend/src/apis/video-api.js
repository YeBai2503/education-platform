import axios from '../utils/http'

/**
 * 获取课程的所有章节和小节（包含学习进度）
 * @param {number} courseId - 课程ID
 * @returns {Promise} - 返回章节和小节数据
 */
export const getCourseChaptersRequest = (courseId) => {
    return axios.get(`/vapi/video/course/${courseId}/chapters`)
}

/**
 * 获取小节详情
 * @param {number} sectionId - 小节ID
 * @returns {Promise} - 返回小节详情数据
 */
export const getSectionDetailRequest = (sectionId) => {
    return axios.get(`/vapi/video/section/${sectionId}`)
}

/**
 * 更新小节学习进度
 * @param {number} sectionId - 小节ID
 * @param {number} progress - 学习进度(0-100)
 * @returns {Promise} - 返回更新结果
 */
export const updateSectionProgressRequest = (sectionId, progress) => {
    return axios.post(`/vapi/video/section/${sectionId}/progress?progress=${progress}`)
}

/**
 * 更新章节
 * @param {number} chapterId - 章节ID
 * @param {Object} chapterData - 章节数据
 * @returns {Promise} - 返回更新后的章节数据
 */
export const updateChapterRequest = (chapterId, chapterData) => {
    return axios.put(`/vapi/video/chapter/${chapterId}`, chapterData)
}

/**
 * 添加或更新小节
 * @param {Object} sectionData - 小节数据，包含文件
 * @returns {Promise} - 返回添加或更新后的小节数据
 */
export const addOrUpdateSectionRequest = (sectionData) => {
    // 使用FormData处理文件上传
    const formData = new FormData()
    
    console.log('准备构建FormData:', sectionData);
    
    // 添加基本字段
    if (sectionData.id) formData.append('id', sectionData.id)
    formData.append('chapterId', sectionData.chapterId)
    if (sectionData.title) formData.append('title', sectionData.title)
    if (sectionData.description) formData.append('description', sectionData.description)
    if (sectionData.rank) formData.append('rank', sectionData.rank)
    
    // 添加文件
    if (sectionData.video) {
        console.log('添加视频文件到FormData:', sectionData.video instanceof File ? {
            name: sectionData.video.name,
            size: sectionData.video.size,
            type: sectionData.video.type
        } : '非文件对象');
        
        if (sectionData.video instanceof File) {
            formData.append('video', sectionData.video);
        } else {
            console.error('视频不是有效的File对象');
        }
    }
    
    if (sectionData.cover) {
        console.log('添加封面图片到FormData:', sectionData.cover instanceof File ? {
            name: sectionData.cover.name,
            size: sectionData.cover.size,
            type: sectionData.cover.type
        } : '非文件对象');
        
        if (sectionData.cover instanceof File) {
            formData.append('cover', sectionData.cover);
        } else {
            console.error('封面不是有效的File对象');
        }
    }
    
    // 检查FormData内容
    console.log('FormData内容检查:');
    for (let [key, value] of formData.entries()) {
        console.log(`${key}: ${value instanceof File ? `文件: ${value.name} (${value.size} bytes)` : value}`);
    }
    
    return axios.post('/vapi/video/section', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

/**
 * 删除小节
 * @param {number} sectionId - 小节ID
 * @returns {Promise} - 返回删除结果
 */
export const deleteSectionRequest = (sectionId) => {
    return axios.delete(`/vapi/video/section/${sectionId}`)
}

/**
 * 添加章节
 * @param {Object} chapterData - 章节数据
 * @returns {Promise} - 返回添加后的章节数据
 */
export const addChapterRequest = (chapterData) => {
    return axios.post('/vapi/video/chapter', chapterData)
}

/**
 * 删除章节
 * @param {number} chapterId - 章节ID
 * @returns {Promise} - 返回删除结果
 */
export const deleteChapterRequest = (chapterId) => {
    return axios.delete(`/vapi/video/chapter/${chapterId}`)
} 