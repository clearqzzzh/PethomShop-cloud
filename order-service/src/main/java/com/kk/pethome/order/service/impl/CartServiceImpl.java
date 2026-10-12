package com.kk.pethome.order.service.impl;

import com.kk.pethome.order.mapper.CartMapper;
import com.kk.pethome.order.pojo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CartServiceImpl {
    @Autowired
    private CartMapper cartMapper;

    public List<CartItem> getCart(Long userId) {
        return cartMapper.findByUserId(userId);
    }

    public void add(Long userId, Long productId, String category, int quantity) {
        CartItem exist = cartMapper.findByUserAndProduct(userId, productId);
        if (exist != null) {
            cartMapper.updateQuantity(exist.getId(), exist.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setCategory(category);
            item.setQuantity(quantity);
            item.setCreateTime(new Date());
            cartMapper.Add(item);
        }
    }

    public void updateQuantity(Long cartItemId, int quantity) {
        cartMapper.updateQuantity(cartItemId, quantity);
    }

    public void remove(Long cartItemId) {
        cartMapper.Delete(cartItemId);
    }

    public void removeBatch(List<Long> ids) {
        cartMapper.deleteByIds(ids);
    }

    public void clearCart(Long userId) {
        cartMapper.clearByUserId(userId);
    }
}
