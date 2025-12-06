package com.baymax.exam.experiment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.core.result.ResultCode;
import com.baymax.exam.experiment.model.ExperimentSubmit;
import com.baymax.exam.experiment.service.IExperimentService;
import com.baymax.exam.experiment.service.IExperimentSubmitService;
import com.baymax.exam.experiment.service.ITeamService;
import com.baymax.exam.experiment.vo.ExperimentSubmitVO;
import com.baymax.exam.experiment.vo.ExperimentVO;
import com.baymax.exam.experiment.vo.TeamVO;
import com.baymax.exam.user.feign.UserClient;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Arrays;

/**
 * 实验提交Controller
 */
@Slf4j
@RestController
@RequestMapping("/experiment/submit")
@Tag(name = "实验提交管理")
public class ExperimentSubmitController {
    
    @Autowired
    private IExperimentSubmitService experimentSubmitService;
    
    @Autowired
    private IExperimentService experimentService;
    
    @Autowired
    private UserClient userClient;
    
    @Autowired
    private ITeamService teamService;
    
    /**
     * 分页查询实验的提交列表（老师查看）
     */
    @Operation(summary = "分页查询实验的提交列表")
    @GetMapping("/teacher/experiment/{experimentId}/list")
    public Result<IPage<ExperimentSubmitVO>> pageSubmits(
            @PathVariable Integer experimentId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Integer teacherId = UserAuthUtil.getUserId();
        
        // 获取实验信息
        ExperimentVO experiment = experimentService.getExperimentDetail(experimentId);
        if (experiment == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "实验不存在");
        }
        
