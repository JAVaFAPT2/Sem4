package com.example.beskbd.rest;

import com.example.beskbd.dto.request.OrderCreationRequest; // Assuming you have an order creation DTO
import com.example.beskbd.dto.response.ApiResponse;
import com.example.beskbd.dto.response.OrderResponse;
import com.example.beskbd.services.OrderService; // Assuming you have an OrderService
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 360000)
public class RestOrderController {

    private final OrderService orderService;

    @PostMapping("create")
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreationRequest request) {
        OrderResponse orderResponse = orderService.createNewOrder(request);
        return ApiResponse.<OrderResponse>builder()
                .data(orderResponse) // Use data instead of result
                .success(true)
                .build();
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ApiResponse.<List<OrderResponse>>builder()
                .data(orders)
                .success(true)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        return ApiResponse.<OrderResponse>builder()
                .data(orderResponse)
                .success(true)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrderById(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return ApiResponse.<Void>builder()
                .data(null) // No content to return
                .success(true)
                .build();
    }
}