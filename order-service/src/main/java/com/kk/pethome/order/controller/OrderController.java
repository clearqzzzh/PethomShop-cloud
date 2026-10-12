package com.kk.pethome.order.controller;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.order.mapper.OrderMapper;
import com.kk.pethome.order.pojo.Order;
import com.kk.pethome.order.service.impl.DirectOrderService;
import com.kk.pethome.order.service.impl.LogisticsService;
import com.kk.pethome.order.service.impl.OrderServiceImpl;
import com.kk.pethome.order.service.impl.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private DirectOrderService directOrderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private RefundService refundService;

    @PostMapping("/create")
    @Operation(summary = "从购物车创建订单")
    public jsonresult create(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String recipient = (String) body.getOrDefault("recipient", "收货人");
        String phone = (String) body.getOrDefault("phone", "13800000000");
        String address = (String) body.getOrDefault("address", "请填写地址");
        List<Long> cartItemIds = body.containsKey("cartItemIds") ?
            ((List<?>) body.get("cartItemIds")).stream().map(o -> Long.valueOf(o.toString())).toList() : null;
        Long couponId = body.containsKey("couponId") && body.get("couponId") != null
            ? Long.valueOf(body.get("couponId").toString()) : null;
        try {
            Order order = orderService.createOrder(userId, cartItemIds, recipient, phone, address, couponId);
            return jsonresult.success(order);
        } catch (RuntimeException e) {
            return jsonresult.fail().setMessage(e.getMessage());
        }
    }

    @GetMapping
    public jsonresult getOrders(@RequestParam Long userId) {
        return jsonresult.success(orderService.getOrders(userId));
    }

    /** Agent 售后场景：查询用户最近N笔订单 */
    @GetMapping("/list")
    @Operation(summary = "查询用户最近订单（Agent专用）")
    public jsonresult getRecentOrders(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        return jsonresult.success(orderService.getRecentOrders(userId, limit));
    }

    /** 管理端：查询所有订单（分页） */
    @GetMapping("/admin/list")
    @Operation(summary = "管理端查询所有订单")
    public jsonresult adminListOrders(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        List<Order> allOrders = orderMapper.lookAll();
        // 按状态筛选
        if (status != null) {
            allOrders = allOrders.stream()
                    .filter(o -> o.getStatus() != null && o.getStatus().equals(status))
                    .toList();
        }
        // 按创建时间倒序
        allOrders.sort((a, b) -> {
            if (b.getCreateTime() == null || a.getCreateTime() == null) return 0;
            return b.getCreateTime().compareTo(a.getCreateTime());
        });
        // 分页
        int total = allOrders.size();
        int from = (currentPage - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Order> pageOrders = (from >= total) ? List.of() : allOrders.subList(from, to);
        // 填充订单明细
        for (Order o : pageOrders) {
            o.setItems(orderMapper.findItems(o.getId()));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", pageOrders);
        result.put("total", total);
        return jsonresult.success(result);
    }

    @GetMapping("/{id}")
    public jsonresult getOrder(@PathVariable Long id) {
        return jsonresult.success(orderService.getOrder(id));
    }

    /** 根据订单号查询订单 */
    @GetMapping("/sn/{orderSn}")
    @Operation(summary = "根据订单号查询订单")
    public jsonresult getOrderBySn(@PathVariable String orderSn) {
        com.kk.pethome.order.pojo.Order order = orderMapper.findByOrderSn(orderSn);
        if (order == null) {
            return jsonresult.fail().setMessage("订单号不存在");
        }
        order.setItems(orderMapper.findItems(order.getId()));
        return jsonresult.success(order);
    }

    /** Agent 工具直接下单（不走购物车） */
    @PostMapping("/direct")
    @Operation(summary = "Agent直接下单")
    public jsonresult createDirect(@RequestBody Map<String, Object> body) {
        try {
            Order order = directOrderService.createDirect(body);
            return jsonresult.success(order);
        } catch (Exception e) {
            return jsonresult.fail().setMessage(e.getMessage());
        }
    }

    /** 支付确认——更新订单状态为已支付 + 自动模拟发货 */
    @PutMapping("/{id}/pay")
    @Operation(summary = "更新订单为已支付（自动模拟发货）")
    public jsonresult payOrder(@PathVariable Long id) {
        Date now = new Date();
        orderMapper.updateStatus(id, 1, now);
        // 自动模拟发货：支付后30分钟"发货"，生成物流单号
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, 30);
        Date shipTime = cal.getTime();
        String trackingNo = logisticsService.generateTrackingNo(id);
        orderMapper.shipOrder(id, trackingNo, shipTime);
        return jsonresult.success(Map.of("orderId", id, "status", "shipped", "trackingNo", trackingNo));
    }

    /** 查询物流轨迹 */
    @GetMapping("/logistics/{id}")
    @Operation(summary = "查询订单物流轨迹")
    public jsonresult getLogistics(@PathVariable Long id) {
        Order order = orderMapper.lookById(id);
        if (order == null) {
            return jsonresult.fail().setMessage("订单不存在");
        }
        List<Map<String, Object>> timeline = logisticsService.getLogisticsTimeline(order);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", id);
        result.put("trackingNo", order.getTrackingNo());
        result.put("courier", order.getTrackingNo() != null ? logisticsService.getCourierName(order.getTrackingNo()) : null);
        result.put("status", order.getStatus());
        result.put("statusText", statusText(order.getStatus()));
        result.put("recipient", order.getRecipient());
        result.put("phone", order.getPhone());
        result.put("address", order.getAddress());
        result.put("timeline", timeline);
        return jsonresult.success(result);
    }

    /** 确认收货 */
    @PutMapping("/{id}/receive")
    @Operation(summary = "确认收货")
    public jsonresult confirmReceive(@PathVariable Long id) {
        int rows = orderMapper.receiveOrder(id, new Date());
        if (rows > 0) {
            return jsonresult.success("ok");
        }
        return jsonresult.fail().setMessage("订单状态不允许确认收货");
    }

    /** Agent 售后场景：申请退款（旧接口，兼容Agent原有工具） */
    @PostMapping("/{id}/refund")
    @Operation(summary = "申请退款（一键退货）")
    public jsonresult requestRefund(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "用户申请退款");
        // 旧接口兼容：直接走一键退货流程
        // 从订单获取 userId
        Order order = orderMapper.lookById(id);
        if (order == null) {
            return jsonresult.fail().setMessage("订单不存在");
        }
        Map<String, Object> result = refundService.oneClickRefund(id, order.getUserId(), reason);
        if ((Boolean) result.get("success")) {
            return jsonresult.success(result);
        }
        return jsonresult.fail().setMessage((String) result.get("message"));
    }

    /** 检查退货资格 */
    @GetMapping("/{id}/return/check")
    @Operation(summary = "检查退货资格")
    public jsonresult checkReturnEligibility(@PathVariable Long id) {
        return jsonresult.success(refundService.checkEligibility(id));
    }

    /** 申请退货 */
    @PostMapping("/{id}/return/apply")
    @Operation(summary = "申请退货")
    public jsonresult applyForReturn(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String reason = (String) body.getOrDefault("reason", "用户申请退货");
        return jsonresult.success(refundService.applyForReturn(id, userId, reason));
    }

    /** 审核通过退货 */
    @PutMapping("/{id}/return/approve")
    @Operation(summary = "审核通过退货")
    public jsonresult approveReturn(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String remark = body != null ? body.getOrDefault("remark", "审核通过") : "审核通过";
        return jsonresult.success(refundService.approveReturn(id, remark));
    }

    /** 审核拒绝退货 */
    @PutMapping("/{id}/return/reject")
    @Operation(summary = "审核拒绝退货")
    public jsonresult rejectReturn(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String remark = body != null ? body.getOrDefault("remark", "审核不通过") : "审核不通过";
        return jsonresult.success(refundService.rejectReturn(id, remark));
    }

    /** 完成退款 */
    @PutMapping("/{id}/return/complete")
    @Operation(summary = "完成退款")
    public jsonresult completeRefund(@PathVariable Long id) {
        return jsonresult.success(refundService.completeRefund(id));
    }

    /** 查询退货信息 */
    @GetMapping("/{id}/return/info")
    @Operation(summary = "查询退货信息")
    public jsonresult getReturnInfo(@PathVariable Long id) {
        return jsonresult.success(refundService.getRefundInfo(id));
    }

    /** Agent 一键退货（申请+审核+退款一步到位） */
    @PostMapping("/{id}/return/oneclick")
    @Operation(summary = "一键退货（Agent专用）")
    public jsonresult oneClickReturn(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String reason = (String) body.getOrDefault("reason", "用户申请退货");
        return jsonresult.success(refundService.oneClickRefund(id, userId, reason));
    }

    /** 手动触发发货（管理端/测试用） */
    @PutMapping("/{id}/ship")
    @Operation(summary = "手动触发发货")
    public jsonresult manualShip(@PathVariable Long id) {
        Order order = orderMapper.lookById(id);
        if (order == null) {
            return jsonresult.fail().setMessage("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() < 1) {
            return jsonresult.fail().setMessage("订单未支付，无法发货");
        }
        if (order.getStatus() >= 2) {
            return jsonresult.fail().setMessage("订单已发货");
        }
        String trackingNo = logisticsService.generateTrackingNo(id);
        orderMapper.shipOrder(id, trackingNo, new Date());
        return jsonresult.success(Map.of("trackingNo", trackingNo));
    }

    /** 管理端：修改订单状态（发货/退款等） */
    @PutMapping("/admin/{id}/status")
    @Operation(summary = "管理员修改订单状态")
    public jsonresult adminUpdateStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer newStatus = (Integer) body.get("status");
        if (newStatus == null) {
            return jsonresult.fail().setMessage("状态不能为空");
        }
        Order order = orderMapper.lookById(id);
        if (order == null) {
            return jsonresult.fail().setMessage("订单不存在");
        }
        switch (newStatus) {
            case 2 -> { // 发货
                if (order.getStatus() == null || order.getStatus() < 1) {
                    return jsonresult.fail().setMessage("订单未支付，无法发货");
                }
                String trackingNo = logisticsService.generateTrackingNo(id);
                orderMapper.shipOrder(id, trackingNo, new Date());
                return jsonresult.success(Map.of("trackingNo", trackingNo, "status", 2));
            }
            case 3 -> { // 确认收货
                int rows = orderMapper.receiveOrder(id, new Date());
                if (rows > 0) return jsonresult.success(Map.of("status", 3));
                return jsonresult.fail().setMessage("状态不允许确认收货");
            }
            case 5 -> { // 退款
                if (order.getStatus() == null || order.getStatus() < 1 || order.getStatus() >= 4) {
                    return jsonresult.fail().setMessage("订单状态不允许退款");
                }
                int rows = orderMapper.refundOrder(id);
                if (rows > 0) return jsonresult.success(Map.of("status", 5));
                return jsonresult.fail().setMessage("退款失败");
            }
            default -> { return jsonresult.fail().setMessage("不支持的状态值"); }
        }
    }

    /** 管理端：订单统计 */
    @GetMapping("/admin/stats")
    @Operation(summary = "管理端订单统计")
    public jsonresult adminOrderStats() {
        List<Order> all = orderMapper.lookAll();
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalOrders", all.size());

        int pending = 0, paid = 0, shipped = 0, received = 0, refunded = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal todayRevenue = BigDecimal.ZERO;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        for (Order o : all) {
            Integer s = o.getStatus();
            if (s == null) continue;
            switch (s) {
                case 0 -> pending++;
                case 1 -> paid++;
                case 2 -> shipped++;
                case 3 -> received++;
                case 4 -> refunded++;
            }
            if (s >= 1 && s <= 3 && o.getTotalAmount() != null) {
                totalRevenue = totalRevenue.add(o.getTotalAmount());
                if (o.getCreateTime() != null && sdf.format(o.getCreateTime()).equals(today)) {
                    todayRevenue = todayRevenue.add(o.getTotalAmount());
                }
            }
        }

        Map<String, Integer> statusDist = new LinkedHashMap<>();
        statusDist.put("pending", pending);
        statusDist.put("paid", paid);
        statusDist.put("shipped", shipped);
        statusDist.put("received", received);
        statusDist.put("refunded", refunded);

        // 按日期统计最近7天订单量
        Map<String, Integer> dailyOrders = new LinkedHashMap<>();
        java.text.SimpleDateFormat dayFmt = new java.text.SimpleDateFormat("MM-dd");
        Calendar cal = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            dailyOrders.put(dayFmt.format(cal.getTime()), 0);
        }
        for (Order o : all) {
            if (o.getCreateTime() != null) {
                String day = dayFmt.format(o.getCreateTime());
                if (dailyOrders.containsKey(day)) {
                    dailyOrders.merge(day, 1, Integer::sum);
                }
            }
        }

        stats.put("totalRevenue", totalRevenue);
        stats.put("todayRevenue", todayRevenue);
        stats.put("statusDistribution", statusDist);
        stats.put("dailyOrders", dailyOrders);
        return jsonresult.success(stats);
    }

    private String statusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待付款";
            case 1 -> "待发货";
            case 2 -> "运输中";
            case 3 -> "已到货";
            case 4 -> "退货中";
            case 5 -> "已退款";
            default -> "未知";
        };
    }

    @GetMapping("/stats")
    @Operation(summary = "用户消费统计")
    public jsonresult getOrderStats(@RequestParam Long userId) {
        List<Order> orders = orderService.getOrders(userId);
        Map<String, Object> stats = new LinkedHashMap<>();
        BigDecimal totalSpent = BigDecimal.ZERO;
        int totalOrders = orders.size();
        int paidOrders = 0;
        Map<String, BigDecimal> categoryStats = new LinkedHashMap<>();

        for (Order o : orders) {
            if (o.getStatus() != null && o.getStatus() >= 1) {
                paidOrders++;
                if (o.getTotalAmount() != null) {
                    totalSpent = totalSpent.add(o.getTotalAmount());
                }
            }
            // Category breakdown from order items
            if (o.getItems() != null) {
                for (com.kk.pethome.order.pojo.OrderItem item : o.getItems()) {
                    if (o.getStatus() != null && o.getStatus() >= 1) {
                        String cat = item.getCategory() != null ? item.getCategory() : "其他";
                        BigDecimal existing = categoryStats.getOrDefault(cat, BigDecimal.ZERO);
                        categoryStats.put(cat, existing.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
                    }
                }
            }
        }

        stats.put("totalSpent", totalSpent);
        stats.put("totalOrders", totalOrders);
        stats.put("paidOrders", paidOrders);
        stats.put("categoryStats", categoryStats);

        // Monthly spending (last 6 months)
        Map<String, BigDecimal> monthlyStats = new LinkedHashMap<>();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        for (Order o : orders) {
            if (o.getStatus() != null && o.getStatus() >= 1 && o.getCreateTime() != null) {
                String month = sdf.format(o.getCreateTime());
                BigDecimal existing = monthlyStats.getOrDefault(month, BigDecimal.ZERO);
                monthlyStats.put(month, existing.add(o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO));
            }
        }
        stats.put("monthlyStats", monthlyStats);

        return jsonresult.success(stats);
    }
}
