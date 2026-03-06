package com.paperlearning.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paperlearning.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteRepository extends BaseMapper<Favorite> {
}
