package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.dto.*;
import com.unimelbproject.healthcareequipmentassistant.interfaces.IResponse;
import com.unimelbproject.healthcareequipmentassistant.interfaces.Response;
import com.unimelbproject.healthcareequipmentassistant.models.Order;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.services.EmailService;
import com.unimelbproject.healthcareequipmentassistant.services.OrderService;
import com.unimelbproject.healthcareequipmentassistant.annotations.RequirePermission;
import com.unimelbproject.healthcareequipmentassistant.security.Permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order API", description = "Operations related to customer orders.")
public class OrderController {

    // Using constructor injection for best practice
    private final OrderService orderService;
    private final EmailService emailService;

    public OrderController(OrderService orderService, EmailService emailService) {
        this.orderService = orderService;
        this.emailService = emailService;
    }

    /**
     * Retrieve a paginated list of all orders.
     * Requires administrative privileges.
     *
     * @param cursor Page number (starting from 0)
     * @param size   Number of orders per page
     * @return A page of orders
     */
    @Operation(summary = "Get all orders (paginated)", description = "Returns a page of all orders. Requires admin role.")
    @GetMapping
    public ResponseEntity<Response<Page<OrderSummaryDto>>> getAllOrders(
            @RequestParam(defaultValue = "0") int cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderSummaryDto> page = orderService.getAllOrders(cursor, size);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * Retrieve all orders for the currently authenticated user.
     *
     * @param user The authenticated user's details (injected by Spring Security)
     * @return A list of orders for the current user.
     */
    @Operation(summary = "Get orders for the current user", description = "Returns a list of orders associated with the authenticated user.")
    @GetMapping("/my")
    public IResponse<List<Order>> getMyOrders(@AuthenticationPrincipal User user) {
        if (user == null || user.getId() == null) {
            // Using Response.failure(String message) from interfaces.Response
            return Response.failure("User not authenticated or user ID not available.");
        }
        List<Order> userOrders = orderService.getOrdersByUserId(user.getId());
        // Using Response.success(T data) from interfaces.Response
        return Response.success(userOrders);
    }

    /**
     * Search orders by business partner username.
     *
     * @param name The username of the business partner to search for
     * @return A list of orders placed by business partners with the given username
     */
    @Operation(summary = "Search orders by business partner name", description = "Returns a list of orders placed by business partners with the specified username.")
    @RequirePermission(Permission.ORDER_READ)
    @GetMapping("/search")
    public IResponse<List<Order>> searchOrdersByPartnerName(@RequestParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            return Response.failure("Partner name parameter is required and cannot be empty.");
        }
        
        List<Order> orders = orderService.getOrdersByPartnerUsername(name.trim());
        return Response.success(orders);
    }

    /**
     * Retrieve detailed information for a specific order by its ID.
     * Includes product details for items within the order.
     * After the order is placed, an email will be sent to the user.
     *
     * @param orderId The ID of the order.
     * @return The order details DTO if found, otherwise 404.
     */
    @Operation(summary = "Get order details by ID", description = "Returns detailed information for a specific order, including associated product details.")
    @GetMapping("/{orderId}")
    public ResponseEntity<Response<OrderDetailsDto>> getOrderDetails(@PathVariable Long orderId) {
        Optional<OrderDetailsDto> orderDetails = orderService.getOrderDetails(orderId);

        return orderDetails.map(details -> ResponseEntity.ok(Response.success(details)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.failure("Order not found")));
    }

    @Operation(summary = "Place order", description = "Create an order with line items for the authenticated user.")
    @PostMapping("/place")
    public ResponseEntity<Response<OrderDetailsDto>> placeOrder(@AuthenticationPrincipal User user,
                                                                @Valid @RequestBody PlaceOrderRequest request) {
        if (user == null || user.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Response.failure("User not authenticated."));
        }

        Optional<OrderDetailsDto> placed = orderService.placeOrder(user.getId(), request);

        if (placed.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Response.failure("Invalid order payload or empty items."));
        }

        //notification mail when order placed
        emailService.sendEmail(user.getEmail(), "[NOV] Order placed successfully!", "Congratulations! Your order ("+ request.getItems().toString() + ") has been successfully placed.");

        // 201 Created is appropriate for new resource
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success(placed.get()));
    }

    /**
     * Update the status of an order.
     *
     * @param orderId The ID of the order to update
     * @param request The status update request containing the new status
     * @return The updated order or error response
     */
    @Operation(summary = "Update order status", description = "Updates the status of a specific order by ID.")
    @RequirePermission(Permission.ORDER_UPDATE)
    @PostMapping("/{orderId}/status")
    public ResponseEntity<Response<Order>> updateOrderStatus(@PathVariable Long orderId,
                                                           @Valid @RequestBody UpdateOrderStatusRequest request) {
        Optional<Order> updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
        
        if (updatedOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.failure("Order not found"));
        }

        return ResponseEntity.ok(Response.success(updatedOrder.get()));
    }

    /**
     * Add a message/comment to an order.
     *
     * @param orderId The ID of the order to add message to
     * @param request The message request containing the message content
     * @return The updated order with the new message or error response
     */
    @Operation(summary = "Add message to order", description = "Adds a message/comment to a specific order by ID.")
    @RequirePermission(Permission.ORDER_UPDATE)
    @PostMapping("/{orderId}/message")
    public ResponseEntity<Response<Order>> addOrderMessage(@PathVariable Long orderId,
                                                         @Valid @RequestBody OrderMessageRequest request) {
        Optional<Order> updatedOrder = orderService.addOrderMessage(orderId, request.getMessage());
        
        if (updatedOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.failure("Order not found"));
        }
        
        return ResponseEntity.ok(Response.success(updatedOrder.get()));
    }
}