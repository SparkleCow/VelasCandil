package com.velas.candil.services.product;

import com.velas.candil.entities.candle.Candle;
import com.velas.candil.entities.cartItem.CartItem;
import com.velas.candil.entities.order.Order;
import com.velas.candil.entities.shoppingCart.ShoppingCart;
import com.velas.candil.entities.user.User;
import com.velas.candil.mappers.ShoppingCartMapper;
import com.velas.candil.models.shoppingCart.ShoppingCartResponseDto;
import com.velas.candil.repositories.CandleRepository;
import com.velas.candil.repositories.ShoppingCartRepository;
import com.velas.candil.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartServiceImp implements ShoppingCartService{

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserRepository userRepository;
    private final CandleRepository candleRepository;

    @Override
    public ShoppingCartResponseDto getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));

        return shoppingCartMapper.toDto(shoppingCartRepository.findByUser(user)
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart();
                    cart.setUser(user);
                    return shoppingCartRepository.save(cart);
                }));
    }

    @Override
    public ShoppingCartResponseDto getCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        return shoppingCartMapper.toDto(shoppingCartRepository.findByUser(user)
                .orElseThrow(()->new RuntimeException("ShoppingCart not found")));
    }

    @Override
    public ShoppingCartResponseDto addItem(Long userId, Long candleId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShoppingCart cart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        Candle candle = candleRepository.findById(candleId)
                .orElseThrow(() -> new RuntimeException("Candle not found"));

        CartItem existingItem = cart.getCartItems().stream()
                .filter(x -> Objects.equals(x.getCandle().getId(), candleId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {

            int newQuantity = existingItem.getQuantity() + 1;

            if (candle.getStock() < newQuantity) {
                throw new RuntimeException("Not enough stock available");
            }

            existingItem.increaseQuantity(1);

        } else {

            if (candle.getStock() < 1) {
                throw new RuntimeException("Out of stock");
            }

            CartItem cartItem = new CartItem();
            cartItem.setCandle(candle);
            cartItem.setQuantity(1);
            cartItem.setPriceSnapshot(candle.getPrice());
            cartItem.recalculateSubtotal();

            cart.addItem(cartItem);
        }

        cart.recalculateSubTotal();

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Override
    public ShoppingCartResponseDto removeItem(Long userId, Long candleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(x -> Objects.equals(x.getCandle().getId(), candleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Shopping cart item not found"));

        shoppingCart.removeItem(cartItem);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ShoppingCartResponseDto increaseItemQuantity(Long userId, Long candleId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShoppingCart cart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("ShoppingCart not found"));

        CartItem item = cart.getCartItems().stream()
                .filter(x -> Objects.equals(x.getCandle().getId(), candleId))
                .findFirst()
                .orElse(null);

        if (item != null) {

            int newQuantity = item.getQuantity() + 1;

            if (item.getCandle().getStock() < newQuantity) {
                throw new RuntimeException("Not enough stock available");
            }

            item.increaseQuantity(1);

        } else {

            Candle candle = candleRepository.findById(candleId)
                    .orElseThrow(() -> new RuntimeException("Candle not found"));

            if (candle.getStock() < 1) {
                throw new RuntimeException("Out of stock");
            }

            CartItem cartItem = new CartItem();
            cartItem.setCandle(candle);
            cartItem.setQuantity(1);
            cartItem.setPriceSnapshot(candle.getPrice());
            cartItem.recalculateSubtotal();

            cart.addItem(cartItem);
        }

        cart.recalculateSubTotal();

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Override
    public ShoppingCartResponseDto decreaseItemQuantity(Long userId, Long candleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShoppingCart cart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(x -> Objects.equals(x.getCandle().getId(), candleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Shopping cart item not found"));

        if (cartItem.getQuantity() > 1) {
            cartItem.decreaseQuantity(1);
        } else {
            cart.removeItem(cartItem);
        }

        cart.recalculateSubTotal();

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Override
    public ShoppingCartResponseDto clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShoppingCart cart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        cart.getCartItems().clear();
        cart.recalculateSubTotal();

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Override
    public Order checkout(Long userId) {
        //TODO
        return null;
    }
}
