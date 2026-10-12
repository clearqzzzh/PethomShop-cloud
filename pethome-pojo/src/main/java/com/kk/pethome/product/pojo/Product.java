package com.kk.pethome.product.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BasicPojo {
    // 通用字段
    private String name;
    private String brand;
    private BigDecimal price;
    private String weight;
    private String features;
    private Integer stock = 0;
    private String category;
    private String imageUrl;
    private BigDecimal rating = BigDecimal.ZERO;

    // 猫粮 / 狗粮 特有
    private String suitableFor;    // 适用阶段/品种
    private Boolean grainFree;     // 是否无谷

    // 狗粮 特有
    private String dogSize;        // 适用体型

    // 零食 特有
    private String snackType;      // 冻干/磨牙棒/罐头/肉干/奶酪
    private String suitablePet;    // 猫/狗/通用

    // 用品 特有
    private String supplyType;     // 猫砂盆/饮水机/玩具/狗窝/外出包

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
