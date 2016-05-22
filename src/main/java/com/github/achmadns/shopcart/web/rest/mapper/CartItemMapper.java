package com.github.achmadns.shopcart.web.rest.mapper;

import com.github.achmadns.shopcart.domain.*;
import com.github.achmadns.shopcart.web.rest.dto.CartItemDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity CartItem and its DTO CartItemDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CartItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "cart.id", target = "cartId")
    CartItemDTO cartItemToCartItemDTO(CartItem cartItem);

    List<CartItemDTO> cartItemsToCartItemDTOs(List<CartItem> cartItems);

    @Mapping(source = "productId", target = "product")
    @Mapping(source = "cartId", target = "cart")
    CartItem cartItemDTOToCartItem(CartItemDTO cartItemDTO);

    List<CartItem> cartItemDTOsToCartItems(List<CartItemDTO> cartItemDTOs);

    default Product productFromId(Long id) {
        if (id == null) {
            return null;
        }
        Product product = new Product();
        product.setId(id);
        return product;
    }

    default Cart cartFromId(Long id) {
        if (id == null) {
            return null;
        }
        Cart cart = new Cart();
        cart.setId(id);
        return cart;
    }
}
