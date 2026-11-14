package edu.jsp.ecommerce.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.response.ApiResponse;
import edu.jsp.ecommerce.response.WishlistResponse;
import edu.jsp.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

	private final WishlistService wishlistService;

	@GetMapping
	public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(@RequestParam String email) {

		return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlist(email), "Wishlist fetched"));
	}

	@PostMapping("/{productId}")
	public ResponseEntity<ApiResponse<WishlistResponse>> add(@PathVariable Long productId, @RequestParam String email) {

		return ResponseEntity
				.ok(ApiResponse.success(wishlistService.addToWishlist(email, productId), "Added to wishlist"));
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResponse<WishlistResponse>> remove(@PathVariable Long productId,
			@RequestParam String email) {

		return ResponseEntity
				.ok(ApiResponse.success(wishlistService.removeFromWishlist(email, productId), "Removed from wishlist"));
	}

	@GetMapping("/{productId}/check")
	public ResponseEntity<ApiResponse<Map<String, Boolean>>> check(@PathVariable Long productId,
			@RequestParam String email) {

		boolean inWl = wishlistService.isInWishlist(email, productId);

		return ResponseEntity.ok(ApiResponse.success(Map.of("inWishlist", inWl), "Checked"));
	}

	@DeleteMapping("/clear")
	public ResponseEntity<ApiResponse<Void>> clear(@RequestParam String email) {

		wishlistService.clearWishlist(email);

		return ResponseEntity.ok(ApiResponse.success(null, "Wishlist cleared"));
	}
}
