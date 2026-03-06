package com.paperlearning.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paperlearning.entity.LearningProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LearningProgressRepository extends BaseMapper<LearningProgress> {
    
    @Select("SELECT * FROM learning_progress WHERE user_id = #{userId} ORDER BY updated_at DESC")
    List<LearningProgress> findByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM learning_progress WHERE user_id = #{userId} AND paper_id = #{paperId}")
    LearningProgress findByUserIdAndPaperId(@Param("userId") Long userId, @Param("paperId") Long paperId);
}
