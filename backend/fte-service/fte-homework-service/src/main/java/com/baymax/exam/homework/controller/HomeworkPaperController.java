package com.baymax.exam.homework.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.center.feign.QuestionClient;
import com.baymax.exam.center.model.Question;
import com.baymax.exam.common.core.result.PageResult;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.core.result.ResultCode;
import com.baymax.exam.homework.model.HomeworkPaper;
import com.baymax.exam.homework.model.HomeworkQuestion;
import com.baymax.exam.homework.service.impl.HomeworkPaperServiceImpl;
import com.baymax.exam.homework.service.impl.HomeworkQuestionServiceImpl;
import com.baymax.exam.homework.vo.HomeworkPaperVo;
import com.baymax.exam.user.feign.CourseClient;
import com.baymax.exam.user.model.Courses;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 作业试卷信息表 前端控制器
 * </p>
 *
 */
@Tag(name = "作业管理")
@Validated
@RestController
@RequestMapping("/homework-paper")
public class HomeworkPaperController {
    @Autowired
    CourseClient courseClient;
    @Autowired
    HomeworkPaperServiceImpl homeworkPaperService;
    @Autowired
    HomeworkQuestionServiceImpl homeworkQuestionService;
    @Autowired
    QuestionClient questionClient;

    @Operation(summary = "添加/更新作业")
    @PostMapping("/update")
    public Result update(@RequestBody @Validated HomeworkPaperVo paperVo) {
        HomeworkPaper homeworkPaper = paperVo.getHomeworkPaper();
        String info = "添加成功";
        Integer teacherId = null;
        // 更改，以前那种可能会查询两次数据库
        if (homeworkPaper.getId() != null) {
            HomeworkPaper tempHomeworkPaper = homeworkPaperService.getById(homeworkPaper.getId());
            if (tempHomeworkPaper != null) {
                teacherId = tempHomeworkPaper.getTeacherId();
            }
            info = "更新成功";
        } else {
            Courses course = courseClient.findCourse(homeworkPaper.getCourseId());
            if (course != null) {
                teacherId = course.getUserId();
            }
        }
        Integer userId = UserAuthUtil.getUserId();
        if (teacherId != null && !Objects.equals(teacherId, userId)) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        // 删除作业题目，然后再添加
        if (homeworkPaper.getId() != null) {
            LambdaQueryWrapper<HomeworkQuestion> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(HomeworkQuestion::getHomeworkId, homeworkPaper.getId());
            homeworkQuestionService.remove(queryWrapper);
        }
        homeworkPaper.setTeacherId(userId);
        // 更新作业选项
        homeworkPaperService.saveOrUpdate(homeworkPaper);
        // 重新添加题目
        List<HomeworkQuestion> list = paperVo.getQuestions().stream().map(integer -> {
            HomeworkQuestion homeworkQuestion = new HomeworkQuestion();
            homeworkQuestion.setQuestionId(integer);
            homeworkQuestion.setHomeworkId(homeworkPaper.getId());
            return homeworkQuestion;
        }).collect(Collectors.toList());
        homeworkQuestionService.saveBatch(list);
        return Result.msgSuccess(info);
    }

    @Operation(summary = "删除作业")
    @PostMapping("/delete/{homeworkId}")
    public Result delete(@PathVariable Integer homeworkId) {
        HomeworkPaper homeworkPaper = homeworkPaperService.getById(homeworkId);
        Integer userId = UserAuthUtil.getUserId();
        if (homeworkPaper == null || !Objects.equals(homeworkPaper.getTeacherId(), userId)) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        
        // 先删除关联的作业题目
        LambdaQueryWrapper<HomeworkQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkQuestion::getHomeworkId, homeworkId);
        homeworkQuestionService.remove(queryWrapper);
        
        // 再删除作业本身
        homeworkPaperService.removeById(homeworkId);
        return Result.msgSuccess("删除成功");
    }

    @Operation(summary = "获取作业题目信息")
    @GetMapping("/detail/{homeworkId}")
    public Result detail(@PathVariable Integer homeworkId) {
        HomeworkPaper homeworkPaper = homeworkPaperService.getById(homeworkId);
        Integer userId = UserAuthUtil.getUserId();
        if (homeworkPaper == null || !Objects.equals(homeworkPaper.getTeacherId(), userId)) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        LambdaQueryWrapper<HomeworkQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkQuestion::getHomeworkId, homeworkId);
        List<HomeworkQuestion> list = homeworkQuestionService.list(queryWrapper);
        HomeworkPaperVo paper = new HomeworkPaperVo();
        paper.setHomeworkPaper(homeworkPaper);
        paper.setQuestions(list.stream().map(HomeworkQuestion::getQuestionId).collect(Collectors.toSet()));
        return Result.success(paper);
    }

    @Operation(summary = "获取作业信息")
    @GetMapping("/info/{homeworkId}")
    public Result<HomeworkPaper> info(@PathVariable Integer homeworkId) {
        HomeworkPaper homeworkPaper = homeworkPaperService.getById(homeworkId);
        if (homeworkPaper == null) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        return Result.success(homeworkPaper);
    }

    @Operation(summary = "获取作业列表")
    @GetMapping("/list/{courseId}")
    public Result list(@PathVariable Integer courseId,
                       @RequestParam(defaultValue = "1", required = false) Integer page,
                       @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        Integer userId = UserAuthUtil.getUserId();
        LambdaQueryWrapper<HomeworkPaper> queryWrapper = new LambdaQueryWrapper<>();
        Map<SFunction<HomeworkPaper, ?>, Object> queryMap = new HashMap<>();
        queryMap.put(HomeworkPaper::getCourseId, courseId);
        queryMap.put(HomeworkPaper::getTeacherId, userId);
        queryWrapper.allEq(queryMap);
        queryWrapper.orderByDesc(HomeworkPaper::getCreatedAt);
        Page<HomeworkPaper> pa = new Page<>(page, pageSize);
        Page<HomeworkPaper> record = homeworkPaperService.page(pa, queryWrapper);
        return Result.success(PageResult.setResult(record));
    }

    @Operation(summary = "作业数据统计")
    @GetMapping("/statistics/{homeworkId}")
    public Result statistics(@PathVariable Integer homeworkId) {
        HomeworkPaper homeworkPaper = homeworkPaperService.getById(homeworkId);
        Integer userId = UserAuthUtil.getUserId();
        if (homeworkPaper == null || !Objects.equals(homeworkPaper.getTeacherId(), userId)) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        List<Question> list = homeworkQuestionService.getQuestionByHomeworkId(homeworkId);
        final Map<String, List<Question>> collect = list.stream()
            .collect(Collectors.groupingBy(i -> i.getType().getLabel()));
        Map<String, Integer> questionStatistics = new HashMap<>();
        collect.forEach((key, value) -> {
            questionStatistics.put(key, value.size());
        });
        Float total = (float) list.stream().mapToDouble(Question::getScore).sum();
        Map<String, Object> result = new HashMap<>();
        result.put("questionCount", list.size());
        result.put("homeworkPaper", homeworkPaper);
        result.put("questionStatistics", questionStatistics);
        result.put("totalScore", total);
        return Result.success(result);
    }
} 