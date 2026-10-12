package com.kk.pethome.basic.mapper;

import com.kk.pethome.basic.query.BaseQuery;


import java.util.List;


public interface BaseMapper<T> {
    /**
     * 查询所有部门
     * @return 部门列表
     */
    List<T> lookAll();
    /**
     * 根據id查詢
     */
    T lookById(Long id);
    /**
     * 查詢總條數
     */
    Integer loadcount(BaseQuery<T> baseQuery);
    /**
     * 查詢每页数据
     * @return 部门列表
     */
    List<T> lookPage(BaseQuery<T> baseQuery);
    /**
     * 新增部门
     */
    void Add(T t);
    /**
     * 更新部门
     */
    void Update(T t);
    /**
     * 删除部门
     */
    void Delete(Long id);
    /**
     * 批量删除部门
     */
    void DeleteBatch(List<Long> ids);
}
