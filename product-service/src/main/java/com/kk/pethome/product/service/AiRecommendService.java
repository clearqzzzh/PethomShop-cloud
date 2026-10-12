package com.kk.pethome.product.service;

import com.kk.pethome.product.pojo.AiRecommendLog;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AiRecommendService {
    /**
     * 调用AI Agent获取推荐语，并记录日志
     * @param keyword   搜索关键词
     * @param sessionId 会话ID
     * @param productIds 推荐的商品ID列表（逗号分隔）
     * @return AI推荐日志记录
     */
    AiRecommendLog recommendProduct(String keyword, String sessionId, String productIds);

    /**
     * 查询会话的历史推荐记录
     * @param sessionId 会话ID
     * @param limit     返回条数
     * @return 历史记录列表
     */
    List<AiRecommendLog> getHistoryLogs(String sessionId, int limit);

    /**
     * 调用 FastAPI AI Agent 获取推荐语
     * @param keyword 搜索关键词
     * @return AI返回的推荐语，失败返回null
     */
    String callAiAgent(String keyword);
}
