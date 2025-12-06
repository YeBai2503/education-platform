package com.baymax.exam.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.common.core.base.LoginUser;
import com.baymax.exam.question.enums.QuestionStatusEnum;
import com.baymax.exam.question.mapper.AnswerMapper;
import com.baymax.exam.question.model.Answer;
import com.baymax.exam.question.model.AnswerDTO;
import com.baymax.exam.question.model.AnswerVO;
import com.baymax.exam.question.model.Question;
import com.baymax.exam.question.service.AnswerService;
import com.baymax.exam.question.service.QuestionService;
import com.baymax.exam.user.feign.CourseClient;
import com.baymax.exam.user.model.Courses;
import com.baymax.exam.user.model.JoinClass;
import com.baymax.exam.web.utils.UserAuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description：回答Service实现类
 */
@Service
@Slf4j
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements AnswerService {

    private QuestionService questionService;
    private final CourseClient courseClient;
    
    @Autowired
    public AnswerServiceImpl(CourseClient courseClient) {
        this.courseClient = courseClient;
    }
    
    @Autowired
    public void setQuestionService(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnswerVO addAnswer(AnswerDTO answerDTO) {
        // 获取当前用户信息
        Integer userId = UserAuthUtil.getUserId();
        
        // 验证问题是否存在
        Question question = questionService.getById(answerDTO.getQuestionId());
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }
        
        // 验证用户是否属于该课程
        Integer courseId = question.getCourseId();
        JoinClass joinClass = courseClient.joinCourseByStuId(courseId, userId);
        if (joinClass == null) {
            // 查询课程信息，如果是课程创建者也允许回答问题
            Courses course = courseClient.findCourse(courseId);
            if (course == null || !course.getUserId().equals(userId)) {
                throw new RuntimeException("您不是该课程的学生或教师，无法回答此问题");
            }
        }
        
        // 创建回答实体并保存
        Answer answer = new Answer();
        BeanUtils.copyProperties(answerDTO, answer);
        answer.setUserId(userId);
        answer.setIsAccepted(0); // 默认未被采纳
        answer.setLikes(0);
        
        // 保存回答
        this.save(answer);
        
        // 更新问题回答数量
        questionService.updateQuestionAnswerCount(answerDTO.getQuestionId(), 1);
        
        // 构建返回结果
        AnswerVO answerVO = convertToVO(answer);
        LoginUser loginUser = UserAuthUtil.getUser();
        if (loginUser != null) {
            answerVO.setUsername(loginUser.getUsername());
        }
        
        return answerVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAnswer(AnswerDTO answerDTO) {
        // 获取当前用户信息
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("用户未登录或登录信息已失效，请重新登录");
        }
        
        // 查询回答是否存在
        Answer answer = this.getById(answerDTO.getId());
        if (answer == null) {
            return false;
        }
        
        // 判断是否为回答创建者
        if (!Objects.equals(answer.getUserId(), userId)) {
            return false;
        }
        
        // 不允许修改已采纳的回答
        if (answer.getIsAccepted() == 1) {
            return false;
        }
        
        // 更新回答
        answer.setContent(answerDTO.getContent());
        
        return this.updateById(answer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAnswer(Integer id) {
        // 获取当前用户信息
        Integer userId = UserAuthUtil.getUserId();
        
        // 查询回答是否存在
        Answer answer = this.getById(id);
        if (answer == null) {
            return false;
        }
        
        // 判断是否为回答创建者
        if (!Objects.equals(answer.getUserId(), userId)) {
            return false;
        }
        
        // 不允许删除已采纳的回答
        if (answer.getIsAccepted() == 1) {
            return false;
        }
        
        // 删除回答
        boolean result = this.removeById(id);
        
        // 更新问题回答数量
        if (result) {
            questionService.updateQuestionAnswerCount(answer.getQuestionId(), -1);
        }
        
        return result;
    }

    @Override
    public AnswerVO getAnswerById(Integer id) {
        // 查询回答
        Answer answer = this.getById(id);
        if (answer == null) {
            return null;
        }
        
        // 构建返回结果
        return convertToVO(answer);
    }

    @Override
    public List<AnswerVO> getAnswersByQuestionId(Integer questionId) {
        // 查询问题回答列表
        LambdaQueryWrapper<Answer> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Answer::getQuestionId, questionId)
               .orderByDesc(Answer::getIsAccepted)  // 已采纳的回答排在前面
               .orderByDesc(Answer::getLikes)       // 点赞多的排在前面
               .orderByDesc(Answer::getCreateTime); // 按创建时间降序
        
        List<Answer> answers = this.list(wrapper);
        
        // 转换为VO列表
        return answers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptAnswer(Integer answerId, Integer questionId) {
        // 获取当前用户信息
        Integer userId = UserAuthUtil.getUserId();
        
        // 查询问题是否存在
        Question question = questionService.getById(questionId);
        if (question == null) {
            return false;
        }
        
        // 判断是否为问题创建者
        if (!Objects.equals(question.getUserId(), userId)) {
            return false;
        }
        
        // 查询回答是否存在
        Answer answer = this.getById(answerId);
        if (answer == null || !Objects.equals(answer.getQuestionId(), questionId)) {
            return false;
        }
        
        // 将问题下所有回答设为未采纳
        LambdaUpdateWrapper<Answer> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Answer::getQuestionId, questionId)
               .set(Answer::getIsAccepted, 0);
        this.update(wrapper);
        
        // 将当前回答设为已采纳
        answer.setIsAccepted(1);
        this.updateById(answer);
        
        // 更新问题状态为已解决
        Question updateQuestion = new Question();
        updateQuestion.setId(questionId);
        updateQuestion.setStatus(QuestionStatusEnum.SOLVED.getCode());
        return questionService.updateById(updateQuestion);
    }
    
    /**
     * 将回答实体转换为VO
     *
     * @param answer 回答实体
     * @return 回答VO
     */
    private AnswerVO convertToVO(Answer answer) {
        if (answer == null) {
            return null;
        }
        
        AnswerVO vo = new AnswerVO();
        BeanUtils.copyProperties(answer, vo);
        
        // 设置用户信息
        try {
            // 这里可以调用用户服务获取用户详情
            // 此处简化处理，实际中应调用相关服务获取数据
            vo.setUsername("用户" + answer.getUserId());
            vo.setUserAvatar("默认头像");
            vo.setCommentCount(0); // 评论数量，实际中应查询评论表获取
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
        }
        
        return vo;
    }
} 