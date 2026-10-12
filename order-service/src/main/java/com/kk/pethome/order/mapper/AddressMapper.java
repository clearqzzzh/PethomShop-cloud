package com.kk.pethome.order.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.order.pojo.UserAddress;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AddressMapper extends BaseMapper<UserAddress> {
    List<UserAddress> findByUserId(@Param("userId") Long userId);
    int setDefault(@Param("userId") Long userId, @Param("id") Long id);
}
