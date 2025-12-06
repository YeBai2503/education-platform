package com.baymax.exam.experiment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.experiment.mapper.ExperimentMapper;
import com.baymax.exam.experiment.mapper.ExperimentSubmitMapper;
import com.baymax.exam.experiment.model.Experiment;
import com.baymax.exam.experiment.model.ExperimentSubmit;
import com.baymax.exam.experiment.service.IExperimentSubmitService;
import com.baymax.exam.experiment.utils.FileUtils;
import com.baymax.exam.experiment.vo.ExperimentSubmitVO;
import com.baymax.exam.file.feign.FileDetailClient;
import com.baymax.exam.user.feign.UserClient;
import com.baymax.exam.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

/**
 * 实验提交Service实现类
 */
@Slf4j
@Service
public class ExperimentSubmitServiceImpl extends ServiceImpl<ExperimentSubmitMapper, ExperimentSubmit> implements IExperimentSubmitService {
    
    @Autowired
    private ExperimentSubmitMapper experimentSubmitMapper;
    
    @Autowired
    private ExperimentMapper experimentMapper;
    
    @Autowired
    private UserClient userClient;
    
    @Autowired
    private FileUtils fileUtils;
    
    @Autowired
    private FileDetailClient fileDetailClient;
    
    @Value("${experiment.domain-url:http://192.168.78.91:10030/}")
    private String domainUrl;
    
    @Override
    public IPage<ExperimentSubmitVO> pageSubmitsByExperimentId(Page<ExperimentSubmit> page, Integer experimentId) {
        // 查询提交列表
        LambdaQueryWrapper<ExperimentSubmit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExperimentSubmit::getExperimentId, experimentId)
                .orderByDesc(ExperimentSubmit::getCreatedAt);
        
        IPage<ExperimentSubmit> submitPage = experimentSubmitMapper.selectPage(page, wrapper);
        
        // 转换为VO
        IPage<ExperimentSubmitVO> voPage = submitPage.convert(submit -> {
            ExperimentSubmitVO vo = convertToVO(submit);
            
            // 获取学生信息
            try {
                User user = userClient.getUserById(submit.getStudentId()).getData();
                if (user != null) {
                    vo.setStudentName(user.getNickname());
                }
            } catch (Exception e) {
                log.error("获取学生信息失败", e);
                // 设置默认学生名称，避免前端显示问题
                vo.setStudentName("学生" + submit.getStudentId());
            }
            
            // 获取实验标题
            try {
                Experiment experiment = experimentMapper.selectById(submit.getExperimentId());
                if (experiment != null) {
                    vo.setExperimentTitle(experiment.getTitle());
                }
            } catch (Exception e) {
                log.error("获取实验信息失败", e);
            }
            
            return vo;
        });
        
