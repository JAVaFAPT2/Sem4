package com.project.shopapp.services.orderdetails;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderDetailService implements IOrderDetailService {
  private final OrderRepository orderRepository;
  private final OrderDetailRepository orderDetailRepository;
  private final ProductRepository productRepository;

  private static final Logger logger = LoggerFactory.getLogger(OrderDetailService.class);

  @Override
  @Transactional
  public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
    Order order = getOrder(orderDetailDTO.getOrderId());
    Product product = getProduct(orderDetailDTO.getProductId());

    OrderDetail orderDetail = OrderDetail.builder()
            .order(order)
            .product(product)
            .numberOfProducts(orderDetailDTO.getNumberOfProducts())
            .price(orderDetailDTO.getPrice())
            .totalMoney(orderDetailDTO.getTotalMoney())
            .color(orderDetailDTO.getColor())
            .build();

    logger.info("Creating Order Detail for Order ID: {}", order.getId());

    return orderDetailRepository.save(orderDetail);
  }

  private Order getOrder(Long orderId) throws DataNotFoundException {
    return orderRepository.findById(orderId)
            .orElseThrow(() -> new DataNotFoundException("Cannot find Order with id: " + orderId));
  }

  private Product getProduct(Long productId) throws DataNotFoundException {
    return productRepository.findById(productId)
            .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + productId));
  }

  @Override
  public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
    return orderDetailRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Cannot find OrderDetail with id: " + id));
  }

  @Override
  @Transactional
  public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
    OrderDetail existingOrderDetail = getOrderDetail(id);
    Order existingOrder = getOrder(orderDetailDTO.getOrderId());
    Product existingProduct = getProduct(orderDetailDTO.getProductId());

    existingOrderDetail.setPrice(orderDetailDTO.getPrice());
    existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
    existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
    existingOrderDetail.setColor(orderDetailDTO.getColor());
    existingOrderDetail.setOrder(existingOrder);
    existingOrderDetail.setProduct(existingProduct);

    logger.info("Updating Order Detail with ID: {}", id);

    return orderDetailRepository.save(existingOrderDetail);
  }

  @Override
  @Transactional
  public void deleteById(Long id) throws DataNotFoundException {
    if (!orderDetailRepository.existsById(id)) {
      throw new DataNotFoundException("Cannot find OrderDetail with id: " + id);
    }
    orderDetailRepository.deleteById(id);
    logger.info("Deleted Order Detail with ID: {}", id);
  }

  @Override
  public List<OrderDetail> findByOrderId(Long orderId) {
    return orderDetailRepository.findByOrderId(orderId);
  }
}