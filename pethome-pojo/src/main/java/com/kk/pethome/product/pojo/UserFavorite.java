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
public class UserFavorite extends BasicPojo {
    private Long userId;
    private Long productId;
    private String category;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
