package com.baymax.exam.experiment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.experiment.model.Team;
import com.baymax.exam.experiment.vo.TeamVO;

import java.util.List;

/**
 * 队伍Service接口
 */
public interface ITeamService extends IService<Team> {
    
    /**
     * 创建队伍
     *
     * @param teamVO 队伍信息
     * @return 创建后的队伍信息
     */
    TeamVO createTeam(TeamVO teamVO);
    
    /**
     * 更新队伍信息
     *
     * @param id 队伍ID
     * @param teamVO 队伍信息
     * @return 更新后的队伍信息
     */
    TeamVO updateTeam(Integer id, TeamVO teamVO);
    
    /**
     * 删除队伍
     *
     * @param id 队伍ID
     * @param userId 操作用户ID（必须是队长）
     * @return 是否删除成功
     */
    boolean deleteTeam(Integer id, Integer userId);
    
    /**
     * 加入队伍
     *
     * @param teamId 队伍ID
     * @param userId 用户ID
     * @return 是否加入成功
     */
    boolean joinTeam(Integer teamId, Integer userId);
    
    /**
     * 退出队伍
     *
     * @param teamId 队伍ID
     * @param userId 用户ID
     * @return 是否退出成功
     */
    boolean leaveTeam(Integer teamId, Integer userId);
    
    /**
     * 获取队伍详情
     *
     * @param id 队伍ID
     * @return 队伍详情
     */
    TeamVO getTeamDetail(Integer id);
    
    /**
     * 获取项目实训的所有队伍
     *
     * @param experimentId 项目实训ID
     * @return 队伍列表
     */
    List<TeamVO> getTeamsByExperimentId(Integer experimentId);
    
    /**
     * 获取用户在项目实训中的队伍
     *
     * @param experimentId 项目实训ID
     * @param userId 用户ID
     * @return 队伍信息
     */
    TeamVO getUserTeam(Integer experimentId, Integer userId);
    
    /**
     * 检查用户是否为队长
     *
     * @param teamId 队伍ID
     * @param userId 用户ID
     * @return 是否为队长
     */
    boolean isTeamHeader(Integer teamId, Integer userId);
    
    /**
     * 检查用户是否已加入队伍
     *
     * @param experimentId 项目实训ID
     * @param userId 用户ID
     * @return 是否已加入队伍
     */
    boolean hasJoinedTeam(Integer experimentId, Integer userId);
} 