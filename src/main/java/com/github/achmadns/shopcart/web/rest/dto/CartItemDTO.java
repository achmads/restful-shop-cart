package com.github.achmadns.shopcart.web.rest.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the CartItem entity.
 */
public class CartItemDTO implements Serializable {

    private Long id;

    private Double price;


    private Long productId;

    private Long cartId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CartItemDTO cartItemDTO = (CartItemDTO) o;

        if ( ! Objects.equals(id, cartItemDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CartItemDTO{" +
            "id=" + id +
            ", price='" + price + "'" +
            '}';
    }
}
