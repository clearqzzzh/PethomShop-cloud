package com.kk.pethome.basic.service;

import com.kk.pethome.basic.query.BaseQuery;
import com.kk.pethome.basic.result.PageList;

import java.util.List;

public interface IBaseService<T> {
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
    /**
     * 分页查询
     * 总条数：total
     * 每页数据：list<Department> data
     */
    PageList<T>queryPage(BaseQuery<T> baseQuery);
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
