package com.example.beskbd.rest;

import com.example.beskbd.dto.object.CartDTO;
import com.example.beskbd.dto.response.ApiResponse;
import com.example.beskbd.dto.object.CartItemDTO;
import com.example.beskbd.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", maxAge = 360000)
public class RestCartController {

    private final CartService cartService;

    @Autowired
    public RestCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{cartId}")
    public ApiResponse<CartDTO> getBasketById(@PathVariable Long cartId) {
        CartDTO cart = cartService.loadCartById(cartId);
        return ApiResponse.<CartDTO>builder()
                .success(true)
                .data(cart)
                .build();
    }

    @GetMapping("/{cartId}/totalPrice")
    public ApiResponse<BigDecimal> calculateTotalPrice(@PathVariable Long cartId) {
        BigDecimal totalPrice = cartService.calculateTotalPrice(cartId);
        return ApiResponse.<BigDecimal>builder()
                .success(true)
                .data(totalPrice)
                .build();
    }

    @PostMapping()
    public ApiResponse<CartItemDTO> addCart(@RequestParam String nameCart) {
        CartItemDTO newBasket = cartService.addCart(nameCart);
        return ApiResponse.<CartItemDTO>builder()
                .success(true)
                .data(newBasket)
                .build();
    }

    @PostMapping("/{cartId}/items")
    public ApiResponse<String> addItemToBasket(@PathVariable Long cartId,
                                               @RequestParam Long productId,
                                               @RequestParam int quantity) {
        cartService.addItemToCarts(cartId, productId, quantity);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Item added to basket successfully")
                .build();
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ApiResponse<String> removeItemFromBasket(@PathVariable Long cartId,
                                                    @PathVariable Long itemId) {
        cartService.removeItemFromCart(cartId, itemId);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Item removed from basket successfully")
                .build();
    }

    @DeleteMapping("/{cartId}")
    public ApiResponse<String> deleteBasket(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Basket deleted successfully")
                .build();
    }

    @PutMapping("/{cartId}")
    public ApiResponse<String> updateBasket(@PathVariable Long cartId, @RequestParam String nameCart) {
        cartService.updateCart(cartId, nameCart);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Cart updated successfully")
                .build();
    }
}