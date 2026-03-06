package com.paperlearning.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paperlearning.entity.PaperAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PaperAnalysisRepository extends BaseMapper<PaperAnalysis> {
    
    @Select("SELECT * FROM paper_analysis WHERE paper_id = #{paperId} LIMIT 1")
    PaperAnalysis findByPaperId(@Param("paperId") Long paperId);
}
