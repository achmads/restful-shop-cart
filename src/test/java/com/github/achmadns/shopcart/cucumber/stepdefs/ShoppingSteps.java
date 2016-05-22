package com.github.achmadns.shopcart.cucumber.stepdefs;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;


import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingSteps extends StepDefs {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private TestRestTemplate rest = new TestRestTemplate();
    @Value("http://localhost:${local.server.port}")
    private String baseUrl;
    private final Map<String, Double> invoices = new HashMap<>(4);

    @Given("^visitors pick their items:")
    public void pick_items(DataTable data) throws Throwable {
        // example
        // | achmad | 1 | tv           | 100  |
        for (DataTableRow row : data.getGherkinRows()) {
            // TODO parse each row and post the item
            final List<String> cells = row.getCells();
            log.info("Data line {}: {}", row.getLine(), cells);
        }
    }

    @When("^([\\w]*) checks out the cart$")
    public void checkout_cart(String visitor) {
        log.info("visitor", visitor);
        // TODO checkout
    }

    @When("^([\\w]*) checks out the cart with coupon ([\\w]*)$")
    public void checkout_cart_with_coupon(String visitor, String coupon) {
        log.info("{} checks out with coupon {}", visitor, coupon);
        // TODO checkout with coupon
    }

    @Then("^([\\w]*) confirms he has USD ([\\d\\.]*) invoice$")
    public void check_invoice(String visitor, Double invoice) {
        log.info("{} has to pay USD {}", visitor, invoice);
//        assertThat(invoices.get(visitor)).isEqualTo(invoice);
    }

    @Then("^([\\w]*) cancel the ([\\w-]+)$")
    public void cancel_item(String visitor, String itemName) {
        log.info("{} cancel {}", visitor, itemName);
        // TODO cancel the cart item
    }
}
