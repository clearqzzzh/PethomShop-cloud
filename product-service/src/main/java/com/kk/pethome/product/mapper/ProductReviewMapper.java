package com.kk.pethome.product.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.product.pojo.ProductReview;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductReviewMapper extends BaseMapper<ProductReview> {
    /**
     * 根据商品ID查询评价列表
     * @param productId 商品ID
     * @return 评价列表
     */
    List<ProductReview> findByProductId(@Param("productId") Long productId);

    /**
     * 根据用户ID查询评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ProductReview> findByUserId(@Param("userId") Long userId);
}
