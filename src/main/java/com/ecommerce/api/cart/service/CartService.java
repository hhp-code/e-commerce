package com.ecommerce.api.cart.service;

import com.ecommerce.api.cart.service.repository.CartItemRepository;
import com.ecommerce.api.cart.service.repository.CartRepository;
import com.ecommerce.api.order.service.repository.UserRepository;
import com.ecommerce.api.product.service.repository.ProductRepository;
import com.ecommerce.domain.Cart;
import com.ecommerce.domain.CartItem;
import com.ecommerce.domain.Product;
import com.ecommerce.domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional(readOnly = true)
    public Cart getCart(Long cartId) {
        return cartRepository.getById(cartId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Cart addItemToCart(CartCommand.Add add) {
        User user = userRepository.getById(add.userId()).orElseThrow(EntityNotFoundException::new);
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart = cartRepository.saveAndGet(cart).orElseThrow(EntityNotFoundException::new);
        }
        Product product = productRepository.getProduct(add.productId()).orElseThrow(EntityNotFoundException::new);
        CartItem cartItem = new CartItem(product, add.quantity());
        cartItemRepository.save(cartItem);
        cart.addCartItem(cartItem);
        return cartRepository.saveAndGet(cart).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }

    @Transactional
    public Cart removeItemFromCart(Long productId, Long userId) {
        User user = getUser(userId);
        Cart cart = user.getCart();
        Product product = getProduct(productId);
        CartItem cartItem = cart.getCartItem(product).orElseThrow(EntityNotFoundException::new);
        cart.removeCartItem(cartItem);
        return cart;
    }

    @Transactional
    public Cart updateCartItemQuantity(CartCommand.Update update) {
        long userId = update.userId();
        User user = getUser(userId);
        Cart cart = user.getCart();
        Product product = getProduct(update.productId());
        CartItem cartItem = cart.getCartItem(product).orElseThrow(EntityNotFoundException::new);
        cartItem.setQuantity(update.quantity());
        return cart;
    }



    private User getUser(long userId) {
        return userRepository.getById(userId).orElseThrow(
                EntityNotFoundException::new
        );
    }

    private Product getProduct(long update) {
        return productRepository.getProduct(update).orElseThrow(
                EntityNotFoundException::new
        );
    }
}
