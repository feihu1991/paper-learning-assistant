package com.paperlearning.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paperlearning.entity.Paper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaperRepository extends BaseMapper<Paper> {
}
