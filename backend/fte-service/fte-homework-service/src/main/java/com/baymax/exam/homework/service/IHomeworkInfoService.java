package com.baymax.exam.homework.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.homework.model.HomeworkInfo;

import java.util.List;

/**
 * <p>
 * 作业发布信息 服务接口
 * </p>
 *
 */
public interface IHomeworkInfoService extends IService<HomeworkInfo> {
    /**
     * 获取学生作业信息
     * @param page
     * @param queryWrapper
     * @return
     */
    IPage<HomeworkInfo> getStuHomeworkInfo(Page<HomeworkInfo> page, QueryWrapper<HomeworkInfo> queryWrapper);
    
    /**
     * 删除作业信息缓存
     * @param homeworkInfoId
     */
    void deleteCacheHomeworkInfo(Integer homeworkInfoId);
    
    /**
     * 根据课程ID列表获取作业列表
     *
     * @param page      分页对象
     * @param courseIds 课程ID列表
     * @param status    作业状态（0:全部、1：未开始、2：进行中、3：结束）
     * @return 分页后的作业列表
     */
    IPage<HomeworkInfo> getHomeworkListByCourseIds(Page<HomeworkInfo> page, List<Integer> courseIds, Integer status);
} 