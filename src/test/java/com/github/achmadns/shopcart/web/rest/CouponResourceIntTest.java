package com.github.achmadns.shopcart.web.rest;

import com.github.achmadns.shopcart.JhipsterApp;
import com.github.achmadns.shopcart.domain.Coupon;
import com.github.achmadns.shopcart.repository.CouponRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CouponResource REST controller.
 *
 * @see CouponResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JhipsterApp.class)
@WebAppConfiguration
@IntegrationTest
public class CouponResourceIntTest {

    private static final String DEFAULT_CODE = "AAAAA";
    private static final String UPDATED_CODE = "BBBBB";

    private static final Double DEFAULT_DISCOUNT = 1D;
    private static final Double UPDATED_DISCOUNT = 2D;

    private static final Boolean DEFAULT_PERCENTAGE = false;
    private static final Boolean UPDATED_PERCENTAGE = true;

    @Inject
    private CouponRepository couponRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCouponMockMvc;

    private Coupon coupon;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CouponResource couponResource = new CouponResource();
        ReflectionTestUtils.setField(couponResource, "couponRepository", couponRepository);
        this.restCouponMockMvc = MockMvcBuilders.standaloneSetup(couponResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        coupon = new Coupon();
        coupon.setCode(DEFAULT_CODE);
        coupon.setDiscount(DEFAULT_DISCOUNT);
        coupon.setPercentage(DEFAULT_PERCENTAGE);
    }

    @Test
    @Transactional
    public void createCoupon() throws Exception {
        int databaseSizeBeforeCreate = couponRepository.findAll().size();

        // Create the Coupon

        restCouponMockMvc.perform(post("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(coupon)))
                .andExpect(status().isCreated());

        // Validate the Coupon in the database
        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeCreate + 1);
        Coupon testCoupon = coupons.get(coupons.size() - 1);
        assertThat(testCoupon.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCoupon.getDiscount()).isEqualTo(DEFAULT_DISCOUNT);
        assertThat(testCoupon.isPercentage()).isEqualTo(DEFAULT_PERCENTAGE);
    }

    @Test
    @Transactional
    public void getAllCoupons() throws Exception {
        // Initialize the database
        couponRepository.saveAndFlush(coupon);

        // Get all the coupons
        restCouponMockMvc.perform(get("/api/coupons?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(coupon.getId().intValue())))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
                .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT.doubleValue())))
                .andExpect(jsonPath("$.[*].percentage").value(hasItem(DEFAULT_PERCENTAGE.booleanValue())));
    }

    @Test
    @Transactional
    public void getCoupon() throws Exception {
        // Initialize the database
        couponRepository.saveAndFlush(coupon);

        // Get the coupon
        restCouponMockMvc.perform(get("/api/coupons/{id}", coupon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(coupon.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.discount").value(DEFAULT_DISCOUNT.doubleValue()))
            .andExpect(jsonPath("$.percentage").value(DEFAULT_PERCENTAGE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCoupon() throws Exception {
        // Get the coupon
        restCouponMockMvc.perform(get("/api/coupons/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCoupon() throws Exception {
        // Initialize the database
        couponRepository.saveAndFlush(coupon);
        int databaseSizeBeforeUpdate = couponRepository.findAll().size();

        // Update the coupon
        Coupon updatedCoupon = new Coupon();
        updatedCoupon.setId(coupon.getId());
        updatedCoupon.setCode(UPDATED_CODE);
        updatedCoupon.setDiscount(UPDATED_DISCOUNT);
        updatedCoupon.setPercentage(UPDATED_PERCENTAGE);

        restCouponMockMvc.perform(put("/api/coupons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCoupon)))
                .andExpect(status().isOk());

        // Validate the Coupon in the database
        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeUpdate);
        Coupon testCoupon = coupons.get(coupons.size() - 1);
        assertThat(testCoupon.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCoupon.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
        assertThat(testCoupon.isPercentage()).isEqualTo(UPDATED_PERCENTAGE);
    }

    @Test
    @Transactional
    public void deleteCoupon() throws Exception {
        // Initialize the database
        couponRepository.saveAndFlush(coupon);
        int databaseSizeBeforeDelete = couponRepository.findAll().size();

        // Get the coupon
        restCouponMockMvc.perform(delete("/api/coupons/{id}", coupon.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Coupon> coupons = couponRepository.findAll();
        assertThat(coupons).hasSize(databaseSizeBeforeDelete - 1);
    }
}
