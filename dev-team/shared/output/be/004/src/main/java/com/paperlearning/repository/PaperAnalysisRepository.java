package com.paperlearning.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paperlearning.entity.PaperAnalysis;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaperAnalysisRepository extends BaseMapper<PaperAnalysis> {
}
