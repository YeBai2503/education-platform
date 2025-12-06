package com.baymax.exam.experiment.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件工具类，用于处理实验相关的文件上传和删除
 */
@Slf4j
@Component
public class FileUtils {
    
    @Value("${spring.file-storage.local[0].base-path:G:/aaa_study_platform/Temp/}")
    private String baseStoragePath;
    
    @Value("${experiment.domain-url:http://192.168.78.91:10030/}")
    private String domainUrl;
    
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    /**
     * 生成唯一文件夹名
     * 
     * @return 基于时间戳和随机字符的唯一文件夹名
     */
    private String generateUniqueFolderName() {
        // 使用时间戳作为前缀
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        
        // 生成6位随机字符
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        
        return timestamp + "_" + sb.toString();
    }
    
    /**
     * 上传文件
     *
     * @param files 文件数组
     * @param directory 存储目录，相对于baseStoragePath
     * @return 文件URL列表
     */
    public List<String> uploadFiles(MultipartFile[] files, String directory) throws IOException {
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }
        
        // 限制文件数量不超过5个
        int fileCount = Math.min(files.length, 5);
        List<String> fileUrls = new ArrayList<>(fileCount);
        
        // 创建基础目录
        String basePath = "static/experiments/" + directory;
        Path baseDir = Paths.get(baseStoragePath, basePath);
        Files.createDirectories(baseDir);
        
        // 上传文件
        for (int i = 0; i < fileCount; i++) {
            MultipartFile file = files[i];
            if (file != null && !file.isEmpty()) {
                // 获取原始文件名
                String originalFilename = file.getOriginalFilename();
                
                // 为每个文件创建一个唯一的子文件夹
                String uniqueFolderName = generateUniqueFolderName();
                Path fileDir = baseDir.resolve(uniqueFolderName);
                Files.createDirectories(fileDir);
                
                // 在子文件夹中使用原始文件名保存文件
                Path filePath = fileDir.resolve(originalFilename);
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                
                // 生成URL
                String fileUrl = domainUrl + basePath + "/" + uniqueFolderName + "/" + originalFilename;
                fileUrls.add(fileUrl);
                
                log.info("文件已上传: {}", filePath);
            }
        }
        
        return fileUrls;
    }
    
    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        try {
            // 从URL中提取相对路径
            String relativePath = null;
            if (fileUrl.startsWith(domainUrl)) {
                relativePath = fileUrl.substring(domainUrl.length());
            } else {
                relativePath = fileUrl;
            }
            
            // 构建文件路径
            Path filePath = Paths.get(baseStoragePath, relativePath);
            
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                // 删除文件
                Files.delete(filePath);
                log.info("成功删除文件: {}", filePath);
                
                // 删除文件后，检查并清理空目录
                deleteEmptyParentDirectories(filePath);
                
                return true;
            } else {
                log.warn("文件不存在: {}", filePath);
                return false;
            }
        } catch (Exception e) {
            log.error("删除文件失败: {}", fileUrl, e);
            return false;
        }
    }
    
    /**
     * 删除多个文件
     *
     * @param fileUrls 文件URL列表
     * @return 成功删除的文件数量
     */
    public int deleteFiles(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return 0;
        }
        
        // 去重，避免删除同一个文件多次
        Set<String> uniqueUrls = new HashSet<>(fileUrls);
        log.info("删除文件，原始URL数量: {}, 去重后URL数量: {}", fileUrls.size(), uniqueUrls.size());
        
        int count = 0;
        for (String fileUrl : uniqueUrls) {
            if (deleteFile(fileUrl)) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * 删除目录及其所有内容
     *
     * @param dirUrl 目录URL
     * @return 是否删除成功
     */
    public boolean deleteDirectory(String dirUrl) {
        if (dirUrl == null || dirUrl.isEmpty()) {
            return false;
        }
        
        try {
            // 从URL中提取相对路径
            String relativePath = null;
            if (dirUrl.startsWith(domainUrl)) {
                relativePath = dirUrl.substring(domainUrl.length());
            } else {
                relativePath = dirUrl;
            }
            
            // 构建目录路径
            Path dirPath = Paths.get(baseStoragePath, relativePath);
            
            if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
                // 删除目录及其所有内容
                Files.walk(dirPath)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                
                log.info("成功删除目录: {}", dirPath);
                
                // 删除目录后，检查并清理空的父目录
                deleteEmptyParentDirectories(dirPath);
                
                return true;
            } else {
                log.warn("目录不存在: {}", dirPath);
                return false;
            }
        } catch (Exception e) {
            log.error("删除目录失败: {}", dirUrl, e);
            return false;
        }
    }
    
    /**
     * 递归删除空的父目录
     *
     * @param path 起始路径
     */
    private void deleteEmptyParentDirectories(Path path) {
        try {
            Path parent = path.getParent();
            
            // 检查是否已经到达baseStoragePath的根目录或static目录，防止删除过多
            Path basePath = Paths.get(baseStoragePath);
            Path staticPath = Paths.get(baseStoragePath, "static");
            Path experimentsPath = Paths.get(baseStoragePath, "static", "experiments");
            
            // 如果父目录是baseStoragePath或static目录或experiments目录，则停止递归
            if (parent == null || parent.equals(basePath) || parent.equals(staticPath) || 
                    parent.equals(experimentsPath) || 
                    (parent.startsWith(basePath) && parent.getNameCount() <= basePath.getNameCount() + 2)) {
                return;
            }
            
            // 检查目录是否存在且为空
            if (Files.exists(parent) && Files.isDirectory(parent)) {
                try (java.util.stream.Stream<Path> entries = Files.list(parent)) {
                    // 如果目录为空，则删除
                    if (!entries.findFirst().isPresent()) {
                        Files.delete(parent);
                        log.info("删除空目录: {}", parent);
                        
                        // 递归检查上一级目录
                        deleteEmptyParentDirectories(parent);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("删除空目录失败: {}", path.getParent(), e);
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".bin";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return ".bin";
    }
} 