package edu.jsp.ecommerce.service;

import edu.jsp.ecommerce.response.CartResponse;

public interface CartService {
    CartResponse getCart(String email);
    CartResponse addToCart(String email, Long productId, int quantity);
    CartResponse updateCartItem(String email, Long cartItemId, int quantity);
    CartResponse removeFromCart(String email, Long cartItemId);
    void clearCart(String email);
}

