package com.kk.pethome.api;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.product.pojo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service", path = "/api/product", fallback = ProductFeignFallback.class)
public interface ProductFeignAPI {

    @GetMapping("/search")
    PageList<Product> search(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String category,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "12") int size);

    @GetMapping("/{id}")
    jsonresult getDetail(@PathVariable Long id);

    @GetMapping("/hot")
    List<Product> getHot(@RequestParam(defaultValue = "8") int limit);

    @PostMapping("/updateOrderAndStock")
    jsonresult updateOrderAndStock(@RequestParam Long orderId);
}
