package com.kk.pethome.system.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.system.pojo.permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<permission> {
    /**
     * 根据权限编号查询权限
     * @param permissionSn
     * @return
     */
    permission loadBySn(String permissionSn);

    void save(permission permission);

    List<String> lookByEmployeeId(Long id);
}

