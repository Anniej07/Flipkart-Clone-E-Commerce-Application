package edu.jsp.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.jsp.ecommerce.entity.Seller;

public interface SellerRepository  extends  JpaRepository<Seller, Long> {

	Optional<Seller>   findByUserId(Long userId);
}
