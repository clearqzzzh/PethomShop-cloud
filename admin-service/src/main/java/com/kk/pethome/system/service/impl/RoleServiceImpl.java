package com.kk.pethome.system.service.impl;

import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.system.Dto.RoleDto;
import com.kk.pethome.system.mapper.RoleMapper;
import com.kk.pethome.system.pojo.Role;
import com.kk.pethome.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> lookAll() {
        return roleMapper.lookAll();
    }

    @Override
    public Role lookById(Long id) {
        return roleMapper.lookById(id);
    }

    @Override
    public PageList<Role> queryPage(BaseQuery<Role> baseQuery) {
        PageList<Role> pageList = new PageList<>();
        pageList.setTotal(roleMapper.loadcount(baseQuery));
        pageList.setData(roleMapper.lookPage(baseQuery));
        return pageList;
    }

    @Override
    public void Add(Role role) {
        roleMapper.Add(role);
    }

    @Override
    public void Update(Role role) {
        roleMapper.Update(role);
    }

    @Override
    public void Delete(Long id) {
        roleMapper.Delete(id);
    }

    @Override
    public void DeleteBatch(List<Long> ids) {
        roleMapper.DeleteBatch(ids);
    }

    @Override
    public RoleDto getRoleWithPerms(Long id) {
        Role role = roleMapper.lookById(id);
        if (role == null) return null;
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setSn(role.getSn());
        dto.setPermissions(roleMapper.getPermissionIdsByRoleId(id));
        dto.setMenus(roleMapper.getMenuIdsByRoleId(id));
        return dto;
    }

    @Override
    @Transactional
    public void saveRoleWithPerms(RoleDto dto) {
        Role role = new Role();
        role.setName(dto.getName());
        role.setSn(dto.getSn());
        if (dto.getId() != null && dto.getId() > 0) {
            role.setId(dto.getId());
            roleMapper.Update(role);
            // Reassign permissions and menus
            roleMapper.deleteRolePermissions(dto.getId());
            roleMapper.deleteRoleMenus(dto.getId());
        } else {
            roleMapper.Add(role);
        }
        Long roleId = role.getId();
        if (dto.getPermissions() != null) {
            for (Long permId : dto.getPermissions()) {
                roleMapper.insertRolePermission(roleId, permId);
            }
        }
        if (dto.getMenus() != null) {
            for (Long menuId : dto.getMenus()) {
                roleMapper.insertRoleMenu(roleId, menuId);
            }
        }
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        roleMapper.deleteRolePermissions(roleId);
        if (permissionIds != null) {
            for (Long permId : permissionIds) {
                roleMapper.insertRolePermission(roleId, permId);
            }
        }
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMapper.deleteRoleMenus(roleId);
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                roleMapper.insertRoleMenu(roleId, menuId);
            }
        }
    }
}
