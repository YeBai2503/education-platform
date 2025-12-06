package com.baymax.exam.video.controller;

import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.common.core.result.ResultCode;
import com.baymax.exam.common.core.exception.ResultException;
import com.baymax.exam.file.feign.FileDetailClient;
import com.baymax.exam.video.model.Chapter;
import com.baymax.exam.video.model.Section;
import com.baymax.exam.video.service.IChapterService;
import com.baymax.exam.video.service.IProgressService;
import com.baymax.exam.video.service.ISectionService;
import com.baymax.exam.video.utils.VideoUtils;
import com.baymax.exam.video.vo.ChapterVO;
import com.baymax.exam.video.vo.SectionFormVO;
import com.baymax.exam.video.vo.SectionVO;
import com.baymax.exam.web.annotation.Inner;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 视频控制器
 */
@Slf4j
@RestController
@RequestMapping("/video")
@Tag(name = "视频管理")
public class VideoController {

    @Autowired
    private IChapterService chapterService;

    @Autowired
    private ISectionService sectionService;

    @Autowired
    private IProgressService progressService;

    @Autowired
    private FileDetailClient fileDetailClient;
    
    @Autowired
    private VideoUtils videoUtils;
    
    @Value("${video.temp-dir:/tmp/videos}")
    private String tempDir;
    
    @Value("${video.domain-url:http://192.168.78.91:10030/}")
    private String domainUrl;
    
    @Value("${spring.file-storage.local[0].base-path:G:/aaa_study_platform/Temp/}")
    private String baseStoragePath;
    
    @Value("${ffmpeg.ffmpeg-path:ffmpeg}")
    private String ffmpegPath;
    
    @Value("${ffmpeg.ffprobe-path:ffprobe}")
    private String ffprobePath;

    /**
     * 获取课程的所有章节和小节（内部接口）
     */
    @Inner
    @GetMapping("/inner/course/{courseId}/chapters")
    public Result<List<ChapterVO>> getInnerCourseChapters(@PathVariable Integer courseId) {
        return Result.success(chapterService.getChaptersByCourseId(courseId));
    }

    /**
     * 获取课程的所有章节和小节（公共接口，不需要身份认证，不包含学习进度）
     */
    @Operation(summary = "公开获取课程的所有章节和小节")
    @GetMapping("/public/course/{courseId}/chapters")
    public Result<List<ChapterVO>> getPublicCourseChapters(@PathVariable Integer courseId) {
        return Result.success(chapterService.getChaptersByCourseId(courseId));
    }

    /**
     * 获取课程的所有章节和小节（包含学习进度）
     */
    @Operation(summary = "获取课程的所有章节和小节")
    @GetMapping("/course/{courseId}/chapters")
    public Result<List<ChapterVO>> getCourseChapters(@PathVariable Integer courseId) {
        Integer userId = UserAuthUtil.getUserId();
        return Result.success(chapterService.getChaptersByCourseIdWithProgress(courseId, userId));
    }

