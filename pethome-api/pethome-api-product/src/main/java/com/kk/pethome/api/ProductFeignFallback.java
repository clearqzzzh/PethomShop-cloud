package com.kk.pethome.api;

import com.kk.pethome.basic.result.PageList;
import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.product.pojo.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * ProductFeignAPI 降级处理
 * 当下游 product-service 不可用时返回兜底数据
 */
@Component
public class ProductFeignFallback implements ProductFeignAPI {

    @Override
    public PageList<Product> search(String keyword, String category, int page, int size) {
        return new PageList<>(0, Collections.emptyList());
    }

    @Override
    public jsonresult getDetail(Long id) {
        return jsonresult.fail("商品服务暂时不可用，请稍后再试");
    }

    @Override
    public List<Product> getHot(int limit) {
        return Collections.emptyList();
    }

    @Override
    public jsonresult updateOrderAndStock(Long orderId) {
        return jsonresult.fail("商品服务暂时不可用，库存更新失败");
    }
}
