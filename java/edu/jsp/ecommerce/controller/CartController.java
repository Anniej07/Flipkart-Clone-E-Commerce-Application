package edu.jsp.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.response.ApiResponse;
import edu.jsp.ecommerce.response.CartResponse;
import edu.jsp.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@GetMapping
	public ResponseEntity<ApiResponse<CartResponse>> getCart(@RequestParam String email) {

		return ResponseEntity.ok(ApiResponse.success(cartService.getCart(email), "Cart fetched successfully"));
	}

	@PostMapping("/add")
	public ResponseEntity<ApiResponse<CartResponse>> addToCart(@RequestParam String email, @RequestParam Long productId,
			@RequestParam(defaultValue = "1") int quantity) {

		return ResponseEntity.ok(
				ApiResponse.success(cartService.addToCart(email, productId, quantity), "Added to cart successfully"));
	}

	@PutMapping("/items/{cartItemId}")
	public ResponseEntity<ApiResponse<CartResponse>> updateItem(@RequestParam String email,
			@PathVariable Long cartItemId, @RequestParam int quantity) {

		return ResponseEntity.ok(ApiResponse.success(cartService.updateCartItem(email, cartItemId, quantity),
				"Cart updated successfully"));
	}

	@DeleteMapping("/items/{cartItemId}")
	public ResponseEntity<ApiResponse<CartResponse>> removeItem(@RequestParam String email,
			@PathVariable Long cartItemId) {

		return ResponseEntity
				.ok(ApiResponse.success(cartService.removeFromCart(email, cartItemId), "Item removed successfully"));
	}

	@DeleteMapping("/clear")
	public ResponseEntity<ApiResponse<String>> clearCart(@RequestParam String email) {

		cartService.clearCart(email);

		return ResponseEntity.ok(ApiResponse.success("Cart is empty", "Cart cleared successfully"));
	}
}