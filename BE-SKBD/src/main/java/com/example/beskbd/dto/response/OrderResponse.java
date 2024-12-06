package com.example.beskbd.dto.response;

import com.example.beskbd.entities.Order;
import com.example.beskbd.entities.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderResponse {
    private Long id; // Unique identifier for the order
    private Long customerId; // ID of the customer associated with the order
    private List<OrderItemResponse> items; // A list of items in the order
    private LocalDateTime orderDate; // Date when the order was placed
    private BigDecimal totalAmount; // Total amount for the order
    private Order.Status status; // Status of the order (e.g., Pending, Shipped, Delivered)
}
