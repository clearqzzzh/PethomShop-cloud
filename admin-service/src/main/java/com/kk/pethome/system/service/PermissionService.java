package com.kk.pethome.system.service;

import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.system.pojo.permission;
import java.util.List;

public interface PermissionService {
    List<permission> lookAll();
    permission lookById(Long id);
    PageList<permission> queryPage(BaseQuery<permission> baseQuery);
    void Add(permission p);
    void Update(permission p);
    void Delete(Long id);
    List<String> lookByEmployeeId(Long empId);
}
