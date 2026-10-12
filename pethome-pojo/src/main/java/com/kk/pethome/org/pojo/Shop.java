package com.kk.pethome.org.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shop extends BasicPojo {
    @Excel(name = "店铺名字" ,orderNum = "1", width = 30, isImportField = "true_st")
    private String name;
    @Excel(name = "联系电话" ,orderNum = "2", width = 30, isImportField = "true_st")
    private String tel;
    @Excel(name = "创建时间", orderNum = "3", width = 30, isImportField = "true_st")
    private Date registerTime = new Date();
    @Excel(name = "状态" ,orderNum = "4", width = 30, isImportField = "true_st")
    private Integer state = 1;
    @Excel(name = "地址" ,orderNum = "5", width = 30, isImportField = "true_st")
    private String address;
    @Excel(name = "店铺图片" ,orderNum = "6", width = 30, isImportField = "true_st")
    private String logo;
    private Long adminId;
    private Employee admin;
    public Long getAdminId() {
        return this.adminId;
    }
}
