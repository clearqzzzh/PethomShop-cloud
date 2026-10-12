package com.kk.pethome.product.controller;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.product.mapper.ProductMapper;
import com.kk.pethome.product.mapper.UserFavoriteMapper;
import com.kk.pethome.product.pojo.Product;
import com.kk.pethome.product.pojo.UserFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("/api/favorite")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    /** 查询用户收藏列表 */
    @GetMapping
    public jsonresult list(@RequestParam Long userId) {
        try {
            return jsonresult.success(userFavoriteMapper.findByUserId(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("查询收藏列表失败");
        }
    }

    /** 添加收藏 */
    @PostMapping
    public jsonresult add(@RequestBody UserFavorite fav) {
        try {
            // 检查是否已收藏
            int count = userFavoriteMapper.exists(fav.getUserId(), fav.getProductId());
            if (count > 0) {
                return jsonresult.fail().setMessage("该商品已收藏，请勿重复添加");
            }
            // 自动填充商品详情（兜底：即使前端没传也能正确存储）
            if (fav.getProductName() == null || fav.getProductImage() == null || fav.getProductPrice() == null) {
                Product product = productMapper.lookById(fav.getProductId());
                if (product != null) {
                    if (fav.getProductName() == null) fav.setProductName(product.getName());
                    if (fav.getProductImage() == null) fav.setProductImage(product.getImageUrl());
                    if (fav.getProductPrice() == null) fav.setProductPrice(product.getPrice());
                    if (fav.getCategory() == null) fav.setCategory(product.getCategory());
                }
            }
            fav.setCreateTime(new Date());
            userFavoriteMapper.insert(fav);
            return jsonresult.success(fav);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("添加收藏失败");
        }
    }

    /** 取消收藏 */
    @DeleteMapping("/{id}")
    public jsonresult delete(@PathVariable Long id) {
        try {
            userFavoriteMapper.deleteById(id);
            return jsonresult.success("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return jsonresult.fail().setMessage("取消收藏失败");
        }
    }
}
