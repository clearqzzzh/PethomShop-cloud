package com.kk.pethome.admin.service.impl;

import com.kk.pethome.admin.mapper.AdminMapper;
import com.kk.pethome.admin.pojo.Admin;
import com.kk.pethome.admin.service.AdminService;
import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public List<Admin> lookAll() { return adminMapper.lookAll(); }

    @Override
    public Admin lookById(Long id) { return adminMapper.lookById(id); }

    @Override
    public PageList<Admin> queryPage(BaseQuery<Admin> query) {
        PageList<Admin> pageList = new PageList<>();
        pageList.setTotal(adminMapper.loadcount(query));
        pageList.setData(adminMapper.lookPage(query));
        return pageList;
    }

    @Override
    public Admin findByUsername(String username) { return adminMapper.findByUsername(username); }

    @Override
    public void add(Admin admin) { adminMapper.Add(admin); }

    @Override
    public void update(Admin admin) { adminMapper.Update(admin); }

    @Override
    public void delete(Long id) { adminMapper.Delete(id); }
}
