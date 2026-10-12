package com.kk.pethome.product.controller;

import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.product.pojo.Product;
import com.kk.pethome.product.service.ProductService;
import com.kk.pethome.user.pojo.User;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Operation(summary = "商品搜索（支持关键词+分类筛选）")
    @GetMapping("/search")
    public jsonresult search(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String category,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "12") int size) {
        try {
            PageList<Product> pageList = productService.searchByKeyword(keyword, category, page, size);
            return jsonresult.success(pageList);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public jsonresult getProductDetail(@PathVariable Long id, @RequestParam(required = false) String category) {
        try {
            Product product = category != null ? productService.getProductByIdAndCategory(id, category) : productService.getProductById(id);
            if (product == null) {
                return jsonresult.fail().setMessage("商品不存在");
            }
            return jsonresult.success(product);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("查询失败");
        }
    }

    @Operation(summary = "热门商品")
    @GetMapping("/hot")
    public jsonresult getHotProducts(@RequestParam(defaultValue = "8") int limit) {
        try {
            List<Product> products = productService.getHotProducts(limit);
            return jsonresult.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("查询热门商品失败");
        }
    }

    // ═══════ 管理端商品 CRUD ═══════

    @Operation(summary = "管理端：新增商品")
    @PostMapping("/admin")
    public jsonresult adminAdd(@RequestBody Product product) {
        try {
            product.setCreateTime(new java.util.Date());
            if (product.getRating() == null) product.setRating(java.math.BigDecimal.valueOf(5.0));
            productService.Add(product);
            return jsonresult.success(product);
        } catch (Exception e) {
            return jsonresult.fail().setMessage("新增失败：" + e.getMessage());
        }
    }

    @Operation(summary = "管理端：更新商品")
    @PutMapping("/admin/{id}")
    public jsonresult adminUpdate(@PathVariable Long id, @RequestBody Product product) {
        try {
            product.setId(id);
            product.setUpdateTime(new java.util.Date());
            productService.Update(product);
            return jsonresult.success(null);
        } catch (Exception e) {
            return jsonresult.fail().setMessage("更新失败：" + e.getMessage());
        }
    }

    @Operation(summary = "管理端：删除商品")
    @DeleteMapping("/admin/{id}")
    public jsonresult adminDelete(@PathVariable Long id) {
        try {
            productService.Delete(id);
            return jsonresult.success(null);
        } catch (Exception e) {
            return jsonresult.fail().setMessage("删除失败：" + e.getMessage());
        }
    }

    @Operation(summary = "管理端：商品统计")
    @GetMapping("/admin/stats")
    public jsonresult adminProductStats() {
        try {
            PageList<Product> all = productService.searchByKeyword(null, null, 1, 10000);
            List<Product> products = all.getData();
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalProducts", products.size());

            int lowStock = 0;
            Map<String, Integer> categoryCount = new LinkedHashMap<>();
            for (Product p : products) {
                if (p.getStock() != null && p.getStock() < 10) lowStock++;
                String cat = p.getCategory() != null ? p.getCategory() : "其他";
                categoryCount.merge(cat, 1, Integer::sum);
            }
            stats.put("lowStockCount", lowStock);
            stats.put("categoryDistribution", categoryCount);
            return jsonresult.success(stats);
        } catch (Exception e) {
            return jsonresult.fail().setMessage("统计失败：" + e.getMessage());
        }
    }
}
