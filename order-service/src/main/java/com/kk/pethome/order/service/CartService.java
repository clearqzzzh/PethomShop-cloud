package com.kk.pethome.order.service;

import com.kk.pethome.order.pojo.CartItem;

import java.util.List;

public interface CartService {
    List<CartItem> getCart(Long userId);
    void add(Long userId, Long productId, String category, int quantity);
    void updateQuantity(Long cartItemId, int quantity);
    void remove(Long cartItemId);
    void removeBatch(List<Long> ids);
    void clearCart(Long userId);
}
