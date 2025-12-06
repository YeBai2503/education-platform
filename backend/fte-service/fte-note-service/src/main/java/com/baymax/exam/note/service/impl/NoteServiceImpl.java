package com.baymax.exam.note.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baymax.exam.note.mapper.NoteMapper;
import com.baymax.exam.note.model.Note;
import com.baymax.exam.note.model.dto.NoteDTO;
import com.baymax.exam.note.model.vo.NoteVO;
import com.baymax.exam.note.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ：Baymax
 * @date ：Created in 2025/07/04
 * @description：笔记服务实现类
 */
@Slf4j
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    @Override
    public NoteVO addNote(NoteDTO noteDTO) {
        Note note = new Note();
        BeanUtils.copyProperties(noteDTO, note);
        
        LocalDateTime now = LocalDateTime.now();
        note.setCreatedAt(now);
        note.setUpdatedAt(now);
        
        save(note);
        
        NoteVO noteVO = new NoteVO();
        BeanUtils.copyProperties(note, noteVO);
        return noteVO;
    }

    @Override
    public boolean updateNote(NoteDTO noteDTO) {
        if (noteDTO.getId() == null) {
            return false;
        }
        
        Note note = getById(noteDTO.getId());
        if (note == null) {
            return false;
        }
        
        // 检查是否是当前用户的笔记
        if (!note.getUserId().equals(noteDTO.getUserId())) {
            log.warn("用户 {} 尝试更新不属于自己的笔记 {}", noteDTO.getUserId(), noteDTO.getId());
            return false;
        }
        
        BeanUtils.copyProperties(noteDTO, note);
        note.setUpdatedAt(LocalDateTime.now());
        
        return updateById(note);
    }

    @Override
    public boolean deleteNote(Integer id) {
        return removeById(id);
    }
    
    @Override
    public boolean deleteNoteByUserIdAndNoteId(Integer userId, Integer noteId) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getId, noteId)
                    .eq(Note::getUserId, userId);
        
        long count = count(queryWrapper);
        if (count == 0) {
            log.warn("用户 {} 尝试删除不存在或不属于自己的笔记 {}", userId, noteId);
            return false;
        }
        
        return remove(queryWrapper);
    }

    @Override
    public NoteVO getNoteById(Integer id) {
        Note note = getById(id);
        if (note == null) {
            return null;
        }
        
        NoteVO noteVO = new NoteVO();
        BeanUtils.copyProperties(note, noteVO);
        return noteVO;
    }
    
    @Override
    public NoteVO getNoteByUserIdAndNoteId(Integer userId, Integer noteId) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getId, noteId)
                    .eq(Note::getUserId, userId);
        
        Note note = getOne(queryWrapper);
        if (note == null) {
            log.warn("用户 {} 尝试查询不存在或不属于自己的笔记 {}", userId, noteId);
            return null;
        }
        
        NoteVO noteVO = new NoteVO();
        BeanUtils.copyProperties(note, noteVO);
        return noteVO;
    }

    @Override
    public List<NoteVO> getNotesByUserId(Integer userId) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId, userId)
                    .orderByDesc(Note::getUpdatedAt);
        
        List<Note> notes = list(queryWrapper);
        
        return notes.stream().map(note -> {
            NoteVO noteVO = new NoteVO();
            BeanUtils.copyProperties(note, noteVO);
            return noteVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<NoteVO> getNotesByCourseId(Integer courseId) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getCourseId, courseId)
                    .orderByDesc(Note::getUpdatedAt);
        
        List<Note> notes = list(queryWrapper);
        
        return notes.stream().map(note -> {
            NoteVO noteVO = new NoteVO();
            BeanUtils.copyProperties(note, noteVO);
            return noteVO;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<NoteVO> getNotesByUserIdAndCourseId(Integer userId, Integer courseId) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId, userId)
                    .eq(Note::getCourseId, courseId)
                    .orderByDesc(Note::getUpdatedAt);
        
        List<Note> notes = list(queryWrapper);
        
        return notes.stream().map(note -> {
            NoteVO noteVO = new NoteVO();
            BeanUtils.copyProperties(note, noteVO);
            return noteVO;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<NoteVO> pageUserNotes(Integer userId, Integer page, Integer size) {
        Page<Note> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId, userId)
                    .orderByDesc(Note::getUpdatedAt);
        
        Page<Note> notePage = page(pageParam, queryWrapper);
        
        // 转换成VO
        List<NoteVO> noteVOList = notePage.getRecords().stream().map(note -> {
            NoteVO noteVO = new NoteVO();
            BeanUtils.copyProperties(note, noteVO);
            return noteVO;
        }).collect(Collectors.toList());
        
        Page<NoteVO> noteVOPage = new Page<>(notePage.getCurrent(), notePage.getSize(), notePage.getTotal());
        noteVOPage.setRecords(noteVOList);
        
        return noteVOPage;
    }
} 