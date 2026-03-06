package com.paperlearning.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paperlearning.entity.AnalysisTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnalysisTaskRepository extends BaseMapper<AnalysisTask> {
}
