package com.kk.pethome.system.controller;

import com.kk.pethome.basic.ann.PreAuthorize;
import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.system.pojo.permission;
import com.kk.pethome.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public jsonresult list(BaseQuery<permission> query) {
        return jsonresult.success(permissionService.queryPage(query));
    }

    @GetMapping("/all")
    public jsonresult all() {
        return jsonresult.success(permissionService.lookAll());
    }

    @GetMapping("/{id}")
    public jsonresult getById(@PathVariable Long id) {
        return jsonresult.success(permissionService.lookById(id));
    }

    @PostMapping
    @PreAuthorize(value = "permission:add", name = "新增权限")
    public jsonresult add(@RequestBody permission p) {
        permissionService.Add(p);
        return jsonresult.success(p);
    }

    @PutMapping("/{id}")
    @PreAuthorize(value = "permission:edit", name = "编辑权限")
    public jsonresult update(@PathVariable Long id, @RequestBody permission p) {
        p.setId(id);
        permissionService.Update(p);
        return jsonresult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(value = "permission:delete", name = "删除权限")
    public jsonresult delete(@PathVariable Long id) {
        permissionService.Delete(id);
        return jsonresult.success(null);
    }
}
