package com.kk.pethome.product.mapper;

import com.kk.pethome.basic.mapper.BaseMapper;
import com.kk.pethome.product.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 根据关键词模糊查询商品（匹配 name 或 brand）
     */
    List<Product> findByKeyword(@Param("keyword") String keyword);

    /**
     * 分页模糊搜索商品（按 name 或 brand）
     */
    List<Product> searchByNameOrBrand(@Param("keyword") String keyword,
                                      @Param("offset") int offset,
                                      @Param("size") int size);

    /**
     * 统计搜索结果总数
     */
    int countSearch(@Param("keyword") String keyword);

    /**
     * 按分类分页查询商品
     */
    List<Product> searchByCategory(@Param("category") String category,
                                   @Param("offset") int offset,
                                   @Param("size") int size);

    /**
     * 统计某分类商品总数
     */
    int countByCategory(@Param("category") String category);

    /**
     * 查询全部商品（分页）
     */
    List<Product> searchAll(@Param("offset") int offset,
                            @Param("size") int size);

    /**
     * 统计全部商品总数
     */
    int countAll();

    /**
     * 获取热门商品（按评分降序，库存降序）
     */
    List<Product> getHotProducts(@Param("limit") int limit);

}
