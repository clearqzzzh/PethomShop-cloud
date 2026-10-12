package com.kk.pethome.system.controller;

import com.kk.pethome.basic.ann.PreAuthorize;
import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.system.Dto.RoleDto;
import com.kk.pethome.system.pojo.Role;
import com.kk.pethome.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public jsonresult list(BaseQuery<Role> query) {
        return jsonresult.success(roleService.queryPage(query));
    }

    @GetMapping("/all")
    public jsonresult all() {
        return jsonresult.success(roleService.lookAll());
    }

    @GetMapping("/{id}")
    public jsonresult getById(@PathVariable Long id) {
        return jsonresult.success(roleService.lookById(id));
    }

    @GetMapping("/{id}/detail")
    public jsonresult getDetail(@PathVariable Long id) {
        return jsonresult.success(roleService.getRoleWithPerms(id));
    }

    @PostMapping
    @PreAuthorize(value = "role:add", name = "新增角色")
    public jsonresult add(@RequestBody Role role) {
        roleService.Add(role);
        return jsonresult.success(role);
    }

    @PutMapping("/{id}")
    @PreAuthorize(value = "role:edit", name = "编辑角色")
    public jsonresult update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        roleService.Update(role);
        return jsonresult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(value = "role:delete", name = "删除角色")
    public jsonresult delete(@PathVariable Long id) {
        roleService.Delete(id);
        return jsonresult.success(null);
    }

    // Save role with permissions and menus assignment
    @PostMapping("/saveWithPerms")
    public jsonresult saveWithPerms(@RequestBody RoleDto dto) {
        roleService.saveRoleWithPerms(dto);
        return jsonresult.success(null);
    }

    // Assign permissions to role
    @PutMapping("/{roleId}/permissions")
    public jsonresult assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return jsonresult.success(null);
    }

    // Assign menus to role
    @PutMapping("/{roleId}/menus")
    public jsonresult assignMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return jsonresult.success(null);
    }
}
