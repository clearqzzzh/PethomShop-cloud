package com.kk.pethome.system.service;

import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.system.Dto.RoleDto;
import com.kk.pethome.system.pojo.Role;

import java.util.List;

public interface RoleService {
    List<Role> lookAll();
    Role lookById(Long id);
    PageList<Role> queryPage(BaseQuery<Role> baseQuery);
    void Add(Role role);
    void Update(Role role);
    void Delete(Long id);
    void DeleteBatch(List<Long> ids);

    // Get role with its permission IDs and menu IDs
    RoleDto getRoleWithPerms(Long id);

    // Save role and assign permissions/menus
    void saveRoleWithPerms(RoleDto dto);

    // Assign permissions to role
    void assignPermissions(Long roleId, List<Long> permissionIds);

    // Assign menus to role
    void assignMenus(Long roleId, List<Long> menuIds);
}
