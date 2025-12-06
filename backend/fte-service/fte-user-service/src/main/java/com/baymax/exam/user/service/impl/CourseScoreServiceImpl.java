package com.baymax.exam.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.user.mapper.CourseScoreMapper;
import com.baymax.exam.user.model.CourseScore;
import com.baymax.exam.user.service.ICourseScoreService;
import com.baymax.exam.user.vo.CourseScoreVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 课程评分服务实现类
 */
@Service
public class CourseScoreServiceImpl extends ServiceImpl<CourseScoreMapper, CourseScore> implements ICourseScoreService {

    @Autowired
    private CourseScoreMapper courseScoreMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rateCourse(CourseScore courseScore) {
        // 计算总评分（五个方面的平均值），保留三位小数
        float sumScore = (courseScore.getVideoScore() + courseScore.getExamScore() + 
                        courseScore.getExperimentScore() + courseScore.getProjectScore() + 
                        courseScore.getHomeworkScore()) / 5.0f;
        
        // 使用BigDecimal保留三位小数
        BigDecimal bd = new BigDecimal(sumScore);
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        courseScore.setSumScore(bd.floatValue());
        
        // 检查学生是否已经评分过该课程
        CourseScore existingScore = getStudentCourseScore(courseScore.getCourseId(), courseScore.getStudentId());
        if (existingScore != null) {
            // 更新评分
            courseScore.setId(existingScore.getId());
            return updateById(courseScore);
        } else {
            // 新增评分
            return save(courseScore);
        }
    }

    @Override
    public CourseScoreVo getCourseTotalScore(Integer courseId) {
        return courseScoreMapper.getCourseTotalScore(courseId);
    }

    @Override
    public List<CourseScoreVo> getTopRatedCourses(Integer limit) {
        return courseScoreMapper.getTopRatedCourses(limit);
    }

    @Override
    public CourseScore getStudentCourseScore(Integer courseId, Integer studentId) {
        LambdaQueryWrapper<CourseScore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseScore::getCourseId, courseId)
                    .eq(CourseScore::getStudentId, studentId);
        return getOne(queryWrapper);
    }
} 