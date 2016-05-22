package com.github.achmadns.shopcart.web.rest.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Created on 5/23/16.
 */
@Data
public class CartDTO {
    private Double total;
    private Double discount;
    private String coupon;
    private List<CartItemDTO> items = new ArrayList<>();

    public void add(CartItemDTO item) {
            this.items.add(item);
    }
}
