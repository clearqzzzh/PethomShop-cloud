package com.kk.pethome.org.controller;

import com.kk.pethome.basic.ann.PreAuthorize;
import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.org.pojo.Employee;
import com.kk.pethome.org.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public jsonresult list(BaseQuery<Employee> query) {
        return jsonresult.success(employeeService.queryPage(query));
    }

    @GetMapping("/all")
    public jsonresult all() {
        return jsonresult.success(employeeService.lookAll());
    }

    @GetMapping("/{id}")
    public jsonresult getById(@PathVariable Long id) {
        return jsonresult.success(employeeService.lookById(id));
    }

    @PostMapping
    @PreAuthorize(value = "employee:add", name = "新增员工")
    public jsonresult add(@RequestBody Employee emp) {
        employeeService.Add(emp);
        return jsonresult.success(emp);
    }

    @PutMapping("/{id}")
    @PreAuthorize(value = "employee:edit", name = "编辑员工")
    public jsonresult update(@PathVariable Long id, @RequestBody Employee emp) {
        emp.setId(id);
        employeeService.Update(emp);
        return jsonresult.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(value = "employee:delete", name = "删除员工")
    public jsonresult delete(@PathVariable Long id) {
        employeeService.Delete(id);
        return jsonresult.success(null);
    }
}
