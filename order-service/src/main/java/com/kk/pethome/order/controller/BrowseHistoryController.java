package com.kk.pethome.order.controller;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.order.mapper.BrowseHistoryMapper;
import com.kk.pethome.order.pojo.BrowseHistory;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/history")
public class BrowseHistoryController {

    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;

    @GetMapping
    @Operation(summary = "查询浏览历史")
    public jsonresult list(@RequestParam Long userId,
                           @RequestParam(defaultValue = "50") int limit) {
        List<BrowseHistory> list = browseHistoryMapper.findByUserId(userId, limit);
        return jsonresult.success(list);
    }

    @PostMapping
    @Operation(summary = "记录浏览事件")
    public jsonresult add(@RequestBody BrowseHistory h) {
        // 先删除同用户同商品的旧记录，避免重复
        browseHistoryMapper.deleteByUserAndProduct(h.getUserId(), h.getProductId());
        h.setBrowseTime(new Date());
        browseHistoryMapper.insert(h);
        return jsonresult.success(h);
    }

    @DeleteMapping
    @Operation(summary = "清空用户浏览历史")
    public jsonresult clear(@RequestParam Long userId) {
        // 删除该用户所有浏览记录（通过设置极大 limit 或直接全表删）
        // 这里用 findByUserId 拿到所有 id 逐条删太慢，直接用 mapper 批量删
        browseHistoryMapper.deleteByUser(userId);
        return jsonresult.success("ok");
    }
}
