package com.kk.pethome.org.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.org.pojo.Employee;

public interface EmployeeMapper extends BaseMapper<Employee> {

    Employee lookByName(String username);

    Employee queryByLoginInfoId(Long id);
}















