package com.baymax.exam.experiment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.experiment.model.JoinTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 队伍成员Mapper接口
 */
@Mapper
public interface JoinTeamMapper extends BaseMapper<JoinTeam> {
    
    /**
     * 根据队伍ID查询成员列表
     *
     * @param teamId 队伍ID
     * @return 成员列表
     */
    @Select("SELECT * FROM ep_join_team WHERE team_id = #{teamId}")
    List<JoinTeam> getMembersByTeamId(@Param("teamId") Integer teamId);
    
    /**
     * 根据用户ID和队伍ID查询成员记录
     *
     * @param userId 用户ID
     * @param teamId 队伍ID
     * @return 成员记录
     */
    @Select("SELECT * FROM ep_join_team WHERE user_id = #{userId} AND team_id = #{teamId}")
    JoinTeam getMemberByUserIdAndTeamId(@Param("userId") Integer userId, @Param("teamId") Integer teamId);
    
    /**
     * 根据用户ID查询加入的队伍
     *
     * @param userId 用户ID
     * @param experimentId 项目实训ID
     * @return 队伍ID
     */
    @Select("SELECT jt.* FROM ep_join_team jt " +
            "JOIN ep_team t ON jt.team_id = t.id " +
            "WHERE jt.user_id = #{userId} AND t.experiment_id = #{experimentId}")
    JoinTeam getUserTeam(@Param("userId") Integer userId, @Param("experimentId") Integer experimentId);
    
    /**
     * 统计队伍成员数量
     *
     * @param teamId 队伍ID
     * @return 成员数量
     */
    @Select("SELECT COUNT(*) FROM ep_join_team WHERE team_id = #{teamId}")
    int countByTeamId(@Param("teamId") Integer teamId);
    
    /**
     * 检查用户是否已加入任何队伍（针对特定项目实训）
     *
     * @param userId 用户ID
     * @param experimentId 项目实训ID
     * @return 是否已加入队伍
     */
    @Select("SELECT COUNT(*) FROM ep_join_team jt " +
            "JOIN ep_team t ON jt.team_id = t.id " +
            "WHERE jt.user_id = #{userId} AND t.experiment_id = #{experimentId}")
    int checkUserInTeam(@Param("userId") Integer userId, @Param("experimentId") Integer experimentId);
} 