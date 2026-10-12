package com.kk.pethome.basic.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseQuery<T> {
    //
    //当前页码
    private Integer currentPage=1;
    //每页条数
    private Integer pageSize=5;
    //查询参数
    private T params;
    //查询起始索引
    public Integer getCurrentPage() {
        return (currentPage-1)* this.pageSize;
    }
}
