package edu.jsp.ecommerce.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.jsp.ecommerce.entity.Cart;
import edu.jsp.ecommerce.entity.CartItem;
import edu.jsp.ecommerce.entity.Product;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.exeception.BadRequestException;
import edu.jsp.ecommerce.exeception.ResourceNotFoundException;
import edu.jsp.ecommerce.repository.CartRepositroy;
import edu.jsp.ecommerce.repository.ProductRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import edu.jsp.ecommerce.response.CartResponse;
import edu.jsp.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepositroy cartRepositroy;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Override
	public CartResponse getCart(String email) {

		Cart cart = getOrCreate(email);

		return mapToResponse(cart);
	}

	@Override
	public CartResponse addToCart(String email, Long productId, int quantity) {
		Cart cart = getOrCreate(email);
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

		if (!product.isActive()) {
			throw new BadRequestException("Product not avaiable!!!");
		}

		if (product.getInventory() == null || product.getInventory().getQuantity() <= quantity) {
			throw new BadRequestException("Requested Quantity  not avaiable!!!");
		}

		cart.getItems().stream().filter(c -> c.getProduct().getId().equals(productId)).findFirst().ifPresentOrElse(
				ct -> ct.setQuantity(quantity + ct.getQuantity()),
				() -> CartItem.builder().cart(cart).product(product).quantity(quantity).build());

		return mapToResponse(cartRepositroy.save(cart));
	}

	@Override
	public CartResponse updateCartItem(String email, Long cartItemId, int quantity) {
		 Cart cart = getOrCreate(email);

		CartItem cartItem = 	cart.getItems()
		 		.stream()
		 		.filter(c ->  c.getId().equals(cartItemId))
		 		.findFirst()
		 		.orElseThrow(
		 				()  ->  new ResourceNotFoundException("CartItem  not found!!!!")
		 				);
		 	
		 if(quantity <= 0) {
			 cart.getItems().remove(cartItem);
		 }
		 else {
			 cartItem.setQuantity(quantity);
		 }
		 		
		 
		return mapToResponse(cartRepositroy.save(cart));
	}

	@Override
	public CartResponse removeFromCart(String email, Long cartItemId) {
		Cart cart = getOrCreate(email);

		cart.getItems().removeIf(c -> c.getId().equals(cartItemId));

		return mapToResponse(cartRepositroy.save(cart));
	}

	@Override
	public void clearCart(String email) {

		Cart cart = getOrCreate(email);

		cart.getItems().clear();

		cartRepositroy.save(cart);
	}

	private Cart getOrCreate(String email) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(email + " not found!!"));

		return cartRepositroy.findByUserId(user.getId()).orElseGet(() -> Cart.builder().user(user).build());

	}

	public CartResponse mapToResponse(Cart cart) {

		List<CartResponse.CartItemResponse> items = cart.getItems().stream().map(item -> {
			BigDecimal totalprice = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

			return CartResponse.CartItemResponse.builder().cartItemId(item.getId()).productId(item.getProduct().getId())
					.productName(item.getProduct().getName()).thumbnailUrl(item.getProduct().getThumbnailUrl())
					.unitPrice(item.getProduct().getPrice()).quantity(item.getQuantity()).subTotal(totalprice).build();
		}).toList();

		BigDecimal totalAmount = BigDecimal.valueOf(0.0);
		int totalQuantity = 0;

		for (CartResponse.CartItemResponse it : items) {
			totalAmount = totalAmount.add(it.getSubTotal());
			totalQuantity = totalQuantity + it.getQuantity();
		}

		return CartResponse.builder().cartId(cart.getId()).items(items).totalAmount(totalAmount)
				.totalItems(totalQuantity).build();

	}

}
