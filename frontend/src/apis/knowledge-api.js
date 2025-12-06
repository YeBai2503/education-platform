import axios from '../utils/http'

/**
 * 获取知识库资源列表
 * @param {Object} params 查询参数 - type、tag、currentPage、pageSize
 * @returns {Promise}
 */
export const getKnowledgeList = (params) => {
  return axios.get('/fapi/knowledge/user', { params: params })
}

/**
 * 搜索知识库资源
 * @param {Object} params 搜索参数 - keyword、type、tag、currentPage、pageSize
 * @returns {Promise}
 */
export const searchKnowledge = (params) => {
  
  return axios.get('/fapi/knowledge/search', { params: params })
}

/**
 * 获取知识库资源详情
 * @param {String} id 资源ID
 * @returns {Promise}
 */
export const getKnowledgeDetail = (id) => {
  return axios.get(`/fapi/knowledge/${id}`)
}

/**
 * 上传知识库文件
 * @param {File} file 文件对象
 * @param {Object} params 参数对象 - name、isPublic、tag、type
 * @returns {Promise}
 */
export const uploadKnowledgeFile = (file, params = {}) => {
  const formData = new FormData();
  formData.append('file', file);
  
  // 将参数添加到formData中，而不是URL查询参数
  Object.keys(params).forEach(key => {
    formData.append(key, params[key]);
  });
  
  return axios.post('/fapi/knowledge/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除知识库资源
 * @param {String} id 资源ID
 * @returns {Promise}
 */
export const deleteKnowledge = (id) => {
  return axios.delete(`/fapi/knowledge/${id}`)
} 