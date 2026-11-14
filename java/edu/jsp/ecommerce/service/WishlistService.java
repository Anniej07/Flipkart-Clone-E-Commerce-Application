package edu.jsp.ecommerce.service;

import edu.jsp.ecommerce.response.WishlistResponse;

public interface WishlistService {

	WishlistResponse getWishlist(String email);

	WishlistResponse addToWishlist(String email, Long productId);

	WishlistResponse removeFromWishlist(String email, Long productId);

	boolean isInWishlist(String email, Long productId);

	void clearWishlist(String email);

}
