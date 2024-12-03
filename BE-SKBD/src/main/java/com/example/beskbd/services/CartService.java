package com.example.beskbd.services;

import com.example.beskbd.dto.request.CartDTO;
import com.example.beskbd.dto.response.CartItemDTO;
import com.example.beskbd.entities.Cart;
import com.example.beskbd.entities.CartItem;
import com.example.beskbd.entities.Product;
import com.example.beskbd.entities.ProductAttribute;
import com.example.beskbd.repositories.CartItemRepository;
import com.example.beskbd.repositories.CartRepository;
import com.example.beskbd.repositories.ProductAttributeRepository;
import com.example.beskbd.repositories.ProductRepository; // Ensure you have this
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductAttributeRepository productRepository; // Added ProductRepository

    // Method to convert Cart to CartDTO
    private CartDTO convertToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(cart.getCartItems().stream()
                .map(this::convertCartItemToDTO)
                .collect(Collectors.toList()));
        // Add other properties as needed
        return cartDTO;
    }

    // Method to convert CartItem to CartItemDTO
    private CartItemDTO convertCartItemToDTO(CartItem item) {
        return new CartItemDTO(item.getProduct().getId(),
                item.getProduct().getColor(),
                item.getQuantity(),
                item.getProduct().getPrice());
    }

    public CartDTO loadCartById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return convertToDTO(cart);
    }

    public List<CartItemDTO> getAllItems(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return cart.getCartItems().stream()
                .map(this::convertCartItemToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal calculateTotalPrice(Long cartId) {
        CartDTO cart = loadCartById(cartId);
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItemDTO item : cart.getItems()) {
            BigDecimal itemPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal taxedPrice = itemPrice.multiply(BigDecimal.valueOf(1.2)); // Adjust tax if needed
            totalPrice = totalPrice.add(taxedPrice);
        }

        return totalPrice;
    }

    public CartItemDTO addCart(String nameCart) {
        Cart cart = new Cart();
        cart.setNameCart(nameCart);
        cartRepository.save(cart);
        return convertCartItemToDTO(cart.getCartItems().get(0));
    }

    public void addItemToCarts(Long cartId, Long productId, int quantity) {
        // Retrieve the cart by cartId
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Retrieve the product by productId
        ProductAttribute product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if the item already exists in the cart
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // If the item exists, update the quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // If the item does not exist, create a new CartItem
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setQuantity(quantity);
            cartItem.setProduct(product); // Correct method to set the product
            cartItem.setCreatedDate(LocalDateTime.now());

            // Add the new CartItem to the cart
            cart.getCartItems().add(cartItem);
        }

        // Save the cart (this will also save the CartItem if it's new)
        cartRepository.save(cart);
    }

    public void removeItemFromCart(Long cartId, Long itemId) {
        // Retrieve the cart by cartId
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Find the cart item with the specified itemId
        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Remove the cart item from the cart
        cart.getCartItems().remove(itemToRemove);

        // Save the cart (this will also cascade save the changes since it's a relationship)
        cartRepository.save(cart);
    }

    public void deleteCart(Long cartId) {
            // Retrieve the cart by cartId
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        // Remove the cart item from the cart
        cartRepository.delete(cart);
    }

    public void updateCart(Long cartId, String nameCart) {
            // Retrieve the cart by cartId
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        // Update the cart name
        cart.setNameCart(nameCart);
        // Save the cart (this will also save the CartItem if it's new)
        cartRepository.save(cart);
    }
}