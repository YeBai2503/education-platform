package com.baymax.exam.experiment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.core.result.ResultCode;
import com.baymax.exam.experiment.model.Experiment;
import com.baymax.exam.experiment.service.IExperimentService;
import com.baymax.exam.experiment.vo.ExperimentVO;
import com.baymax.exam.user.feign.CourseClient;
import com.baymax.exam.user.feign.UserClient;
import com.baymax.exam.user.model.Courses;
import com.baymax.exam.user.model.JoinClass;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实验Controller
 */
@Slf4j
@RestController
@RequestMapping("/experiment/publish")
@Tag(name = "实验管理")
public class ExperimentController {
    
    @Autowired
    private IExperimentService experimentService;
    
    @Autowired
    private UserClient userClient;
    
    @Autowired
    private CourseClient courseClient;
    
    /**
     * 分页查询课程下的实验列表
     */
    @Operation(summary = "分页查询课程下的实验列表")
    @GetMapping("/course/{courseId}/list")
    public Result<IPage<ExperimentVO>> pageExperiments(
            @PathVariable Integer courseId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type) {
        
        Page<Experiment> page = new Page<>(current, size);
        IPage<ExperimentVO> result = experimentService.pageExperimentsByCourseId(page, courseId, type);
        return Result.success(result);
    }
    
