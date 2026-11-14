package edu.jsp.ecommerce.repository;


import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.jsp.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findByActiveTrue(Pageable pageable);
	
	Page<Product> findByActiveTrueAndBrandId(Long brandId, Pageable pageable);
	Page<Product> findByActiveTrueAndCategoryId(Long category, Pageable pageable);
	Page<Product> findByActiveTrueAndSellerId(Long sellerId, Pageable pageable);
	Page<Product> findByActiveTrueAndCategoryIdAndBrandId
						(Long category,Long brandId , Pageable pageable);
	
	@Query(value = "select  p from Product p  where p.active =  true "
	+   "And  LOWER(p.name)   like   LOWER(CONCAT('%',:keyword,'%')) "
	+   "OR  LOWER(p.description)   like   LOWER(CONCAT('%',:keyword,'%')) "
			)
	Page<Product>  searchBykeyword(@Param(value = "keyword")  String keyword,Pageable pageable);

	
	 @Query("SELECT p FROM Product p WHERE p.active = true " +
	           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
	           "AND (:brandId    IS NULL OR p.brand.id    = :brandId) " +
	           "AND (:minPrice   IS NULL OR p.price       >= :minPrice) " +
	           "AND (:maxPrice   IS NULL OR p.price       <= :maxPrice) " +
	           "AND (:keyword    IS NULL OR"
	           + " LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
	           "     OR :keyword IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%',:keyword,'%')))")
	    Page<Product> filterProducts(
	            @Param("categoryId") Long categoryId,
	            @Param("brandId")    Long brandId,
	            @Param("minPrice")   BigDecimal minPrice,
	            @Param("maxPrice")   BigDecimal maxPrice,
	            @Param("keyword")    String keyword,
	            Pageable pageable);
}
