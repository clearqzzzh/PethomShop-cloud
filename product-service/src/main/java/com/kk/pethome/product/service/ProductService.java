package com.kk.pethome.product.service;

import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.basic.service.IBaseService;
import com.kk.pethome.product.pojo.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService extends IBaseService<Product> {
    /**
     * 根据关键词搜索商品（模糊匹配 name 或 brand）
     */
    List<Product> searchByKeyword(String keyword);

    /**
     * 分页搜索商品（支持 keyword 关键词 + category 分类筛选）
     * 优先级：keyword > category > 全部
     *
     * @param keyword  搜索关键词（可选）
     * @param category 商品分类（可选）
     * @param page     页码
     * @param size     每页条数
     * @return 分页结果
     */
    PageList<Product> searchByKeyword(String keyword, String category, int page, int size);

    /**
     * 根据ID查询商品详情
     */
    Product getProductById(Long id);
    Product getProductByIdAndCategory(Long id, String category);

    /**
     * 获取热门商品（按评分降序，库存降序）
     */
    List<Product> getHotProducts(int limit);
}
