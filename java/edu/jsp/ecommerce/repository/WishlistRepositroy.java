package edu.jsp.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.jsp.ecommerce.entity.Wishlist;

public interface WishlistRepositroy  extends  JpaRepository<Wishlist, Long> {

	Optional<Wishlist>  findbyUserId(long userid);
	
}
