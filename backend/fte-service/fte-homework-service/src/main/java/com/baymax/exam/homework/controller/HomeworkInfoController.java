package com.baymax.exam.homework.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.VueRouteLocationRaw;
import com.baymax.exam.common.core.exception.ResultException;
import com.baymax.exam.common.core.result.PageResult;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.core.result.ResultCode;
import com.baymax.exam.homework.model.HomeworkClass;
import com.baymax.exam.homework.model.HomeworkInfo;
import com.baymax.exam.homework.model.HomeworkPaper;
import com.baymax.exam.homework.service.impl.HomeworkClassServiceImpl;
import com.baymax.exam.homework.service.impl.HomeworkInfoServiceImpl;
import com.baymax.exam.homework.service.impl.HomeworkPaperServiceImpl;
import com.baymax.exam.homework.vo.HomeworkInfoVo;
import com.baymax.exam.message.feign.MessageServiceClient;
import com.baymax.exam.message.model.MessageInfo;
import com.baymax.exam.user.feign.ClassesClient;
import com.baymax.exam.user.feign.CourseClient;
import com.baymax.exam.user.model.Classes;
import com.baymax.exam.user.model.Courses;
import com.baymax.exam.user.model.JoinClass;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 作业发布信息 前端控制器
 * </p>
 *
 */
@Validated
@Slf4j
@Tag(name = "作业管理")
@RestController
@RequestMapping("/homework-info")
public class HomeworkInfoController {
    @Autowired
    HomeworkInfoServiceImpl homeworkInfoService;
    @Autowired
    HomeworkClassServiceImpl homeworkClassService;
    @Autowired
    HomeworkPaperServiceImpl homeworkPaperService;
    @Autowired
    CourseClient courseClient;
    @Autowired
    MessageServiceClient messageServiceClient;
    @Autowired
    ClassesClient classesClient;

    @Operation(summary = "发布作业信息")
    @PostMapping("/update")
    public Result update(@RequestBody @Validated HomeworkInfoVo homeworkInfoVo) {
        HomeworkInfo homeworkInfo = homeworkInfoVo.getHomeworkInfo();
        
        // 确保classIds不为空
        if (homeworkInfoVo.getClassIds() == null || homeworkInfoVo.getClassIds().isEmpty()) {
            return Result.msgInfo("班级列表不能为空");
        }
        
        String info = "添加成功";
        Integer teacherId = null;
        // 更改，以前那种可能会查询两次数据库
        if (homeworkInfo.getId() != null) {
            HomeworkInfo tempHomework = homeworkInfoService.getById(homeworkInfo.getId());
            if (tempHomework != null) {
                teacherId = tempHomework.getTeacherId();
            }
            info = "更新成功";
        } else {
            Courses course = courseClient.findCourse(homeworkInfo.getCourseId());
            if (course != null) {
                teacherId = course.getUserId();
            }
            if (homeworkInfo.getStartTime().isBefore(LocalDateTime.now())) {
                return Result.msgInfo("作业时间不合法");
            }
        }
        Integer userId = UserAuthUtil.getUserId();
        if (teacherId != null && teacherId.intValue() != userId.intValue()) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }

