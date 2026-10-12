package com.kk.pethome.system.service.impl;

import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.system.mapper.PermissionMapper;
import com.kk.pethome.system.pojo.permission;
import com.kk.pethome.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<permission> lookAll() { return permissionMapper.lookAll(); }

    @Override
    public permission lookById(Long id) { return permissionMapper.lookById(id); }

    @Override
    public PageList<permission> queryPage(BaseQuery<permission> baseQuery) {
        PageList<permission> pageList = new PageList<>();
        pageList.setTotal(permissionMapper.loadcount(baseQuery));
        pageList.setData(permissionMapper.lookPage(baseQuery));
        return pageList;
    }

    @Override
    public void Add(permission p) { permissionMapper.Add(p); }

    @Override
    public void Update(permission p) { permissionMapper.Update(p); }

    @Override
    public void Delete(Long id) { permissionMapper.Delete(id); }

    @Override
    public List<String> lookByEmployeeId(Long empId) { return permissionMapper.lookByEmployeeId(empId); }
}
