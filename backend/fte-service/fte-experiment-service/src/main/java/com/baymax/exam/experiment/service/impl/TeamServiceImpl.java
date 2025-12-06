package com.baymax.exam.experiment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.experiment.mapper.JoinTeamMapper;
import com.baymax.exam.experiment.mapper.TeamMapper;
import com.baymax.exam.experiment.model.JoinTeam;
import com.baymax.exam.experiment.model.Team;
import com.baymax.exam.experiment.service.ITeamService;
import com.baymax.exam.experiment.vo.TeamMemberVO;
import com.baymax.exam.experiment.vo.TeamVO;
import com.baymax.exam.user.feign.UserClient;
import com.baymax.exam.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 队伍Service实现类
 */
@Slf4j
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {
    
    @Autowired
    private TeamMapper teamMapper;
    
    @Autowired
    private JoinTeamMapper joinTeamMapper;
    
    @Autowired
    private UserClient userClient;
    
    @Autowired
    private ExperimentSubmitServiceImpl experimentSubmitService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeamVO createTeam(TeamVO teamVO) {
        try {
            // 检查用户是否已加入其他队伍
            if (hasJoinedTeam(teamVO.getExperimentId(), teamVO.getHeaderId())) {
                throw new RuntimeException("您已加入其他队伍，请先退出");
            }
            
            // 创建队伍记录
            Team team = new Team();
            BeanUtils.copyProperties(teamVO, team);
            
            // 设置时间
            LocalDateTime now = LocalDateTime.now();
            team.setCreatedAt(now);
            team.setUpdatedAt(now);
            
            // 保存队伍
            teamMapper.insert(team);
            
            // 创建队长加入记录
            JoinTeam joinTeam = new JoinTeam();
            joinTeam.setUserId(team.getHeaderId());
            joinTeam.setTeamId(team.getId());
            joinTeam.setCreatedAt(now);
            joinTeam.setUpdatedAt(now);
            joinTeamMapper.insert(joinTeam);
            
            // 返回队伍详情
            return getTeamDetail(team.getId());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建队伍失败", e);
            throw new RuntimeException("创建队伍失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeamVO updateTeam(Integer id, TeamVO teamVO) {
        try {
            // 获取队伍信息
            Team team = teamMapper.selectById(id);
            if (team == null) {
                throw new RuntimeException("队伍不存在");
            }
            
            // 检查是否为队长
            if (!team.getHeaderId().equals(teamVO.getHeaderId())) {
                throw new RuntimeException("只有队长才能修改队伍信息");
            }
            
            // 更新队伍信息
            team.setName(teamVO.getName());
            team.setUpdatedAt(LocalDateTime.now());
            teamMapper.updateById(team);
            
            // 返回更新后的队伍详情
            return getTeamDetail(id);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新队伍失败", e);
            throw new RuntimeException("更新队伍失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(Integer id, Integer userId) {
        try {
            // 获取队伍信息
            Team team = teamMapper.selectById(id);
            if (team == null) {
                throw new RuntimeException("队伍不存在");
            }
            
            // 检查是否为队长
            if (!team.getHeaderId().equals(userId)) {
                throw new RuntimeException("只有队长才能删除队伍");
            }
            
            // 获取所有队员
            List<JoinTeam> members = joinTeamMapper.getMembersByTeamId(id);
            
            // 删除所有队员记录
            for (JoinTeam member : members) {
                joinTeamMapper.deleteById(member.getId());
            }
            
            // 删除队伍
            return teamMapper.deleteById(id) > 0;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除队伍失败", e);
            throw new RuntimeException("删除队伍失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(Integer teamId, Integer userId) {
        try {
            // 获取队伍信息
            Team team = teamMapper.selectById(teamId);
            if (team == null) {
                throw new RuntimeException("队伍不存在");
            }
            
            // 检查用户是否已加入其他队伍
            if (hasJoinedTeam(team.getExperimentId(), userId)) {
                throw new RuntimeException("您已加入其他队伍，请先退出");
            }
            
            // 检查是否已加入该队伍
            JoinTeam existingMember = joinTeamMapper.getMemberByUserIdAndTeamId(userId, teamId);
            if (existingMember != null) {
                throw new RuntimeException("您已加入该队伍");
            }
            
            // 创建加入记录
            JoinTeam joinTeam = new JoinTeam();
            joinTeam.setUserId(userId);
            joinTeam.setTeamId(teamId);
            LocalDateTime now = LocalDateTime.now();
            joinTeam.setCreatedAt(now);
            joinTeam.setUpdatedAt(now);
            
            return joinTeamMapper.insert(joinTeam) > 0;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("加入队伍失败", e);
            throw new RuntimeException("加入队伍失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean leaveTeam(Integer teamId, Integer userId) {
        try {
            // 获取队伍信息
            Team team = teamMapper.selectById(teamId);
            if (team == null) {
                throw new RuntimeException("队伍不存在");
            }
            
            // 检查是否已加入该队伍
            JoinTeam member = joinTeamMapper.getMemberByUserIdAndTeamId(userId, teamId);
            if (member == null) {
                throw new RuntimeException("您不是该队伍成员");
            }
            
            // 如果是队长退出，则解散队伍
            if (team.getHeaderId().equals(userId)) {
                return deleteTeam(teamId, userId);
            }
            
            // 普通成员退出
            return joinTeamMapper.deleteById(member.getId()) > 0;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("退出队伍失败", e);
            throw new RuntimeException("退出队伍失败: " + e.getMessage());
        }
    }
    
    @Override
    public TeamVO getTeamDetail(Integer id) {
        try {
            // 获取队伍基本信息
            Team team = teamMapper.selectById(id);
            if (team == null) {
                return null;
            }
            
            // 转换为VO
            TeamVO teamVO = new TeamVO();
            BeanUtils.copyProperties(team, teamVO);
            
            // 获取队长信息
            User header = userClient.getBaseUserInfoById(team.getHeaderId());
            if (header != null) {
                teamVO.setHeaderName(header.getNickname());
            }
            
            // 获取队伍成员
            List<JoinTeam> joinTeamList = joinTeamMapper.getMembersByTeamId(id);
            List<TeamMemberVO> members = new ArrayList<>();
            
            for (JoinTeam joinTeam : joinTeamList) {
                TeamMemberVO memberVO = new TeamMemberVO();
                memberVO.setUserId(joinTeam.getUserId());
                memberVO.setJoinTime(joinTeam.getCreatedAt());
                
                // 设置是否为队长
                memberVO.setIsHeader(joinTeam.getUserId().equals(team.getHeaderId()));
                
                // 获取用户信息
                User user = userClient.getBaseUserInfoById(joinTeam.getUserId());
                if (user != null) {
                    memberVO.setUserName(user.getNickname());
                    memberVO.setUserPicture(user.getPicture());
                }
                
                members.add(memberVO);
            }
            
            // 设置成员列表和数量
            teamVO.setMembers(members);
            teamVO.setMemberCount(members.size());
            
            // 获取队伍得分（从队长的提交记录中获取）
            teamVO.setScore(experimentSubmitService.getStudentScore(team.getExperimentId(), team.getHeaderId()));
            
            return teamVO;
        } catch (Exception e) {
            log.error("获取队伍详情失败", e);
            throw new RuntimeException("获取队伍详情失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<TeamVO> getTeamsByExperimentId(Integer experimentId) {
        try {
            // 获取队伍列表
            List<Team> teams = teamMapper.getTeamsByExperimentId(experimentId);
            
            // 转换为VO列表
            return teams.stream()
                    .map(team -> getTeamDetail(team.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取项目实训队伍列表失败", e);
            throw new RuntimeException("获取项目实训队伍列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public TeamVO getUserTeam(Integer experimentId, Integer userId) {
        try {
            // 查询用户加入的队伍
            JoinTeam joinTeam = joinTeamMapper.getUserTeam(userId, experimentId);
            if (joinTeam == null) {
                return null;
            }
            
            // 返回队伍详情
            return getTeamDetail(joinTeam.getTeamId());
        } catch (Exception e) {
            log.error("获取用户队伍失败", e);
            throw new RuntimeException("获取用户队伍失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isTeamHeader(Integer teamId, Integer userId) {
        try {
            Team team = teamMapper.selectById(teamId);
            return team != null && team.getHeaderId().equals(userId);
        } catch (Exception e) {
            log.error("检查用户是否为队长失败", e);
            return false;
        }
    }
    
    @Override
    public boolean hasJoinedTeam(Integer experimentId, Integer userId) {
        try {
            return joinTeamMapper.checkUserInTeam(userId, experimentId) > 0;
        } catch (Exception e) {
            log.error("检查用户是否已加入队伍失败", e);
            return false;
        }
    }
} 