        // 作业是不是我的
        HomeworkPaper homeworkPaper = homeworkPaperService.getById(homeworkInfo.getHomeworkId());
        if (homeworkPaper.getTeacherId().intValue() != userId.intValue()) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }

        // 删除作业班级，然后再添加
        if (homeworkInfo.getId() != null) {
            LambdaQueryWrapper<HomeworkClass> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(HomeworkClass::getHomeworkInfoId, homeworkInfo.getId());
            homeworkClassService.remove(queryWrapper);
        }
        homeworkInfo.setTeacherId(userId);
        // 更新作业信息
        homeworkInfoService.saveOrUpdate(homeworkInfo);
        // 删除缓存
        homeworkInfoService.deleteCacheHomeworkInfo(homeworkInfo.getId());
        // 重新添加作业班级
        List<HomeworkClass> list = homeworkInfoVo.getClassIds().stream().map(cId -> {
            HomeworkClass homeworkClass = new HomeworkClass();
            homeworkClass.setClassId(cId);
            homeworkClass.setHomeworkInfoId(homeworkInfo.getId());
            if (homeworkInfo.getStartTime() != null) {
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setTitle(homeworkInfo.getTitle());
                final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                messageInfo.setIntroduce(homeworkInfo.getStartTime().format(dateTimeFormatter) + "~" + homeworkInfo.getEndTime().format(dateTimeFormatter));
                messageInfo.setUserId(userId);
                messageInfo.setClientId(UserAuthUtil.getUser().getClientId());
                messageInfo.setTargetId(cId);
                VueRouteLocationRaw vueRouteLocationRaw = new VueRouteLocationRaw();
                vueRouteLocationRaw.setName("HomeworkManage");
                vueRouteLocationRaw.setParams(Map.of("", homeworkInfo.getCourseId()));
                messageInfo.setPath(vueRouteLocationRaw.getJson());
                log.info("作业通知消息内容：{}", JSONUtil.toJsonStr(messageInfo));
                messageServiceClient.systemCourseMessage(messageInfo);
            }
            return homeworkClass;
        }).collect(Collectors.toList());
        homeworkClassService.saveBatch(list);
        return Result.msgSuccess(info);
    }

    @Operation(summary = "获取作业信息")
    @GetMapping("/detail/{homeworkInfoId}")
    public Result<HomeworkInfoVo> detail(@PathVariable Integer homeworkInfoId) {
        HomeworkInfo homeworkInfo = homeworkInfoService.getById(homeworkInfoId);
        Integer userId = UserAuthUtil.getUserId();
        if (homeworkInfo == null || homeworkInfo.getTeacherId().intValue() != userId.intValue()) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        LambdaQueryWrapper<HomeworkClass> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkClass::getHomeworkInfoId, homeworkInfoId);
        List<HomeworkClass> list = homeworkClassService.list(queryWrapper);

        HomeworkInfoVo paper = new HomeworkInfoVo();
        paper.setHomeworkInfo(homeworkInfo);
        paper.setPaper(homeworkPaperService.getById(homeworkInfo.getHomeworkId()));
        // 班级信息
        Set<Integer> classIds = list.stream().map(HomeworkClass::getClassId).collect(Collectors.toSet());
        
        // 防止空集合导致SQL错误
        if (classIds == null || classIds.isEmpty()) {
            paper.setClassIds(classIds);
            paper.setClassList(Collections.emptyList());
            return Result.success(paper);
        }
        
        Result<List<Classes>> classListByIds = classesClient.getClassListByIds(classIds, homeworkInfo.getCourseId());
        try {
            paper.setClassList(classListByIds.getResultDate());
        } catch (ResultException e) {
            log.error("获取班级信息失败", e);
            paper.setClassList(Collections.emptyList());
        }
        paper.setClassIds(classIds);
        return Result.success(paper);
    }

    @Operation(summary = "删除作业")
    @PostMapping("/delete/{homeworkInfoId}")
    public Result delete(@PathVariable Integer homeworkInfoId) {
        HomeworkInfo homeworkInfo = homeworkInfoService.getById(homeworkInfoId);
        Integer userId = UserAuthUtil.getUserId();
        if (homeworkInfo == null || homeworkInfo.getTeacherId().intValue() != userId.intValue()) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        
        // 先删除关联的作业班级
        LambdaQueryWrapper<HomeworkClass> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeworkClass::getHomeworkInfoId, homeworkInfoId);
        homeworkClassService.remove(queryWrapper);
        
        // 删除缓存
        homeworkInfoService.deleteCacheHomeworkInfo(homeworkInfoId);
        
        // 再删除作业信息本身
        homeworkInfoService.removeById(homeworkInfoId);
        return Result.msgSuccess("删除成功");
    }

    @Operation(summary = "获取作业列表")
    @GetMapping("/list/{courseId}")
    public Result list(@PathVariable Integer courseId,
                       @Schema(description = "0:全部、1：未开始、2：进行中、3：结束")
                       @RequestParam(defaultValue = "0", required = false) Integer status,
                       @RequestParam(defaultValue = "1", required = false) Integer page,
                       @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        Integer userId = UserAuthUtil.getUserId();
        Courses course = courseClient.findCourse(courseId);
        if (course == null) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }

        QueryWrapper<HomeworkInfo> queryWrapper = new QueryWrapper<>();
        switch (status) {
            case 1:
                // 未开始
                queryWrapper.apply(true,
                        "date_format (start_time,'%Y-%m-%d %H:%i:%S') > date_format(now(),'%Y-%m-%d %H:%i:%S')");
                break;
            case 2:
                // 进行中
                queryWrapper.apply(true,
                        "date_format (start_time,'%Y-%m-%d %H:%i:%S')<= date_format(now(),'%Y-%m-%d %H:%i:%S')")
                        .apply(true,
                                "date_format (end_time,'%Y-%m-%d %H:%i:%S') >= date_format(now(),'%Y-%m-%d %H:%i:%S')");
                break;
            case 3:
                // 结束
                queryWrapper.apply(true,
                        "date_format (end_time,'%Y-%m-%d %H:%i:%S') < date_format(now(),'%Y-%m-%d %H:%i:%S')");
                break;
        }
        queryWrapper.orderByDesc("created_at");
        Page<HomeworkInfo> pa = new Page<>(page, pageSize);
        IPage<HomeworkInfo> record;
        if (course.getUserId().intValue() == userId.intValue()) {
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("course_id", courseId);
            queryMap.put("teacher_id", userId);
            queryWrapper.allEq(queryMap);
            record = homeworkInfoService.page(pa, queryWrapper);
        } else {
            JoinClass joinClass = courseClient.joinCourseByStuId(courseId, userId);
            if (joinClass == null) {
                return Result.failed(ResultCode.PARAM_ERROR);
            }
            queryWrapper.eq("ec.class_id", joinClass.getClassId());
            record = homeworkInfoService.getStuHomeworkInfo(pa, queryWrapper);
        }
        return Result.success(PageResult.setResult(record));
    }

    @Operation(summary = "获取作业发布信息")
    @GetMapping("/info/{homeworkInfoId}")
    public Result<HomeworkInfo> info(@PathVariable Integer homeworkInfoId) {
        HomeworkInfo homeworkInfo = homeworkInfoService.getById(homeworkInfoId);
        if (homeworkInfo == null) {
            return Result.failed(ResultCode.PARAM_ERROR);
        }
        return Result.success(homeworkInfo);
    }
    
    @Operation(summary = "获取当前用户所有课程的作业列表")
    @GetMapping("/my-all-homework")
    public Result getAllHomeworkList(
            @Schema(description = "0:全部、1：未开始、2：进行中、3：结束")
            @RequestParam(defaultValue = "0", required = false) Integer status,
            @RequestParam(defaultValue = "1", required = false) Integer pageNum,
            @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        
        Integer userId = UserAuthUtil.getUserId();
        if (userId == null) {
            return Result.failed("用户未登录");
        }
        
        // 获取用户参加的所有课程ID
        List<Integer> courseIds = courseClient.getStudentAllCourses(userId);
        if (courseIds == null || courseIds.isEmpty()) {
            // 创建空结果集
            PageResult<HomeworkInfo> emptyResult = new PageResult<>();
            emptyResult.setList(Collections.emptyList());
            emptyResult.setTotal(0);
            emptyResult.setPages(0);
            emptyResult.setCurrent(pageNum);
            return Result.success(emptyResult);
        }
        
        // 使用课程ID列表查询作业信息
        Page<HomeworkInfo> page = new Page<>(pageNum, pageSize);
        IPage<HomeworkInfo> homeworkList = homeworkInfoService.getHomeworkListByCourseIds(page, courseIds, status);
        
        return Result.success(PageResult.setResult(homeworkList));
    }
} 