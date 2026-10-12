package com.kk.pethome.pay.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.pay.pojo.PayLog;
import org.apache.ibatis.annotations.Param;

public interface PayLogMapper extends BaseMapper<PayLog> {

    /** 根据支付宝交易号查询（幂等性校验） */
    PayLog findByOutTradeNo(@Param("outTradeNo") String outTradeNo);

    /** 插入支付流水 */
    int insert(PayLog payLog);

    /** 更新支付状态 */
    int updateStatus(@Param("outTradeNo") String outTradeNo,
                     @Param("status") Integer status,
                     @Param("payTime") java.util.Date payTime);
}
