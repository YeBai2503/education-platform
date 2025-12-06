package com.baymax.exam.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baymax.exam.homework.model.HomeworkScoreRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 作业得分记录 Mapper 接口
 * </p>
 *
 */
@Mapper
public interface HomeworkScoreRecordMapper extends BaseMapper<HomeworkScoreRecord> {
    void batchUpdateByList(List<HomeworkScoreRecord> scoreRecordList);
} 