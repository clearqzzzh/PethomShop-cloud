package com.kk.pethome.user.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.user.pojo.loginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginInfoMapper extends BaseMapper<loginInfo> {

    loginInfo selectByUsername(@Param("username") String username, @Param("type") Integer type);
}
