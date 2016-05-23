package com.github.achmadns.shopcart.repository;

import com.github.achmadns.shopcart.domain.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Cart entity.
 */
@SuppressWarnings("unused")
public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findBySession(String session);
}
