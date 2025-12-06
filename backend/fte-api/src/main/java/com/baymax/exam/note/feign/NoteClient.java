package com.baymax.exam.note.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.note.model.NoteDTO;
import com.baymax.exam.note.model.NoteVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//笔记客户端
@FeignClient(value = "exam-note", contextId = "NoteClient", path = "/note")
public interface NoteClient {

    /**
     * 添加笔记
     *
     * @param noteDTO 笔记DTO
     * @return 笔记VO
     */
    @PostMapping("/add")
    Result<NoteVO> addNote(@RequestBody NoteDTO noteDTO);

    /**
     * 更新笔记
     *
     * @param noteDTO 笔记DTO
     * @return 是否更新成功
     */
    @PutMapping("/update")
    Result<Boolean> updateNote(@RequestBody NoteDTO noteDTO);

    /**
     * 删除笔记
     *
     * @param id 笔记ID
     * @return 是否删除成功
     */
    @DeleteMapping("/delete/{id}")
    Result<Boolean> deleteNote(@PathVariable("id") Integer id);

    /**
     * 根据ID获取笔记
     *
     * @param id 笔记ID
     * @return 笔记VO
     */
    @GetMapping("/get/{id}")
    Result<NoteVO> getNoteById(@PathVariable("id") Integer id);

    /**
     * 获取当前用户笔记列表
     *
     * @return 笔记VO列表
     */
    @GetMapping("/list/user")
    Result<List<NoteVO>> getUserNotes();

    /**
     * 根据课程ID获取当前用户的笔记列表
     *
     * @param courseId 课程ID
     * @return 笔记VO列表
     */
    @GetMapping("/list/course/{courseId}")
    Result<List<NoteVO>> getNotesByCourseId(@PathVariable("courseId") Integer courseId);

    /**
     * 分页获取当前用户笔记
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页笔记VO
     */
    @GetMapping("/page/{page}/{size}")
    Result<Page<NoteVO>> pageUserNotes(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size);
} 