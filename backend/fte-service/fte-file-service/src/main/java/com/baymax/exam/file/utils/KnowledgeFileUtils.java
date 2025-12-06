package com.baymax.exam.file.utils;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 知识库文件工具类，用于处理知识库相关的文件上传和删除
 */
@Slf4j
@Component
public class KnowledgeFileUtils {
    
    @Value("${spring.file-storage.local[0].base-path:G:/aaa_study_platform/Temp/}")
    private String baseStoragePath;
    
    @Value("${spring.file-storage.local[0].domain:http://192.168.78.91:10030/}")
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
     * 上传知识库文件
     *
     * @param file 文件
     * @param userId 用户ID
     * @return 文件URL
     * @throws IOException 文件处理异常
     */
    public String uploadKnowledgeFile(MultipartFile file, Integer userId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("文件为空");
        }
        
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        
        // 创建基础目录
        String basePath = "static/knowledge/" + userId;
        Path baseDir = Paths.get(baseStoragePath, basePath);
        Files.createDirectories(baseDir);
        
        // 为文件创建一个唯一的子文件夹
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
        log.info("知识库文件已上传: {}", filePath);
        
        return fileUrl;
    }
    
    /**
     * 删除知识库文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean deleteKnowledgeFile(String fileUrl) {
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
                log.info("成功删除知识库文件: {}", filePath);
                
                // 删除文件后，检查并清理空目录
                deleteEmptyParentDirectories(filePath);
                
                return true;
            } else {
                log.warn("知识库文件不存在: {}", filePath);
                return false;
            }
        } catch (Exception e) {
            log.error("删除知识库文件失败: {}", fileUrl, e);
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
            Path knowledgePath = Paths.get(baseStoragePath, "static", "knowledge");
            
            // 如果父目录是baseStoragePath或static目录或knowledge目录，则停止递归
            if (parent == null || parent.equals(basePath) || parent.equals(staticPath) || 
                    parent.equals(knowledgePath) || 
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
     * 比较两个URL是否实质上相等，考虑URL编码差异
     * 
     * @param url1 第一个URL
     * @param url2 第二个URL
     * @return 是否相等
     */
    public boolean urlsEqual(String url1, String url2) {
        if (url1 == null || url2 == null) {
            return url1 == url2;
        }
        
        try {
            // 解码URL
            String decodedUrl1 = java.net.URLDecoder.decode(url1, java.nio.charset.StandardCharsets.UTF_8.name());
            String decodedUrl2 = java.net.URLDecoder.decode(url2, java.nio.charset.StandardCharsets.UTF_8.name());
            
            // 标准化处理：将+替换为空格，移除前后空白
            decodedUrl1 = decodedUrl1.replace("+", " ").trim();
            decodedUrl2 = decodedUrl2.replace("+", " ").trim();
            
            return decodedUrl1.equals(decodedUrl2);
        } catch (java.io.UnsupportedEncodingException e) {
            // 如果解码失败，退回到直接比较
            log.warn("URL解码失败，退回到直接比较: {}", e.getMessage());
            return url1.equals(url2);
        }
    }
} 