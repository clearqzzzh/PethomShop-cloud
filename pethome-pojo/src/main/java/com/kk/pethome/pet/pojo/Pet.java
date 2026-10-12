package com.kk.pethome.pet.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kk.pethome.basic.pojo.BasicPojo;
import com.kk.pethome.org.pojo.Shop;
import com.kk.pethome.user.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet extends BasicPojo {
    private String name;
    private String resources;
    private BigDecimal saleprice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date onsaletime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date offsaletime;
    private Integer state=0;
    private BigDecimal costprice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createtime=new Date();
    private Long typeId;
    private Long shopId;
    private Long userId;
    private Long search_master_msgId;
    private PetType type;
    private Shop shop;
    private User user;
}
