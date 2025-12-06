package com.baymax.exam.experiment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.experiment.model.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 队伍Mapper接口
 */
@Mapper
public interface TeamMapper extends BaseMapper<Team> {
    
    /**
     * 根据项目实训ID查询队伍列表
     *
     * @param experimentId 项目实训ID
     * @return 队伍列表
     */
    @Select("SELECT * FROM ep_team WHERE experiment_id = #{experimentId}")
    List<Team> getTeamsByExperimentId(@Param("experimentId") Integer experimentId);
    
    /**
     * 根据队长ID查询队伍
     *
     * @param headerId 队长ID
     * @param experimentId 项目实训ID
     * @return 队伍
     */
    @Select("SELECT * FROM ep_team WHERE header_id = #{headerId} AND experiment_id = #{experimentId}")
    Team getTeamByHeaderId(@Param("headerId") Integer headerId, @Param("experimentId") Integer experimentId);
    
    /**
     * 根据项目实训ID统计队伍数量
     *
     * @param experimentId 项目实训ID
     * @return 队伍数量
     */
    @Select("SELECT COUNT(*) FROM ep_team WHERE experiment_id = #{experimentId}")
    int countByExperimentId(@Param("experimentId") Integer experimentId);
} 