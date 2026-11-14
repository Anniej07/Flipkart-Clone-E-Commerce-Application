package edu.jsp.ecommerce.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.jsp.ecommerce.entity.Product;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.entity.Wishlist;
import edu.jsp.ecommerce.exeception.ResourceNotFoundException;
import edu.jsp.ecommerce.repository.ProductRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import edu.jsp.ecommerce.repository.WishlistRepositroy;
import edu.jsp.ecommerce.response.ProductResponse;
import edu.jsp.ecommerce.response.WishlistResponse;
import edu.jsp.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishlistService {

	private final UserRepository userRepository;
	private final WishlistRepositroy wishlistRepositroy;
	private final ProductRepository productRepository;
	private final ProductServiceImpl productService;

	@Override
	public WishlistResponse getWishlist(String email) {

		Wishlist wishlist = getOrCreate(email);

		return mapToResponse(wishlist);
	}

	@Override
	public WishlistResponse addToWishlist(String email, Long productId) {
		Wishlist wishlist = getOrCreate(email);

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("product  not found!!!"));
		wishlist.getProducts().add(product);

		return mapToResponse(wishlistRepositroy.save(wishlist));
	}

	@Override
	public WishlistResponse removeFromWishlist(String email, Long productId) {
		Wishlist wishlist = getOrCreate(email);

		wishlist.getProducts().removeIf(p -> p.getId().equals(productId));

		return mapToResponse(wishlistRepositroy.save(wishlist));

	}

	@Override
	public boolean isInWishlist(String email, Long productId) {
		Wishlist wishlist = getOrCreate(email);

		return wishlist.getProducts().stream().anyMatch(p -> p.getId().equals(productId));

	}

	@Override
	public void clearWishlist(String email) {
		Wishlist wishlist = getOrCreate(email);
		wishlist.getProducts().clear();
		wishlistRepositroy.save(wishlist);
	}

	public Wishlist getOrCreate(String email) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(email + " not found!!!"));

		return wishlistRepositroy.findbyUserId(user.getId()).orElseGet(() -> Wishlist.builder().user(user).build());
	}

	private WishlistResponse mapToResponse(Wishlist wl) {
		List<ProductResponse> items = wl.getProducts().stream().map(productService::mapToResponse).toList();
		return WishlistResponse.builder().wishlistId(wl.getId()).products(items).totalItems(items.size()).build();
	}

}
