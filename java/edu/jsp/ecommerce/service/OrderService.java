package edu.jsp.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.jsp.ecommerce.enums.OrderStatus;
import edu.jsp.ecommerce.request.OrderRequest;
import edu.jsp.ecommerce.response.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(String email, OrderRequest request);
    OrderResponse getOrderById(Long id, String email);
    Page<OrderResponse> getUserOrders(String email, Pageable pageable);
    Page<OrderResponse> getAllOrders(Pageable pageable);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
    void cancelOrder(Long id, String email, String reason);
}
