package com.baymax.exam.experiment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.experiment.model.Experiment;
import com.baymax.exam.experiment.vo.ExperimentVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 实验Service接口
 */
public interface IExperimentService extends IService<Experiment> {
    
    /**
     * 分页查询课程下的实验列表
     *
     * @param page 分页参数
     * @param courseId 课程ID
     * @param type 类型：0-实验，1-项目实训，null-全部
     * @return 实验列表
     */
    IPage<ExperimentVO> pageExperimentsByCourseId(Page<Experiment> page, Integer courseId, Integer type);
    
    /**
     * 获取实验详情
     *
     * @param id 实验ID
     * @return 实验详情
     */
    ExperimentVO getExperimentDetail(Integer id);
    
    /**
     * 创建实验
     *
     * @param experimentVO 实验信息
     * @param file1 附件文件1
     * @param file2 附件文件2
     * @param file3 附件文件3
     * @param file4 附件文件4
     * @param file5 附件文件5
     * @return 创建后的实验信息
     */
    ExperimentVO createExperiment(ExperimentVO experimentVO, 
                                 MultipartFile file1,
                                 MultipartFile file2,
                                 MultipartFile file3,
                                 MultipartFile file4,
                                 MultipartFile file5);
    
    /**
     * 更新实验
     *
     * @param id 实验ID
     * @param experimentVO 实验信息
     * @param file1 附件文件1
     * @param file2 附件文件2
     * @param file3 附件文件3
     * @param file4 附件文件4
     * @param file5 附件文件5
     * @return 更新后的实验信息
     */
    ExperimentVO updateExperiment(Integer id, ExperimentVO experimentVO, 
                                 MultipartFile file1,
                                 MultipartFile file2,
                                 MultipartFile file3,
                                 MultipartFile file4,
                                 MultipartFile file5);
    
    /**
     * 更新实验（支持指定删除的文件URL列表）
     *
     * @param id 实验ID
     * @param experimentVO 实验信息
     * @param filesToDelete 要删除的文件URL列表
     * @param file1 附件文件1
     * @param file2 附件文件2
     * @param file3 附件文件3
     * @param file4 附件文件4
     * @param file5 附件文件5
     * @return 更新后的实验信息
     */
    ExperimentVO updateExperimentWithFileDelete(Integer id, ExperimentVO experimentVO, List<String> filesToDelete, 
                                              MultipartFile file1,
                                              MultipartFile file2,
                                              MultipartFile file3,
                                              MultipartFile file4,
                                              MultipartFile file5);
    
    /**
     * 删除实验
     *
     * @param id 实验ID
     * @return 是否删除成功
     */
    boolean deleteExperiment(Integer id);
    
    /**
     * 统计课程下的实验数量
     *
     * @param courseId 课程ID
     * @param type 类型：0-实验，1-项目实训，null-全部
     * @return 实验数量
     */
    int countByCourseId(Integer courseId, Integer type);
} 