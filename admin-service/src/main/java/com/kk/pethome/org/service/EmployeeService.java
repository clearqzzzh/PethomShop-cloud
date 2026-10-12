package com.kk.pethome.org.service;

import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.org.pojo.Employee;
import java.util.List;

public interface EmployeeService {
    List<Employee> lookAll();
    Employee lookById(Long id);
    PageList<Employee> queryPage(BaseQuery<Employee> baseQuery);
    void Add(Employee emp);
    void Update(Employee emp);
    void Delete(Long id);
    Employee lookByName(String username);
    Employee queryByLoginInfoId(Long loginInfoId);
}
