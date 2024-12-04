package com.example.beskbd.dto.object;

import com.example.beskbd.dto.response.PromotionProductDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {
    private Long id;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<PromotionProductDTO> promotionProductList;
}