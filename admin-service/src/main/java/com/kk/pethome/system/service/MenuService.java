package com.kk.pethome.system.service;

import com.kk.pethome.system.pojo.Menu;
import java.util.List;

public interface MenuService {
    List<Menu> lookAll();
    Menu lookById(Long id);
    List<Menu> queryByEmpId(Long empId);
    void Add(Menu menu);
    void Update(Menu menu);
    void Delete(Long id);
}
