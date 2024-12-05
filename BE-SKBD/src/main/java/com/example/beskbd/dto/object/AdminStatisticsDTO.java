package com.example.beskbd.dto.object;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatisticsDTO {
    private int totalUsers;
    private int totalProducts;
    private int totalOrders;
    private int totalCategories;
    private int totalReviews;
}
