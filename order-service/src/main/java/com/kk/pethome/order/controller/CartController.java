package com.kk.pethome.order.controller;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.order.service.impl.CartServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartServiceImpl cartService;

    @GetMapping
    public jsonresult getCart(@RequestParam Long userId) {
        return jsonresult.success(cartService.getCart(userId));
    }

    @PostMapping("/add")
    public jsonresult add(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        String category = (String) body.get("category");
        int quantity = body.get("quantity") != null ? Integer.parseInt(body.get("quantity").toString()) : 1;
        cartService.add(userId, productId, category, quantity);
        return jsonresult.success("已加入购物车");
    }

    @PutMapping("/{id}/quantity")
    public jsonresult updateQuantity(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        int quantity = Integer.parseInt(body.get("quantity").toString());
        cartService.updateQuantity(id, quantity);
        return jsonresult.success("ok");
    }

    @DeleteMapping("/{id}")
    public jsonresult remove(@PathVariable Long id) {
        cartService.remove(id);
        return jsonresult.success("ok");
    }

    @PostMapping("/clear")
    public jsonresult clear(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        cartService.clearCart(userId);
        return jsonresult.success("ok");
    }
}
