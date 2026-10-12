package com.kk.pethome.org.service.impl;

import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.org.mapper.EmployeeMapper;
import com.kk.pethome.org.pojo.Employee;
import com.kk.pethome.org.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public List<Employee> lookAll() { return employeeMapper.lookAll(); }

    @Override
    public Employee lookById(Long id) { return employeeMapper.lookById(id); }

    @Override
    public PageList<Employee> queryPage(BaseQuery<Employee> baseQuery) {
        PageList<Employee> pageList = new PageList<>();
        pageList.setTotal(employeeMapper.loadcount(baseQuery));
        pageList.setData(employeeMapper.lookPage(baseQuery));
        return pageList;
    }

    @Override
    public void Add(Employee emp) { employeeMapper.Add(emp); }

    @Override
    public void Update(Employee emp) { employeeMapper.Update(emp); }

    @Override
    public void Delete(Long id) { employeeMapper.Delete(id); }

    @Override
    public Employee lookByName(String username) { return employeeMapper.lookByName(username); }

    @Override
    public Employee queryByLoginInfoId(Long loginInfoId) { return employeeMapper.queryByLoginInfoId(loginInfoId); }
}
