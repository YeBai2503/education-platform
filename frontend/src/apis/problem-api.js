import http from '../utils/http'

/**
 * 根据课程ID获取问题列表
 * 接口: /exam-question/question/list/course/{courseId}
 * @param {number} courseId - 课程ID
 * @returns {Promise<{code: string, data: Array, msg: string}>} - 返回问题列表
 */
export function getQuestionListByCourseId(courseId) {
  return http.get(`/qapi/question/list/course/${courseId}`)
}

/**
 * 创建新问题
 * 接口: /exam-question/question/add
 * @param {Object} questionData - 问题数据 {title, content, courseId}
 * @returns {Promise<{code: string, data: QuestionVO, msg: string}>} - 返回创建结果
 */
export function createQuestion(questionData) {
  return http.post('/qapi/question/add', questionData)
}

/**
 * 获取问题详情
 * 接口: /exam-question/question/get/{id}
 * @param {number} questionId - 问题ID
 * @returns {Promise<{code: string, data: QuestionVO, msg: string}>} - 返回问题详情
 */
export function getQuestionDetail(questionId) {
  return http.get(`/qapi/question/get/${questionId}`)
}

/**
 * 获取问题回答列表
 * 接口: /exam-question/question/answer/list/{questionId}
 * @param {number} questionId - 问题ID
 * @returns {Promise<{code: string, data: Array, msg: string}>} - 返回问题回答列表
 */
export function getQuestionAnswers(questionId) {
  return http.get(`/qapi/question/answer/list/${questionId}`)
}

/**
 * 获取回答详情
 * 接口: /exam-question/question/answer/get/{id}
 * @param {number} answerId - 回答ID
 * @returns {Promise<{code: string, data: Object, msg: string}>} - 返回回答详情
 */
export function getAnswerDetail(answerId) {
  return http.get(`/qapi/question/answer/get/${answerId}`)
}

/**
 * 提交问题回答
 * 接口: /exam-question/question/answer/add
 * @param {Object} answerData - 回答数据 {questionId, content}
 * @returns {Promise<{code: string, data: AnswerVO, msg: string}>} - 返回提交结果
 */
export function submitAnswer(answerData) {
  return http.post('/qapi/question/answer/add', answerData)
}

/**
 * 标记最佳答案/采纳回答
 * 接口: /exam-question/question/answer/accept/{answerId}/{questionId}
 * @param {number} answerId - 回答ID
 * @param {number} questionId - 问题ID
 * @returns {Promise<{code: string, data: boolean, msg: string}>} - 返回标记结果
 */
export function markBestAnswer(answerId, questionId) {
  return http.put(`/qapi/question/answer/accept/${answerId}/${questionId}`)
}

/**
 * 点赞问题
 * 接口: /exam-question/question/{questionId}/like
 * @param {number} questionId - 问题ID
 * @returns {Promise} - 返回点赞结果
 */
export function likeQuestion(questionId) {
  return http.post(`/qapi/question/${questionId}/like`)
}

/**
 * 收藏问题
 * 接口: /exam-question/question/{questionId}/favorite
 * @param {number} questionId - 问题ID
 * @returns {Promise} - 返回收藏结果
 */
export function favoriteQuestion(questionId) {
  return http.post(`/qapi/question/${questionId}/favorite`)
}

/**
 * 点赞回答
 * 接口: /exam-question/question/answer/{answerId}/like
 * @param {number} answerId - 回答ID
 * @returns {Promise} - 返回点赞结果
 */
export function likeAnswer(answerId) {
  return http.post(`/qapi/question/answer/${answerId}/like`)
}

/**
 * 获取问题评论列表
 * 接口: /exam-question/question/comment/question/{questionId}
 * @param {number} questionId - 问题ID
 * @returns {Promise<{code: string, data: Array<CommentVO>, msg: string}>} - 返回评论列表
 */
export function getQuestionComments(questionId) {
  return http.get(`/qapi/question/comment/question/${questionId}`)
}

/**
 * 提交评论
 * 接口: /exam-question/question/comment/add
 * @param {Object} commentData - 评论数据 {content, questionId, parentId}
 * @returns {Promise<{code: string, data: CommentVO, msg: string}>} - 返回提交结果
 */
export function submitComment(commentData) {
  return http.post('/qapi/question/comment/add', commentData)
}

/**
 * 删除评论
 * 接口: /exam-question/question/comment/delete/{id}
 * @param {number} commentId - 评论ID
 * @returns {Promise<{code: string, data: boolean, msg: string}>} - 返回删除结果
 */
export function deleteComment(commentId) {
  return http.delete(`/qapi/question/comment/delete/${commentId}`)
}

/**
 * 删除问题
 * 接口: /exam-question/question/delete/{id}
 * @param {number} questionId - 问题ID
 * @returns {Promise<{code: string, data: boolean, msg: string}>} - 返回删除结果
 */
export function deleteQuestion(questionId) {
  return http.delete(`/qapi/question/delete/${questionId}`)
} 