package com.kk.pethome.product.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kk.pethome.product.mapper.AiRecommendLogMapper;
import com.kk.pethome.product.pojo.AiRecommendLog;
import com.kk.pethome.product.service.AiRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiRecommendServiceImpl implements AiRecommendService {
    @Autowired
    private AiRecommendLogMapper aiRecommendLogMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.agent.url:http://localhost:8000/chat}")
    private String aiAgentUrl;

    @Value("${ai.agent.timeout:30000}")
    private int aiAgentTimeout;

    @Override
    public AiRecommendLog recommendProduct(String keyword, String sessionId, String productIds) {
        // 调用 AI Agent 获取推荐语
        String aiReply = callAiAgent(keyword);

        // 构建日志记录
        AiRecommendLog log = new AiRecommendLog();
        log.setSessionId(sessionId);
        log.setKeyword(keyword);
        log.setProductIds(productIds);
        log.setAiReply(aiReply);
        log.setCreateTime(new Date());

        // 存入数据库
        aiRecommendLogMapper.Add(log);
        return log;
    }

    @Override
    public List<AiRecommendLog> getHistoryLogs(String sessionId, int limit) {
        return aiRecommendLogMapper.findBySessionId(sessionId, limit);
    }

    @Override
    public String callAiAgent(String keyword) {
        try {
            // 构建请求体：{"messages": [{"role": "user", "content": "推荐..."}]}
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", "请帮我推荐宠物商品，用户搜索了关键词：" + keyword + "，请根据搜索结果给出专业的推荐建议。");
            messages.add(userMessage);
            requestBody.put("messages", messages);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(requestBody), headers);

            // 发送POST请求到 FastAPI Agent
            ResponseEntity<String> response = restTemplate.exchange(
                    aiAgentUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JSONObject result = JSON.parseObject(response.getBody());
                // 常见返回格式：{"reply": "...", "code": 200} 或 {"data": {"reply": "..."}}
                if (result.containsKey("reply")) {
                    return result.getString("reply");
                } else if (result.containsKey("data")) {
                    JSONObject data = result.getJSONObject("data");
                    return data != null && data.containsKey("reply") ? data.getString("reply") : null;
                }
                return result.toJSONString();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "AI助手暂时无法连接，已为您展示搜索结果中的热门商品。";
        }
    }
}
