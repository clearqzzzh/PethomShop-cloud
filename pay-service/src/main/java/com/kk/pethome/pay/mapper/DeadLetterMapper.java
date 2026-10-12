package com.kk.pethome.pay.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.pay.pojo.DeadLetter;

public interface DeadLetterMapper extends BaseMapper<DeadLetter> {

    /** 插入死信记录 */
    int insert(DeadLetter deadLetter);
}
