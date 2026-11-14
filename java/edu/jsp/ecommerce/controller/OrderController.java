package edu.jsp.ecommerce.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.request.OrderRequest;
import edu.jsp.ecommerce.response.ApiResponse;
import edu.jsp.ecommerce.response.OrderResponse;
import edu.jsp.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @RequestParam String email,
            @Valid @RequestBody OrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        orderService.placeOrder(email, request),
                        "Order placed"
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestParam String email,
            Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        orderService.getUserOrders(email, pageable),
                        "Orders fetched"
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable Long id,
            @RequestParam String email) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        orderService.getOrderById(id, email),
                        "Order fetched"
                ));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long id,
            @RequestParam String email,
            @RequestParam(required = false) String reason) {

        orderService.cancelOrder(id, email, reason);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Order cancelled")
        );
    }
}
