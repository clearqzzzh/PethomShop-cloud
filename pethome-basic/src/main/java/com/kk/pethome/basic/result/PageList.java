package com.kk.pethome.basic.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *封装分页数据
 */
@Data
public class PageList<T> {
    private Integer total = 0;
    private List<T> data = new ArrayList<>();

    public PageList() {}

    public PageList(Integer total, List<T> data) {
        this.total = total;
        this.data = data;
    }
}
