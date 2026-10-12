package com.kk.pethome.product.controller;

import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.product.pojo.AiRecommendLog;
import com.kk.pethome.product.pojo.Product;
import com.kk.pethome.product.service.AiRecommendService;
import com.kk.pethome.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/ai")
public class AiRecommendController {
    @Autowired
    private ProductService productService;

    @Autowired
    private AiRecommendService aiRecommendService;

    @Operation(summary = "AI智能推荐商品")
    @PostMapping("/recommend")
    public jsonresult aiRecommend(@RequestBody Map<String, String> request,
                                  HttpSession session) {
        try {
            String keyword = request.get("keyword");
            if (keyword == null || keyword.trim().isEmpty()) {
                return jsonresult.fail().setMessage("请输入搜索关键词");
            }

            String sessionId = session.getId();

            // 1. 搜索商品（默认第一页12条）
            PageList<Product> pageList = productService.searchByKeyword(keyword, null, 1, 12);
            List<Product> products = pageList.getData();

            // 2. 提取商品ID列表
            String productIds = products.stream()
                    .map(p -> String.valueOf(p.getId()))
                    .collect(Collectors.joining(","));

            // 3. 调用 AI Agent 获取推荐语
            AiRecommendLog log = aiRecommendService.recommendProduct(keyword, sessionId, productIds);

            // 4. 查询历史记录
            List<AiRecommendLog> history = aiRecommendService.getHistoryLogs(sessionId, 10);

            // 5. 组装返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("total", pageList.getTotal());
            result.put("aiReply", log.getAiReply());
            result.put("history", history);

            return jsonresult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("AI推荐失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取AI推荐历史记录")
    @GetMapping("/history")
    public jsonresult getHistory(HttpSession session,
                                 @RequestParam(defaultValue = "10") int limit) {
        try {
            String sessionId = session.getId();
            List<AiRecommendLog> history = aiRecommendService.getHistoryLogs(sessionId, limit);
            return jsonresult.success(history);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("查询历史记录失败");
        }
    }
}
