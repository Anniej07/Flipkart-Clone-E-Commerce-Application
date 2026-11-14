package edu.jsp.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.jsp.ecommerce.entity.Cart;

public interface CartRepositroy  extends  JpaRepository<Cart, Long> {
	
	
	Optional<Cart>   findByUserId(Long userId);
	
	

}
