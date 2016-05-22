package com.github.achmadns.shopcart.web.rest;

import com.github.achmadns.shopcart.JhipsterApp;
import com.github.achmadns.shopcart.domain.CartItem;
import com.github.achmadns.shopcart.repository.CartItemRepository;
import com.github.achmadns.shopcart.web.rest.dto.CartItemDTO;
import com.github.achmadns.shopcart.web.rest.mapper.CartItemMapper;

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
 * Test class for the CartItemResource REST controller.
 *
 * @see CartItemResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JhipsterApp.class)
@WebAppConfiguration
@IntegrationTest
public class CartItemResourceIntTest {


    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    @Inject
    private CartItemRepository cartItemRepository;

    @Inject
    private CartItemMapper cartItemMapper;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCartItemMockMvc;

    private CartItem cartItem;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CartItemResource cartItemResource = new CartItemResource();
        ReflectionTestUtils.setField(cartItemResource, "cartItemRepository", cartItemRepository);
        ReflectionTestUtils.setField(cartItemResource, "cartItemMapper", cartItemMapper);
        this.restCartItemMockMvc = MockMvcBuilders.standaloneSetup(cartItemResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        cartItem = new CartItem();
        cartItem.setPrice(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    public void createCartItem() throws Exception {
        int databaseSizeBeforeCreate = cartItemRepository.findAll().size();

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.cartItemToCartItemDTO(cartItem);

        restCartItemMockMvc.perform(post("/api/cart-items")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
                .andExpect(status().isCreated());

        // Validate the CartItem in the database
        List<CartItem> cartItems = cartItemRepository.findAll();
        assertThat(cartItems).hasSize(databaseSizeBeforeCreate + 1);
        CartItem testCartItem = cartItems.get(cartItems.size() - 1);
        assertThat(testCartItem.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    public void getAllCartItems() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);

        // Get all the cartItems
        restCartItemMockMvc.perform(get("/api/cart-items?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(cartItem.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())));
    }

    @Test
    @Transactional
    public void getCartItem() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);

        // Get the cartItem
        restCartItemMockMvc.perform(get("/api/cart-items/{id}", cartItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(cartItem.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCartItem() throws Exception {
        // Get the cartItem
        restCartItemMockMvc.perform(get("/api/cart-items/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCartItem() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();

        // Update the cartItem
        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setId(cartItem.getId());
        updatedCartItem.setPrice(UPDATED_PRICE);
        CartItemDTO cartItemDTO = cartItemMapper.cartItemToCartItemDTO(updatedCartItem);

        restCartItemMockMvc.perform(put("/api/cart-items")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cartItemDTO)))
                .andExpect(status().isOk());

        // Validate the CartItem in the database
        List<CartItem> cartItems = cartItemRepository.findAll();
        assertThat(cartItems).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItems.get(cartItems.size() - 1);
        assertThat(testCartItem.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void deleteCartItem() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);
        int databaseSizeBeforeDelete = cartItemRepository.findAll().size();

        // Get the cartItem
        restCartItemMockMvc.perform(delete("/api/cart-items/{id}", cartItem.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<CartItem> cartItems = cartItemRepository.findAll();
        assertThat(cartItems).hasSize(databaseSizeBeforeDelete - 1);
    }
}
