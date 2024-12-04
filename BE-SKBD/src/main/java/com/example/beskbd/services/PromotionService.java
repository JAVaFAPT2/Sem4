package com.example.beskbd.services;

import com.example.beskbd.dto.object.ProductDTO;
import com.example.beskbd.dto.request.PromotionCreationRequest;
import com.example.beskbd.dto.object.PromotionDTO; // Assuming you have a PromotionDTO
import com.example.beskbd.dto.response.PromotionProductDTO;
import com.example.beskbd.entities.Promotion;
import com.example.beskbd.entities.PromotionProduct;
import com.example.beskbd.repositories.PromotionProductRepository;
import com.example.beskbd.repositories.PromotionRepository;
import com.example.beskbd.repositories.ProductRepository; // Assuming you have a ProductRepository
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {
    @Autowired
    private PromotionProductRepository promotionProductRepository;
    @Autowired
    private  PromotionRepository promotionRepository;
    @Autowired
    private  ProductRepository productRepository; // For fetching Product entities

    public PromotionDTO createPromotion(PromotionCreationRequest request) {
        // Create a new Promotion entity
        Promotion promotion = new Promotion();
        promotion.setDescription(request.getDescription());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());

        // Process the list of promotion products
        if (request.getPromotionProductList() != null) {
            List<PromotionProduct> promotionProducts = request.getPromotionProductList().stream()
                    .map(productRequest -> {
                        PromotionProduct promotionProduct = new PromotionProduct();
                        // Fetch the product from the repository using the product ID
                        var product = productRepository.findById(productRequest.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found: " + productRequest.getProductId()));

                        promotionProduct.setProduct(product);
                        promotionProduct.setPercentage(productRequest.getPercentage());
                        return promotionProduct;
                    }).collect(Collectors.toList());

            promotion.setPromotionProductList(promotionProducts);
        }

        // Save the promotion
        Promotion savedPromotion = promotionRepository.save(promotion);
        return convertToDTO(savedPromotion);
    }

    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PromotionDTO getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found: " + id));
        return convertToDTO(promotion);
    }
    public List<ProductDTO> getPromotionProducts(Long promotionId) {
        // Fetching PromotionProducts by promotionId
        List<PromotionProduct> promotionProducts = promotionProductRepository.findByPromotionId(promotionId);

        // Mapping PromotionProduct to ProductDTO
        return promotionProducts.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    // Example of a mapping function
    private ProductDTO mapToProductDTO(PromotionProduct promotionProduct) {
        var product = promotionProduct.getProduct(); // Get the product associated with the promotion product
        var firstAttribute = product.getAttributes().isEmpty() ? null : product.getAttributes().get(0); // Safely get the first attribute

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(firstAttribute != null ? firstAttribute.getPrice() : BigDecimal.ZERO) // Set default price if no attributes
                .build();
    }

    private PromotionDTO convertToDTO(Promotion promotion) {
        // Convert Promotion entity to PromotionDTO
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setDescription(promotion.getDescription());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());

        // Convert PromotionProduct entities to DTOs if needed
        List<PromotionProductDTO> productDTOs = promotion.getPromotionProductList().stream()
                .map(product -> new PromotionProductDTO(product.getId(), product.getProduct().getId(), product.getPercentage()))
                .collect(Collectors.toList());

        dto.setPromotionProductList(productDTOs);
        return dto;
    }
}