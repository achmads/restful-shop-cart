package com.github.achmadns.shopcart.cucumber.stepdefs;

import com.github.achmadns.shopcart.repository.CartItemRepository;
import com.github.achmadns.shopcart.repository.CartRepository;
import com.github.achmadns.shopcart.web.rest.dto.CartDTO;
import com.github.achmadns.shopcart.web.rest.dto.CartItemDTO;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

public class ShoppingSteps extends StepDefs {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private TestRestTemplate rest = new TestRestTemplate();
    @Value("http://localhost:${local.server.port}")
    private String baseUrl;
    private final Map<String, Double> invoices = new HashMap<>(4);
    @Inject
    private CartRepository cartRepository;
    @Inject
    private CartItemRepository cartItemRepository;

    @Given("database preparation")
    @Transactional
    public void clean_db() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        assertThat(cartItemRepository.count()).isEqualTo(0L);
        assertThat(cartRepository.count()).isEqualTo(0L);
    }

    @Given("^visitors pick non existent items:")
    public void pick__non_existent_items(DataTable data) throws Throwable {
        postCartItems(data, BAD_REQUEST);
    }

    private void postCartItems(DataTable data, HttpStatus expectedResponseStatus) {
        for (DataTableRow row : data.getGherkinRows()) {
            final List<String> cells = row.getCells();
            log.info("Data line {}: {}", row.getLine(), cells);
            final HttpStatus statusCode = rest.postForEntity(baseUrl + "/api/carts/{visitor}/item/{quantity}/{productShortName}", null,
                CartItemDTO.class, cells.subList(0, cells.size() - 1).toArray(new Object[]{})).getStatusCode();
            assertThat(statusCode).isEqualTo(expectedResponseStatus);
        }
    }

    @Given("^visitors pick their items:")
    public void pick_items(DataTable data) throws Throwable {
        postCartItems(data, OK);
    }

    @When("^([\\w]*) checks out the cart and got USD ([\\d\\.]*) invoice$")
    public void checkout_cart(String visitor, Double invoice) {
        log.info("visitor", visitor);
        final ResponseEntity<CartDTO> response = rest.getForEntity(baseUrl + "/api/carts/invoice/{coupon}", CartDTO.class, visitor);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getTotal()).isEqualTo(invoice);
    }

    @When("^([\\w]*) checks out the cart and got nothing$")
    public void checkout_cart(String visitor) {
        log.info("visitor", visitor);
        final ResponseEntity<CartDTO> response = rest.getForEntity(baseUrl + "/api/carts/invoice/{coupon}", CartDTO.class, visitor);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @When("^([\\w]*) checks out the cart with coupon ([\\w]*) and got USD ([\\d\\.]*) invoice$")
    public void checkout_cart_with_coupon(String visitor, String coupon, Double invoice) {
        log.info("{} checks out with coupon {}", visitor, coupon);
        final ResponseEntity<CartDTO> response = rest.getForEntity(baseUrl + "/api/carts/invoice/{visitor}/coupon/{coupon}", CartDTO.class, visitor, coupon);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getTotal()).isEqualTo(invoice);
    }

    @Then("^([\\w]*) cancel the ([\\w-]+)$")
    public void cancel_item(String visitor, String productShortName) {
        log.info("{} cancel {}", visitor, productShortName);
        rest.delete(baseUrl + "/api/carts/{visitor}/item/{productShortName}", visitor, productShortName);
    }
}
