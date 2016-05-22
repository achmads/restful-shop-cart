package com.github.achmadns.shopcart.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.github.achmadns.shopcart.domain.Coupon;
import com.github.achmadns.shopcart.repository.CouponRepository;
import com.github.achmadns.shopcart.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Coupon.
 */
@RestController
@RequestMapping("/api")
public class CouponResource {

    private final Logger log = LoggerFactory.getLogger(CouponResource.class);
        
    @Inject
    private CouponRepository couponRepository;
    
    /**
     * POST  /coupons : Create a new coupon.
     *
     * @param coupon the coupon to create
     * @return the ResponseEntity with status 201 (Created) and with body the new coupon, or with status 400 (Bad Request) if the coupon has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/coupons",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) throws URISyntaxException {
        log.debug("REST request to save Coupon : {}", coupon);
        if (coupon.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("coupon", "idexists", "A new coupon cannot already have an ID")).body(null);
        }
        Coupon result = couponRepository.save(coupon);
        return ResponseEntity.created(new URI("/api/coupons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("coupon", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /coupons : Updates an existing coupon.
     *
     * @param coupon the coupon to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated coupon,
     * or with status 400 (Bad Request) if the coupon is not valid,
     * or with status 500 (Internal Server Error) if the coupon couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/coupons",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Coupon> updateCoupon(@RequestBody Coupon coupon) throws URISyntaxException {
        log.debug("REST request to update Coupon : {}", coupon);
        if (coupon.getId() == null) {
            return createCoupon(coupon);
        }
        Coupon result = couponRepository.save(coupon);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("coupon", coupon.getId().toString()))
            .body(result);
    }

    /**
     * GET  /coupons : get all the coupons.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of coupons in body
     */
    @RequestMapping(value = "/coupons",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Coupon> getAllCoupons() {
        log.debug("REST request to get all Coupons");
        List<Coupon> coupons = couponRepository.findAll();
        return coupons;
    }

    /**
     * GET  /coupons/:id : get the "id" coupon.
     *
     * @param id the id of the coupon to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the coupon, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/coupons/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Coupon> getCoupon(@PathVariable Long id) {
        log.debug("REST request to get Coupon : {}", id);
        Coupon coupon = couponRepository.findOne(id);
        return Optional.ofNullable(coupon)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /coupons/:id : delete the "id" coupon.
     *
     * @param id the id of the coupon to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/coupons/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        log.debug("REST request to delete Coupon : {}", id);
        couponRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("coupon", id.toString())).build();
    }

}