        // 验证用户是否为该课程的教师
        if (!isTeacher(teacherId, experiment.getCourseId())) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该课程的教师");
        }
        
        Page<ExperimentSubmit> page = new Page<>(current, size);
        IPage<ExperimentSubmitVO> result = experimentSubmitService.pageSubmitsByExperimentId(page, experimentId);
        return Result.success(result);
    }
    
    /**
     * 获取学生的实验提交详情（学生查看自己的提交）
     */
    @Operation(summary = "学生查看自己的实验提交详情")
    @GetMapping("/student/experiment/{experimentId}/detail")
    public Result<ExperimentSubmitVO> getStudentSubmit(@PathVariable Integer experimentId) {
        Integer studentId = UserAuthUtil.getUserId();
        
        // 检查实验是否存在
        ExperimentVO experiment = experimentService.getExperimentDetail(experimentId);
        if (experiment == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "实验不存在");
        }
        
        ExperimentSubmitVO submitVO = experimentSubmitService.getStudentSubmit(experimentId, studentId);
        return Result.success(submitVO);
    }
    
    /**
     * 获取指定学生的实验提交详情（老师查看）
     */
    @Operation(summary = "老师查看指定学生的实验提交详情")
    @GetMapping("/teacher/experiment/{experimentId}/student/{studentId}/detail")
    public Result<ExperimentSubmitVO> getStudentSubmitByTeacher(
            @PathVariable Integer experimentId,
            @PathVariable Integer studentId) {
        
        Integer teacherId = UserAuthUtil.getUserId();
        
        // 获取实验信息
        ExperimentVO experiment = experimentService.getExperimentDetail(experimentId);
        if (experiment == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "实验不存在");
        }
        
        // 验证用户是否为该课程的教师
        if (!isTeacher(teacherId, experiment.getCourseId())) {
            return Result.failed(ResultCode.PARAM_ERROR, "您不是该课程的教师");
        }
        
        ExperimentSubmitVO submitVO = experimentSubmitService.getStudentSubmit(experimentId, studentId);
        if (submitVO == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "提交记录不存在");
        }
        return Result.success(submitVO);
    }
    
    /**
     * 提交实验
     */
    @Operation(summary = "学生提交实验")
    @PostMapping("/student/create")
    public Result<ExperimentSubmitVO> submitExperiment(
            @RequestParam("experimentId") @NotNull(message = "实验ID不能为空") Integer experimentId,
            @RequestParam(value = "detail", required = false) String detail,
            @RequestPart(value = "file1", required = false) MultipartFile file1,
            @RequestPart(value = "file2", required = false) MultipartFile file2,
            @RequestPart(value = "file3", required = false) MultipartFile file3,
            @RequestPart(value = "file4", required = false) MultipartFile file4,
            @RequestPart(value = "file5", required = false) MultipartFile file5) {
        
        Integer studentId = UserAuthUtil.getUserId();
        
        // 打印接收到的文件信息
        log.info("接收到实验提交请求，学生ID: {}, 实验ID: {}", studentId, experimentId);
        
        try {
            // 检查实验是否存在
            ExperimentVO experiment = experimentService.getExperimentDetail(experimentId);
            if (experiment == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "实验不存在");
            }
            
            // 如果是项目实训类型，检查用户是否为队长
            if (experiment.getType() != null && experiment.getType() == 1) {
                // 检查用户是否有队伍
                TeamVO team = teamService.getUserTeam(experimentId, studentId);
                if (team == null) {
                    return Result.failed(ResultCode.PARAM_ERROR, "您尚未加入任何队伍，无法提交项目实训");
                }
                
                // 检查用户是否为队长
                if (!team.getHeaderId().equals(studentId)) {
                    return Result.failed(ResultCode.PARAM_ERROR, "只有队长才能提交项目实训");
                }
            }
            
            // 检查是否已提交
            if (experimentSubmitService.checkStudentSubmitted(experimentId, studentId)) {
                return Result.failed(ResultCode.PARAM_ERROR, "已经提交过该实验，请使用更新功能");
            }
            
            // 创建提交VO
            ExperimentSubmitVO submitVO = new ExperimentSubmitVO();
            submitVO.setExperimentId(experimentId);
            submitVO.setStudentId(studentId);
            submitVO.setDetail(detail);
            
            ExperimentSubmitVO result = experimentSubmitService.submitExperiment(submitVO, file1, file2, file3, file4, file5);
            return Result.success(result);
        } catch (Exception e) {
            log.error("提交实验失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "提交实验失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新实验提交
     */
    @Operation(summary = "学生更新实验提交")
    @PutMapping("/student/update/{id}")
    public Result<ExperimentSubmitVO> updateSubmit(
            @PathVariable Integer id,
            @RequestParam(value = "detail", required = false) String detail,
            @RequestParam(value = "filesToDelete", required = false) String[] filesToDeleteArray,
            @RequestPart(value = "file1", required = false) MultipartFile file1,
            @RequestPart(value = "file2", required = false) MultipartFile file2,
            @RequestPart(value = "file3", required = false) MultipartFile file3,
            @RequestPart(value = "file4", required = false) MultipartFile file4,
            @RequestPart(value = "file5", required = false) MultipartFile file5) {
        
        Integer studentId = UserAuthUtil.getUserId();
        
        try {
            // 获取提交信息
            ExperimentSubmit existingSubmit = experimentSubmitService.getById(id);
            if (existingSubmit == null || !existingSubmit.getStudentId().equals(studentId)) {
                return Result.failed(ResultCode.PARAM_ERROR, "提交记录不存在或无权限修改");
            }
            
            // 获取实验信息
            ExperimentVO experiment = experimentService.getExperimentDetail(existingSubmit.getExperimentId());
            if (experiment == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "实验不存在");
            }
            
            // 如果是项目实训类型，检查用户是否为队长
            if (experiment.getType() != null && experiment.getType() == 1) {
                // 检查用户是否有队伍
                TeamVO team = teamService.getUserTeam(existingSubmit.getExperimentId(), studentId);
                if (team == null) {
                    return Result.failed(ResultCode.PARAM_ERROR, "您尚未加入任何队伍，无法更新项目实训提交");
                }
                
                // 检查用户是否为队长
                if (!team.getHeaderId().equals(studentId)) {
                    return Result.failed(ResultCode.PARAM_ERROR, "只有队长才能更新项目实训提交");
                }
            }
            
            // 创建更新VO
            ExperimentSubmitVO submitVO = new ExperimentSubmitVO();
            submitVO.setId(id);
            submitVO.setExperimentId(existingSubmit.getExperimentId());
            submitVO.setStudentId(studentId);
            submitVO.setDetail(detail != null ? detail : existingSubmit.getDetail());
            
            // 转换文件删除数组为列表
            List<String> filesToDelete = null;
            if (filesToDeleteArray != null && filesToDeleteArray.length > 0) {
                filesToDelete = Arrays.asList(filesToDeleteArray);
                log.info("要删除的文件: {}", filesToDelete);
            }
            
            // 调用服务更新提交，同时传递要删除的文件URL列表
            ExperimentSubmitVO result = experimentSubmitService.updateSubmitWithFileDelete(id, submitVO, filesToDelete, file1, file2, file3, file4, file5);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新提交失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "更新提交失败: " + e.getMessage());
        }
    }
    
    /**
     * 评分（老师操作）
     */
    @Operation(summary = "老师评分")
    @PutMapping("/teacher/grade/{submitId}")
    public Result<Boolean> gradeSubmit(
            @PathVariable Integer submitId,
            @RequestParam @NotNull @Min(0) @Max(100) Integer score) {
        
        Integer teacherId = UserAuthUtil.getUserId();
        
        try {
            // 获取提交信息
            ExperimentSubmit submit = experimentSubmitService.getById(submitId);
            if (submit == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "提交记录不存在");
            }
            
            // 获取实验信息
            ExperimentVO experiment = experimentService.getExperimentDetail(submit.getExperimentId());
            if (experiment == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "实验不存在");
            }
            
            // 验证用户是否为该课程的教师
            if (!isTeacher(teacherId, experiment.getCourseId())) {
                return Result.failed(ResultCode.PARAM_ERROR, "您不是该课程的教师");
            }
            
            boolean result = experimentSubmitService.gradeSubmit(submitId, score);
            return Result.success(result);
        } catch (Exception e) {
            log.error("评分失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "评分失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计实验的提交数量
     */
    @Operation(summary = "统计实验的提交数量")
    @GetMapping("/experiment/{experimentId}/count")
    public Result<Integer> countSubmits(@PathVariable Integer experimentId) {
        int count = experimentSubmitService.countByExperimentId(experimentId);
        return Result.success(count);
    }
    
    /**
     * 检查学生是否已提交实验
     */
    @Operation(summary = "检查学生是否已提交实验")
    @GetMapping("/student/experiment/{experimentId}/check")
    public Result<Boolean> checkSubmitted(@PathVariable Integer experimentId) {
        Integer studentId = UserAuthUtil.getUserId();
        boolean submitted = experimentSubmitService.checkStudentSubmitted(experimentId, studentId);
        return Result.success(submitted);
    }
    
    /**
     * 检查用户是否为课程的教师
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否为教师
     */
    private boolean isTeacher(Integer userId, Integer courseId) {
        try {
            // TODO: 实现教师权限检查逻辑，目前简化为根据用户ID和课程ID检查权限
            // 可以调用课程服务或用户服务进行验证
            // 此处暂时返回true，后续实现完整逻辑
            return true;
        } catch (Exception e) {
            log.error("验证教师权限失败", e);
            return false;
        }
    }
} 