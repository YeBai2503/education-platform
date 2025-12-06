package com.baymax.exam.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.common.core.base.LoginUser;
import com.baymax.exam.question.enums.CommentTypeEnum;
import com.baymax.exam.question.enums.QuestionStatusEnum;
import com.baymax.exam.question.mapper.QuestionMapper;
import com.baymax.exam.question.model.*;
import com.baymax.exam.question.service.AnswerService;
import com.baymax.exam.question.service.CommentService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description：问题Service实现类
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final CourseClient courseClient;
    private AnswerService answerService;
    private CommentService commentService;
    
    @Autowired
    public QuestionServiceImpl(CourseClient courseClient) {
        this.courseClient = courseClient;
    }
    
    @Autowired
    public void setAnswerService(AnswerService answerService) {
        this.answerService = answerService;
    }
    
    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionVO addQuestion(QuestionDTO questionDTO) {
        // 获取当前用户信息
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("用户未登录或登录信息已失效，请重新登录");
        }
        
        // 验证用户是否属于该课程
        JoinClass joinClass = courseClient.joinCourseByStuId(questionDTO.getCourseId(), userId);
        if (joinClass == null) {
            // 查询课程信息，如果是课程创建者也允许发布问题
            Courses course = courseClient.findCourse(questionDTO.getCourseId());
            if (course == null || !course.getUserId().equals(userId)) {
                throw new RuntimeException("您不是该课程的学生或教师，无法在此课程发布问题");
            }
        }
        
        // 创建问题实体并保存
        Question question = new Question();
        BeanUtils.copyProperties(questionDTO, question);
        question.setUserId(userId);
        question.setStatus(QuestionStatusEnum.UNSOLVED.getCode());
        question.setViews(0);
        question.setLikes(0);
        question.setFavorites(0);
        question.setAnswerCount(0);
        
        // 保存问题
        this.save(question);
        
        // 构建返回结果
        QuestionVO questionVO = convertToVO(question);
        LoginUser loginUser = UserAuthUtil.getUser();
        if (loginUser != null) {
            questionVO.setUsername(loginUser.getUsername());
        }
        
        return questionVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQuestion(QuestionDTO questionDTO) {
        // 获取当前用户信息
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("用户未登录或登录信息已失效，请重新登录");
        }
        
        // 查询问题是否存在
        Question question = this.getById(questionDTO.getId());
        if (question == null) {
            return false;
        }
        
        // 判断是否为问题创建者
        if (!Objects.equals(question.getUserId(), userId)) {
            return false;
        }
        
        // 更新问题
        BeanUtils.copyProperties(questionDTO, question);
        
        return this.updateById(question);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestion(Integer id) {
        // 获取当前用户信息
        Integer userId = UserAuthUtil.getUserId();
        
        // 查询问题是否存在
        Question question = this.getById(id);
        if (question == null) {
            return false;
        }
        
        // 判断是否为问题创建者
        if (!Objects.equals(question.getUserId(), userId)) {
            return false;
        }
        
        // 1. 删除问题相关的所有评论
        LambdaQueryWrapper<Comment> commentWrapper = Wrappers.lambdaQuery();
        commentWrapper.eq(Comment::getType, CommentTypeEnum.QUESTION_COMMENT.getCode())
                     .eq(Comment::getRelatedId, id);
        commentService.remove(commentWrapper);
        
        // 2. 查询问题的所有回答
        LambdaQueryWrapper<Answer> answerWrapper = Wrappers.lambdaQuery();
        answerWrapper.eq(Answer::getQuestionId, id);
        List<Answer> answers = answerService.list(answerWrapper);
        
        // 3. 删除每个回答相关的评论
        for (Answer answer : answers) {
            LambdaQueryWrapper<Comment> answerCommentWrapper = Wrappers.lambdaQuery();
            answerCommentWrapper.eq(Comment::getType, CommentTypeEnum.ANSWER_COMMENT.getCode())
                               .eq(Comment::getRelatedId, answer.getId());
            commentService.remove(answerCommentWrapper);
        }
        
        // 4. 删除所有回答
        answerService.remove(answerWrapper);
        
        // 5. 最后删除问题
        return this.removeById(id);
    }

    @Override
    public QuestionVO getQuestionById(Integer id) {
        // 查询问题
        Question question = this.getById(id);
        if (question == null) {
            return null;
        }
        
        // 获取当前用户信息
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("用户未登录或登录信息已失效，请重新登录");
        }
        
        // 验证用户是否属于该课程
        Integer courseId = question.getCourseId();
        JoinClass joinClass = courseClient.joinCourseByStuId(courseId, userId);
        boolean isCourseTeacher = false;
        
        if (joinClass == null) {
            // 查询课程信息，如果是课程创建者也允许查看问题
            Courses course = courseClient.findCourse(courseId);
            if (course == null) {
                throw new RuntimeException("课程不存在");
            }
            
            if (!course.getUserId().equals(userId)) {
                throw new RuntimeException("您不是该课程的学生或教师，无法查看此问题");
            }
            
            isCourseTeacher = true;
        }
        
        // 增加浏览次数
        question.setViews(question.getViews() + 1);
        this.updateById(question);
        
        // 构建返回结果
        return convertToVO(question);
    }

    @Override
    public List<QuestionVO> getUserQuestions() {
        // 获取当前用户信息
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("用户未登录或登录信息已失效，请重新登录");
        }
        
        // 查询用户问题列表
        LambdaQueryWrapper<Question> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Question::getUserId, userId)
               .orderByDesc(Question::getCreateTime);
        
        List<Question> questions = this.list(wrapper);
        
        // 转换为VO列表
        return questions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionVO> getQuestionsByCourseId(Integer courseId) {
        // 获取当前用户信息
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("用户未登录或登录信息已失效，请重新登录");
        }
        
        // 验证用户是否属于该课程
        JoinClass joinClass = courseClient.joinCourseByStuId(courseId, userId);
        boolean isCourseTeacher = false;
        
        if (joinClass == null) {
            // 查询课程信息，如果是课程创建者也允许查看问题
            Courses course = courseClient.findCourse(courseId);
            if (course == null) {
                throw new RuntimeException("课程不存在");
            }
            
            if (!course.getUserId().equals(userId)) {
                throw new RuntimeException("您不是该课程的学生或教师，无法查看此课程的问题");
            }
            
            isCourseTeacher = true;
        }
        
        // 查询课程问题列表
        LambdaQueryWrapper<Question> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Question::getCourseId, courseId)
               .orderByDesc(Question::getCreateTime);
        
        List<Question> questions = this.list(wrapper);
        
        // 转换为VO列表
        return questions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<QuestionVO> pageQuestionsByCourseId(Integer courseId, Integer page, Integer size) {
        // 获取当前用户信息
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
        } catch (Exception e) {
            throw new RuntimeException("用户未登录或登录信息已失效，请重新登录");
        }
        
        // 验证用户是否属于该课程
        JoinClass joinClass = courseClient.joinCourseByStuId(courseId, userId);
        boolean isCourseTeacher = false;
        
        if (joinClass == null) {
            // 查询课程信息，如果是课程创建者也允许查看问题
            Courses course = courseClient.findCourse(courseId);
            if (course == null) {
                throw new RuntimeException("课程不存在");
            }
            
            if (!course.getUserId().equals(userId)) {
                throw new RuntimeException("您不是该课程的学生或教师，无法查看此课程的问题");
            }
            
            isCourseTeacher = true;
        }
        
        // 构建分页对象
        Page<Question> questionPage = new Page<>(page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<Question> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Question::getCourseId, courseId)
               .orderByDesc(Question::getCreateTime);
        
        // 执行分页查询
        Page<Question> resultPage = this.page(questionPage, wrapper);
        
        // 构建返回结果
        Page<QuestionVO> voPage = new Page<>();
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setSize(resultPage.getSize());
        voPage.setTotal(resultPage.getTotal());
        voPage.setPages(resultPage.getPages());
        
        // 转换记录为VO
        List<QuestionVO> records = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(records);
        
        return voPage;
    }

    @Override
    public boolean updateQuestionAnswerCount(Integer questionId, Integer increment) {
        // 查询问题是否存在
        Question question = this.getById(questionId);
        if (question == null) {
            return false;
        }
        
        // 更新回答数量
        int newCount = Math.max(0, question.getAnswerCount() + increment);
        question.setAnswerCount(newCount);
        
        return this.updateById(question);
    }
    
    /**
     * 将问题实体转换为VO
     *
     * @param question 问题实体
     * @return 问题VO
     */
    private QuestionVO convertToVO(Question question) {
        if (question == null) {
            return null;
        }
        
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(question, vo);
        
        // 设置用户信息
        try {
            // 这里可以调用用户服务获取用户详情
            // 设置课程信息
            // 此处简化处理，实际中应调用相关服务获取数据
            vo.setUsername("用户" + question.getUserId());
            vo.setCourseName("课程" + question.getCourseId());
        } catch (Exception e) {
            log.error("获取用户或课程信息异常", e);
        }
        
        return vo;
    }
} 