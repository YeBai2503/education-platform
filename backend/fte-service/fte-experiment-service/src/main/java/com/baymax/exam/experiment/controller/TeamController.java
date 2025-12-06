package com.baymax.exam.experiment.controller;

import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.core.result.ResultCode;
import com.baymax.exam.experiment.model.Team;
import com.baymax.exam.experiment.service.IExperimentService;
import com.baymax.exam.experiment.service.ITeamService;
import com.baymax.exam.experiment.vo.ExperimentVO;
import com.baymax.exam.experiment.vo.TeamVO;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 队伍Controller
 */
@Slf4j
@RestController
@RequestMapping("/experiment/team")
@Tag(name = "项目实训队伍管理")
public class TeamController {
    
    @Autowired
    private ITeamService teamService;
    
    @Autowired
    private IExperimentService experimentService;
    
    /**
     * 创建队伍
     */
    @Operation(summary = "创建队伍")
    @PostMapping("/create")
    public Result<TeamVO> createTeam(@RequestParam("experimentId") Integer experimentId,
                                @RequestParam("name") String name) {
        Integer userId = UserAuthUtil.getUserId();
        
        try {
            // 检查项目实训是否存在
            ExperimentVO experiment = experimentService.getExperimentDetail(experimentId);
            if (experiment == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "项目实训不存在");
            }
            
            // 检查是否为项目实训类型
            if (experiment.getType() == null || experiment.getType() != 1) {
                return Result.failed(ResultCode.PARAM_ERROR, "该实验不是项目实训类型");
            }
            
            // 创建TeamVO对象
            TeamVO teamVO = new TeamVO();
            teamVO.setExperimentId(experimentId);
            teamVO.setName(name);
            teamVO.setHeaderId(userId);
            
            TeamVO result = teamService.createTeam(teamVO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("创建队伍失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "创建队伍失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新队伍信息
     */
    @Operation(summary = "更新队伍信息")
    @PutMapping("/{id}")
    public Result<TeamVO> updateTeam(@PathVariable Integer id, @RequestBody @Valid TeamVO teamVO) {
        Integer userId = UserAuthUtil.getUserId();
        
        try {
            // 检查队伍是否存在
            TeamVO existingTeam = teamService.getTeamDetail(id);
            if (existingTeam == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "队伍不存在");
            }
            
            // 检查是否为队长
            if (!existingTeam.getHeaderId().equals(userId)) {
                return Result.failed(ResultCode.PARAM_ERROR, "只有队长才能修改队伍信息");
            }
            
            // 设置队长ID和项目实训ID（不允许修改）
            teamVO.setHeaderId(userId);
            teamVO.setExperimentId(existingTeam.getExperimentId());
            
            TeamVO result = teamService.updateTeam(id, teamVO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新队伍失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "更新队伍失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除队伍
     */
    @Operation(summary = "删除队伍")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTeam(@PathVariable Integer id) {
        Integer userId = UserAuthUtil.getUserId();
        
        try {
            boolean result = teamService.deleteTeam(id, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除队伍失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "删除队伍失败: " + e.getMessage());
        }
    }
    
    /**
     * 加入队伍
     */
    @Operation(summary = "加入队伍")
    @PostMapping("/join/{teamId}")
    public Result<Boolean> joinTeam(@PathVariable Integer teamId) {
        Integer userId = UserAuthUtil.getUserId();
        
        try {
            boolean result = teamService.joinTeam(teamId, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("加入队伍失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "加入队伍失败: " + e.getMessage());
        }
    }
    
    /**
     * 退出队伍
     */
    @Operation(summary = "退出队伍")
    @PostMapping("/leave/{teamId}")
    public Result<Boolean> leaveTeam(@PathVariable Integer teamId) {
        Integer userId = UserAuthUtil.getUserId();
        
        try {
            boolean result = teamService.leaveTeam(teamId, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("退出队伍失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "退出队伍失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取队伍详情
     */
    @Operation(summary = "获取队伍详情")
    @GetMapping("/{id}")
    public Result<TeamVO> getTeamDetail(@PathVariable Integer id) {
        try {
            TeamVO team = teamService.getTeamDetail(id);
            if (team == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "队伍不存在");
            }
            return Result.success(team);
        } catch (Exception e) {
            log.error("获取队伍详情失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "获取队伍详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取项目实训的所有队伍
     */
    @Operation(summary = "获取项目实训的所有队伍")
    @GetMapping("/experiment/{experimentId}/list")
    public Result<List<TeamVO>> getTeamsByExperimentId(@PathVariable Integer experimentId) {
        try {
            // 检查项目实训是否存在
            ExperimentVO experiment = experimentService.getExperimentDetail(experimentId);
            if (experiment == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "项目实训不存在");
            }
            
            // 检查是否为项目实训类型
            if (experiment.getType() == null || experiment.getType() != 1) {
                return Result.failed(ResultCode.PARAM_ERROR, "该实验不是项目实训类型");
            }
            
            List<TeamVO> teams = teamService.getTeamsByExperimentId(experimentId);
            return Result.success(teams);
        } catch (Exception e) {
            log.error("获取项目实训队伍列表失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "获取项目实训队伍列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户在项目实训中的队伍
     */
    @Operation(summary = "获取用户在项目实训中的队伍")
    @GetMapping("/experiment/{experimentId}/my-team")
    public Result<TeamVO> getUserTeam(@PathVariable Integer experimentId) {
        Integer userId = UserAuthUtil.getUserId();
        
        try {
            // 检查项目实训是否存在
            ExperimentVO experiment = experimentService.getExperimentDetail(experimentId);
            if (experiment == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "项目实训不存在");
            }
            
            // 检查是否为项目实训类型
            if (experiment.getType() == null || experiment.getType() != 1) {
                return Result.failed(ResultCode.PARAM_ERROR, "该实验不是项目实训类型");
            }
            
            TeamVO team = teamService.getUserTeam(experimentId, userId);
            return Result.success(team);
        } catch (Exception e) {
            log.error("获取用户队伍失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "获取用户队伍失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否已加入队伍
     */
    @Operation(summary = "检查用户是否已加入队伍")
    @GetMapping("/experiment/{experimentId}/check-joined")
    public Result<Boolean> hasJoinedTeam(@PathVariable Integer experimentId) {
        Integer userId = UserAuthUtil.getUserId();
        
        try {
            boolean hasJoined = teamService.hasJoinedTeam(experimentId, userId);
            return Result.success(hasJoined);
        } catch (Exception e) {
            log.error("检查用户是否已加入队伍失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "检查用户是否已加入队伍失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否为队长
     */
    @Operation(summary = "检查用户是否为队长")
    @GetMapping("/{teamId}/check-header")
    public Result<Boolean> isTeamHeader(@PathVariable Integer teamId) {
        Integer userId = UserAuthUtil.getUserId();
        
        try {
            boolean isHeader = teamService.isTeamHeader(teamId, userId);
            return Result.success(isHeader);
        } catch (Exception e) {
            log.error("检查用户是否为队长失败", e);
            return Result.failed(ResultCode.PARAM_ERROR, "检查用户是否为队长失败: " + e.getMessage());
        }
    }
} 