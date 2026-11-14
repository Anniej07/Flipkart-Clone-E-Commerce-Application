package edu.jsp.ecommerce.controller;



import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.request.ProductRequest;
import edu.jsp.ecommerce.response.ApiResponse;
import edu.jsp.ecommerce.response.ProductResponse;
import edu.jsp.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = buildPageable(page, size, sortBy);

        boolean hasFilter = categoryId != null || brandId != null ||
                minPrice != null || maxPrice != null ||
                keyword != null || !"newest".equals(sortBy);

        Page<ProductResponse> result = hasFilter
                ? productService.filterProducts(
                        categoryId, brandId, minPrice, maxPrice, keyword, pageable)
                : productService.getAllProducts(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(result,"Products fetched"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getProductById(id),"Product fetched"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "newest") String sort,
            Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.searchProducts(keyword, pageable),"Search results"));
    }

    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getProductsByCategory(categoryId, pageable),"Products by category"));
    }

    
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getByBrand(
            @PathVariable Long brandId,
            Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getProductsByBrand(brandId, pageable),"Products by brand"));
    }

   
    @GetMapping("/my-products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getMyProducts(
            @RequestParam String email,
            Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getSellerProducts(email, pageable),"Seller products"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request,
            @RequestParam String email) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        productService.createProduct(request, email),"Product created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @RequestParam String email) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.updateProduct(id, request, email),"Product updated"));
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @RequestParam String email) {

        productService.deleteProduct(id, email);

        return ResponseEntity.ok(
                ApiResponse.success(null,"Product deleted"));
    }

    private Pageable buildPageable(int page, int size, String sort) {

        Sort s = switch (sort) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" ->   Sort.by("price").descending();
            case "name_asc" ->  Sort.by("name").ascending();
            case "name_desc" -> Sort.by("name").descending();
            default ->  Sort.by("createdAt").descending();
        };

        return PageRequest.of(page, size, s);
    }
}
