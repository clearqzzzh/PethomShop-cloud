package com.kk.pethome.user.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.user.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User getByPhone(String phone);
}
