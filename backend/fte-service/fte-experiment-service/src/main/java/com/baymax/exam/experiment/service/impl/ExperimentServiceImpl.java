package com.baymax.exam.experiment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.experiment.mapper.ExperimentMapper;
import com.baymax.exam.experiment.mapper.ExperimentSubmitMapper;
import com.baymax.exam.experiment.model.Experiment;
import com.baymax.exam.experiment.service.IExperimentService;
import com.baymax.exam.experiment.utils.FileUtils;
import com.baymax.exam.experiment.vo.ExperimentVO;
import com.baymax.exam.file.feign.FileDetailClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

/**
 * 实验Service实现类
 */
@Slf4j
@Service
public class ExperimentServiceImpl extends ServiceImpl<ExperimentMapper, Experiment> implements IExperimentService {
    
    @Autowired
    private ExperimentMapper experimentMapper;
    
    @Autowired
    private ExperimentSubmitMapper experimentSubmitMapper;
    
    @Autowired
    private FileUtils fileUtils;
    
    @Autowired
    private FileDetailClient fileDetailClient;
    
    @Value("${experiment.domain-url:http://192.168.78.91:10030/}")
    private String domainUrl;
    
    @Override
    public IPage<ExperimentVO> pageExperimentsByCourseId(Page<Experiment> page, Integer courseId, Integer type) {
        // 查询实验列表
        LambdaQueryWrapper<Experiment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Experiment::getCourseId, courseId);
        
        // 根据类型筛选
        if (type != null) {
            wrapper.eq(Experiment::getType, type);
        }
        
        wrapper.orderByDesc(Experiment::getCreatedAt);
        
        IPage<Experiment> experimentPage = experimentMapper.selectPage(page, wrapper);
        
        // 转换为VO
        IPage<ExperimentVO> voPage = experimentPage.convert(experiment -> {
            ExperimentVO vo = convertToVO(experiment);
            // 统计提交数量
            vo.setSubmittedCount(experimentSubmitMapper.countByExperimentId(experiment.getId()));
            return vo;
        });
        
