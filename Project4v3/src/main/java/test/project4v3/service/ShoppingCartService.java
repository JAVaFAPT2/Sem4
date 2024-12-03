package test.project4v3.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import test.project4v3.dto.CartDTO;
import test.project4v3.dto.CartItemDTO;
import test.project4v3.entity.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Service
@Setter
public class ShoppingCartService {

    // Map to hold user carts, keyed by user ID
    private final Map<String, CartDTO> userCarts = new HashMap<>();

    // Adds an item to the user's cart
    public void addToCart(User userId, CartItemDTO cartItem) {
        // Get or create a cart for the user
        CartDTO cart = userCarts.computeIfAbsent(String.valueOf(userId), id -> new CartDTO(id, new ArrayList<>()));

        // Check if the item already exists in the cart
        Optional<CartItemDTO> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(cartItem.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update the quantity if it exists
            existingItem.get().setQuantity(existingItem.get().getQuantity() + cartItem.getQuantity());
        } else {
            // Add new item to the cart
            cart.getItems().add(cartItem);
        }
    }

    // Clears all items from the user's cart
    public void clearCart(String userId) {
        CartDTO cart = userCarts.get(userId);
        if (cart != null) {
            cart.getItems().clear();
        }
    }

    // Removes a specific item from the user's cart
    public void removeFromCart(String userId, Long productId) {
        CartDTO cart = userCarts.get(userId);
        if (cart != null) {
            cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        }
    }

    // Retrieves the user's cart
    public CartDTO getCart(User userId) {
        return userCarts.getOrDefault(userId, new CartDTO(String.valueOf(userId), new ArrayList<>()));
    }
}