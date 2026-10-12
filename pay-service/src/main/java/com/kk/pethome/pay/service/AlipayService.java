package com.kk.pethome.pay.service;

import java.math.BigDecimal;

public interface AlipayService {

    /**
     * 创建支付订单，生成支付宝支付页面 HTML
     * @param orderId   商户订单号
     * @param amount    支付金额
     * @param subject   商品标题
     * @return 支付页面 HTML
     */
    String createPayOrder(String orderId, BigDecimal amount, String subject);

    /**
     * 验证支付宝异步通知签名
     * @param params 支付宝 POST 过来的参数 Map
     * @return true-签名验证通过
     */
    boolean verifyCallback(java.util.Map<String, String> params);
}
