package com.kk.pethome.admin.service;

import com.kk.pethome.admin.pojo.Admin;
import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;

import java.util.List;

/**
 * 管理员 Service
 */
public interface AdminService {
    List<Admin> lookAll();
    Admin lookById(Long id);
    PageList<Admin> queryPage(BaseQuery<Admin> query);
    Admin findByUsername(String username);
    void add(Admin admin);
    void update(Admin admin);
    void delete(Long id);
}
