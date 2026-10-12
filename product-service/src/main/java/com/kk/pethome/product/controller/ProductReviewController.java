package com.kk.pethome.product.controller;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.product.mapper.ProductReviewMapper;
import com.kk.pethome.product.pojo.ProductReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/review")
public class ProductReviewController {

    @Autowired
    private ProductReviewMapper productReviewMapper;

    /** 根据商品ID查询评价列表 */
    @GetMapping("/product/{productId}")
    public jsonresult findByProductId(@PathVariable Long productId) {
        try {
            return jsonresult.success(productReviewMapper.findByProductId(productId));
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("查询商品评价失败");
        }
    }

    /** 根据用户ID查询评价列表 */
    @GetMapping("/user/{userId}")
    public jsonresult findByUserId(@PathVariable Long userId) {
        try {
            return jsonresult.success(productReviewMapper.findByUserId(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("查询用户评价失败");
        }
    }

    /** 添加评价 */
    @PostMapping
    public jsonresult add(@RequestBody ProductReview review) {
        try {
            review.setCreateTime(new Date());
            productReviewMapper.Add(review);
            return jsonresult.success(review);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("添加评价失败");
        }
    }

    /** 删除评价 */
    @DeleteMapping("/{id}")
    public jsonresult delete(@PathVariable Long id) {
        try {
            productReviewMapper.Delete(id);
            return jsonresult.success("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("删除评价失败");
        }
    }
}
