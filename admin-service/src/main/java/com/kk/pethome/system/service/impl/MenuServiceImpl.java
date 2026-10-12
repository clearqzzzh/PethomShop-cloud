package com.kk.pethome.system.service.impl;

import com.kk.pethome.system.mapper.MenuMapper;
import com.kk.pethome.system.pojo.Menu;
import com.kk.pethome.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> lookAll() { return menuMapper.lookAll(); }

    @Override
    public Menu lookById(Long id) { return menuMapper.lookById(id); }

    @Override
    public List<Menu> queryByEmpId(Long empId) { return menuMapper.queryByEmpId(empId); }

    @Override
    public void Add(Menu menu) { menuMapper.Add(menu); }

    @Override
    public void Update(Menu menu) { menuMapper.Update(menu); }

    @Override
    public void Delete(Long id) { menuMapper.Delete(id); }
}
