package com.kk.pethome.system.controller;

import com.kk.pethome.basic.ann.PreAuthorize;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.system.pojo.Menu;
import com.kk.pethome.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public jsonresult list() {
        return jsonresult.success(menuService.lookAll());
    }

    @GetMapping("/tree")
    public jsonresult tree() {
        return jsonresult.success(menuService.lookAll());
    }

    @GetMapping("/{id}")
    public jsonresult getById(@PathVariable Long id) {
        return jsonresult.success(menuService.lookById(id));
    }

    @GetMapping("/emp/{empId}")
    public jsonresult getByEmpId(@PathVariable Long empId) {
        return jsonresult.success(menuService.queryByEmpId(empId));
    }

    @PostMapping
    @PreAuthorize(value = "menu:add", name = "新增菜单")
    public jsonresult add(@RequestBody Menu menu) {
        menuService.Add(menu);
        return jsonresult.success(menu);
    }

    @PutMapping("/{id}")
    @PreAuthorize(value = "menu:edit", name = "编辑菜单")
    public jsonresult update(@PathVariable Long id, @RequestBody Menu menu) {
        menu.setId(id);
        menuService.Update(menu);
        return jsonresult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(value = "menu:delete", name = "删除菜单")
    public jsonresult delete(@PathVariable Long id) {
        menuService.Delete(id);
        return jsonresult.success(null);
    }
}
