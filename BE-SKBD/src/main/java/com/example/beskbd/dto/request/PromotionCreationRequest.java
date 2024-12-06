package com.example.beskbd.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCreationRequest {
    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    private List<PromotionProductRequest> promotionProductList;

}
