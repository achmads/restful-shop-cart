package com.github.achmadns.shopcart.repository;

import com.github.achmadns.shopcart.domain.CartItem;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the CartItem entity.
 */
@SuppressWarnings("unused")
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

}