    /**
     * 获取小节详情（包含学习进度）
     */
    @Operation(summary = "获取小节详情")
    @GetMapping("/section/{sectionId}")
    public Result<SectionVO> getSectionDetail(@PathVariable Integer sectionId) {
        Integer userId = UserAuthUtil.getUserId();
        SectionVO sectionVO = sectionService.getSectionWithProgress(sectionId, userId);
        if (sectionVO == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "小节不存在");
        }
        return Result.success(sectionVO);
    }

    /**
     * 更新学习进度
     */
    @Operation(summary = "更新学习进度")
    @PostMapping("/section/{sectionId}/progress")
    public Result<Boolean> updateProgress(@PathVariable Integer sectionId, @RequestParam Integer progress) {
        Integer userId = UserAuthUtil.getUserId();
        if (progress < 0 || progress > 100) {
            return Result.failed(ResultCode.PARAM_ERROR, "进度值必须在0-100之间");
        }
        boolean result = progressService.updateProgress(sectionId, userId, progress);
        return Result.success(result);
    }

    /**
     * 添加章节
     */
    @Operation(summary = "添加章节")
    @PostMapping("/chapter")
    public Result<Chapter> addChapter(@RequestBody Chapter chapter) {
        Integer userId = UserAuthUtil.getUserId();
        if (chapter.getCourseId() == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "课程ID不能为空");
        }
        // TODO: 验证用户是否为该课程的教师
        
        LocalDateTime now = LocalDateTime.now();
        chapter.setCreatedAt(now);
        chapter.setUpdatedAt(now);
        chapterService.save(chapter);
        return Result.success(chapter);
    }

    /**
     * 更新章节
     */
    @Operation(summary = "更新章节")
    @PutMapping("/chapter/{chapterId}")
    public Result<Chapter> updateChapter(@PathVariable Integer chapterId, @RequestBody Chapter chapter) {
        Integer userId = UserAuthUtil.getUserId();
        chapter.setId(chapterId);
        chapter.setUpdatedAt(LocalDateTime.now());
        // TODO: 验证用户是否为该课程的教师
        
        if (!chapterService.updateById(chapter)) {
            return Result.failed(ResultCode.PARAM_ERROR, "章节不存在");
        }
        return Result.success(chapter);
    }

    /**
     * 删除章节
     */
    @Operation(summary = "删除章节")
    @DeleteMapping("/chapter/{chapterId}")
    public Result<Boolean> deleteChapter(@PathVariable Integer chapterId) {
        Integer userId = UserAuthUtil.getUserId();
        // TODO: 验证用户是否为该课程的教师
        
        if (!chapterService.removeById(chapterId)) {
            return Result.failed(ResultCode.PARAM_ERROR, "章节不存在");
        }
        return Result.success(true);
    }

    /**
     * 添加或更新小节
     */
    @Operation(summary = "添加或更新小节")
    @PostMapping("/section")
    public Result<Section> saveSection(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("chapterId") Integer chapterId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("rank") Integer rank,
            @RequestPart(value = "video", required = false) MultipartFile video,
            @RequestPart(value = "cover", required = false) MultipartFile cover) throws ResultException {
        
        Integer userId = UserAuthUtil.getUserId();
        // TODO: 验证用户是否为该课程的教师
        
        Section section;
        boolean isUpdate = id != null;
        String oldVideoUrl = null;
        String oldCoverUrl = null;
        
        if (isUpdate) {
            // 更新小节
            section = sectionService.getById(id);
            if (section == null) {
                return Result.failed(ResultCode.PARAM_ERROR, "小节不存在");
            }
            // 保存旧的URL，用于后续删除旧文件
            oldVideoUrl = section.getVideoUrl();
            oldCoverUrl = section.getCoverUrl();
        } else {
            // 创建小节
            section = new Section();
            section.setChapterId(chapterId);
            section.setCreatedAt(LocalDateTime.now());
        }
        
        // 设置基本属性
        section.setTitle(title);
        section.setDescription(description);
        section.setRank(rank);
        section.setUpdatedAt(LocalDateTime.now());
        
        // 处理视频文件上传
        if (video != null && !video.isEmpty()) {
            try {
                log.info("接收到视频文件: 名称={}, 大小={}字节, 内容类型={}", 
                        video.getOriginalFilename(), video.getSize(), video.getContentType());
                
                // 获取章节信息
                Chapter chapter = chapterService.getById(chapterId);
                if (chapter == null) {
                    return Result.failed(ResultCode.PARAM_ERROR, "章节不存在");
                }
                
                // 创建输出目录结构: static/videos/{courseId}/{chapterId}/{uuid}
                String uuid = UUID.randomUUID().toString();
                String relativePath = String.format("static/videos/%d/%d/%s", chapter.getCourseId(), chapterId, uuid);
                Path outputDir = Paths.get(baseStoragePath, relativePath);
                Files.createDirectories(outputDir);
                
                // 创建目标MP4文件
                String mp4FileName = UUID.randomUUID().toString() + ".mp4";
                File mp4File = new File(outputDir.toFile(), mp4FileName);
                
                // 记录开始时间
                long startSaveTime = System.currentTimeMillis();
                
                // 直接保存上传的文件到输出目录
                try (InputStream inputStream = video.getInputStream()) {
                    Files.copy(inputStream, mp4File.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                
                log.info("MP4文件已保存: {}, 耗时: {}ms", mp4File.getAbsolutePath(), 
                        System.currentTimeMillis() - startSaveTime);
                
                if (!mp4File.exists()) {
                    throw new IOException("MP4文件保存失败: " + mp4File.getAbsolutePath());
                }
                
                // 获取视频时长
                long startDurationTime = System.currentTimeMillis();
                Integer duration = videoUtils.getVideoDuration(mp4File);
                log.info("获取视频时长完成: {}秒, 耗时: {}ms", duration, 
                        System.currentTimeMillis() - startDurationTime);
                
                if (duration != null) {
                    section.setDuration(duration);
                }
                
                // 设置视频URL（直接指向MP4文件）
                String mp4Url = domainUrl + relativePath + "/" + mp4FileName;
                section.setVideoUrl(mp4Url);
            } catch (IOException e) {
                log.error("处理视频文件失败", e);
                return Result.failed(ResultCode.PARAM_ERROR, "视频处理失败: " + e.getMessage());
            }
        }
        
        // 处理封面图片上传
        if (cover != null && !cover.isEmpty()) {
            try {
                log.info("接收到封面文件: 名称={}, 大小={}字节, 内容类型={}", 
                        cover.getOriginalFilename(), cover.getSize(), cover.getContentType());
                
                // 使用自定义逻辑上传封面，避免使用临时文件
                String coverFileName = UUID.randomUUID().toString() + getFileExtension(cover.getOriginalFilename());
                String relativePath = String.format("static/covers/%d/%s", userId, coverFileName);
                Path coverPath = Paths.get(baseStoragePath, relativePath);
                
                // 确保目录存在
                Files.createDirectories(coverPath.getParent());
                
                // 直接保存文件
                try (InputStream inputStream = cover.getInputStream()) {
                    Files.copy(inputStream, coverPath, StandardCopyOption.REPLACE_EXISTING);
                }
                
                // 构建完整URL
                String coverUrl = domainUrl + relativePath;
                section.setCoverUrl(coverUrl);
                
                log.info("封面已上传: {}", coverPath);
            } catch (Exception e) {
                log.error("处理封面文件失败", e);
                return Result.failed(ResultCode.PARAM_ERROR, "封面处理失败: " + e.getMessage());
            }
        }
        
        // 保存或更新小节
        boolean saveResult;
        if (isUpdate) {
            saveResult = sectionService.updateById(section);
        } else {
            saveResult = sectionService.save(section);
        }
        
        // 如果更新成功且上传了新文件，删除旧文件
        if (saveResult && isUpdate) {
            // 如果上传了新视频，删除旧视频
            if (video != null && !video.isEmpty() && oldVideoUrl != null && !oldVideoUrl.isEmpty()) {
                videoUtils.deleteVideoFile(oldVideoUrl);
            }
            
            // 如果上传了新封面，删除旧封面
            if (cover != null && !cover.isEmpty() && oldCoverUrl != null && !oldCoverUrl.isEmpty()) {
                videoUtils.deleteCoverImage(oldCoverUrl);
            }
        }
        
        return Result.success(section);
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".jpg";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return ".jpg";
    }

    /**
     * 删除小节
     */
    @Operation(summary = "删除小节")
    @DeleteMapping("/section/{sectionId}")
    public Result<Boolean> deleteSection(@PathVariable Integer sectionId) {
        Integer userId = UserAuthUtil.getUserId();
        // TODO: 验证用户是否为该课程的教师
        
        // 先获取小节信息，以便删除相关文件
        Section section = sectionService.getById(sectionId);
        if (section == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "小节不存在");
        }
        
        // 删除数据库记录
        boolean result = sectionService.removeById(sectionId);
        
        if (result) {
            // 删除视频文件
            if (section.getVideoUrl() != null && !section.getVideoUrl().isEmpty()) {
                videoUtils.deleteVideoFile(section.getVideoUrl());
            }
            
            // 删除封面图片
            if (section.getCoverUrl() != null && !section.getCoverUrl().isEmpty()) {
                videoUtils.deleteCoverImage(section.getCoverUrl());
            }
        }
        
        return Result.success(result);
    }

    /**
     * 上传视频
     */
    @Operation(summary = "上传视频")
    @PostMapping("/upload")
    public Result<String> uploadVideo(@RequestPart("file") MultipartFile file) throws ResultException {
        Integer userId = UserAuthUtil.getUserId();
        // 调用文件服务上传视频
        Result<String> result = fileDetailClient.uploadImage(file, "/static/videos/" + userId, userId.toString(), "video");
        
        // 确保返回完整URL
        String videoUrl = result.getResultDate();
        if (videoUrl != null && !videoUrl.startsWith("http")) {
            videoUrl = domainUrl + videoUrl;
        }
        
        return Result.success(videoUrl);
    }
} 