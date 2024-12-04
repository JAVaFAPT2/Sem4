package com.example.beskbd.rest;

import com.example.beskbd.dto.object.ProductDTO;
import com.example.beskbd.dto.request.PromotionCreationRequest;
import com.example.beskbd.dto.response.ApiResponse;
import com.example.beskbd.dto.object.PromotionDTO;
import com.example.beskbd.services.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class RestPromotionController {

    private final PromotionService promotionService;

    @PostMapping("/create")
    public ApiResponse<PromotionDTO> createPromotion(@Valid @RequestBody PromotionCreationRequest request) {
        // Validation logic for date range
        if (request.getStartDate().isAfter(request.getEndDate())) {
            // Optionally, you can create a custom exception to handle this case
            throw new RuntimeException("Invalid date range: Start date must be before end date");
        }

        PromotionDTO promotionDTO = promotionService.createPromotion(request);
        return ApiResponse.<PromotionDTO>builder()
                .success(true)
                .data(promotionDTO)
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<PromotionDTO>> getAllPromotions() {
        return ApiResponse.<List<PromotionDTO>>builder()
                .success(true)
                .data(promotionService.getAllPromotions())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PromotionDTO> getPromotionById(@PathVariable Long id) {
        PromotionDTO promotionDTO = promotionService.getPromotionById(id);
        return ApiResponse.<PromotionDTO>builder()
                .success(true)
                .data(promotionDTO)
                .build();
    }

    @GetMapping("/products/{promotionId}")
    public ApiResponse<List<ProductDTO>> getPromotionProducts(@PathVariable Long promotionId) {
        List<ProductDTO> productDTOs = promotionService.getPromotionProducts(promotionId);
        return ApiResponse.<List<ProductDTO>>builder()
                .success(true)
                .data(productDTOs)
                .build();
    }
}