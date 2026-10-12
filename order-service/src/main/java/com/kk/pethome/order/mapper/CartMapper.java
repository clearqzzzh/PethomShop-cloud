package com.kk.pethome.order.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.order.pojo.CartItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CartMapper extends BaseMapper<CartItem> {
    /** 查某用户的购物车（含商品信息） */
    List<CartItem> findByUserId(@Param("userId") Long userId);
    /** 按用户+商品查是否已存在 */
    CartItem findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
    /** 更新数量 */
    int updateQuantity(@Param("id") Long id, @Param("quantity") int quantity);
    /** 批量删除 */
    int deleteByIds(@Param("ids") List<Long> ids);
    /** 清空某用户购物车 */
    int clearByUserId(@Param("userId") Long userId);
}
