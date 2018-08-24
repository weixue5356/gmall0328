package com.atguigu.service;

import com.atguigu.bean.CartInfo;

import java.util.List;

public interface CartService {
    CartInfo ifCartExist(CartInfo cartInfo);

    void syncCache(String userId);

    void saveCart(CartInfo cartInfo);

    void updateCart(CartInfo cartInfoDb);

    List<CartInfo> getCartCache(String userId);

    void updateCartCheked(CartInfo cartInfo);

    void combineCart(List<CartInfo> cartInfoList, String useId);

    List<CartInfo> getCartCacheByCheckd(String userId);

    void deleteCartById(List<CartInfo> cartInfoList);
}
