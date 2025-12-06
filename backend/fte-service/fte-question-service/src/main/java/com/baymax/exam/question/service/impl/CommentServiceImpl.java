package com.baymax.exam.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.common.core.base.LoginUser;
import com.baymax.exam.question.enums.CommentTypeEnum;
import com.baymax.exam.question.mapper.CommentMapper;
import com.baymax.exam.question.model.Comment;
import com.baymax.exam.question.model.CommentDTO;
import com.baymax.exam.question.model.CommentVO;
import com.baymax.exam.question.service.CommentService;
import com.baymax.exam.web.utils.UserAuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description：评论Service实现类
 */
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO addComment(CommentDTO commentDTO) {
        // 获取当前用户信息
        Integer userId = UserAuthUtil.getUserId();
        
        // 创建评论实体
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        comment.setUserId(userId);
        
        // 如果parentId为null，则设置为0表示一级评论
        if (comment.getParentId() == null) {
            comment.setParentId(0);
        }
        
        // 保存评论
        this.save(comment);
        
        // 构建返回结果
        CommentVO commentVO = convertToVO(comment);
        LoginUser loginUser = UserAuthUtil.getUser();
        if (loginUser != null) {
            commentVO.setUsername(loginUser.getUsername());
        }
        
        return commentVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Integer id) {
        // 获取当前用户信息
        Integer userId = UserAuthUtil.getUserId();
        
        // 查询评论是否存在
        Comment comment = this.getById(id);
        if (comment == null) {
            return false;
        }
        
        // 判断是否为评论创建者
        if (!Objects.equals(comment.getUserId(), userId)) {
            return false;
        }
        
        // 递归删除子评论
        deleteChildComments(id);
        
        // 删除评论
        return this.removeById(id);
    }

    @Override
    public List<CommentVO> getQuestionComments(Integer questionId) {
        return getComments(CommentTypeEnum.QUESTION_COMMENT.getCode(), questionId);
    }

    @Override
    public List<CommentVO> getAnswerComments(Integer answerId) {
        return getComments(CommentTypeEnum.ANSWER_COMMENT.getCode(), answerId);
    }
    
    /**
     * 获取评论列表并构建评论树
     *
     * @param type 评论类型
     * @param relatedId 关联ID
     * @return 评论VO列表
     */
    private List<CommentVO> getComments(Integer type, Integer relatedId) {
        // 查询所有相关评论
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Comment::getType, type)
               .eq(Comment::getRelatedId, relatedId)
               .orderByAsc(Comment::getCreateTime);
        
        List<Comment> comments = this.list(wrapper);
        if (comments.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为VO
        List<CommentVO> commentVOs = comments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建评论树
        return buildCommentTree(commentVOs);
    }
    
    /**
     * 构建评论树
     *
     * @param commentVOs 评论VO列表
     * @return 评论树
     */
    private List<CommentVO> buildCommentTree(List<CommentVO> commentVOs) {
        // 按id映射所有评论
        Map<Integer, CommentVO> commentMap = commentVOs.stream()
                .collect(Collectors.toMap(CommentVO::getId, Function.identity()));
        
        // 返回结果集合
        List<CommentVO> resultList = new ArrayList<>();
        
        // 遍历处理每条评论
        for (CommentVO comment : commentVOs) {
            // 如果是一级评论，加入结果集
            if (comment.getParentId() == 0) {
                resultList.add(comment);
            } else {
                // 如果是子评论，找到父评论并添加到其children中
                CommentVO parentComment = commentMap.get(comment.getParentId());
                if (parentComment != null) {
                    if (parentComment.getChildren() == null) {
                        parentComment.setChildren(new ArrayList<>());
                    }
                    parentComment.getChildren().add(comment);
                } else {
                    // 父评论不存在，作为一级评论处理
                    resultList.add(comment);
                }
            }
        }
        
        return resultList;
    }
    
    /**
     * 递归删除子评论
     *
     * @param parentId 父评论ID
     */
    private void deleteChildComments(Integer parentId) {
        // 查询子评论
        LambdaQueryWrapper<Comment> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Comment::getParentId, parentId);
        List<Comment> childComments = this.list(wrapper);
        
        // 递归删除
        for (Comment childComment : childComments) {
            deleteChildComments(childComment.getId());
            this.removeById(childComment.getId());
        }
    }
    
    /**
     * 将评论实体转换为VO
     *
     * @param comment 评论实体
     * @return 评论VO
     */
    private CommentVO convertToVO(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        
        // 设置用户信息
        try {
            // 这里可以调用用户服务获取用户详情
            // 此处简化处理，实际中应调用相关服务获取数据
            vo.setUsername("用户" + comment.getUserId());
            vo.setUserAvatar("默认头像");
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
        }
        
        return vo;
    }
} 