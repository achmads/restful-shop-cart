package com.github.achmadns.shopcart.repository;

import com.github.achmadns.shopcart.domain.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Product entity.
 */
@SuppressWarnings("unused")
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByShortname(String shortname);
}
