package com.project.shopapp.services.orders;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.dtos.OrderWithDetailsDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final OrderDetailRepository orderDetailRepository;
  private final ModelMapper modelMapper;

  @Override
  @Transactional
  public Order createOrder(OrderDTO orderDTO) throws DataNotFoundException {
    User user = validateUser(orderDTO.getUserId());
    validateShippingDate(orderDTO.getShippingDate());

    Order order = mapOrderDTOToOrder(orderDTO);
    order.setUser(user);
    order.setOrderDate(LocalDate.now());
    order.setStatus(OrderStatus.PENDING);
    order.setActive(true);
    orderRepository.save(order);

    List<OrderDetail> orderDetails = mapCartItemsToOrderDetails(orderDTO.getCartItems(), order);
    orderDetailRepository.saveAll(orderDetails);
    return order;
  }

  private User validateUser(Long userId) throws DataNotFoundException {
    return userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + userId));
  }

  private void validateShippingDate(LocalDate shippingDate) throws DataNotFoundException {
    if (shippingDate == null || shippingDate.isBefore(LocalDate.now())) {
      throw new DataNotFoundException("Shipping date must be at least today!");
    }
  }

  private Order mapOrderDTOToOrder(OrderDTO orderDTO) {
    Order order = new Order();
    modelMapper.map(orderDTO, order);
    return order;
  }

  private List<OrderDetail> mapCartItemsToOrderDetails(List<CartItemDTO> cartItems, Order order) throws DataNotFoundException {
    List<OrderDetail> orderDetails = new ArrayList<>();
    for (CartItemDTO cartItemDTO : cartItems) {
      Product product = productRepository.findById(cartItemDTO.getProductId())
              .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + cartItemDTO.getProductId()));

      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrder(order);
      orderDetail.setProduct(product);
      orderDetail.setNumberOfProducts(cartItemDTO.getQuantity());
      orderDetail.setPrice(product.getPrice());
      orderDetails.add(orderDetail);
    }
    return orderDetails;
  }

  @Override
  @Transactional
  public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + id));
    User user = validateUser(orderDTO.getUserId());

    modelMapper.map(orderDTO, order);
    order.setUser(user);

    return orderRepository.save(order);
  }

  @Override
  @Transactional
  public void deleteOrder(Long id) throws DataNotFoundException {
    Order order = orderRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + id));
    // Perform soft delete
    order.setActive(false);
    orderRepository.save(order);
  }

  @Override
  public Order getOrder(Long id) throws DataNotFoundException {
    return orderRepository.findById(id).orElseThrow(() ->
            new DataNotFoundException("Cannot find order with id: " + id));
  }

  @Override
  public List<Order> findByUserId(Long userId) {
    return orderRepository.findByUserId(userId);
  }

  @Override
  public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
    return orderRepository.findByKeyword(keyword, pageable);
  }
}