        return voPage;
    }
    
    @Override
    public ExperimentSubmitVO getStudentSubmit(Integer experimentId, Integer studentId) {
        // 查询提交记录
        LambdaQueryWrapper<ExperimentSubmit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExperimentSubmit::getExperimentId, experimentId)
                .eq(ExperimentSubmit::getStudentId, studentId);
        
        ExperimentSubmit submit = experimentSubmitMapper.selectOne(wrapper);
        if (submit == null) {
            return null;
        }
        
        ExperimentSubmitVO vo = convertToVO(submit);
        
        // 获取学生信息
        try {
            User user = userClient.getUserById(studentId).getData();
            if (user != null) {
                vo.setStudentName(user.getNickname());
            } else {
                // 如果用户服务返回null，设置默认名称
                vo.setStudentName("学生" + studentId);
            }
        } catch (Exception e) {
            log.error("获取学生信息失败", e);
            // 设置默认学生名称，避免前端显示问题
            vo.setStudentName("学生" + studentId);
        }
        
        // 获取实验标题
        try {
            Experiment experiment = experimentMapper.selectById(experimentId);
            if (experiment != null) {
                vo.setExperimentTitle(experiment.getTitle());
            }
        } catch (Exception e) {
            log.error("获取实验信息失败", e);
        }
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExperimentSubmitVO submitExperiment(ExperimentSubmitVO submitVO, 
                                              MultipartFile file1, 
                                              MultipartFile file2, 
                                              MultipartFile file3, 
                                              MultipartFile file4, 
                                              MultipartFile file5) {
        try {
            // 打印接收到的文件信息
            log.info("服务层处理实验提交，学生ID: {}, 实验ID: {}", submitVO.getStudentId(), submitVO.getExperimentId());
            
            // 收集非空文件
            List<MultipartFile> validFiles = new ArrayList<>();
            if (file1 != null && !file1.isEmpty()) validFiles.add(file1);
            if (file2 != null && !file2.isEmpty()) validFiles.add(file2);
            if (file3 != null && !file3.isEmpty()) validFiles.add(file3);
            if (file4 != null && !file4.isEmpty()) validFiles.add(file4);
            if (file5 != null && !file5.isEmpty()) validFiles.add(file5);
            
            // 检查实验是否存在
            Experiment experiment = experimentMapper.selectById(submitVO.getExperimentId());
            if (experiment == null) {
                throw new RuntimeException("实验不存在");
            }
            
            // 检查是否已提交
            if (checkStudentSubmitted(submitVO.getExperimentId(), submitVO.getStudentId())) {
                throw new RuntimeException("已经提交过该实验，请使用更新功能");
            }
            
            // 创建提交记录
            ExperimentSubmit submit = new ExperimentSubmit();
            BeanUtils.copyProperties(submitVO, submit);
            
            // 设置时间
            LocalDateTime now = LocalDateTime.now();
            submit.setCreatedAt(now);
            submit.setUpdatedAt(now);
            
            // 上传文件
            if (!validFiles.isEmpty()) {
                // 创建目录路径：static/experiments/experimentId/submit/studentId/uuid
                String uuid = UUID.randomUUID().toString();
                String directory = submitVO.getExperimentId() + "/submit/" + submitVO.getStudentId() + "/" + uuid;
                
                try {
                    // 使用文件工具上传文件
                    log.info("开始上传文件到目录: {}", directory);
                    
                    // 转换为数组便于使用现有的上传方法
                    MultipartFile[] fileArray = validFiles.toArray(new MultipartFile[0]);
                    List<String> fileUrls = fileUtils.uploadFiles(fileArray, directory);
                
                // 设置文件数量和URL
                submit.setNumFile(fileUrls.size());
                
                // 设置文件URL
                for (int i = 0; i < fileUrls.size(); i++) {
                    switch (i) {
                            case 0: submit.setFile1URL(fileUrls.get(i)); break;
                            case 1: submit.setFile2URL(fileUrls.get(i)); break;
                            case 2: submit.setFile3URL(fileUrls.get(i)); break;
                            case 3: submit.setFile4URL(fileUrls.get(i)); break;
                            case 4: submit.setFile5URL(fileUrls.get(i)); break;
                    }
                    }
                } catch (Exception e) {
                    log.error("上传文件失败", e);
                    throw new RuntimeException("上传文件失败: " + e.getMessage());
                }
            } else {
                submit.setNumFile(0);
            }
            
            // 保存提交
            experimentSubmitMapper.insert(submit);
            
            // 返回VO
            return getStudentSubmit(submit.getExperimentId(), submit.getStudentId());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("提交实验失败", e);
            throw new RuntimeException("提交实验失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExperimentSubmitVO updateSubmit(Integer id, ExperimentSubmitVO submitVO, 
                                          MultipartFile file1, 
                                          MultipartFile file2, 
                                          MultipartFile file3, 
                                          MultipartFile file4, 
                                          MultipartFile file5) {
        try {
            // 获取原提交记录
            ExperimentSubmit submit = experimentSubmitMapper.selectById(id);
            if (submit == null) {
                throw new RuntimeException("提交记录不存在");
            }
            
            // 保存旧的文件URL，用于后续删除
            List<String> oldFileUrls = new ArrayList<>();
            if (submit.getNumFile() != null && submit.getNumFile() > 0) {
                if (submit.getFile1URL() != null) oldFileUrls.add(submit.getFile1URL());
                if (submit.getFile2URL() != null) oldFileUrls.add(submit.getFile2URL());
                if (submit.getFile3URL() != null) oldFileUrls.add(submit.getFile3URL());
                if (submit.getFile4URL() != null) oldFileUrls.add(submit.getFile4URL());
                if (submit.getFile5URL() != null) oldFileUrls.add(submit.getFile5URL());
            }
            
            // 更新基本信息
            submit.setDetail(submitVO.getDetail() != null ? submitVO.getDetail() : submit.getDetail());
            submit.setUpdatedAt(LocalDateTime.now());
            
            boolean filesUpdated = false; // 标记是否更新了文件
            
            // 判断是否需要清空所有文件
            if (submitVO.getFileUrls() == null) {
                // 清空所有文件URL
                submit.setNumFile(0);
                submit.setFile1URL(null);
                submit.setFile2URL(null);
                submit.setFile3URL(null);
                submit.setFile4URL(null);
                submit.setFile5URL(null);
                filesUpdated = true;
            }
            
            // 收集非空文件
            List<MultipartFile> validFiles = new ArrayList<>();
            if (file1 != null && !file1.isEmpty()) validFiles.add(file1);
            if (file2 != null && !file2.isEmpty()) validFiles.add(file2);
            if (file3 != null && !file3.isEmpty()) validFiles.add(file3);
            if (file4 != null && !file4.isEmpty()) validFiles.add(file4);
            if (file5 != null && !file5.isEmpty()) validFiles.add(file5);
            
            // 如果有新文件上传
            if (!validFiles.isEmpty()) {
                // 创建目录路径：static/experiments/experimentId/submit/studentId/uuid
                String uuid = UUID.randomUUID().toString();
                String directory = submit.getExperimentId() + "/submit/" + submit.getStudentId() + "/" + uuid;
                
                try {
                    // 使用文件工具上传文件
                    MultipartFile[] fileArray = validFiles.toArray(new MultipartFile[0]);
                    List<String> fileUrls = fileUtils.uploadFiles(fileArray, directory);
                
                // 设置文件数量和URL
                submit.setNumFile(fileUrls.size());
                
                // 重置所有文件URL
                submit.setFile1URL(null);
                submit.setFile2URL(null);
                submit.setFile3URL(null);
                submit.setFile4URL(null);
                submit.setFile5URL(null);
                
                // 设置新的文件URL
                for (int i = 0; i < fileUrls.size(); i++) {
                    switch (i) {
                            case 0: submit.setFile1URL(fileUrls.get(i)); break;
                            case 1: submit.setFile2URL(fileUrls.get(i)); break;
                            case 2: submit.setFile3URL(fileUrls.get(i)); break;
                            case 3: submit.setFile4URL(fileUrls.get(i)); break;
                            case 4: submit.setFile5URL(fileUrls.get(i)); break;
                        }
                    }
                    
                    filesUpdated = true;
                } catch (Exception e) {
                    log.error("上传文件失败", e);
                    throw new RuntimeException("上传文件失败: " + e.getMessage());
                    }
                }
                
            // 更新提交
            experimentSubmitMapper.updateById(submit);
            
            // 如果更新了文件，删除旧文件
            if (filesUpdated && !oldFileUrls.isEmpty()) {
                int deletedCount = fileUtils.deleteFiles(oldFileUrls);
                log.info("删除了{}个旧文件", deletedCount);
            }
            
            // 返回更新后的提交详情
            return getStudentSubmit(submit.getExperimentId(), submit.getStudentId());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新提交失败", e);
            throw new RuntimeException("更新提交失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExperimentSubmitVO updateSubmitWithFileDelete(Integer id, ExperimentSubmitVO submitVO, 
                                               List<String> filesToDelete,
                                               MultipartFile file1, 
                                               MultipartFile file2, 
                                               MultipartFile file3, 
                                               MultipartFile file4, 
                                               MultipartFile file5) {
        try {
            // 获取原提交记录
            ExperimentSubmit submit = experimentSubmitMapper.selectById(id);
            if (submit == null) {
                throw new RuntimeException("提交记录不存在");
            }
            
            // 获取当前文件URL列表，确保没有重复
            Set<String> currentFileUrlSet = new HashSet<>();
            List<String> currentFileUrls = new ArrayList<>();
            if (submit.getNumFile() != null && submit.getNumFile() > 0) {
                if (submit.getFile1URL() != null && currentFileUrlSet.add(submit.getFile1URL()))
                    currentFileUrls.add(submit.getFile1URL());
                if (submit.getFile2URL() != null && currentFileUrlSet.add(submit.getFile2URL()))
                    currentFileUrls.add(submit.getFile2URL());
                if (submit.getFile3URL() != null && currentFileUrlSet.add(submit.getFile3URL()))
                    currentFileUrls.add(submit.getFile3URL());
                if (submit.getFile4URL() != null && currentFileUrlSet.add(submit.getFile4URL()))
                    currentFileUrls.add(submit.getFile4URL());
                if (submit.getFile5URL() != null && currentFileUrlSet.add(submit.getFile5URL()))
                    currentFileUrls.add(submit.getFile5URL());
            }
            
            // 检查是否有重复文件URL
            if (currentFileUrls.size() != currentFileUrlSet.size()) {
                log.warn("检测到数据库中有重复的文件URL，已去重");
            }
            
            // 如果数据库中的numFile与实际文件数不一致，记录日志
            if (submit.getNumFile() != null && submit.getNumFile() != currentFileUrls.size()) {
                log.warn("数据库中的文件数量({})与实际文件数量({})不一致，将进行修正", 
                        submit.getNumFile(), currentFileUrls.size());
            }
            
            // 更新基本信息
            submit.setDetail(submitVO.getDetail() != null ? submitVO.getDetail() : submit.getDetail());
            submit.setUpdatedAt(LocalDateTime.now());
            
            // 处理要删除的文件
            List<String> filesToKeep = new ArrayList<>(currentFileUrls);
            List<String> actualFilesToDelete = new ArrayList<>();
            boolean filesChanged = false;
            
            if (filesToDelete != null && !filesToDelete.isEmpty()) {
                log.info("收到删除文件请求: {}", filesToDelete);
                
                // 找出实际要删除的文件（确保文件存在于当前文件列表中）
                for (String fileUrl : filesToDelete) {
                    // 使用改进的URL比较方法
                    for (String currentUrl : currentFileUrls) {
                        if (urlsEqual(currentUrl, fileUrl)) {
                            actualFilesToDelete.add(currentUrl); // 使用数据库中的URL进行删除
                            removeUrlFromList(filesToKeep, currentUrl);
                            filesChanged = true;
                            break;
                        }
                    }
                }
                
                log.info("实际要删除的文件: {}", actualFilesToDelete);
                log.info("保留的文件: {}", filesToKeep);
                
                // 删除文件
                if (!actualFilesToDelete.isEmpty()) {
                    int deletedCount = fileUtils.deleteFiles(actualFilesToDelete);
                    log.info("删除了{}个文件", deletedCount);
                }
            }
            
            // 收集非空文件
            List<MultipartFile> validFiles = new ArrayList<>();
            if (file1 != null && !file1.isEmpty()) validFiles.add(file1);
            if (file2 != null && !file2.isEmpty()) validFiles.add(file2);
            if (file3 != null && !file3.isEmpty()) validFiles.add(file3);
            if (file4 != null && !file4.isEmpty()) validFiles.add(file4);
            if (file5 != null && !file5.isEmpty()) validFiles.add(file5);
            
            // 上传新文件
            List<String> newFileUrls = new ArrayList<>(filesToKeep);
            
            if (!validFiles.isEmpty()) {
                // 确保总文件数不超过5个
                int remainingSlots = 5 - filesToKeep.size();
                if (remainingSlots > 0) {
                    int fileCount = Math.min(validFiles.size(), remainingSlots);
                    
                    // 只使用允许的数量的文件
                    List<MultipartFile> filesToUpload = validFiles.subList(0, fileCount);
                    
                    // 创建目录路径：static/experiments/experimentId/submit/studentId/uuid
                    String uuid = UUID.randomUUID().toString();
                    String directory = submit.getExperimentId() + "/submit/" + submit.getStudentId() + "/" + uuid;
                    
                    try {
                        // 使用文件工具上传文件
                        MultipartFile[] fileArray = filesToUpload.toArray(new MultipartFile[0]);
                        List<String> uploadedFileUrls = fileUtils.uploadFiles(fileArray, directory);
                        
                        // 添加新上传的文件URL
                        newFileUrls.addAll(uploadedFileUrls);
                        filesChanged = true;
                    } catch (Exception e) {
                        log.error("上传文件失败", e);
                        throw new RuntimeException("上传文件失败: " + e.getMessage());
                    }
                } else {
                    log.warn("文件数量已达上限(5个)，无法上传新文件");
                }
            }
            
            // 更新文件URL
            // 先清空所有文件URL字段
            submit.setFile1URL(null);
            submit.setFile2URL(null);
            submit.setFile3URL(null);
            submit.setFile4URL(null);
            submit.setFile5URL(null);
            
            // 设置新的文件URL
            for (int i = 0; i < newFileUrls.size() && i < 5; i++) {
                switch (i) {
                    case 0: submit.setFile1URL(newFileUrls.get(0)); break;
                    case 1: submit.setFile2URL(newFileUrls.get(1)); break;
                    case 2: submit.setFile3URL(newFileUrls.get(2)); break;
                    case 3: submit.setFile4URL(newFileUrls.get(3)); break;
                    case 4: submit.setFile5URL(newFileUrls.get(4)); break;
                }
            }
            
            // 如果没有文件变更但数据库中的numFile与实际文件数不一致，进行修正
            if (!filesChanged && submit.getNumFile() != currentFileUrls.size()) {
                submit.setNumFile(currentFileUrls.size());
                log.info("修正文件数量为: {}", currentFileUrls.size());
            } else if (filesChanged) {
                // 更新文件数量
                submit.setNumFile(newFileUrls.size());
                log.info("更新文件数量为: {}", newFileUrls.size());
            }
            
            // 确保numFile字段与实际文件数量一致
            int actualFileCount = 0;
            if (submit.getFile1URL() != null) actualFileCount++;
            if (submit.getFile2URL() != null) actualFileCount++;
            if (submit.getFile3URL() != null) actualFileCount++;
            if (submit.getFile4URL() != null) actualFileCount++;
            if (submit.getFile5URL() != null) actualFileCount++;
            
            if (submit.getNumFile() != actualFileCount) {
                log.warn("文件数量不一致，设置的numFile为: {}, 实际文件数为: {}, 进行修正", 
                        submit.getNumFile(), actualFileCount);
                submit.setNumFile(actualFileCount);
            }
            
            // 更新提交
            int updatedRows = experimentSubmitMapper.updateById(submit);
            log.info("更新提交记录: {} 行受影响, 最终文件数量: {}", updatedRows, submit.getNumFile());
            
            // 返回更新后的提交详情
            return getStudentSubmit(submit.getExperimentId(), submit.getStudentId());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新提交失败", e);
            throw new RuntimeException("更新提交失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean gradeSubmit(Integer id, Integer score) {
        try {
            // 验证分数范围
            if (score < 0 || score > 100) {
                throw new RuntimeException("分数必须在0-100之间");
            }
            
            // 获取提交记录
            ExperimentSubmit submit = experimentSubmitMapper.selectById(id);
            if (submit == null) {
                throw new RuntimeException("提交记录不存在");
            }
            
            // 设置分数和更新时间
            submit.setScore(score);
            submit.setUpdatedAt(LocalDateTime.now());
            
            // 更新提交
            return experimentSubmitMapper.updateById(submit) > 0;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("评分失败", e);
            throw new RuntimeException("评分失败: " + e.getMessage());
        }
    }
    
    @Override
    public int countByExperimentId(Integer experimentId) {
        LambdaQueryWrapper<ExperimentSubmit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExperimentSubmit::getExperimentId, experimentId);
        return Math.toIntExact(count(wrapper));
    }
    
    @Override
    public boolean checkStudentSubmitted(Integer experimentId, Integer studentId) {
        LambdaQueryWrapper<ExperimentSubmit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExperimentSubmit::getExperimentId, experimentId)
                .eq(ExperimentSubmit::getStudentId, studentId);
        return experimentSubmitMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public Integer getStudentScore(Integer experimentId, Integer studentId) {
        try {
            LambdaQueryWrapper<ExperimentSubmit> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExperimentSubmit::getExperimentId, experimentId)
                    .eq(ExperimentSubmit::getStudentId, studentId);
            
            ExperimentSubmit submit = experimentSubmitMapper.selectOne(wrapper);
            return submit != null ? submit.getScore() : null;
        } catch (Exception e) {
            log.error("获取学生实验得分失败", e);
            return null;
        }
    }
    
    /**
     * 将提交实体转换为VO
     */
    private ExperimentSubmitVO convertToVO(ExperimentSubmit submit) {
        if (submit == null) {
            return null;
        }
        
        ExperimentSubmitVO vo = new ExperimentSubmitVO();
        BeanUtils.copyProperties(submit, vo);
        
        // 处理文件URL列表
        List<String> fileUrls = new ArrayList<>();
        if (submit.getNumFile() != null && submit.getNumFile() > 0) {
            if (submit.getFile1URL() != null) fileUrls.add(submit.getFile1URL());
            if (submit.getFile2URL() != null) fileUrls.add(submit.getFile2URL());
            if (submit.getFile3URL() != null) fileUrls.add(submit.getFile3URL());
            if (submit.getFile4URL() != null) fileUrls.add(submit.getFile4URL());
            if (submit.getFile5URL() != null) fileUrls.add(submit.getFile5URL());
        }
        vo.setFileUrls(fileUrls);
        
        return vo;
    }

    /**
     * 判断两个URL是否相等，忽略URL中的域名部分
     */
    private boolean urlsEqual(String url1, String url2) {
        // 移除域名部分，只保留路径和参数
        String baseUrl1 = url1.replaceFirst("http://[^/]+/", "");
        String baseUrl2 = url2.replaceFirst("http://[^/]+/", "");
        return baseUrl1.equals(baseUrl2);
    }

    /**
     * 从列表中移除URL
     */
    private void removeUrlFromList(List<String> list, String url) {
        list.removeIf(item -> urlsEqual(item, url));
    }
} 