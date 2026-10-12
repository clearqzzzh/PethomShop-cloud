package com.kk.pethome.order.pojo;

import com.kk.pethome.basic.pojo.BasicPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BasicPojo {
    private Long orderId;
    private Long userId;
    private Long productId;
    private String category;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
