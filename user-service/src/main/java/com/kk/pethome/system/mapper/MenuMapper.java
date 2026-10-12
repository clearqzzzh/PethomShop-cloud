package com.kk.pethome.system.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.system.pojo.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    List<Menu> queryByEmpId(Long id);
}
