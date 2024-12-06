package com.example.beskbd.dto.request;

import com.example.beskbd.dto.object.OrderAttributeDto;
import com.example.beskbd.dto.response.CreateOrderResponse;
import com.example.beskbd.entities.User;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrderCreationRequest {
    private User userId;
    private String productDescription;
    private List<CreateOrderResponse> orderItems;
    private String shippingAddress;
    private Long orderItemsID;
    private List<OrderAttributeDto> attributes;


}
