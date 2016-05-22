package com.github.achmadns.shopcart.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.github.achmadns.shopcart.domain.CartItem;
import com.github.achmadns.shopcart.repository.CartItemRepository;
import com.github.achmadns.shopcart.web.rest.util.HeaderUtil;
import com.github.achmadns.shopcart.web.rest.dto.CartItemDTO;
import com.github.achmadns.shopcart.web.rest.mapper.CartItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing CartItem.
 */
@RestController
@RequestMapping("/api")
public class CartItemResource {

    private final Logger log = LoggerFactory.getLogger(CartItemResource.class);
        
    @Inject
    private CartItemRepository cartItemRepository;
    
    @Inject
    private CartItemMapper cartItemMapper;
    
    /**
     * POST  /cart-items : Create a new cartItem.
     *
     * @param cartItemDTO the cartItemDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cartItemDTO, or with status 400 (Bad Request) if the cartItem has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cart-items",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CartItemDTO> createCartItem(@RequestBody CartItemDTO cartItemDTO) throws URISyntaxException {
        log.debug("REST request to save CartItem : {}", cartItemDTO);
        if (cartItemDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("cartItem", "idexists", "A new cartItem cannot already have an ID")).body(null);
        }
        CartItem cartItem = cartItemMapper.cartItemDTOToCartItem(cartItemDTO);
        cartItem = cartItemRepository.save(cartItem);
        CartItemDTO result = cartItemMapper.cartItemToCartItemDTO(cartItem);
        return ResponseEntity.created(new URI("/api/cart-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("cartItem", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cart-items : Updates an existing cartItem.
     *
     * @param cartItemDTO the cartItemDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cartItemDTO,
     * or with status 400 (Bad Request) if the cartItemDTO is not valid,
     * or with status 500 (Internal Server Error) if the cartItemDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cart-items",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CartItemDTO> updateCartItem(@RequestBody CartItemDTO cartItemDTO) throws URISyntaxException {
        log.debug("REST request to update CartItem : {}", cartItemDTO);
        if (cartItemDTO.getId() == null) {
            return createCartItem(cartItemDTO);
        }
        CartItem cartItem = cartItemMapper.cartItemDTOToCartItem(cartItemDTO);
        cartItem = cartItemRepository.save(cartItem);
        CartItemDTO result = cartItemMapper.cartItemToCartItemDTO(cartItem);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("cartItem", cartItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cart-items : get all the cartItems.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of cartItems in body
     */
    @RequestMapping(value = "/cart-items",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<CartItemDTO> getAllCartItems() {
        log.debug("REST request to get all CartItems");
        List<CartItem> cartItems = cartItemRepository.findAll();
        return cartItemMapper.cartItemsToCartItemDTOs(cartItems);
    }

    /**
     * GET  /cart-items/:id : get the "id" cartItem.
     *
     * @param id the id of the cartItemDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cartItemDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/cart-items/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CartItemDTO> getCartItem(@PathVariable Long id) {
        log.debug("REST request to get CartItem : {}", id);
        CartItem cartItem = cartItemRepository.findOne(id);
        CartItemDTO cartItemDTO = cartItemMapper.cartItemToCartItemDTO(cartItem);
        return Optional.ofNullable(cartItemDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /cart-items/:id : delete the "id" cartItem.
     *
     * @param id the id of the cartItemDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/cart-items/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        log.debug("REST request to delete CartItem : {}", id);
        cartItemRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("cartItem", id.toString())).build();
    }

}
