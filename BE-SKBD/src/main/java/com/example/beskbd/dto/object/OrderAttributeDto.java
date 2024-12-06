package com.example.beskbd.dto.object;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderAttributeDto {
    @NotBlank
    private Long orderId;
    @NotBlank
    private Long customerId;
    @NotBlank
    private LocalDateTime orderDate;
    @NotBlank
    private BigDecimal totalAmount;
    @NotBlank
    private String status; // e.g., "Pending", "Completed", "Cancelled"
    @NotBlank
    private String shippingAddress;
}
