package com.kk.pethome.order.mapper;

import com.kk.pethome.order.pojo.RefundLog;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RefundLogMapper {
    int insert(RefundLog log);
    int updateStatus(@Param("id") Long id, @Param("status") int status, @Param("adminRemark") String adminRemark);
    RefundLog findById(@Param("id") Long id);
    RefundLog findByOrderId(@Param("orderId") Long orderId);
    List<RefundLog> findByUserId(@Param("userId") Long userId);
    List<RefundLog> findAll();
}
