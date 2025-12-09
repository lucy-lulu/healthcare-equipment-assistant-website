package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.dto.OrderDetailsDto;
import com.unimelbproject.healthcareequipmentassistant.dto.ItemInOrderDto;
import com.unimelbproject.healthcareequipmentassistant.dto.OrderSummaryDto;
import com.unimelbproject.healthcareequipmentassistant.models.Order;
import com.unimelbproject.healthcareequipmentassistant.models.OrderProduct;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.repositories.OrderRepo;
import com.unimelbproject.healthcareequipmentassistant.repositories.OrderProductRepo;
import com.unimelbproject.healthcareequipmentassistant.repositories.ProductRepo; // Needed to fetch product details
import com.unimelbproject.healthcareequipmentassistant.dto.PlaceOrderRequest;
import com.unimelbproject.healthcareequipmentassistant.models.Product;
import com.unimelbproject.healthcareequipmentassistant.repositories.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepository;

    @Autowired
    private OrderProductRepo orderProductRepository;

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private UserRepo userRepo;

    /**
     * Retrieve a paginated list of all orders.
     * @param page Page number (starting from 0)
     * @param size Number of orders per page
     * @return A page of orders
     */
    public Page<OrderSummaryDto> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAll(pageable);

        return orderRepository.findAll(pageable)
                .map(order -> {
                    User user = userRepo.findById(order.getUserId()).orElse(null);
                    String username = user != null ? user.getUsername() : null;
                    String email = user != null ? user.getEmail() : null;

                    return new OrderSummaryDto(
                            order.getId(),
                            order.getOrderDate(),
                            order.getTotalAmount(),
                            order.getStatus(),
                            order.getUserId(),
                            username,
                            email,
                            order.getOrderTrackingNumber(),
                            order.getShippingTrackingNumber(),
                            order.getComment()
                    );
                });
    }

    /**
     * Retrieve a list of orders for a specific user.
     * @param userId The ID of the user.
     * @return A list of orders belonging to the user.
     */
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * Retrieve orders placed by business partners with a specific username.
     * @param username The username of the business partner.
     * @return A list of orders placed by business partners with the given username.
     */
    public List<Order> getOrdersByPartnerUsername(String username) {
        return orderRepository.findOrdersByPartnerUsername(username, User.UserRole.partner);
    }

    /**
     * Update the status of an order.
     * @param orderId The ID of the order to update.
     * @param newStatus The new status to set.
     * @return Optional containing the updated order if successful, empty if order not found.
     */
    @Transactional
    public Optional<Order> updateOrderStatus(Long orderId, String newStatus) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        
        if (orderOptional.isEmpty()) {
            return Optional.empty();
        }
        
        Order order = orderOptional.get();
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        
        return Optional.of(updatedOrder);
    }

    /**
     * Add a message/comment to an order.
     * @param orderId The ID of the order to add message to.
     * @param message The message to add.
     * @return Optional containing the updated order if successful, empty if order not found.
     */
    @Transactional
    public Optional<Order> addOrderMessage(Long orderId, String message) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        
        if (orderOptional.isEmpty()) {
            return Optional.empty();
        }
        
        Order order = orderOptional.get();
        // Append new message to existing comment
        String existingComment = order.getComment();
        String newComment;
        
        if (existingComment == null || existingComment.trim().isEmpty()) {
            newComment = message;
        } else {
            newComment = existingComment + "\n---\n" + message;
        }
        
        order.setComment(newComment);
        Order updatedOrder = orderRepository.save(order);
        
        return Optional.of(updatedOrder);
    }

    /**
     * Retrieve detailed information for a specific order, including product details.
     * @param orderId The ID of the order.
     * @return An Optional containing OrderDetailsDTO if the order is found, otherwise empty.
     */
    @Transactional(readOnly = true) // Use transactional for fetching related data
    public Optional<OrderDetailsDto> getOrderDetails(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOptional.get();

        List<OrderProduct> orderProducts = orderProductRepository.findById_OrderId(orderId);

        // Map OrderProduct entities to ItemInOrderDto
        List<ItemInOrderDto> items = orderProducts.stream()
                .map(op -> {
                    // Fetch product details for SKU and Name
                    // Consider adding a cache for products or fetching all needed products in one go
                    // if performance is critical for orders with many diverse products
                    return productRepository.findById(op.getProduct().getId())
                            .map(product -> new ItemInOrderDto(
                                    product.getId(),
                                    product.getSku(),
                                    product.getName(),
                                    op.getPrice(),
                                    op.getQuantity()
                            ))
                            .orElse(null); // Handle case where product might not be found (shouldn't happen with proper foreign keys)
                })
                .filter(java.util.Objects::nonNull) // Filter out nulls if a product wasn't found
                .collect(Collectors.toList());

        // Create the OrderDetailsDTO
        OrderDetailsDto orderDetailsDTO = new OrderDetailsDto(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getUserId(),
                order.getOrderTrackingNumber(),
                order.getShippingTrackingNumber(),
                order.getComment(),
                items
        );

        userRepo.findById(order.getUserId()).ifPresent(u -> {
            orderDetailsDTO.setUsername(u.getUsername());
            orderDetailsDTO.setEmail(u.getEmail());
        });

        return Optional.of(orderDetailsDTO);
    }

    //tier price helper func
    private BigDecimal resolveTierPrice(Product p, Integer level) {
        if (level == null || level == 0) {
            // staff not allowed to place orders by your rule
            throw new IllegalArgumentException("Staff cannot place orders.");
        }
        return switch (level) {
            case 1 -> Objects.requireNonNullElse(p.getPrice1(), BigDecimal.ZERO);
            case 2 -> Objects.requireNonNullElse(p.getPrice2(), BigDecimal.ZERO);
            case 3 -> Objects.requireNonNullElse(p.getPrice3(), BigDecimal.ZERO);
            case 4 -> Objects.requireNonNullElse(p.getPrice4(), BigDecimal.ZERO);
            default -> throw new IllegalArgumentException("Unsupported user level: " + level);
        };
    }

    @Transactional
    public Optional<OrderDetailsDto> placeOrder(String userId, PlaceOrderRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            return Optional.empty();
        }

        //add: Load user to get level
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (user.getLevel() == null || user.getLevel() == 0) {
            throw new IllegalArgumentException("Staff cannot place orders.");
        }
        // 1) Create base order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PLACED"); // initial status
        order.setComment(request.getComment());
        order.setTotalAmount(BigDecimal.ZERO);
        order = orderRepository.save(order); // persist to get order ID

        BigDecimal total = BigDecimal.ZERO;

        // 2) For each item: fetch product
        for (PlaceOrderRequest.OrderItemPayload item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null) {
                continue;
            }

            BigDecimal unitPrice = resolveTierPrice(product, user.getLevel());
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(lineTotal);

            OrderProduct op = new OrderProduct(order, product, item.getQuantity(), unitPrice);
            orderProductRepository.save(op);
        }

        // 3) Update total and save
        order.setTotalAmount(total);
        orderRepository.save(order);

        // 4) Build OrderDetailsDto (reuse existing mapper logic style)
        List<OrderProduct> orderProducts = orderProductRepository.findById_OrderId(order.getId());
        List<ItemInOrderDto> items = orderProducts.stream()
                .map(op -> productRepository.findById(op.getProduct().getId())
                        .map(p -> new ItemInOrderDto(
                                p.getId(),
                                p.getSku(),
                                p.getName(),
                                op.getPrice(),
                                op.getQuantity()))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        OrderDetailsDto details = new OrderDetailsDto(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getUserId(),
                order.getOrderTrackingNumber(),
                order.getShippingTrackingNumber(),
                order.getComment(),
                items
        );

        userRepo.findById(order.getUserId()).ifPresent(u -> {
            details.setUsername(u.getUsername());
            details.setEmail(u.getEmail());
        });

        return Optional.of(details);
    }

}