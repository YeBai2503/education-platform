package com.baymax.exam.note.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baymax.exam.note.model.Note;
import com.baymax.exam.note.model.dto.NoteDTO;
import com.baymax.exam.note.model.vo.NoteVO;

import java.util.List;

/**
 * @author ：Baymax
 * @date ：Created in 2025/07/04
 * @description：笔记服务接口
 */
public interface NoteService extends IService<Note> {

    /**
     * 添加笔记
     *
     * @param noteDTO 笔记DTO
     * @return 笔记VO
     */
    NoteVO addNote(NoteDTO noteDTO);

    /**
     * 更新笔记
     *
     * @param noteDTO 笔记DTO
     * @return 是否更新成功
     */
    boolean updateNote(NoteDTO noteDTO);

    /**
     * 删除笔记
     *
     * @param id 笔记ID
     * @return 是否删除成功
     */
    boolean deleteNote(Integer id);
    
    /**
     * 根据用户ID和笔记ID删除笔记，确保只能删除自己的笔记
     * 
     * @param userId 用户ID
     * @param noteId 笔记ID
     * @return 是否删除成功
     */
    boolean deleteNoteByUserIdAndNoteId(Integer userId, Integer noteId);

    /**
     * 根据ID获取笔记
     *
     * @param id 笔记ID
     * @return 笔记VO
     */
    NoteVO getNoteById(Integer id);
    
    /**
     * 根据用户ID和笔记ID获取笔记，确保只能查看自己的笔记
     * 
     * @param userId 用户ID
     * @param noteId 笔记ID
     * @return 笔记VO
     */
    NoteVO getNoteByUserIdAndNoteId(Integer userId, Integer noteId);

    /**
     * 根据用户ID获取笔记列表
     *
     * @param userId 用户ID
     * @return 笔记VO列表
     */
    List<NoteVO> getNotesByUserId(Integer userId);

    /**
     * 根据课程ID获取笔记列表
     *
     * @param courseId 课程ID
     * @return 笔记VO列表
     */
    List<NoteVO> getNotesByCourseId(Integer courseId);
    
    /**
     * 根据用户ID和课程ID获取笔记列表，确保只能查看自己的笔记
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 笔记VO列表
     */
    List<NoteVO> getNotesByUserIdAndCourseId(Integer userId, Integer courseId);

    /**
     * 分页查询用户的笔记
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页笔记VO
     */
    Page<NoteVO> pageUserNotes(Integer userId, Integer page, Integer size);
} 