        return voPage;
    }
    
    @Override
    public ExperimentVO getExperimentDetail(Integer id) {
        Experiment experiment = experimentMapper.selectById(id);
        if (experiment == null) {
            return null;
        }
        
        ExperimentVO vo = convertToVO(experiment);
        // 统计提交数量
        vo.setSubmittedCount(experimentSubmitMapper.countByExperimentId(id));
        
        return vo;
    }
    
    /**
     * 创建实验
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExperimentVO createExperiment(ExperimentVO experimentVO, 
                                    MultipartFile file1, 
                                    MultipartFile file2, 
                                    MultipartFile file3, 
                                    MultipartFile file4, 
                                    MultipartFile file5) {
        try {
            // 创建实验记录
            Experiment experiment = new Experiment();
            BeanUtils.copyProperties(experimentVO, experiment);
            
            // 设置时间
            LocalDateTime now = LocalDateTime.now();
            experiment.setCreatedAt(now);
            experiment.setUpdatedAt(now);
            
            // 如果类型为空，默认设置为0（实验）
            if (experiment.getType() == null) {
                experiment.setType(0);
            }
        
        // 将单个文件合并为列表
        List<MultipartFile> fileList = new ArrayList<>();
        if (file1 != null && !file1.isEmpty()) fileList.add(file1);
        if (file2 != null && !file2.isEmpty()) fileList.add(file2);
        if (file3 != null && !file3.isEmpty()) fileList.add(file3);
        if (file4 != null && !file4.isEmpty()) fileList.add(file4);
        if (file5 != null && !file5.isEmpty()) fileList.add(file5);
            
            // 上传文件
        if (!fileList.isEmpty()) {
                // 创建目录路径：static/experiments/courseId/publish/uuid
                String uuid = UUID.randomUUID().toString();
            String directory = experiment.getCourseId() + "/publish/" + uuid;
                
            try {
                // 使用文件工具上传文件
                List<String> fileUrls = fileUtils.uploadFiles(fileList.toArray(new MultipartFile[0]), directory);
                
                // 设置文件数量和URL
                experiment.setNumFile(fileUrls.size());
                
                // 设置文件URL
                for (int i = 0; i < fileUrls.size(); i++) {
                    switch (i) {
                        case 0: experiment.setFile1URL(fileUrls.get(0)); break;
                        case 1: experiment.setFile2URL(fileUrls.get(1)); break;
                        case 2: experiment.setFile3URL(fileUrls.get(2)); break;
                        case 3: experiment.setFile4URL(fileUrls.get(3)); break;
                        case 4: experiment.setFile5URL(fileUrls.get(4)); break;
                    }
                }
            } catch (Exception e) {
                log.error("上传文件失败", e);
                throw new RuntimeException("上传文件失败: " + e.getMessage());
                }
            } else {
                experiment.setNumFile(0);
            }
            
            // 保存实验
            experimentMapper.insert(experiment);
            
            // 返回VO
            return getExperimentDetail(experiment.getId());
    } catch (RuntimeException e) {
        throw e;
        } catch (Exception e) {
            log.error("创建实验失败", e);
            throw new RuntimeException("创建实验失败: " + e.getMessage());
        }
    }
    
    @Override
@Transactional(rollbackFor = Exception.class)
public ExperimentVO updateExperiment(Integer id, ExperimentVO experimentVO, 
                                    MultipartFile file1,
                                    MultipartFile file2,
                                    MultipartFile file3,
                                    MultipartFile file4,
                                    MultipartFile file5) {
        try {
            // 获取原实验记录
            Experiment experiment = experimentMapper.selectById(id);
            if (experiment == null) {
                throw new RuntimeException("实验不存在");
            }
            
            // 保存旧的文件URL，用于后续删除
            List<String> oldFileUrls = new ArrayList<>();
            if (experiment.getNumFile() != null && experiment.getNumFile() > 0) {
                if (experiment.getFile1URL() != null) oldFileUrls.add(experiment.getFile1URL());
                if (experiment.getFile2URL() != null) oldFileUrls.add(experiment.getFile2URL());
                if (experiment.getFile3URL() != null) oldFileUrls.add(experiment.getFile3URL());
                if (experiment.getFile4URL() != null) oldFileUrls.add(experiment.getFile4URL());
                if (experiment.getFile5URL() != null) oldFileUrls.add(experiment.getFile5URL());
            }
            
            // 更新基本信息
        experiment.setTitle(experimentVO.getTitle() != null ? experimentVO.getTitle() : experiment.getTitle());
        experiment.setDetail(experimentVO.getDetail() != null ? experimentVO.getDetail() : experiment.getDetail());
        experiment.setDdl(experimentVO.getDdl() != null ? experimentVO.getDdl() : experiment.getDdl());
            experiment.setUpdatedAt(LocalDateTime.now());
        
        boolean filesUpdated = false; // 标记是否更新了文件
        
        // 判断是否需要清空所有文件
        if (experimentVO.getFileUrls() == null) {
            // 清空所有文件URL
            experiment.setNumFile(0);
            experiment.setFile1URL(null);
            experiment.setFile2URL(null);
            experiment.setFile3URL(null);
            experiment.setFile4URL(null);
            experiment.setFile5URL(null);
            filesUpdated = true;
        }
        
        // 将单个文件合并为列表
        List<MultipartFile> fileList = new ArrayList<>();
        if (file1 != null && !file1.isEmpty()) fileList.add(file1);
        if (file2 != null && !file2.isEmpty()) fileList.add(file2);
        if (file3 != null && !file3.isEmpty()) fileList.add(file3);
        if (file4 != null && !file4.isEmpty()) fileList.add(file4);
        if (file5 != null && !file5.isEmpty()) fileList.add(file5);
            
            // 如果有新文件上传
        if (!fileList.isEmpty()) {
                // 创建目录路径：static/experiments/courseId/publish/uuid
                String uuid = UUID.randomUUID().toString();
            String directory = experiment.getCourseId() + "/publish/" + uuid;
                
            try {
                // 使用文件工具上传文件
                List<String> fileUrls = fileUtils.uploadFiles(fileList.toArray(new MultipartFile[0]), directory);
                
                // 设置文件数量和URL
                experiment.setNumFile(fileUrls.size());
                
                // 重置所有文件URL
                experiment.setFile1URL(null);
                experiment.setFile2URL(null);
                experiment.setFile3URL(null);
                experiment.setFile4URL(null);
                experiment.setFile5URL(null);
                
                // 设置新的文件URL
                for (int i = 0; i < fileUrls.size(); i++) {
                    switch (i) {
                        case 0: experiment.setFile1URL(fileUrls.get(0)); break;
                        case 1: experiment.setFile2URL(fileUrls.get(1)); break;
                        case 2: experiment.setFile3URL(fileUrls.get(2)); break;
                        case 3: experiment.setFile4URL(fileUrls.get(3)); break;
                        case 4: experiment.setFile5URL(fileUrls.get(4)); break;
                    }
                }
                
                filesUpdated = true;
            } catch (Exception e) {
                log.error("上传文件失败", e);
                throw new RuntimeException("上传文件失败: " + e.getMessage());
            }
        }
        
        // 更新实验
        experimentMapper.updateById(experiment);
        
        // 如果更新了文件，删除旧文件
        if (filesUpdated && !oldFileUrls.isEmpty()) {
            int deletedCount = fileUtils.deleteFiles(oldFileUrls);
            log.info("删除了{}个旧文件", deletedCount);
        }
        
        // 返回更新后的实验详情
        return getExperimentDetail(id);
    } catch (RuntimeException e) {
        throw e;
    } catch (Exception e) {
        log.error("更新实验失败", e);
        throw new RuntimeException("更新实验失败: " + e.getMessage());
    }
}

/**
 * 比较两个URL是否实质上相等，考虑URL编码差异
 * 
 * @param url1 第一个URL
 * @param url2 第二个URL
 * @return 是否相等
 */
