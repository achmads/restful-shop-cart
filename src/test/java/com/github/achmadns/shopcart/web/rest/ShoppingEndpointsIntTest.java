package com.github.achmadns.shopcart.web.rest;

import com.github.achmadns.shopcart.JhipsterApp;
import com.github.achmadns.shopcart.web.rest.dto.CartDTO;
import com.github.achmadns.shopcart.web.rest.dto.CartItemDTO;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;


/**
 * Test class for the CartResource REST controller.
 *
 * @see CartResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JhipsterApp.class)
@WebIntegrationTest(randomPort = true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShoppingEndpointsIntTest {
    private TestRestTemplate rest = new TestRestTemplate();
    @Value("http://localhost:${local.server.port}")
    private String baseUrl;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void a__put_item_into_cart_per_visitor() {
        final ResponseEntity<CartItemDTO> response = rest.postForEntity(baseUrl + "/api/carts/achmad/item/1/tv", null,
            CartItemDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        final CartItemDTO item = response.getBody();
        assertThat(item.getId()).isNotNull();
        assertThat(item.getCartId()).isNotNull();
        assertThat(item.getProductId()).isNotNull();
        assertThat(item.getPrice()).isEqualTo(100D);
        assertThat(item.getQuantity()).isEqualTo(1);

    }

    @Test
    public void b__get_invoice_per_visitor() {
        final ResponseEntity<CartDTO> response = rest.getForEntity(baseUrl + "/api/carts/invoice/achmad", CartDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        final CartDTO cart = response.getBody();
        assertThat(cart.getDiscount()).isNull();
        assertThat(cart.getItems().size()).isEqualTo(1);
        assertThat(cart.getTotal()).isEqualTo(100D);
    }

    @Test
    public void c__put_another_item_into_cart_per_visitor() {
        final ResponseEntity<CartItemDTO> response = rest.postForEntity(baseUrl + "/api/carts/achmad/item/2/wall-clock",
            null, CartItemDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        final CartItemDTO item = response.getBody();
        assertThat(item.getPrice()).isEqualTo(20D);
    }

    @Test
    public void d__get_invoice_with_discount_per_visitor() {
        final ResponseEntity<CartDTO> response = rest.getForEntity(baseUrl + "/api/carts/invoice/achmad/coupon/ramadhan", CartDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        final CartDTO cart = response.getBody();
        assertThat(cart.getDiscount()).isNotNull();
        assertThat(cart.getItems().size()).isEqualTo(2);
        assertThat(cart.getTotal()).isEqualTo(114D);
    }

    @Test
    public void e__remove_tv_from_cart() {
        rest.delete(baseUrl + "/api/carts/achmad/item/tv");
    }

    @Test
    public void f__get_invoice_with_discount_per_visitor() {
        final ResponseEntity<CartDTO> response = rest.getForEntity(baseUrl + "/api/carts/invoice/achmad/coupon/welcome",
            CartDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        final CartDTO cart = response.getBody();
        assertThat(cart.getDiscount()).isNotNull();
        assertThat(cart.getItems().size()).isEqualTo(1);
        assertThat(cart.getTotal()).isEqualTo(17D);
    }

    @Test
    public void g__get_invoice_with_discount_yet_not_found_coupon_per_visitor() {
        final ResponseEntity<CartDTO> response = rest.getForEntity(baseUrl + "/api/carts/invoice/achmad/coupon/xxx",
            CartDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        final CartDTO cart = response.getBody();
        assertThat(cart.getDiscount()).isNull();
        assertThat(cart.getItems().size()).isEqualTo(1);
        assertThat(cart.getTotal()).isEqualTo(20D);
    }


    @Test
    public void zzz__put_not_found_item_into_cart_per_visitor_should_failed() {
        final ResponseEntity<CartItemDTO> response = rest.postForEntity(baseUrl + "/api/carts/xxx/item/1/tvx", null,
            CartItemDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        final CartItemDTO item = response.getBody();
        assertThat(item.getId()).isNull();

    }

    @Test
    public void zzz__get_not_found_invoice_per_visitor_() {
        final ResponseEntity<CartDTO> response = rest.getForEntity(baseUrl + "/api/carts/invoice/xxx", CartDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        final CartDTO cart = response.getBody();
        assertThat(cart.getTotal()).isNull();
    }

    @Test
    public void zzz__get_not_found_invoice_with_discount_per_visitor() {
        final ResponseEntity<CartDTO> response = rest.getForEntity(baseUrl + "/api/carts/invoice/xxx/coupon/ramadhan",
            CartDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        final CartDTO cart = response.getBody();
        assertThat(cart.getTotal()).isNull();
    }

}
