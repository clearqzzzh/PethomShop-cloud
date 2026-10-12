package com.kk.pethome.org.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.kk.pethome.basic.pojo.BasicPojo;
import com.kk.pethome.basic.query.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department extends BasicPojo {
    private String sn;
    private String name;
    private String dirPath;
    private Integer state;
    private Long managerId;
    private Long parentId;
    //通过managerId查询employee
    private Employee manager;
    //通过parentId查询department
    private Department parent;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Department> children = new ArrayList<>();



}
