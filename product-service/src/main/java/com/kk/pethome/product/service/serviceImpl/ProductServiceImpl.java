package com.kk.pethome.product.service.serviceImpl;

import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.basic.service.impl.BaseServiceImpl;
import com.kk.pethome.product.mapper.ProductMapper;
import com.kk.pethome.product.pojo.Product;
import com.kk.pethome.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends BaseServiceImpl<Product> implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> searchByKeyword(String keyword) {
        return productMapper.findByKeyword(keyword);
    }

    @Override
    public PageList<Product> searchByKeyword(String keyword, String category, int page, int size) {
        int offset = (page - 1) * size;
        int total;
        List<Product> data;

        // 优先级：keyword > category > 全部
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 有关键词 → 模糊搜索 name/brand
            total = productMapper.countSearch(keyword.trim());
            if (total == 0) return new PageList<Product>();
            data = productMapper.searchByNameOrBrand(keyword.trim(), offset, size);
        } else if (category != null && !category.trim().isEmpty() && !"all".equals(category.trim())) {
            // 有分类 → 按分类筛选
            total = productMapper.countByCategory(category.trim());
            if (total == 0) return new PageList<Product>();
            data = productMapper.searchByCategory(category.trim(), offset, size);
        } else {
            // 无关键词、无分类 → 查询全部
            total = productMapper.countAll();
            if (total == 0) return new PageList<Product>();
            data = productMapper.searchAll(offset, size);
        }

        return new PageList<Product>(total, data);
    }

    @Override
    public Product getProductById(Long id) {
        return productMapper.lookById(id);
    }
    public Product getProductByIdAndCategory(Long id, String category) {
        return productMapper.lookById(id);
    }

    @Override
    public List<Product> getHotProducts(int limit) {
        return productMapper.getHotProducts(limit);
    }
}
