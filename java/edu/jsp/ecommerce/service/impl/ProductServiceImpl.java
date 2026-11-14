package edu.jsp.ecommerce.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import edu.jsp.ecommerce.entity.Brand;
import edu.jsp.ecommerce.entity.Category;
import edu.jsp.ecommerce.entity.Inventory;
import edu.jsp.ecommerce.entity.Product;
import edu.jsp.ecommerce.entity.ProductImage;
import edu.jsp.ecommerce.entity.Seller;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.enums.Role;
import edu.jsp.ecommerce.exeception.BadRequestException;
import edu.jsp.ecommerce.exeception.ResourceNotFoundException;
import edu.jsp.ecommerce.repository.BrandRepository;
import edu.jsp.ecommerce.repository.CategoryRepository;
import edu.jsp.ecommerce.repository.InventoryRepository;
import edu.jsp.ecommerce.repository.ProductRepository;
import edu.jsp.ecommerce.repository.SellerRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import edu.jsp.ecommerce.request.ProductRequest;
import edu.jsp.ecommerce.response.ProductResponse;
import edu.jsp.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;
	private final CategoryRepository categoryRepository;
	private final BrandRepository brandRepository;
	private final ProductRepository productRepository;
	private final InventoryRepository inventoryRepository;

	@Override
	public ProductResponse createProduct(ProductRequest request, String sellerEmail) {

		Seller seller = getSeller(sellerEmail);
		Product product = buildProduct(request, seller);
		productRepository.save(product);

		if (!request.getImageUrls().isEmpty()) {
			saveImages(product, request.getImageUrls());
		}

		Inventory inventory = Inventory.builder().product(product).quantity(request.getStockQuantity()).build();
		inventoryRepository.save(inventory);

		product.setInventory(inventory);

		return mapToResponse(product);
	}

	public Seller getSeller(String sellerEmail) {

		User user = userRepository.findByEmail(sellerEmail)
				.orElseThrow(() -> new ResourceNotFoundException(sellerEmail + " email not found!!! "));
		return sellerRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("seller  not found!!! "));

	}

	private Product buildProduct(ProductRequest request, Seller seller) {
		Category category = request.getCategoryId() != null
				? categoryRepository.findById(request.getCategoryId()).orElse(null)
				: null;
		Brand brand = request.getBrandId() != null ? brandRepository.findById(request.getBrandId()).orElse(null) : null;

		return Product.builder().name(request.getName()).description(request.getDescription()).price(request.getPrice())
				.mrp(request.getMrp() != null ? request.getMrp() : request.getPrice()).category(category).brand(brand)
				.seller(seller).thumbnailUrl(request.getThumbnailUrl()).active(true).build();
	}

	private void saveImages(Product product, List<String> images) {

		for (int i = 0; i < images.size(); i++) {
			String url = images.get(i);
			if (url != null && !url.isBlank()) {
				ProductImage image = ProductImage.builder().product(product).imageUrl(url).primaryImage(i == 0)
						.displayOrder(i).build();

				product.getImages().add(image);
			}

		}

	}

	public ProductResponse mapToResponse(Product p) {
		int stock = p.getInventory() != null ? p.getInventory().getQuantity() : 0;

		List<ProductResponse.ProductImageResponse> images = new ArrayList();
		if (p.getImages() != null) {
			images = p.getImages().stream().sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
					.map(img -> ProductResponse.ProductImageResponse.builder().id(img.getId())
							.imageUrl(img.getImageUrl()).primaryImage(img.isPrimaryImage())
							.displayOrder(img.getDisplayOrder()).build())
					.collect(Collectors.toList());
		}

		return ProductResponse.builder().id(p.getId()).name(p.getName()).description(p.getDescription())
				.price(p.getPrice()).mrp(p.getMrp()).averageRating(p.getAverageRating())
				.totalReviews(p.getTotalReviews()).categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
				.categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
				.brandId(p.getBrand() != null ? p.getBrand().getId() : null)
				.brandName(p.getBrand() != null ? p.getBrand().getName() : null)
				.sellerName(p.getSeller() != null ? p.getSeller().getBusinessName() : null)
				.thumbnailUrl(p.getThumbnailUrl()).active(p.isActive()).stockQuantity(stock).createdAt(p.getCreatedAt())
				.images(images).build();
	}

	@Override
	public ProductResponse updateProduct(Long id, ProductRequest request, String sellerEmail) {
		Product product = findProduct(id);
		User user = findUser(sellerEmail);
		if (user.getRoles().stream().noneMatch(r -> r.getName() == Role.ROLE_ADMIN)) {
			Seller seller = sellerRepository.findByUserId(user.getId())
					.orElseThrow(() -> new ResourceNotFoundException("seller not fouund"));

			if (!product.getSeller().getId().equals(seller.getId())) {
				throw new BadRequestException("You can update  only your product!!");
			}
		}
		applyUpdates(product, request);
		if (request.getImageUrls() != null) {
			product.getImages().clear();
			productRepository.save(product);
			saveImages(product, request.getImageUrls());
		}

		if (product.getInventory() != null) {
			product.getInventory().setQuantity(request.getStockQuantity());
			productRepository.save(product);
		} else {
			Inventory inventory = Inventory.builder().product(product).quantity(request.getStockQuantity()).build();
			inventoryRepository.save(inventory);
			product.setInventory(inventory);
		}

		return mapToResponse(product);
	}

	@Override
	public void deleteProduct(Long id, String sellerEmail) {
		User user = findUser(sellerEmail);
		Product product = findProduct(id);

		if (user.getRoles().stream().noneMatch(r -> r.getName() == Role.ROLE_ADMIN)) {
			Seller seller = sellerRepository.findByUserId(user.getId())
					.orElseThrow(() -> new ResourceNotFoundException("seller not fouund"));

			if (!product.getSeller().getId().equals(seller.getId())) {
				throw new BadRequestException("You can delete  only your product!!");
			}

		}

		product.setActive(false);

		productRepository.save(product);

	}

	@Override
	public ProductResponse getProductById(Long id) {
		return mapToResponse(findProduct(id));
	}

	@Override
	public Page<ProductResponse> getAllProducts(Pageable pageable) {

		return productRepository.findByActiveTrue(pageable).map(x -> mapToResponse(x));

	}

	@Override
	public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
		return productRepository.findByActiveTrueAndCategoryId(categoryId, pageable).map(this::mapToResponse);

	}

	@Override
	public Page<ProductResponse> getProductsByBrand(Long brandId, Pageable pageable) {
		return productRepository.findByActiveTrueAndBrandId(brandId, pageable).map(this::mapToResponse);
	}

	@Override
	public Page<ProductResponse> getProductsByCategoryAndBrand(Long categoryId, Long brandId, Pageable pageable) {
		return productRepository.findByActiveTrueAndCategoryIdAndBrandId(categoryId, brandId, pageable)
				.map(this::mapToResponse);
	}

	@Override
	public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
		return productRepository.searchBykeyword(keyword, pageable).map(this::mapToResponse);
	}

	@Override
	public Page<ProductResponse> getSellerProducts(String sellerEmail, Pageable pageable) {

		Seller seller = getSeller(sellerEmail);

		return productRepository.findByActiveTrueAndSellerId(seller.getId(), pageable).map(this::mapToResponse);
	}

	@Override
	public Page<ProductResponse> filterProducts(Long categoryId, Long brandId, BigDecimal minPrice, BigDecimal maxPrice,
			String keyword, Pageable pageable) {
		return productRepository.filterProducts(categoryId, brandId, minPrice, maxPrice, keyword, pageable)
				.map(this::mapToResponse);
	}

	private Product findProduct(Long id) {
		return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found!!"));
	}

	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
	}

	private void applyUpdates(Product product, ProductRequest request) {
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setMrp(request.getMrp() != null ? request.getMrp() : request.getPrice());
		product.setThumbnailUrl(request.getThumbnailUrl());
		if (request.getCategoryId() != null) {
			categoryRepository.findById(request.getCategoryId()).ifPresent(product::setCategory);
		}
		if (request.getBrandId() != null) {
			brandRepository.findById(request.getBrandId()).ifPresent(product::setBrand);
		}
	}

}
