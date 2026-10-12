package com.kk.pethome.order.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单号生成器
 * ============
 * 格式：PH + yyyyMMddHHmmss + 4位随机数
 * 示例：PH202609171430220001
 * 长度：20 位，全局唯一（时间戳 + 随机数，并发安全）
 */
public final class OrderSnGenerator {

    private static final String PREFIX = "PH";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OrderSnGenerator() {}

    /**
     * 生成订单号
     * @return 20 位订单号，如 PH202609171430220001
     */
    public static String generate() {
        String timestamp = LocalDateTime.now().format(FMT);
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return PREFIX + timestamp + random;
    }
}
