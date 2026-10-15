package com.kk.pethome.order.service.impl;

import com.kk.pethome.order.pojo.Order;
import com.kk.pethome.order.service.LogisticsService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 模拟物流服务 —— 根据订单状态和时间节点生成物流轨迹。
 * <p>
 * 设计思路：不单独建表存物流事件，而是根据 payTime / shipTime / receiveTime
 * 动态计算时间线，减少数据库压力，也方便演示。
 * <p>
 * 状态流转：0=待付款 → 1=待发货 → 2=运输中 → 3=已到货
 */
@Service
public class LogisticsServiceImpl implements LogisticsService {

    private static final String[] COURIER_COMPANIES = {"爪爪快递", "萌宠物流", "宠物达达", "闪送宠物专递"};
    private static final String[] CITIES = {"上海", "杭州", "南京", "苏州", "合肥", "武汉", "长沙", "广州", "深圳", "北京"};
    private static final Random RAND = new Random();

    /**
     * 生成模拟物流轨迹。
     *
     * @param order 订单（需含 payTime / shipTime / receiveTime / address）
     * @return 物流事件列表（按时间正序），若未发货返回空列表
     */
    @Override
    public List<Map<String, Object>> getLogisticsTimeline(Order order) {
        List<Map<String, Object>> events = new ArrayList<>();
        if (order == null || order.getShipTime() == null) {
            return events;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String trackingNo = order.getTrackingNo() != null ? order.getTrackingNo() : "未知";
        String courier = pickCourier(trackingNo);

        // 解析收货城市
        String destCity = extractCity(order.getAddress());

        // 发货城市（根据单号哈希选一个不同于目的城市的城市）
        String originCity = pickOriginCity(destCity, trackingNo);

        Date shipTime = order.getShipTime();
        Date createTime = order.getCreateTime() != null ? order.getCreateTime() : new Date(shipTime.getTime() - 86400000L);

        // 1. 下单成功
        events.add(event(sdf.format(createTime), "订单已提交，等待卖家发货", courier, originCity));

        // 2. 支付成功
        if (order.getPayTime() != null) {
            events.add(event(sdf.format(order.getPayTime()), "买家已付款，卖家正在备货", courier, originCity));
        }

        // 3. 卖家发货
        events.add(event(sdf.format(shipTime),
                "卖家已发货，【" + courier + "】已揽件（" + originCity + "转运中心）", courier, originCity));

        // 4. 运输中转（根据当前时间和发货时间动态生成）
        Date now = new Date();
        long shipMs = shipTime.getTime();
        long elapsed = now.getTime() - shipMs;

        if (elapsed > 2 * 3600_000L) {
            // 2小时后：离开发货城市
            Date t1 = new Date(shipMs + 2 * 3600_000L);
            events.add(event(sdf.format(t1),
                    "快件已离开【" + originCity + "】，下一站 " + destCity, courier, originCity));
        }

        if (elapsed > 12 * 3600_000L) {
            // 12小时后：到达中转
            Date t2 = new Date(shipMs + 12 * 3600_000L);
            events.add(event(sdf.format(t2),
                    "快件已到达【" + destCity + "分拨中心】", courier, destCity));
        }

        if (elapsed > 18 * 3600_000L) {
            // 18小时后：派送中
            Date t3 = new Date(shipMs + 18 * 3600_000L);
            events.add(event(sdf.format(t3),
                    "快件正在派送中，快递员（" + fakePhone() + "）正在为您配送", courier, destCity));
        }

        // 5. 已签收
        if (order.getStatus() != null && order.getStatus() >= 3 && order.getReceiveTime() != null) {
            events.add(event(sdf.format(order.getReceiveTime()),
                    "已签收，签收人：本人签收。感谢使用" + courier + "！", courier, destCity));
        } else if (elapsed > 24 * 3600_000L) {
            // 超过24小时自动模拟签收
            Date t4 = new Date(shipMs + 24 * 3600_000L);
            events.add(event(sdf.format(t4),
                    "已签收（超时自动确认），签收人：本人签收。感谢使用" + courier + "！", courier, destCity));
        }

        // 按时间倒序（最新的在前）
        Collections.reverse(events);
        return events;
    }

    /**
     * 根据订单ID生成模拟物流单号。
     * 格式：SF + 12位数字
     */
    @Override
    public String generateTrackingNo(Long orderId) {
        String base = String.format("%08d", orderId);
        String rand = String.format("%04d", RAND.nextInt(10000));
        return "PF" + base + rand;  // PF = PetHome
    }

    /**
     * 获取物流公司名。
     */
    @Override
    public String getCourierName(String trackingNo) {
        return pickCourier(trackingNo);
    }

    // ========== 私有工具方法 ==========

    private Map<String, Object> event(String time, String desc, String courier, String city) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("time", time);
        e.put("description", desc);
        e.put("courier", courier);
        e.put("city", city);
        return e;
    }

    private String pickCourier(String trackingNo) {
        int idx = Math.abs(trackingNo.hashCode()) % COURIER_COMPANIES.length;
        return COURIER_COMPANIES[idx];
    }

    private String pickOriginCity(String destCity, String trackingNo) {
        for (String c : CITIES) {
            if (!c.equals(destCity)) {
                int idx = Math.abs(trackingNo.hashCode() + c.hashCode()) % CITIES.length;
                return CITIES[idx];
            }
        }
        return CITIES[0];
    }

    private String extractCity(String address) {
        if (address == null || address.isEmpty()) return "上海";
        // 尝试从地址中提取城市名
        for (String c : CITIES) {
            if (address.contains(c)) return c;
        }
        // 尝试匹配"XX市"
        int idx = address.indexOf("市");
        if (idx > 0) {
            int start = Math.max(0, idx - 3);
            String sub = address.substring(start, idx);
            // 取最后2个字符作为城市名
            if (sub.length() >= 2) return sub.substring(sub.length() - 2) + "市";
        }
        return "上海";
    }

    private String fakePhone() {
        return "1" + (3 + RAND.nextInt(7)) + String.format("%09d", RAND.nextInt(1_000_000_000));
    }
}
