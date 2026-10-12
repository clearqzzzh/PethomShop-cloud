package com.kk.pethome.pay.mq;

import com.alibaba.fastjson.JSON;
import com.kk.pethome.api.ProductFeignAPI;
import com.kk.pethome.pay.mapper.DeadLetterMapper;
import com.kk.pethome.pay.mapper.PayLogMapper;
import com.kk.pethome.pay.pojo.DeadLetter;
import com.kk.pethome.pay.pojo.PayLog;
import com.kk.pethome.basic.result.jsonresult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 支付成功消息消费者
 * 消费 topic: order_pay_success
 * 重试 3 次后进入死信队列 → 写入 t_mq_dead_letter
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "order_pay_success",
        consumerGroup = "pethome-pay-consumer",
        maxReconsumeTimes = 3
)
public class PaySuccessConsumer implements RocketMQListener<String> {

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private DeadLetterMapper deadLetterMapper;

    @Autowired
    private ProductFeignAPI productFeignAPI;   // Feign 跨服务调用

    @Override
    public void onMessage(String messageBody) {
        log.info("收到支付成功消息: {}", messageBody);
        Map<String, Object> msg = JSON.parseObject(messageBody, Map.class);

        String outTradeNo = (String) msg.get("out_trade_no");
        Long orderId = Long.valueOf(msg.get("order_id").toString());
        BigDecimal amount = new BigDecimal(msg.get("amount").toString());

        // ── 幂等性保证：查 t_pay_log 是否已存在 ──
        PayLog existing = payLogMapper.findByOutTradeNo(outTradeNo);
        if (existing != null && existing.getStatus() == 1) {
            log.info("已处理过此交易，跳过: out_trade_no={}", outTradeNo);
            return;
        }

        try {
            // 1. 插入支付流水
            PayLog payLog = new PayLog();
            payLog.setOrderId(orderId);
            payLog.setOutTradeNo(outTradeNo);
            payLog.setAmount(amount);
            payLog.setStatus(1);
            payLog.setPayTime(new Date());
            payLog.setCreateTime(new Date());
            payLogMapper.insert(payLog);
            log.info("支付流水已记录: out_trade_no={}", outTradeNo);

            // 2. 通过 Feign 调 product-service 更新订单状态 + 扣库存
            jsonresult result = productFeignAPI.updateOrderAndStock(orderId);
            if (!result.getSuccess()) {
                log.error("扣减库存失败: orderId={}, msg={}", orderId, result.getMessage());
                throw new RuntimeException("扣减库存失败: " + result.getMessage());
            }

            log.info("订单处理完成: orderId={}, out_trade_no={}", orderId, outTradeNo);

        } catch (Exception e) {
            log.error("处理支付消息失败: out_trade_no={}, 将重试", outTradeNo, e);
            throw e;  // 抛异常触发 RocketMQ 重试
        }
    }

    /**
     * 死信队列处理：当重试 3 次仍失败时，RocketMQ 自动将消息投递到
     * %DLQ%pethome-pay-consumer 主题。需要另外写一个死信消费者接住它。
     */
    @Component
    @RocketMQMessageListener(
            topic = "%DLQ%pethome-pay-consumer",
            consumerGroup = "pethome-dlq-consumer"
    )
    public class DeadLetterConsumer implements RocketMQListener<String> {
        @Autowired
        private DeadLetterMapper deadLetterMapper;

        @Override
        public void onMessage(String messageBody) {
            log.error("死信消息: {}", messageBody);
            DeadLetter dl = new DeadLetter();
            dl.setTopic("order_pay_success");
            dl.setMessageBody(messageBody);
            dl.setFailReason("重试3次仍失败，进入死信队列");
            dl.setCreateTime(new Date());
            deadLetterMapper.insert(dl);
            log.info("死信已入库，待人工处理");
        }
    }
}
