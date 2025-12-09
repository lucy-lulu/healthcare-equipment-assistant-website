package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.dto.ItemInOrderDto;
import com.unimelbproject.healthcareequipmentassistant.dto.OrderDetailsDto;
import com.unimelbproject.healthcareequipmentassistant.dto.OrderSummaryDto;
import com.unimelbproject.healthcareequipmentassistant.dto.PlaceOrderRequest;
import com.unimelbproject.healthcareequipmentassistant.models.Order;
import com.unimelbproject.healthcareequipmentassistant.models.OrderProduct;
import com.unimelbproject.healthcareequipmentassistant.models.Product;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.repositories.OrderProductRepo;
import com.unimelbproject.healthcareequipmentassistant.repositories.OrderRepo;
import com.unimelbproject.healthcareequipmentassistant.repositories.ProductRepo;
import com.unimelbproject.healthcareequipmentassistant.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService (no Spring context).
 * Uses lenient strictness to avoid UnnecessaryStubbing on shared stubs.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock private OrderRepo orderRepository;
    @Mock private OrderProductRepo orderProductRepository;
    @Mock private ProductRepo productRepository;
    @Mock private UserRepo userRepo;

    @BeforeEach
    void setup() {
        // no-op
    }

    // ---------- Simple listings ----------

    @Test
    @DisplayName("getAllOrders(page,size) -> delegates to repo.findAll(Pageable)")
    void getAllOrders_ok() {
        PageRequest pr = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(new Order(), new Order()), pr, 2);
        when(orderRepository.findAll(pr)).thenReturn(page);

        Page<OrderSummaryDto> result = orderService.getAllOrders(0, 10);

        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(orderRepository).findAll(pr);
    }

    @Test
    @DisplayName("getOrdersByUserId -> delegates to repo.findByUserId")
    void getOrdersByUserId_ok() {
        when(orderRepository.findByUserId("u-1")).thenReturn(List.of(new Order()));
        List<Order> list = orderService.getOrdersByUserId("u-1");
        assertThat(list).hasSize(1);
        verify(orderRepository).findByUserId("u-1");
    }

    @Test
    @DisplayName("getOrdersByPartnerUsername -> delegates to repo.findOrdersByPartnerUsername with role=partner")
    void getOrdersByPartnerUsername_ok() {
        when(orderRepository.findOrdersByPartnerUsername("alice", User.UserRole.partner))
                .thenReturn(List.of(new Order(), new Order()));
        List<Order> list = orderService.getOrdersByPartnerUsername("alice");
        assertThat(list).hasSize(2);
        verify(orderRepository).findOrdersByPartnerUsername("alice", User.UserRole.partner);
    }

    // ---------- Status + Message updates ----------

    @Test
    @DisplayName("updateOrderStatus -> returns updated when order found")
    void updateOrderStatus_found() {
        Order o = new Order();
        o.setId(10L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(o));
        when(orderRepository.save(o)).thenReturn(o);

        Optional<Order> updated = orderService.updateOrderStatus(10L, "SHIPPED");
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo("SHIPPED");

        verify(orderRepository).save(o);
    }

    @Test
    @DisplayName("updateOrderStatus -> empty when order not found")
    void updateOrderStatus_notFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Order> updated = orderService.updateOrderStatus(999L, "CANCELLED");
        assertThat(updated).isEmpty();
        verify(orderRepository, never()).save(any());
    }

    @Nested
    class AddOrderMessage {
        @Test
        @DisplayName("addOrderMessage -> sets message when no existing comment")
        void addMessage_noExisting() {
            Order o = new Order();
            o.setId(5L);
            o.setComment(null);

            when(orderRepository.findById(5L)).thenReturn(Optional.of(o));
            when(orderRepository.save(o)).thenReturn(o);

            Optional<Order> res = orderService.addOrderMessage(5L, "hello");
            assertThat(res).isPresent();
            assertThat(res.get().getComment()).isEqualTo("hello");
        }

        @Test
        @DisplayName("addOrderMessage -> appends with separator when exists")
        void addMessage_append() {
            Order o = new Order();
            o.setId(6L);
            o.setComment("old");

            when(orderRepository.findById(6L)).thenReturn(Optional.of(o));
            when(orderRepository.save(o)).thenReturn(o);

            Optional<Order> res = orderService.addOrderMessage(6L, "new");
            assertThat(res).isPresent();
            assertThat(res.get().getComment()).contains("old").contains("new").contains("---");
        }

        @Test
        @DisplayName("addOrderMessage -> empty when order not found")
        void addMessage_notFound() {
            when(orderRepository.findById(404L)).thenReturn(Optional.empty());
            Optional<Order> res = orderService.addOrderMessage(404L, "x");
            assertThat(res).isEmpty();
        }
    }

    // ---------- getOrderDetails mapping ----------

    @Test
    @DisplayName("getOrderDetails -> maps order + orderProducts + products to DTO")
    void getOrderDetails_ok() {
        Order o = new Order();
        o.setId(100L);
        o.setUserId("u-1");
        o.setStatus("PLACED");
        o.setOrderDate(LocalDateTime.now());
        o.setTotalAmount(new BigDecimal("10.00"));

        // Build mocked order_products (use mocks to avoid constructor coupling)
        Product p1 = mockProduct(1L, "SKU-1", "Prod 1");
        Product p2 = mockProduct(2L, "SKU-2", "Prod 2");

        OrderProduct op1 = mockOrderProduct(p1, 2, new BigDecimal("3.50"));
        OrderProduct op2 = mockOrderProduct(p2, 1, new BigDecimal("3.00"));

        when(orderRepository.findById(100L)).thenReturn(Optional.of(o));
        when(orderProductRepository.findById_OrderId(100L)).thenReturn(List.of(op1, op2));
        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));

        Optional<OrderDetailsDto> dto = orderService.getOrderDetails(100L);

        assertThat(dto).isPresent();
        assertThat(dto.get().getItems()).extracting(ItemInOrderDto::getSku)
                .containsExactlyInAnyOrder("SKU-1", "SKU-2");
        assertThat(dto.get().getItems()).extracting(ItemInOrderDto::getQuantity)
                .containsExactlyInAnyOrder(2, 1);
    }

    @Test
    @DisplayName("getOrderDetails -> empty when order not found")
    void getOrderDetails_notFound() {
        when(orderRepository.findById(777L)).thenReturn(Optional.empty());
        Optional<OrderDetailsDto> dto = orderService.getOrderDetails(777L);
        assertThat(dto).isEmpty();
    }

    // ---------- placeOrder (tier pricing) ----------

    @Nested
    class PlaceOrderFlow {

        @Test
        @DisplayName("placeOrder -> empty optional when payload empty")
        void emptyPayload() {
            Optional<OrderDetailsDto> res = orderService.placeOrder("u1", new PlaceOrderRequest());
            assertThat(res).isEmpty();
        }

        @Test
        @DisplayName("placeOrder -> throws when user not found")
        void userNotFound() {
            PlaceOrderRequest req = new PlaceOrderRequest();
            req.setItems(List.of(item(10L, 1)));
            when(userRepo.findById("missing")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.placeOrder("missing", req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("placeOrder -> throws when staff (level=0) tries to order")
        void staffCannotOrder() {
            User staff = new User();
            staff.setId("staff-1");
            staff.setLevel(0);
            when(userRepo.findById("staff-1")).thenReturn(Optional.of(staff));

            PlaceOrderRequest req = new PlaceOrderRequest();
            req.setItems(List.of(item(1L, 1)));

            assertThatThrownBy(() -> orderService.placeOrder("staff-1", req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Staff cannot place orders");
        }

        @Test
        @DisplayName("placeOrder -> success: uses tier price by user level, computes total, returns DTO")
        void success_level2_pricesUsed() {
            // User level 2 => use price2
            User u = new User();
            u.setId("u-2");
            u.setLevel(2);
            when(userRepo.findById("u-2")).thenReturn(Optional.of(u));

            // Two items
            PlaceOrderRequest req = new PlaceOrderRequest();
            req.setComment("please ship fast");
            req.setItems(List.of(item(10L, 2), item(20L, 1)));

            // Products with tier prices
            Product p10 = mockProductWithPrices(10L, "SKU-10", "P10",
                    null, new BigDecimal("5.00"), null, null);
            Product p20 = mockProductWithPrices(20L, "SKU-20", "P20",
                    null, new BigDecimal("7.25"), null, null);

            when(productRepository.findById(10L)).thenReturn(Optional.of(p10));
            when(productRepository.findById(20L)).thenReturn(Optional.of(p20));

            // When order is saved first time, give it an ID
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
                Order o = inv.getArgument(0);
                if (o.getId() == null) o.setId(500L);
                return o;
            });

            // The service later calls findById_OrderId(orderId) to build DTO items;
            // return orderProducts reflecting what should have been saved
            OrderProduct op1 = mockOrderProduct(p10, 2, new BigDecimal("5.00"));
            OrderProduct op2 = mockOrderProduct(p20, 1, new BigDecimal("7.25"));
            when(orderProductRepository.findById_OrderId(500L)).thenReturn(List.of(op1, op2));

            Optional<OrderDetailsDto> res = orderService.placeOrder("u-2", req);

            assertThat(res).isPresent();
            OrderDetailsDto dto = res.get();
            assertThat(dto.getId()).isEqualTo(500L);
            assertThat(dto.getStatus()).isEqualTo("PLACED");
            // Total = 2*5.00 + 1*7.25 = 17.25
            assertThat(dto.getTotalAmount()).isEqualByComparingTo("17.25");
            assertThat(dto.getItems()).hasSize(2);
            assertThat(dto.getItems()).extracting(ItemInOrderDto::getSku)
                    .containsExactlyInAnyOrder("SKU-10", "SKU-20");

            // Verify that orderProductRepository.save() was invoked at least once
            verify(orderProductRepository, atLeastOnce()).save(any(OrderProduct.class));
        }

        @Test
        @DisplayName("placeOrder -> skips invalid product ids gracefully")
        void skipsInvalidProduct() {
            User u = new User();
            u.setId("u-3");
            u.setLevel(1);
            when(userRepo.findById("u-3")).thenReturn(Optional.of(u));

            PlaceOrderRequest req = new PlaceOrderRequest();
            req.setItems(List.of(item(9999L, 3))); // product id not found

            when(productRepository.findById(9999L)).thenReturn(Optional.empty());
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
                Order o = inv.getArgument(0);
                if (o.getId() == null) o.setId(700L);
                return o;
            });

            when(orderProductRepository.findById_OrderId(700L)).thenReturn(List.of()); // no items

            Optional<OrderDetailsDto> res = orderService.placeOrder("u-3", req);
            assertThat(res).isPresent();
            assertThat(res.get().getTotalAmount()).isEqualByComparingTo("0.00");
            assertThat(res.get().getItems()).isEmpty();
        }
    }

    // ---------- Helpers ----------

    private PlaceOrderRequest.OrderItemPayload item(Long productId, int qty) {
        PlaceOrderRequest.OrderItemPayload i = new PlaceOrderRequest.OrderItemPayload();
        i.setProductId(productId);
        i.setQuantity(qty);
        return i;
    }

    private Product mockProduct(Long id, String sku, String name) {
        Product p = mock(Product.class);
        lenient().when(p.getId()).thenReturn(id);
        lenient().when(p.getSku()).thenReturn(sku);
        lenient().when(p.getName()).thenReturn(name);
        return p;
    }

    private Product mockProductWithPrices(Long id, String sku, String name,
                                          BigDecimal p1, BigDecimal p2, BigDecimal p3, BigDecimal p4) {
        Product p = mockProduct(id, sku, name);
        lenient().when(p.getPrice1()).thenReturn(p1);
        lenient().when(p.getPrice2()).thenReturn(p2);
        lenient().when(p.getPrice3()).thenReturn(p3);
        lenient().when(p.getPrice4()).thenReturn(p4);
        return p;
    }

    private OrderProduct mockOrderProduct(Product product, int qty, BigDecimal price) {
        OrderProduct op = mock(OrderProduct.class);
        lenient().when(op.getProduct()).thenReturn(product);
        lenient().when(op.getQuantity()).thenReturn(qty);
        lenient().when(op.getPrice()).thenReturn(price);
        return op;
    }
}
