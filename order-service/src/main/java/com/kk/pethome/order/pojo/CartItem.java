package com.kk.pethome.order.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BasicPojo {
    private Long userId;
    private Long productId;
    private String category;
    private Integer quantity = 1;
    private Date createTime;
    // 冗余关联字段（join 查回来的，不存库）
    private String productName;
    private BigDecimal productPrice;
    private String productImage;
}
