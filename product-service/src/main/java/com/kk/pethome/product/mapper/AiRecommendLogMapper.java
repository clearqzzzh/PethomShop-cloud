package com.kk.pethome.product.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.product.pojo.AiRecommendLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiRecommendLogMapper extends BaseMapper<AiRecommendLog> {
    /**
     * 根据会话ID查询历史推荐记录
     * @param sessionId 会话ID
     * @param limit     返回条数
     * @return 历史推荐记录列表
     */
    List<AiRecommendLog> findBySessionId(@Param("sessionId") String sessionId,
                                         @Param("limit") int limit);
}
