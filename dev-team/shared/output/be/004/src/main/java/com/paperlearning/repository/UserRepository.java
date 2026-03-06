package com.paperlearning.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paperlearning.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository extends BaseMapper<User> {
}
