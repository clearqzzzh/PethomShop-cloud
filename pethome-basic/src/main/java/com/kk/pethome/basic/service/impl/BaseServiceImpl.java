package com.kk.pethome.basic.service.impl;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.basic.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseServiceImpl<T> implements IBaseService<T> {
@Autowired
BaseMapper<T> baseMapper;

    @Override
    public List<T> lookAll() {
        return baseMapper.lookAll();
    }

    @Override
    public T lookById(Long id) {
        return baseMapper.lookById(id);
    }

    @Override
    public PageList<T> queryPage(BaseQuery<T> baseQuery) {
        //查询总条数
        Integer total = baseMapper.loadcount(baseQuery);
        if (total == 0) {
            return new PageList<T>();
        }
        //查询每页数据
        List<T> data = baseMapper.lookPage(baseQuery);
        //返回结果
        return new PageList<T>(total,data);
    }

    @Override
    public void Add(T t) {
        baseMapper.Add(t);
    }

    @Override
    public void Update(T t) {

        baseMapper.Update(t);
    }



    @Override
    public void Delete(Long id) {
        baseMapper.Delete(id);
    }

    @Override
    public void DeleteBatch(List<Long> ids) {
        baseMapper.DeleteBatch(ids);
    }
}
