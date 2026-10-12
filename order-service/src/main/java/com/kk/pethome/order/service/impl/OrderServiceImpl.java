package com.kk.pethome.order.service.impl;

import com.kk.pethome.api.CouponFeignAPI;
import com.kk.pethome.api.ProductFeignAPI;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.order.mapper.CartMapper;
import com.kk.pethome.order.mapper.OrderMapper;
import com.kk.pethome.order.pojo.CartItem;
import com.kk.pethome.order.pojo.Order;
import com.kk.pethome.order.pojo.OrderItem;
import com.kk.pethome.product.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductFeignAPI productFeignAPI;
    @Autowired
    private CouponFeignAPI couponFeignAPI;

    /** 从购物车结算——生成订单，清空购物车 */
    @Transactional
    public Order createOrder(Long userId, List<Long> cartItemIds,
                             String recipient, String phone, String address,
                             Long couponId) {
        List<CartItem> cartItems = cartMapper.findByUserId(userId);
        if (cartItems.isEmpty()) throw new RuntimeException("购物车为空");

        // 筛选要结算的商品
        List<CartItem> selected = cartItems;
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            selected = cartItems.stream().filter(c -> cartItemIds.contains(c.getId())).toList();
        }
        if (selected.isEmpty()) throw new RuntimeException("未选择商品");

        // 计算总金额 —— 通过 Feign 从 product-service 获取商品价格
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem c : selected) {
            BigDecimal price = BigDecimal.ZERO;
            String name = "商品#" + c.getProductId();
            try {
                jsonresult result = productFeignAPI.getDetail(c.getProductId());
                if (result != null && result.getSuccess() && result.getData() != null) {
                    // 从 jsonresult 的 data 字段提取 Product
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    Product product = mapper.convertValue(result.getData(), Product.class);
                    if (product != null) {
                        price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
                        name = product.getName() != null ? product.getName() : name;
                    }
                }
            } catch (Exception ignored) {
                // Feign 失败时使用默认值
            }
            total = total.add(price.multiply(BigDecimal.valueOf(c.getQuantity())));
            OrderItem oi = new OrderItem();
            oi.setUserId(userId);
            oi.setProductId(c.getProductId());
            oi.setCategory(c.getCategory());
            oi.setName(name);
            oi.setPrice(price);
            oi.setQuantity(c.getQuantity());
            orderItems.add(oi);
        }

        // 优惠券校验 + 折扣
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponId != null && couponId > 0) {
            try {
                Map<String, Object> checkBody = new HashMap<>();
                checkBody.put("userId", userId);
                checkBody.put("couponId", couponId);
                checkBody.put("orderAmount", total.toString());
                jsonresult checkResult = couponFeignAPI.checkAndReserve(checkBody);
                if (checkResult != null && checkResult.getSuccess()) {
                    Object data = checkResult.getData();
                    if (data instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> checkData = (Map<String, Object>) data;
                        Object discount = checkData.get("discountAmount");
                        if (discount != null) {
                            discountAmount = new BigDecimal(discount.toString());
                        }
                    }
                } else {
                    String msg = checkResult != null ? checkResult.getMessage() : "优惠券校验失败";
                    throw new RuntimeException(msg != null ? msg : "优惠券不可用");
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("优惠券服务异常: " + e.getMessage());
            }
        }

        // 创建订单
        Order order = new Order();
        order.setOrderSn(OrderSnGenerator.generate());
        order.setUserId(userId);
        order.setRecipient(recipient);
        order.setPhone(phone);
        order.setAddress(address);
        order.setTotalAmount(total.subtract(discountAmount));
        order.setStatus(0);
        order.setCreateTime(new Date());
        order.setCouponId(couponId != null && couponId > 0 ? couponId : null);
        order.setDiscountAmount(discountAmount);
        orderMapper.insertOrder(order);

        for (OrderItem oi : orderItems) oi.setOrderId(order.getId());
        orderMapper.insertItems(orderItems);

        // 清空已购商品
        List<Long> delIds = selected.stream().map(CartItem::getId).toList();
        cartMapper.deleteByIds(delIds);

        order.setItems(orderItems);
        return order;
    }

    public List<Order> getOrders(Long userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        for (Order o : orders) {
            o.setItems(orderMapper.findItems(o.getId()));
        }
        return orders;
    }

    public Order getOrder(Long orderId) {
        Order o = orderMapper.lookById(orderId);
        if (o != null) o.setItems(orderMapper.findItems(orderId));
        return o;
    }

    /** Agent 售后场景：查询用户最近N笔订单（含明细） */
    public List<Order> getRecentOrders(Long userId, int limit) {
        List<Order> orders = orderMapper.findRecentOrders(userId, limit);
        for (Order o : orders) {
            o.setItems(orderMapper.findItems(o.getId()));
        }
        return orders;
    }
}
