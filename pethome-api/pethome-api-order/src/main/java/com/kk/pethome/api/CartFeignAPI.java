package com.kk.pethome.api;

import com.kk.pethome.basic.result.jsonresult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(contextId = "cartFeignAPI", name = "order-service", path = "/api/cart")
public interface CartFeignAPI {

    @PostMapping("/add")
    jsonresult addToCart(@RequestBody Map<String, Object> cartData);

    @GetMapping
    jsonresult getCart(@RequestParam Long userId);

    @DeleteMapping("/{id}")
    jsonresult removeFromCart(@PathVariable Long id);
}
