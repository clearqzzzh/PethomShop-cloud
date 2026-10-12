package com.kk.pethome.order.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.order.pojo.Order;
import com.kk.pethome.order.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {
    /** 插入订单 */
    int insertOrder(Order order);
    /** 批量插入订单明细 */
    int insertItems(@Param("items") List<OrderItem> items);
    /** 查用户订单列表 */
    List<Order> findByUserId(@Param("userId") Long userId);
    /** 查用户最近N笔订单（Agent 售后场景） */
    List<Order> findRecentOrders(@Param("userId") Long userId, @Param("limit") int limit);
    /** 查订单明细 */
    List<OrderItem> findItems(@Param("orderId") Long orderId);
    /** 更新订单状态 */
    int updateStatus(@Param("id") Long id, @Param("status") int status, @Param("payTime") java.util.Date payTime);
    /** 发货：更新状态为已发货 + 物流单号 + 发货时间 */
    int shipOrder(@Param("id") Long id, @Param("trackingNo") String trackingNo, @Param("shipTime") java.util.Date shipTime);
    /** 确认收货：更新状态为已到货 + 签收时间 */
    int receiveOrder(@Param("id") Long id, @Param("receiveTime") java.util.Date receiveTime);
    /** 退款完成：更新状态为已退款(status=5)，refund_status=4 */
    int refundOrder(@Param("id") Long id);
    /** 发起退货：更新状态为退货中(status=4) */
    int returnOrder(@Param("id") Long id);
    /** 根据订单号查询订单 */
    Order findByOrderSn(@Param("orderSn") String orderSn);
    /** 更新退货状态 */
    int updateRefundStatus(@Param("id") Long id, @Param("refundStatus") int refundStatus);
}
