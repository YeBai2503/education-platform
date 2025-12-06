package com.baymax.exam.experiment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.experiment.model.ExperimentSubmit;
import com.baymax.exam.experiment.vo.ExperimentSubmitVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 实验提交Service接口
 */
public interface IExperimentSubmitService extends IService<ExperimentSubmit> {
    
    /**
     * 分页查询实验的提交列表（老师查看）
     *
     * @param page 分页参数
     * @param experimentId 实验ID
     * @return 提交列表
     */
    IPage<ExperimentSubmitVO> pageSubmitsByExperimentId(Page<ExperimentSubmit> page, Integer experimentId);
    
    /**
     * 获取学生的实验提交详情
     *
     * @param experimentId 实验ID
     * @param studentId 学生ID
     * @return 提交详情
     */
    ExperimentSubmitVO getStudentSubmit(Integer experimentId, Integer studentId);
    
    /**
     * 提交实验
     *
     * @param submitVO 提交信息
     * @param file1 附件文件1
     * @param file2 附件文件2
     * @param file3 附件文件3
     * @param file4 附件文件4
     * @param file5 附件文件5
     * @return 提交后的信息
     */
    ExperimentSubmitVO submitExperiment(ExperimentSubmitVO submitVO, 
                                       MultipartFile file1, 
                                       MultipartFile file2, 
                                       MultipartFile file3, 
                                       MultipartFile file4, 
                                       MultipartFile file5);
    
    /**
     * 更新实验提交
     *
     * @param id 提交ID
     * @param submitVO 提交信息
     * @param file1 附件文件1
     * @param file2 附件文件2
     * @param file3 附件文件3
     * @param file4 附件文件4
     * @param file5 附件文件5
     * @return 更新后的信息
     */
    ExperimentSubmitVO updateSubmit(Integer id, ExperimentSubmitVO submitVO, 
                                   MultipartFile file1, 
                                   MultipartFile file2, 
                                   MultipartFile file3, 
                                   MultipartFile file4, 
                                   MultipartFile file5);
    
    /**
     * 更新实验提交（支持指定删除的文件URL列表）
     *
     * @param id 提交ID
     * @param submitVO 提交信息
     * @param filesToDelete 要删除的文件URL列表
     * @param file1 附件文件1
     * @param file2 附件文件2
     * @param file3 附件文件3
     * @param file4 附件文件4
     * @param file5 附件文件5
     * @return 更新后的信息
     */
    ExperimentSubmitVO updateSubmitWithFileDelete(Integer id, ExperimentSubmitVO submitVO, List<String> filesToDelete, 
                                                 MultipartFile file1, 
                                                 MultipartFile file2, 
                                                 MultipartFile file3, 
                                                 MultipartFile file4, 
                                                 MultipartFile file5);
    
    /**
     * 评分
     *
     * @param id 提交ID
     * @param score 分数
     * @return 是否评分成功
     */
    boolean gradeSubmit(Integer id, Integer score);
    
    /**
     * 统计实验的提交数量
     *
     * @param experimentId 实验ID
     * @return 提交数量
     */
    int countByExperimentId(Integer experimentId);
    
    /**
     * 检查学生是否已提交实验
     *
     * @param experimentId 实验ID
     * @param studentId 学生ID
     * @return 是否已提交
     */
    boolean checkStudentSubmitted(Integer experimentId, Integer studentId);
    
    /**
     * 获取学生的实验得分
     *
     * @param experimentId 实验ID
     * @param studentId 学生ID
     * @return 得分，未评分或未提交返回null
     */
    Integer getStudentScore(Integer experimentId, Integer studentId);
} 