private boolean urlsEqual(String url1, String url2) {
    if (url1 == null || url2 == null) {
        return url1 == url2;
    }
    
    try {
        // 解码URL
        String decodedUrl1 = URLDecoder.decode(url1, StandardCharsets.UTF_8.name());
        String decodedUrl2 = URLDecoder.decode(url2, StandardCharsets.UTF_8.name());
        
        // 标准化处理：将+替换为空格，移除前后空白
        decodedUrl1 = decodedUrl1.replace("+", " ").trim();
        decodedUrl2 = decodedUrl2.replace("+", " ").trim();
        
        return decodedUrl1.equals(decodedUrl2);
    } catch (UnsupportedEncodingException e) {
        // 如果解码失败，退回到直接比较
        log.warn("URL解码失败，退回到直接比较: {}", e.getMessage());
        return url1.equals(url2);
    }
}

/**
 * 检查URL列表中是否包含指定URL，考虑URL编码差异
 * 
 * @param urlList URL列表
 * @param urlToFind 要查找的URL
 * @return 是否包含
 */
private boolean urlListContains(List<String> urlList, String urlToFind) {
    if (urlList == null || urlToFind == null) {
        return false;
    }
    
    for (String url : urlList) {
        if (urlsEqual(url, urlToFind)) {
            return true;
        }
    }
    
    return false;
}

/**
 * 从URL列表中移除指定URL，考虑URL编码差异
 * 
 * @param urlList URL列表
 * @param urlToRemove 要移除的URL
 * @return 是否成功移除
 */
private boolean removeUrlFromList(List<String> urlList, String urlToRemove) {
    if (urlList == null || urlToRemove == null) {
        return false;
    }
    
    for (int i = 0; i < urlList.size(); i++) {
        if (urlsEqual(urlList.get(i), urlToRemove)) {
            urlList.remove(i);
            return true;
        }
    }
    
    return false;
}

