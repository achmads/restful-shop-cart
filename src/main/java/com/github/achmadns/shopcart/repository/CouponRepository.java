package com.github.achmadns.shopcart.repository;

import com.github.achmadns.shopcart.domain.Coupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Coupon entity.
 */
@SuppressWarnings("unused")
public interface CouponRepository extends JpaRepository<Coupon,Long> {

    Optional<Coupon> findByCode(String code);
}
