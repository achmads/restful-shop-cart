package com.github.achmadns.shopcart.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.github.achmadns.shopcart.domain.Cart;
import com.github.achmadns.shopcart.domain.CartItem;
import com.github.achmadns.shopcart.domain.Coupon;
import com.github.achmadns.shopcart.domain.Product;
import com.github.achmadns.shopcart.repository.CartItemRepository;
import com.github.achmadns.shopcart.repository.CartRepository;
import com.github.achmadns.shopcart.repository.CouponRepository;
import com.github.achmadns.shopcart.repository.ProductRepository;
import com.github.achmadns.shopcart.web.rest.dto.CartDTO;
import com.github.achmadns.shopcart.web.rest.dto.CartItemDTO;
import com.github.achmadns.shopcart.web.rest.mapper.CartItemMapper;
import com.github.achmadns.shopcart.web.rest.util.HeaderUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Cart.
 */
@RestController
@RequestMapping("/api")
public class CartResource {

    private final Logger log = LoggerFactory.getLogger(CartResource.class);

    @Inject
    private CartRepository cartRepository;
    @Inject
    private CartItemRepository cartItemRepository;
    @Inject
    private ProductRepository productRepository;
    @Inject
    private CartItemMapper cartItemMapper;
    @Inject
    private CouponRepository couponRepository;
    private final Map<String, Cart> carts = new ConcurrentHashMap<>(4);

    @RequestMapping(value = "/carts/{visitor:.+}/item/{quantity}/{productName}",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<CartItemDTO> add(@PathVariable("visitor") String visitor,
                                           @PathVariable("quantity") int quantity,
                                           @PathVariable("productName") String productName) {
        final CartItemDTO item = new CartItemDTO();
        final Cart cart = carts.get(visitor);
        final CartItem cartItem = new CartItem();
        final Optional<Product> product = productRepository.findByShortname(productName);
        if (!product.isPresent())
            return ResponseEntity.badRequest().body(item);
        cartItem.setProduct(product.get());
        cartItem.setPrice(product.get().getPrice() * quantity);
        if (null == cart) {
            final Cart brandNew = new Cart();
            final HashSet<CartItem> items = new HashSet<>();
            items.add(cartItem);
            brandNew.setItems(items);
            cartItem.setCart(brandNew);
            carts.put(visitor, brandNew);
            cartRepository.saveAndFlush(brandNew);
        } else {
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
            cartRepository.saveAndFlush(cart);
        }
        cartItemRepository.saveAndFlush(cartItem);
        item.setId(cartItem.getId());
        item.setPrice(cartItem.getPrice());
        item.setCartId(cartItem.getCart().getId());
        item.setProductId(product.get().getId());
        return ResponseEntity.ok(item);
    }

    @RequestMapping(value = "/carts/{visitor:.+}/item/{productName}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public void remove(@PathVariable("visitor") String visitor,
                       @PathVariable("productName") String productName) {
        final Cart cart = carts.get(visitor);
        if (null == cart)
            return;
        final Set<CartItem> items = cart.getItems();
        final List<CartItem> cartItems = items.stream().collect(Collectors.partitioningBy(new Predicate<CartItem>() {
            @Override
            public boolean test(CartItem cartItem) {
                return productName.equalsIgnoreCase(cartItem.getProduct().getShortname());
            }
        })).get(false);
        cart.setItems(cartItems.stream().collect(Collectors.toSet()));
        cartRepository.saveAndFlush(cart);
    }

    @RequestMapping(value = "/carts/invoice/{visitor:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<CartDTO> getInvoice(@PathVariable("visitor") String visitor) {
        final CartDTO dto = new CartDTO();
        final Cart cart = carts.get(visitor);
        if (null == cart)
            return ResponseEntity.badRequest().body(dto);
        dto.setTotal(processCartItems(dto, cart));
        cartRepository.saveAndFlush(cart);
        return ResponseEntity.ok(dto);
    }

    private Double processCartItems(CartDTO dto, Cart cart) {
        Double total = 0D;
        for (CartItem item : cart.getItems()) {
            dto.add(cartItemMapper.cartItemToCartItemDTO(item));
            total += item.getPrice();
        }
        cart.setAmount(total);
        return total;
    }

    @RequestMapping(value = "/carts/invoice/{visitor:.+}/coupon/{coupon:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<CartDTO> getInvoiceWithCoupon(@PathVariable("visitor") String visitor,
                                                        @PathVariable("coupon") String code) {
        final CartDTO dto = new CartDTO();
        final Cart cart = carts.get(visitor);
        if (null == cart)
            return ResponseEntity.badRequest().body(dto);
        final Double total = processCartItems(dto, cart);
        final Optional<Coupon> coupon = couponRepository.findByCode(code);
        if (coupon.isPresent()) {
            final Double discount = coupon.get().getDiscount();
            if (coupon.get().isPercentage()) {
                final double percentageDiscount = discount * total / 100;
                cart.setAmount(total - percentageDiscount);
                dto.setDiscount(percentageDiscount);
            } else {
                cart.setAmount(total - discount);
                dto.setDiscount(discount);
            }
        }
        dto.setTotal(cart.getAmount());
        cartRepository.saveAndFlush(cart);
        return ResponseEntity.ok(dto);
    }

    /**
     * POST  /carts : Create a new cart.
     *
     * @param cart the cart to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cart, or with status 400 (Bad Request) if the cart has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/carts",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cart> createCart(@RequestBody Cart cart) throws URISyntaxException {
        log.debug("REST request to save Cart : {}", cart);
        if (cart.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("cart", "idexists", "A new cart cannot already have an ID")).body(null);
        }
        Cart result = cartRepository.save(cart);
        return ResponseEntity.created(new URI("/api/carts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("cart", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /carts : Updates an existing cart.
     *
     * @param cart the cart to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cart,
     * or with status 400 (Bad Request) if the cart is not valid,
     * or with status 500 (Internal Server Error) if the cart couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/carts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cart> updateCart(@RequestBody Cart cart) throws URISyntaxException {
        log.debug("REST request to update Cart : {}", cart);
        if (cart.getId() == null) {
            return createCart(cart);
        }
        Cart result = cartRepository.save(cart);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("cart", cart.getId().toString()))
            .body(result);
    }

    /**
     * GET  /carts : get all the carts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of carts in body
     */
    @RequestMapping(value = "/carts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Cart> getAllCarts() {
        log.debug("REST request to get all Carts");
        List<Cart> carts = cartRepository.findAll();
        return carts;
    }

    /**
     * GET  /carts/:id : get the "id" cart.
     *
     * @param id the id of the cart to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cart, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/carts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cart> getCart(@PathVariable Long id) {
        log.debug("REST request to get Cart : {}", id);
        Cart cart = cartRepository.findOne(id);
        return Optional.ofNullable(cart)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /carts/:id : delete the "id" cart.
     *
     * @param id the id of the cart to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/carts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        log.debug("REST request to delete Cart : {}", id);
        cartRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("cart", id.toString())).build();
    }

}
