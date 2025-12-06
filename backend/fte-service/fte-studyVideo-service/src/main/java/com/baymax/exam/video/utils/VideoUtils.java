package com.baymax.exam.video.utils;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 视频工具类
 */
@Slf4j
@Component
public class VideoUtils {

    @Value("${ffmpeg.ffprobe-path:ffprobe}")
    private String ffprobePath;
    
    @Value("${ffmpeg.ffmpeg-path:ffmpeg}")
    private String ffmpegPath;
    
    @Value("${spring.file-storage.local[0].base-path:G:/aaa_study_platform/Temp/}")
    private String baseStoragePath;
    
    @Value("${video.domain-url:http://192.168.78.91:10030/}")
    private String domainUrl;
    
    @Value("${video.temp-dir:/tmp/videos}")
    private String tempDir;

    /**
     * 获取视频时长（秒）
     *
     * @param videoFile 视频文件
     * @return 视频时长（秒）
     */
    public Integer getVideoDuration(MultipartFile videoFile) {
        if (videoFile == null || videoFile.isEmpty()) {
            return null;
        }

        try {
            // 创建输出目录结构的临时子目录
            String tempDirName = "temp_" + UUID.randomUUID().toString();
            Path tempDir = Paths.get(baseStoragePath, "temp_duration");
            Files.createDirectories(tempDir);
            
            // 创建临时文件
            String tempFileName = UUID.randomUUID().toString() + ".mp4";
            File tempFile = new File(tempDir.toFile(), tempFileName);
            
            try {
                // 保存上传的文件到临时文件
                log.info("保存上传文件到临时文件以获取时长: {}", tempFile.getAbsolutePath());
                videoFile.transferTo(tempFile);
                
                if (!tempFile.exists()) {
                    log.error("临时文件创建失败: {}", tempFile.getAbsolutePath());
                    return null;
                }
                
                // 获取视频时长
                Integer duration = getVideoDuration(tempFile);
                
                return duration;
            } finally {
                // 删除临时文件
                try {
                    if (tempFile.exists() && tempFile.delete()) {
                        log.info("临时文件已删除: {}", tempFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    log.warn("删除临时文件失败: {}", tempFile.getAbsolutePath(), e);
                }
            }
        } catch (Exception e) {
            log.error("获取视频时长失败", e);
            return null;
        }
    }

    /**
     * 获取视频时长（秒）
     *
     * @param videoFile 视频文件
     * @return 视频时长（秒）
     */
    public Integer getVideoDuration(File videoFile) {
        if (videoFile == null || !videoFile.exists()) {
            return null;
        }

        try {
            FFprobe ffprobe = new FFprobe(ffprobePath);
            FFmpegProbeResult probeResult = ffprobe.probe(videoFile.getAbsolutePath());
            
            // 获取视频流
            FFmpegStream stream = probeResult.getStreams().stream()
                    .filter(s -> "video".equals(s.codec_type))
                    .findFirst()
                    .orElse(null);
            
            if (stream != null && stream.duration > 0) {
                return (int) Math.round(stream.duration);
            }
            
            // 如果视频流没有时长信息，尝试从格式中获取
            if (probeResult.getFormat().duration > 0) {
                return (int) Math.round(probeResult.getFormat().duration);
            }
            
            return null;
        } catch (IOException e) {
            log.error("获取视频时长失败", e);
            return null;
        }
    }
    
    /**
     * 将上传的视频文件处理为HLS格式
     *
     * @param videoFile 视频文件
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return HLS主播放列表URL
     * @throws IOException 处理失败时抛出异常
     */
    public String processVideoToHls(MultipartFile videoFile, Integer courseId, Integer chapterId) throws IOException {
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IOException("视频文件为空");
        }
        
        log.info("开始处理视频文件: 原始名称={}, 大小={}字节", videoFile.getOriginalFilename(), videoFile.getSize());
        
        // 创建输出目录结构: static/videos/{courseId}/{chapterId}/{uuid}
        String uuid = UUID.randomUUID().toString();
        String relativePath = String.format("static/videos/%d/%d/%s", courseId, chapterId, uuid);
        Path outputDir = Paths.get(baseStoragePath, relativePath);
        Files.createDirectories(outputDir);
        
        // 创建临时文件
        String tempFileName = UUID.randomUUID().toString() + ".mp4";
        File tempFile = new File(outputDir.toFile(), tempFileName);
        
        try {
            // 直接保存上传的文件到输出目录的临时文件
            log.info("保存上传文件到: {}", tempFile.getAbsolutePath());
            videoFile.transferTo(tempFile);
            
            if (!tempFile.exists()) {
                throw new IOException("临时文件创建失败: " + tempFile.getAbsolutePath());
            }
            
            log.info("临时文件已创建: {}", tempFile.getAbsolutePath());
            
            // 设置HLS主播放列表路径
            String masterPlaylistPath = outputDir.resolve("master.m3u8").toString();
            
            try {
                // 初始化FFmpeg和FFprobe
                FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
                FFprobe ffprobe = new FFprobe(ffprobePath);
                FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
                
                log.info("开始转换视频: 输入={}, 输出={}", tempFile.getAbsolutePath(), masterPlaylistPath);
                
                // 创建FFmpeg命令构建器
                FFmpegBuilder builder = new FFmpegBuilder()
                        .setInput(tempFile.getAbsolutePath())
                        .overrideOutputFiles(true)
                        // 添加视频处理选项
                        .addOutput(masterPlaylistPath)
                        .setFormat("hls")
                        // HLS特定选项
                        .addExtraArgs("-hls_time", "4")                          // 每个片段的时长（秒）
                        .addExtraArgs("-hls_playlist_type", "vod")               // VOD类型播放列表
                        .addExtraArgs("-hls_flags", "independent_segments")      // 独立片段标志
                        .addExtraArgs("-hls_segment_type", "mpegts")             // 片段类型
                        .addExtraArgs("-hls_segment_filename", 
                                outputDir.resolve("segment_%v_%03d.ts").toString()) // 片段文件名格式
                        // 视频处理选项
                        .addExtraArgs("-filter_complex", 
                                "[0:v]split=2[v1][v2]; [v1]copy[v1out]; [v2]scale=w=640:h=360[v2out]")
                        // 设置不同的视频质量
                        .addExtraArgs("-map", "[v1out]", "-c:v:0", "libx264", "-b:v:0", "2M")
                        .addExtraArgs("-map", "[v2out]", "-c:v:1", "libx264", "-b:v:1", "800K")
                        // 设置音频
                        .addExtraArgs("-map", "a:0", "-c:a:0", "aac", "-b:a:0", "128k")
                        .addExtraArgs("-map", "a:0", "-c:a:1", "aac", "-b:a:1", "64k")
                        // 设置变量流映射
                        .addExtraArgs("-var_stream_map", "v:0,a:0 v:1,a:1")
                        .addExtraArgs("-master_pl_name", "master.m3u8")
                        .done();
                
                // 执行FFmpeg命令
                executor.createJob(builder).run();
                
                log.info("视频转换完成: {}", masterPlaylistPath);
                
                // 返回完整URL路径，前端可以直接访问
                return domainUrl + relativePath + "/master.m3u8";
            } catch (Exception e) {
                log.error("转换视频到HLS格式失败", e);
                throw new IOException("转换视频失败: " + e.getMessage(), e);
            }
        } finally {
            // 删除临时文件
            try {
                if (tempFile.exists() && tempFile.delete()) {
                    log.info("临时文件已删除: {}", tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
                log.warn("删除临时文件失败: {}", tempFile.getAbsolutePath(), e);
            }
        }
    }
    
    /**
     * 将MP4视频转换为HLS格式
     * 
     * 此方法已被合并到processVideoToHls中，保留此方法签名以兼容现有代码
     *
     * @param inputVideoPath MP4视频路径
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return HLS主播放列表URL
     * @throws IOException 处理失败时抛出异常
     */
    public String convertToHls(String inputVideoPath, Integer courseId, Integer chapterId) throws IOException {
        // 创建输出目录结构: static/videos/{courseId}/{chapterId}/{uuid}
        String uuid = UUID.randomUUID().toString();
        String relativePath = String.format("static/videos/%d/%d/%s", courseId, chapterId, uuid);
        Path outputDir = Paths.get(baseStoragePath, relativePath);
        Files.createDirectories(outputDir);
        
        // 设置HLS主播放列表路径
        String masterPlaylistPath = outputDir.resolve("master.m3u8").toString();
        
        try {
            // 初始化FFmpeg和FFprobe
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFprobe ffprobe = new FFprobe(ffprobePath);
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            
            // 创建FFmpeg命令构建器
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(inputVideoPath)
                    .overrideOutputFiles(true)
                    // 添加视频处理选项
                    .addOutput(masterPlaylistPath)
                    .setFormat("hls")
                    // HLS特定选项
                    .addExtraArgs("-hls_time", "4")                          // 每个片段的时长（秒）
                    .addExtraArgs("-hls_playlist_type", "vod")               // VOD类型播放列表
                    .addExtraArgs("-hls_flags", "independent_segments")      // 独立片段标志
                    .addExtraArgs("-hls_segment_type", "mpegts")             // 片段类型
                    .addExtraArgs("-hls_segment_filename", 
                            outputDir.resolve("segment_%v_%03d.ts").toString()) // 片段文件名格式
                    // 视频处理选项
                    .addExtraArgs("-filter_complex", 
                            "[0:v]split=2[v1][v2]; [v1]copy[v1out]; [v2]scale=w=640:h=360[v2out]")
                    // 设置不同的视频质量
                    .addExtraArgs("-map", "[v1out]", "-c:v:0", "libx264", "-b:v:0", "2M")
                    .addExtraArgs("-map", "[v2out]", "-c:v:1", "libx264", "-b:v:1", "800K")
                    // 设置音频
                    .addExtraArgs("-map", "a:0", "-c:a:0", "aac", "-b:a:0", "128k")
                    .addExtraArgs("-map", "a:0", "-c:a:1", "aac", "-b:a:1", "64k")
                    // 设置变量流映射
                    .addExtraArgs("-var_stream_map", "v:0,a:0 v:1,a:1")
                    .addExtraArgs("-master_pl_name", "master.m3u8")
                    .done();
            
            // 执行FFmpeg命令
            executor.createJob(builder).run();
            
            // 返回完整URL路径，前端可以直接访问
            return domainUrl + relativePath + "/master.m3u8";
        } catch (Exception e) {
            log.error("转换视频到HLS格式失败", e);
            throw new IOException("转换视频失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除HLS视频文件
     *
     * @param videoUrl HLS视频URL
     * @return 是否删除成功
     */
    public boolean deleteHlsVideo(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return false;
        }
        
        try {
            // 从URL中提取相对路径
            String relativePath = null;
            if (videoUrl.startsWith(domainUrl)) {
                relativePath = videoUrl.substring(domainUrl.length());
            } else {
                relativePath = videoUrl;
            }
            
            // 移除末尾的master.m3u8
            if (relativePath.endsWith("/master.m3u8")) {
                relativePath = relativePath.substring(0, relativePath.length() - "/master.m3u8".length());
            }
            
            // 构建目录路径
            Path videoDir = Paths.get(baseStoragePath, relativePath);
            
            if (Files.exists(videoDir) && Files.isDirectory(videoDir)) {
                // 删除目录及其所有内容
                Files.walk(videoDir)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                
                log.info("成功删除视频文件: {}", videoDir);
                return true;
            } else {
                log.warn("视频目录不存在: {}", videoDir);
                return false;
            }
        } catch (Exception e) {
            log.error("删除视频文件失败: {}", videoUrl, e);
            return false;
        }
    }
    
    /**
     * 删除MP4视频文件
     *
     * @param videoUrl MP4视频URL
     * @return 是否删除成功
     */
    public boolean deleteVideoFile(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return false;
        }
        
        try {
            // 从URL中提取相对路径
            String relativePath = null;
            if (videoUrl.startsWith(domainUrl)) {
                relativePath = videoUrl.substring(domainUrl.length());
            } else {
                relativePath = videoUrl;
            }
            
            // 构建文件路径
            Path videoPath = Paths.get(baseStoragePath, relativePath);
            boolean deleted = false;
            
            // 先尝试删除单个MP4文件
            if (Files.exists(videoPath) && Files.isRegularFile(videoPath)) {
                Files.delete(videoPath);
                log.info("成功删除视频文件: {}", videoPath);
                deleted = true;
                
                // 删除文件后，检查并清理空目录
                deleteEmptyParentDirectories(videoPath);
            }
            
            // 如果单个文件不存在，尝试删除父目录（可能包含视频文件）
            if (!deleted) {
                Path videoDir = videoPath;
                if (Files.isRegularFile(videoPath) || !Files.exists(videoPath)) {
                    videoDir = videoPath.getParent();
                }
                
                if (Files.exists(videoDir) && Files.isDirectory(videoDir)) {
                    // 删除目录及其所有内容
                    Files.walk(videoDir)
                            .sorted(java.util.Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    
                    log.info("成功删除视频目录: {}", videoDir);
                    deleted = true;
                    
                    // 删除目录后，检查并清理空的父目录
                    deleteEmptyParentDirectories(videoDir);
                }
            }
            
            if (!deleted) {
                log.warn("视频文件或目录不存在: {}", videoPath);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("删除视频文件失败: {}", videoUrl, e);
            return false;
        }
    }
    
    /**
     * 递归删除空的父目录
     * 从给定路径的父目录开始，向上递归检查并删除空目录
     *
     * @param path 起始路径
     */
    private void deleteEmptyParentDirectories(Path path) {
        try {
            Path parent = path.getParent();
            
            // 检查是否已经到达baseStoragePath的根目录或static目录，防止删除过多
            Path basePath = Paths.get(baseStoragePath);
            Path staticPath = Paths.get(baseStoragePath, "static");
            
            // 如果父目录是baseStoragePath或static目录或其父目录，则停止递归
            if (parent == null || parent.equals(basePath) || parent.equals(staticPath) || 
                    parent.startsWith(basePath) && parent.getNameCount() <= basePath.getNameCount() + 1) {
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
     * 删除封面图片文件
     *
     * @param coverUrl 封面图片URL
     * @return 是否删除成功
     */
    public boolean deleteCoverImage(String coverUrl) {
        if (coverUrl == null || coverUrl.isEmpty()) {
            return false;
        }
        
        try {
            // 从URL中提取相对路径
            String relativePath = null;
            if (coverUrl.startsWith(domainUrl)) {
                relativePath = coverUrl.substring(domainUrl.length());
            } else {
                relativePath = coverUrl;
            }
            
            // 构建文件路径
            Path coverFile = Paths.get(baseStoragePath, relativePath);
            
            if (Files.exists(coverFile) && Files.isRegularFile(coverFile)) {
                // 删除文件
                Files.delete(coverFile);
                log.info("成功删除封面图片: {}", coverFile);
                
                // 删除文件后，检查并清理空目录
                deleteEmptyParentDirectories(coverFile);
                
                return true;
            } else {
                log.warn("封面图片不存在: {}", coverFile);
                return false;
            }
        } catch (Exception e) {
            log.error("删除封面图片失败: {}", coverUrl, e);
            return false;
        }
    }
} 