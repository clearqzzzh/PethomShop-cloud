package com.kk.pethome.admin.mapper;

import com.kk.pethome.admin.pojo.Admin;
import com.kk.pethome.basic.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 管理员 Mapper
 */
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 按用户名查找管理员
     */
    Admin findByUsername(@Param("username") String username);
}
