import { http } from '@/utils/http'

/**
 * 获取实训项目列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getTrainingList(params) {
  return http.get('/api/training/list', { params })
}

/**
 * 获取我参与的实训项目
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getMyTrainingList(params) {
  return http.get('/api/training/my', { params })
}

/**
 * 获取实训项目详情
 * @param {String|Number} id 项目ID
 * @returns {Promise}
 */
export function getTrainingDetail(id) {
  return http.get(`/api/training/${id}`)
}

/**
 * 创建或更新实训项目
 * @param {Object} data 项目数据
 * @returns {Promise}
 */
export function saveTraining(data) {
  if (data.id) {
    return http.put(`/api/training/${data.id}`, data)
  } else {
    return http.post('/api/training', data)
  }
}

/**
 * 删除实训项目
 * @param {String|Number} id 项目ID
 * @returns {Promise}
 */
export function deleteTraining(id) {
  return http.delete(`/api/training/${id}`)
}

/**
 * 加入实训项目
 * @param {String|Number} id 项目ID
 * @returns {Promise}
 */
export function joinTraining(id) {
  return http.post(`/api/training/${id}/join`)
}

/**
 * 退出实训项目
 * @param {String|Number} id 项目ID
 * @returns {Promise}
 */
export function leaveTraining(id) {
  return http.post(`/api/training/${id}/leave`)
}

/**
 * 获取团队列表
 * @param {String|Number} trainingId 项目ID
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getTeamList(trainingId, params) {
  return http.get(`/api/training/${trainingId}/teams`, { params })
}

/**
 * 创建或更新团队
 * @param {String|Number} trainingId 项目ID
 * @param {Object} data 团队数据
 * @returns {Promise}
 */
export function manageTeam(trainingId, data) {
  if (data.id) {
    return http.put(`/api/training/${trainingId}/teams/${data.id}`, data)
  } else {
    return http.post(`/api/training/${trainingId}/teams`, data)
  }
}

/**
 * 删除团队
 * @param {String|Number} trainingId 项目ID
 * @param {String|Number} teamId 团队ID
 * @returns {Promise}
 */
export function deleteTeam(trainingId, teamId) {
  return http.delete(`/api/training/${trainingId}/teams/${teamId}`)
}

/**
 * 加入团队
 * @param {String|Number} trainingId 项目ID
 * @param {String|Number} teamId 团队ID
 * @returns {Promise}
 */
export function joinTeam(trainingId, teamId) {
  return http.post(`/api/training/${trainingId}/teams/${teamId}/join`)
}

/**
 * 获取任务列表
 * @param {String|Number} trainingId 项目ID
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getTaskList(trainingId, params) {
  return http.get(`/api/training/${trainingId}/tasks`, { params })
}

/**
 * 获取任务详情
 * @param {String|Number} trainingId 项目ID
 * @param {String|Number} taskId 任务ID
 * @returns {Promise}
 */
export function getTaskDetail(trainingId, taskId) {
  return http.get(`/api/training/${trainingId}/tasks/${taskId}`)
}

/**
 * 创建或更新任务
 * @param {String|Number} trainingId 项目ID
 * @param {Object} data 任务数据
 * @returns {Promise}
 */
export function saveTask(trainingId, data) {
  if (data.id) {
    return http.put(`/api/training/${trainingId}/tasks/${data.id}`, data)
  } else {
    return http.post(`/api/training/${trainingId}/tasks`, data)
  }
}

/**
 * 删除任务
 * @param {String|Number} trainingId 项目ID
 * @param {String|Number} taskId 任务ID
 * @returns {Promise}
 */
export function deleteTask(trainingId, taskId) {
  return http.delete(`/api/training/${trainingId}/tasks/${taskId}`)
}

/**
 * 提交任务
 * @param {String|Number} trainingId 项目ID
 * @param {String|Number} taskId 任务ID
 * @param {Object} data 提交数据
 * @returns {Promise}
 */
export function submitTask(trainingId, taskId, data) {
  return http.post(`/api/training/${trainingId}/tasks/${taskId}/submit`, data)
}

/**
 * 评分提交
 * @param {String|Number} submissionId 提交ID
 * @param {Object} data 评分数据
 * @returns {Promise}
 */
export function gradeSubmission(submissionId, data) {
  return http.post(`/api/submissions/${submissionId}/grade`, data)
}

/**
 * 获取项目统计数据
 * @param {String|Number} trainingId 项目ID
 * @returns {Promise}
 */
export function getTrainingStats(trainingId) {
  return http.get(`/api/training/${trainingId}/stats`)
} 