    /**
     * 获取实验详情
     */
    @Operation(summary = "获取实验详情")
    @GetMapping("/detail/{id}")
    public Result<ExperimentVO> getExperimentDetail(@PathVariable Integer id) {
        ExperimentVO experimentVO = experimentService.getExperimentDetail(id);
        if (experimentVO == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "实验不存在");
        }
        return Result.success(experimentVO);
    }
    
    /**
     * 创建实验
     */
    @Operation(summary = "创建实验")
    @PostMapping("/create")
    public Result<ExperimentVO> createExperiment(
            @RequestParam("courseId") @NotNull(message = "课程ID不能为空") Integer courseId,
            @RequestParam("title") @NotEmpty(message = "标题不能为空") String title,
            @RequestParam(value = "detail", required = false) String detail,
            @RequestParam(value = "ddl", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ddl,
            @RequestParam(value = "type", required = false, defaultValue = "0") Integer type,
            @RequestPart(value = "file1", required = false) MultipartFile file1,
            @RequestPart(value = "file2", required = false) MultipartFile file2,
            @RequestPart(value = "file3", required = false) MultipartFile file3,
            @RequestPart(value = "file4", required = false) MultipartFile file4,
            @RequestPart(value = "file5", required = false) MultipartFile file5) {
        
        // 检查用户是否已登录
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
            if (userId == null) {
                return Result.failed(ResultCode.TOKEN_INVALID_OR_EXPIRED, "用户未登录或登录已过期");
            }
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
            return Result.failed(ResultCode.TOKEN_INVALID_OR_EXPIRED, "用户未登录或登录已过期");
        }
        
        // 验证用户是否为该课程的教师
        if (!isTeacher(userId, courseId)) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该课程的教师");
        }
        
        try {
            // 创建实验VO对象
            ExperimentVO experimentVO = new ExperimentVO();
            experimentVO.setCourseId(courseId);
            experimentVO.setTitle(title);
            experimentVO.setDetail(detail);
            experimentVO.setDdl(ddl);
            experimentVO.setType(type);
            
            // 调用服务创建实验，直接传递五个文件参数
            ExperimentVO result = experimentService.createExperiment(experimentVO, file1, file2, file3, file4, file5);
            return Result.success(result);
        } catch (Exception e) {
            log.error("创建实验失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "创建实验失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新实验
     */
    @Operation(summary = "更新实验")
    @PutMapping("/update/{id}")
    public Result<ExperimentVO> updateExperiment(
            @PathVariable Integer id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "detail", required = false) String detail,
            @RequestParam(value = "ddl", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime ddl,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "filesToDelete", required = false) String[] filesToDeleteArray,
            @RequestPart(value = "file1", required = false) MultipartFile file1,
            @RequestPart(value = "file2", required = false) MultipartFile file2,
            @RequestPart(value = "file3", required = false) MultipartFile file3,
            @RequestPart(value = "file4", required = false) MultipartFile file4,
            @RequestPart(value = "file5", required = false) MultipartFile file5) {
        
        // 检查用户是否已登录
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
            if (userId == null) {
                return Result.failed(ResultCode.TOKEN_INVALID_OR_EXPIRED, "用户未登录或登录已过期");
            }
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
            return Result.failed(ResultCode.TOKEN_INVALID_OR_EXPIRED, "用户未登录或登录已过期");
        }
        
        // 获取实验信息
        ExperimentVO existingExperiment = experimentService.getExperimentDetail(id);
        if (existingExperiment == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "实验不存在");
        }
        
        // 验证用户是否为该课程的教师
        if (!isTeacher(userId, existingExperiment.getCourseId())) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该课程的教师");
        }
        
        try {
            // 更新实验VO对象
            ExperimentVO experimentVO = new ExperimentVO();
            experimentVO.setId(id);
            experimentVO.setCourseId(existingExperiment.getCourseId());
            experimentVO.setTitle(title != null ? title : existingExperiment.getTitle());
            experimentVO.setDetail(detail != null ? detail : existingExperiment.getDetail());
            experimentVO.setDdl(ddl != null ? ddl : existingExperiment.getDdl());
            experimentVO.setType(type != null ? type : existingExperiment.getType());
            
            // 转换文件删除数组为列表
            List<String> filesToDelete = null;
            if (filesToDeleteArray != null && filesToDeleteArray.length > 0) {
                filesToDelete = Arrays.asList(filesToDeleteArray);
                log.info("要删除的文件: {}", filesToDelete);
            }
            
            // 保留未被删除的文件URL
            if (filesToDelete != null && !filesToDelete.isEmpty()) {
                List<String> remainingFiles = new ArrayList<>(existingExperiment.getFileUrls());
                if (remainingFiles != null) {
                    remainingFiles.removeAll(filesToDelete);
                    experimentVO.setFileUrls(remainingFiles);
                }
            } else {
                experimentVO.setFileUrls(existingExperiment.getFileUrls());
            }
            
            // 调用服务更新实验，直接传递五个文件参数
            ExperimentVO result = experimentService.updateExperimentWithFileDelete(id, experimentVO, filesToDelete, file1, file2, file3, file4, file5);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新实验失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "更新实验失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除实验
     */
    @Operation(summary = "删除实验")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteExperiment(@PathVariable Integer id) {
        // 检查用户是否已登录
        Integer userId;
        try {
            userId = UserAuthUtil.getUserId();
            if (userId == null) {
                return Result.failed(ResultCode.TOKEN_INVALID_OR_EXPIRED, "用户未登录或登录已过期");
            }
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
            return Result.failed(ResultCode.TOKEN_INVALID_OR_EXPIRED, "用户未登录或登录已过期");
        }
        
        // 获取实验信息
        ExperimentVO experimentVO = experimentService.getExperimentDetail(id);
        if (experimentVO == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "实验不存在");
        }
        
        // 验证用户是否为该课程的教师
        if (!isTeacher(userId, experimentVO.getCourseId())) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该课程的教师");
        }
        
        try {
            // 删除实验
            boolean result = experimentService.deleteExperiment(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除实验失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "删除实验失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计课程下的实验数量
     */
    @Operation(summary = "统计课程下的实验数量")
    @GetMapping("/course/{courseId}/count")
    public Result<Integer> countExperiments(
            @PathVariable Integer courseId,
            @RequestParam(required = false) Integer type) {
        
        try {
            // 检查课程是否存在
            Courses course = courseClient.findCourse(courseId);
            if (course == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "课程不存在");
            }
            
            // 统计实验数量
            int count = experimentService.countByCourseId(courseId, type);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计实验数量失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "统计实验数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 判断用户是否为课程的教师
     */
    private boolean isTeacher(Integer userId, Integer courseId) {
        try {
            // 先查询用户是否为该课程的学生
            JoinClass joinClass = courseClient.joinCourseByStuId(courseId, userId);
            
            // 如果不是学生，检查是否为课程创建者（教师）
            if (joinClass == null) {
                Courses course = courseClient.findCourse(courseId);
                if (course != null && course.getUserId().equals(userId)) {
                    return true; // 是课程创建者
                }
            }
            
            return false;
        } catch (Exception e) {
            log.error("检查教师权限失败", e);
            return false;
        }
    }
} 