@Override
@Transactional(rollbackFor = Exception.class)
public ExperimentVO updateExperimentWithFileDelete(Integer id, ExperimentVO experimentVO, List<String> filesToDelete, 
                                                 MultipartFile file1,
                                                 MultipartFile file2,
                                                 MultipartFile file3,
                                                 MultipartFile file4,
                                                 MultipartFile file5) {
    try {
        // 获取原实验记录
        Experiment experiment = experimentMapper.selectById(id);
        if (experiment == null) {
            throw new RuntimeException("实验不存在");
        }
        
        // 获取当前文件URL列表，确保没有重复
        Set<String> currentFileUrlSet = new HashSet<>();
        List<String> currentFileUrls = new ArrayList<>();
        if (experiment.getNumFile() != null && experiment.getNumFile() > 0) {
            if (experiment.getFile1URL() != null && currentFileUrlSet.add(experiment.getFile1URL())) 
                currentFileUrls.add(experiment.getFile1URL());
            if (experiment.getFile2URL() != null && currentFileUrlSet.add(experiment.getFile2URL())) 
                currentFileUrls.add(experiment.getFile2URL());
            if (experiment.getFile3URL() != null && currentFileUrlSet.add(experiment.getFile3URL())) 
                currentFileUrls.add(experiment.getFile3URL());
            if (experiment.getFile4URL() != null && currentFileUrlSet.add(experiment.getFile4URL())) 
                currentFileUrls.add(experiment.getFile4URL());
            if (experiment.getFile5URL() != null && currentFileUrlSet.add(experiment.getFile5URL())) 
                currentFileUrls.add(experiment.getFile5URL());
                }
        
        // 检查是否有重复文件URL
        if (currentFileUrls.size() != currentFileUrlSet.size()) {
            log.warn("检测到数据库中有重复的文件URL，已去重");
        }
        
        // 如果数据库中的numFile与实际文件数不一致，记录日志
        if (experiment.getNumFile() != null && experiment.getNumFile() != currentFileUrls.size()) {
            log.warn("数据库中的文件数量({})与实际文件数量({})不一致，将进行修正", 
                    experiment.getNumFile(), currentFileUrls.size());
            }
            
        // 更新基本信息
        experiment.setTitle(experimentVO.getTitle() != null ? experimentVO.getTitle() : experiment.getTitle());
        experiment.setDetail(experimentVO.getDetail() != null ? experimentVO.getDetail() : experiment.getDetail());
        experiment.setDdl(experimentVO.getDdl() != null ? experimentVO.getDdl() : experiment.getDdl());
        experiment.setUpdatedAt(LocalDateTime.now());
        
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
                
                // 清空所有文件URL字段
                experiment.setFile1URL(null);
                experiment.setFile2URL(null);
                experiment.setFile3URL(null);
                experiment.setFile4URL(null);
                experiment.setFile5URL(null);
                
                // 设置保留的文件URL
                for (int i = 0; i < filesToKeep.size() && i < 5; i++) {
                    switch (i) {
                        case 0: experiment.setFile1URL(filesToKeep.get(0)); break;
                        case 1: experiment.setFile2URL(filesToKeep.get(1)); break;
                        case 2: experiment.setFile3URL(filesToKeep.get(2)); break;
                        case 3: experiment.setFile4URL(filesToKeep.get(3)); break;
                        case 4: experiment.setFile5URL(filesToKeep.get(4)); break;
                    }
                }
                
                // 更新文件数量
                experiment.setNumFile(filesToKeep.size());
                log.info("更新文件数量为: {}", filesToKeep.size());
            }
        }
        
        // 将单个文件合并为列表
        List<MultipartFile> fileList = new ArrayList<>();
        if (file1 != null && !file1.isEmpty()) fileList.add(file1);
        if (file2 != null && !file2.isEmpty()) fileList.add(file2);
        if (file3 != null && !file3.isEmpty()) fileList.add(file3);
        if (file4 != null && !file4.isEmpty()) fileList.add(file4);
        if (file5 != null && !file5.isEmpty()) fileList.add(file5);
        
        // 如果有新文件上传
        if (!fileList.isEmpty()) {
            // 创建目录路径：static/experiments/courseId/publish/uuid
            String uuid = UUID.randomUUID().toString();
            String directory = experiment.getCourseId() + "/publish/" + uuid;
            
            try {
                // 使用文件工具上传文件
                List<String> newFileUrls = fileUtils.uploadFiles(fileList.toArray(new MultipartFile[0]), directory);
                
                // 计算当前已有文件数量
                int currentFileCount = experiment.getNumFile() != null ? experiment.getNumFile() : 0;
                
                // 检查是否超过5个文件的限制
                if (currentFileCount + newFileUrls.size() > 5) {
                    throw new RuntimeException("文件总数不能超过5个");
                }
                
                // 添加新文件URL
                for (int i = 0; i < newFileUrls.size(); i++) {
                    switch (currentFileCount + i) {
                        case 0: experiment.setFile1URL(newFileUrls.get(i)); break;
                        case 1: experiment.setFile2URL(newFileUrls.get(i)); break;
                        case 2: experiment.setFile3URL(newFileUrls.get(i)); break;
                        case 3: experiment.setFile4URL(newFileUrls.get(i)); break;
                        case 4: experiment.setFile5URL(newFileUrls.get(i)); break;
                    }
                }
                
                // 更新文件数量
                experiment.setNumFile(currentFileCount + newFileUrls.size());
                log.info("更新文件数量为: {}", currentFileCount + newFileUrls.size());
                filesChanged = true;
            } catch (Exception e) {
                log.error("上传文件失败", e);
                throw new RuntimeException("上传文件失败: " + e.getMessage());
            }
        }
        
        // 如果没有文件变更但数据库中的numFile与实际文件数不一致，进行修正
        if (!filesChanged && experiment.getNumFile() != currentFileUrls.size()) {
            experiment.setNumFile(currentFileUrls.size());
            log.info("修正文件数量为: {}", currentFileUrls.size());
        }
        
        // 确保numFile字段与实际文件数量一致
        int actualFileCount = 0;
        if (experiment.getFile1URL() != null) actualFileCount++;
        if (experiment.getFile2URL() != null) actualFileCount++;
        if (experiment.getFile3URL() != null) actualFileCount++;
        if (experiment.getFile4URL() != null) actualFileCount++;
        if (experiment.getFile5URL() != null) actualFileCount++;
        
        if (experiment.getNumFile() != actualFileCount) {
            log.warn("文件数量不一致，设置的numFile为: {}, 实际文件数为: {}, 进行修正", 
                    experiment.getNumFile(), actualFileCount);
            experiment.setNumFile(actualFileCount);
        }
        
        // 更新实验
        int updatedRows = experimentMapper.updateById(experiment);
        log.info("更新实验记录: {} 行受影响, 最终文件数量: {}", updatedRows, experiment.getNumFile());
        
        // 返回更新后的实验详情
        return getExperimentDetail(id);
    } catch (RuntimeException e) {
        throw e;
        } catch (Exception e) {
            log.error("更新实验失败", e);
            throw new RuntimeException("更新实验失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExperiment(Integer id) {
        try {
            // 获取实验信息
            Experiment experiment = experimentMapper.selectById(id);
            if (experiment == null) {
                throw new RuntimeException("实验不存在");
            }
            
            // 获取文件URL列表
            List<String> fileUrls = new ArrayList<>();
            if (experiment.getNumFile() != null && experiment.getNumFile() > 0) {
                if (experiment.getFile1URL() != null) fileUrls.add(experiment.getFile1URL());
                if (experiment.getFile2URL() != null) fileUrls.add(experiment.getFile2URL());
                if (experiment.getFile3URL() != null) fileUrls.add(experiment.getFile3URL());
                if (experiment.getFile4URL() != null) fileUrls.add(experiment.getFile4URL());
                if (experiment.getFile5URL() != null) fileUrls.add(experiment.getFile5URL());
            }
            
            // 删除实验记录
            experimentMapper.deleteById(id);
            
            // 删除相关文件
            if (!fileUrls.isEmpty()) {
                int deletedCount = fileUtils.deleteFiles(fileUrls);
                log.info("删除了{}个文件", deletedCount);
            }
            
            // 检查是否需要删除目录
            try {
                // 构建目录路径前缀
                String dirPrefix = "static/experiments/" + experiment.getCourseId() + "/publish";
                // 尝试删除可能的空目录
                if (fileUrls.size() > 0) {
                    String firstUrl = fileUrls.get(0);
                    String dirPath = firstUrl.substring(0, firstUrl.lastIndexOf("/"));
                    fileUtils.deleteDirectory(dirPath);
                }
            } catch (Exception e) {
                log.warn("删除目录失败", e);
            }
            
            return true;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除实验失败", e);
            throw new RuntimeException("删除实验失败: " + e.getMessage());
        }
    }
    
    @Override
    public int countByCourseId(Integer courseId, Integer type) {
        LambdaQueryWrapper<Experiment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Experiment::getCourseId, courseId);
        
        // 根据类型筛选
        if (type != null) {
            wrapper.eq(Experiment::getType, type);
        }
        
        return Math.toIntExact(count(wrapper));
    }
    
    private ExperimentVO convertToVO(Experiment experiment) {
        if (experiment == null) {
            return null;
        }
        
        ExperimentVO vo = new ExperimentVO();
        BeanUtils.copyProperties(experiment, vo);
        
        // 处理文件URL列表
        List<String> fileUrls = new ArrayList<>();
        if (experiment.getNumFile() != null && experiment.getNumFile() > 0) {
            if (experiment.getFile1URL() != null) fileUrls.add(experiment.getFile1URL());
            if (experiment.getFile2URL() != null) fileUrls.add(experiment.getFile2URL());
            if (experiment.getFile3URL() != null) fileUrls.add(experiment.getFile3URL());
            if (experiment.getFile4URL() != null) fileUrls.add(experiment.getFile4URL());
            if (experiment.getFile5URL() != null) fileUrls.add(experiment.getFile5URL());
        }
        vo.setFileUrls(fileUrls);
        
        return vo;
    }
} 