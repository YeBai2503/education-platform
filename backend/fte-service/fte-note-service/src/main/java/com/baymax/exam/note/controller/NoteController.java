package com.baymax.exam.note.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.note.model.dto.NoteDTO;
import com.baymax.exam.note.model.vo.NoteVO;
import com.baymax.exam.note.service.NoteService;
import com.baymax.exam.web.annotation.Inner;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

/**
 * @author ：Baymax
 * @date ：Created in 2025/07/04
 * @description：笔记控制器
 */
@Slf4j
@RestController
@RequestMapping("/note")
@Tag(name = "笔记管理", description = "笔记相关接口")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping("/add")
    @Operation(summary = "添加笔记", description = "添加新笔记，支持从Token或请求体获取用户ID")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "笔记数据传输对象",
        required = true,
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NoteDTO.class),
            examples = {
                @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "基本笔记",
                    summary = "包含课程ID和内容的基本笔记",
                    value = "{\"courseId\": 101, \"context\": \"这是笔记内容\"}"
                ),
                @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "带用户ID的笔记",
                    summary = "包含用户ID、课程ID和内容的笔记",
                    value = "{\"userId\": 10, \"courseId\": 101, \"context\": \"这是带用户ID的笔记内容\"}"
                )
            }
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "成功添加笔记",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NoteVO.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "用户ID不能为空"),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<NoteVO> addNote(@Validated @RequestBody NoteDTO noteDTO, HttpServletRequest request) {
        // 尝试从ThreadLocal获取用户ID
        try {
            Integer userId = UserAuthUtil.getUserId();
            log.info("从ThreadLocal获取用户ID: {}", userId);
            noteDTO.setUserId(userId); // 设置当前用户ID
        } catch (Exception e) {
            log.error("从ThreadLocal获取用户ID异常: ", e);
            // 如果请求体中已有userId，保留使用
            log.info("使用请求体中的用户ID: {}", noteDTO.getUserId());
        }
        
        // 最后检查userId是否为空
        if (noteDTO.getUserId() == null) {
            log.error("用户ID为空，请求被拒绝");
            return Result.failed("用户ID不能为空");
        }
        
        // userId不为空，继续处理
        NoteVO noteVO = noteService.addNote(noteDTO);
        return Result.success(noteVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新笔记", description = "更新笔记内容")
    public Result<Boolean> updateNote(@Validated @RequestBody NoteDTO noteDTO, HttpServletRequest request) {
        // 尝试从ThreadLocal获取用户ID
        try {
            Integer userId = UserAuthUtil.getUserId();
            log.info("从ThreadLocal获取用户ID: {}", userId);
            noteDTO.setUserId(userId); // 设置当前用户ID
        } catch (Exception e) {
            log.error("从ThreadLocal获取用户ID异常: ", e);
            // 如果请求体中已有userId，保留使用
            log.info("使用请求体中的用户ID: {}", noteDTO.getUserId());
        }
        
        // 最后检查userId是否为空
        if (noteDTO.getUserId() == null) {
            log.error("用户ID为空，请求被拒绝");
            return Result.failed("用户ID不能为空");
        }

        // 最后检查NoteId是否为空
        if (noteDTO.getId() == null) {
            log.error("笔记ID为空，请求被拒绝");
            return Result.failed("笔记ID不能为空");
        }
        
        // userId不为空，继续处理
        boolean result = noteService.updateNote(noteDTO);
        return Result.success(result);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除笔记", description = "根据ID删除笔记")
    public Result<Boolean> deleteNote(@PathVariable Integer id, @RequestParam(required = false) Integer userId, HttpServletRequest request) {
        // 尝试从ThreadLocal获取用户ID
        try {
            userId = UserAuthUtil.getUserId();
            log.info("从ThreadLocal获取用户ID: {}, 删除笔记ID: {}", userId, id);
        } catch (Exception e) {
            log.error("从ThreadLocal获取用户ID异常: ", e);
            // 如果参数中已有userId，保留使用
            log.info("使用参数中的用户ID: {}", userId);
        }
        
        // 最后检查userId是否为空
        if (userId == null) {
            log.error("用户ID为空，请求被拒绝");
            return Result.failed("用户ID不能为空");
        }
        
        // userId不为空，继续处理
        boolean result = noteService.deleteNoteByUserIdAndNoteId(userId, id);
        return Result.success(result);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取笔记", description = "根据ID获取笔记详情")
    public Result<NoteVO> getNoteById(@PathVariable Integer id, @RequestParam(required = false) Integer userId, HttpServletRequest request) {
        // 尝试从ThreadLocal获取用户ID
        try {
            userId = UserAuthUtil.getUserId();
            log.info("从ThreadLocal获取用户ID: {}, 查询笔记ID: {}", userId, id);
        } catch (Exception e) {
            log.error("从ThreadLocal获取用户ID异常: ", e);
            // 如果参数中已有userId，保留使用
            log.info("使用参数中的用户ID: {}", userId);
        }
        
        // 最后检查userId是否为空
        if (userId == null) {
            log.error("用户ID为空，请求被拒绝");
            return Result.failed("用户ID不能为空");
        }
        
        // userId不为空，继续处理
        NoteVO noteVO = noteService.getNoteByUserIdAndNoteId(userId, id);
        return Result.success(noteVO);
    }

    @GetMapping("/list/user")
    @Operation(summary = "获取当前用户笔记列表", description = "获取当前登录用户的笔记列表")
    public Result<List<NoteVO>> getUserNotes(@RequestParam(required = false) Integer userId, HttpServletRequest request) {
        // 尝试从ThreadLocal获取用户ID
        try {
            userId = UserAuthUtil.getUserId();
            log.info("从ThreadLocal获取用户ID: {}", userId);
        } catch (Exception e) {
            log.error("从ThreadLocal获取用户ID异常: ", e);
            // 如果参数中已有userId，保留使用
            log.info("使用参数中的用户ID: {}", userId);
        }
        
        // 最后检查userId是否为空
        if (userId == null) {
            log.error("用户ID为空，请求被拒绝");
            return Result.failed("用户ID不能为空");
        }
        
        // userId不为空，继续处理
        List<NoteVO> noteVOList = noteService.getNotesByUserId(userId);
        return Result.success(noteVOList);
    }

    @GetMapping("/list/course/{courseId}")
    @Operation(summary = "获取课程笔记列表", description = "根据课程ID获取当前用户的笔记列表")
    public Result<List<NoteVO>> getNotesByCourseId(@PathVariable Integer courseId, @RequestParam(required = false) Integer userId, HttpServletRequest request) {
        // 尝试从ThreadLocal获取用户ID
        try {
            userId = UserAuthUtil.getUserId();
            log.info("从ThreadLocal获取用户ID: {}, 课程ID: {}", userId, courseId);
        } catch (Exception e) {
            log.error("从ThreadLocal获取用户ID异常: ", e);
            // 如果参数中已有userId，保留使用
            log.info("使用参数中的用户ID: {}", userId);
        }
        
        // 最后检查userId是否为空
        if (userId == null) {
            log.error("用户ID为空，请求被拒绝");
            return Result.failed("用户ID不能为空");
        }
        
        // userId不为空，继续处理
        List<NoteVO> noteVOList = noteService.getNotesByUserIdAndCourseId(userId, courseId);
        return Result.success(noteVOList);
    }

    @GetMapping("/page/{page}/{size}")
    @Operation(summary = "分页获取当前用户笔记", description = "分页获取当前登录用户的笔记列表")
    public Result<Page<NoteVO>> pageUserNotes(
            @Parameter(description = "页码") @PathVariable Integer page,
            @Parameter(description = "每页大小") @PathVariable Integer size,
            @RequestParam(required = false) Integer userId,
            HttpServletRequest request) {
        // 尝试从ThreadLocal获取用户ID
        try {
            userId = UserAuthUtil.getUserId();
            log.info("从ThreadLocal获取用户ID: {}, 分页查询: page={}, size={}", userId, page, size);
        } catch (Exception e) {
            log.error("从ThreadLocal获取用户ID异常: ", e);
            // 如果参数中已有userId，保留使用
            log.info("使用参数中的用户ID: {}", userId);
        }
        
        // 最后检查userId是否为空
        if (userId == null) {
            log.error("用户ID为空，请求被拒绝");
            return Result.failed("用户ID不能为空");
        }
        
        // userId不为空，继续处理
        Page<NoteVO> noteVOPage = noteService.pageUserNotes(userId, page, size);
        return Result.success(noteVOPage);
    }
    
    /**
     * 内部接口 - 获取指定用户的笔记列表
     */
    @Inner
    @GetMapping("/inner/user/{userId}")
    public List<NoteVO> getInnerUserNotes(@PathVariable Integer userId) {
        log.info("[内部调用] 获取用户ID: {} 的笔记列表", userId);
        return noteService.getNotesByUserId(userId);
    }
    
    /**
     * 内部接口 - 获取指定用户的课程笔记列表
     */
    @Inner
    @GetMapping("/inner/course/{userId}/{courseId}")
    public List<NoteVO> getInnerCourseNotes(
            @PathVariable Integer userId,
            @PathVariable Integer courseId) {
        log.info("[内部调用] 获取用户ID: {}, 课程ID: {} 的笔记列表", userId, courseId);
        return noteService.getNotesByUserIdAndCourseId(userId, courseId);
    }
    
    /**
     * 内部接口 - 添加笔记
     */
    @Inner
    @PostMapping("/inner/add")
    public NoteVO innerAddNote(@RequestBody NoteDTO noteDTO) {
        log.info("[内部调用] 为用户ID: {} 添加笔记", noteDTO.getUserId());
        return noteService.addNote(noteDTO);
    }
} 