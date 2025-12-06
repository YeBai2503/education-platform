package com.baymax.exam.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.homework.mapper.HomeworkInfoMapper;
import com.baymax.exam.homework.model.HomeworkInfo;
import com.baymax.exam.homework.service.IHomeworkInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 作业发布信息 服务实现类
 * </p>
 *
 */
@Service
public class HomeworkInfoServiceImpl extends ServiceImpl<HomeworkInfoMapper, HomeworkInfo> implements IHomeworkInfoService {

    private static final String HOMEWORK_INFO_CACHE_PREFIX = "homework:info:";
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<HomeworkInfo> getStuHomeworkInfo(Page<HomeworkInfo> page, QueryWrapper<HomeworkInfo> queryWrapper) {
        return baseMapper.getStuHomeworkInfo(page, queryWrapper);
    }

    @Override
    public void deleteCacheHomeworkInfo(Integer homeworkInfoId) {
        String key = HOMEWORK_INFO_CACHE_PREFIX + homeworkInfoId;
        redisTemplate.delete(key);
    }
    
    /**
     * 根据课程ID列表获取作业列表
     *
     * @param page      分页对象
     * @param courseIds 课程ID列表
     * @param status    作业状态（0:全部、1：未开始、2：进行中、3：结束）
     * @return 分页后的作业列表
     */
    @Override
    public IPage<HomeworkInfo> getHomeworkListByCourseIds(Page<HomeworkInfo> page, List<Integer> courseIds, Integer status) {
        LambdaQueryWrapper<HomeworkInfo> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加课程ID条件
        queryWrapper.in(HomeworkInfo::getCourseId, courseIds);
        
        // 添加状态筛选
        switch (status) {
            case 1: // 未开始
                queryWrapper.gt(HomeworkInfo::getStartTime, LocalDateTime.now());
                break;
            case 2: // 进行中
                queryWrapper.le(HomeworkInfo::getStartTime, LocalDateTime.now())
                        .ge(HomeworkInfo::getEndTime, LocalDateTime.now());
                break;
            case 3: // 已结束
                queryWrapper.lt(HomeworkInfo::getEndTime, LocalDateTime.now());
                break;
        }
        
        // 按创建时间倒序排序
        queryWrapper.orderByDesc(HomeworkInfo::getCreatedAt);
        
        return page(page, queryWrapper);
    }
} 