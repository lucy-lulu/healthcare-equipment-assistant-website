package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.dto.ItemInOrderDto;
import com.unimelbproject.healthcareequipmentassistant.dto.OrderDetailsDto;
import com.unimelbproject.healthcareequipmentassistant.dto.PlaceOrderRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.UpdateOrderStatusRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.OrderMessageRequest;
import com.unimelbproject.healthcareequipmentassistant.models.Order;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.services.EmailService;
import com.unimelbproject.healthcareequipmentassistant.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock private OrderService orderService;
    @Mock private EmailService emailService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    // ---------- Helpers ----------
    private User mockUser(String id, String name, String email) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setUsername(name);
        u.setRole(User.UserRole.partner);
        return u;
    }

    private OrderDetailsDto mockDetails(Long id) {
        return new OrderDetailsDto(
                id,
                LocalDateTime.now(),
                new BigDecimal("123.45"),
                "PLACED",
                "user-123",
                "ORD-TRK-1",
                "SHP-TRK-1",
                "comment",
                List.of(new ItemInOrderDto(10L, "SKU-10", "Prod 10", new BigDecimal("12.34"), 2))
        );
    }

    private String json(String s) { return s; }

    // ---------- Tests ----------

//    @Test
//    @DisplayName("GET /api/orders -> 200 with Page<Order> JSON")
//    void getAllOrders_ok() throws Exception {
//        Page<Order> page = new PageImpl<>(List.of(new Order(), new Order()), PageRequest.of(0, 2), 2);
//        when(orderService.getAllOrders(0, 2)).thenReturn(page);
//
//        mockMvc.perform(get("/api/orders").param("cursor", "0").param("size", "2"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
//    }

    @Test
    @DisplayName("GET /api/orders/my -> 200 success with data when authenticated")
    void getMyOrders_ok() throws Exception {
        User user = mockUser("user-123", "partner_jane", "jane@example.com");
        when(orderService.getOrdersByUserId("user-123")).thenReturn(List.of(new Order(), new Order()));

        mockMvc.perform(get("/api/orders/my")
                        .requestAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/orders/my -> 200 failure when unauthenticated")
    void getMyOrders_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/orders/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not authenticated or user ID not available."));
    }

    @Test
    @DisplayName("GET /api/orders/search?name=alice -> 200 with success body")
    void searchOrders_ok() throws Exception {
        when(orderService.getOrdersByPartnerUsername("alice")).thenReturn(List.of(new Order()));

        mockMvc.perform(get("/api/orders/search").param("name", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/orders/search?name= -> 200 with failure message")
    void searchOrders_emptyParam() throws Exception {
        mockMvc.perform(get("/api/orders/search").param("name", " "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Partner name parameter is required and cannot be empty."));
    }

    @Test
    @DisplayName("GET /api/orders/{id} -> 200 when found")
    void getOrderDetails_found() throws Exception {
        when(orderService.getOrderDetails(99L)).thenReturn(Optional.of(mockDetails(99L)));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(99));
    }

    @Test
    @DisplayName("GET /api/orders/{id} -> 404 when not found")
    void getOrderDetails_notFound() throws Exception {
        when(orderService.getOrderDetails(1000L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/1000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    @DisplayName("POST /api/orders/place -> 401 when unauthenticated")
    void placeOrder_unauthenticated() throws Exception {
        mockMvc.perform(post("/api/orders/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                          {"items":[{"productId":1,"quantity":2}]}
                        """)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not authenticated."));
    }

    @Test
    @DisplayName("POST /api/orders/place -> 400 when service returns empty (bad payload)")
    void placeOrder_badRequest() throws Exception {
        User user = mockUser("user-123", "partner_jane", "jane@example.com");
        when(orderService.placeOrder(eq("user-123"), any(PlaceOrderRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/orders/place")
                        .requestAttr("user", user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                          {"items":[],"comment":"note"}
                        """)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/orders/place -> 201 when success and email sent")
    void placeOrder_success() throws Exception {
        User user = mockUser("user-123", "partner_jane", "jane@example.com");
        OrderDetailsDto dto = mockDetails(777L);
        when(orderService.placeOrder(eq("user-123"), any(PlaceOrderRequest.class)))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(post("/api/orders/place")
                        .requestAttr("user", user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                          {
                            "items":[{"productId":10,"quantity":2},{"productId":20,"quantity":1}],
                            "comment":"please ship fast"
                          }
                        """)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(777));

        // verify email sent
        ArgumentCaptor<String> to = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> sub = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1)).sendEmail(to.capture(), sub.capture(), body.capture());
    }

    @Test
    @DisplayName("POST /api/orders/{id}/status -> 200 when updated")
    void updateOrderStatus_ok() throws Exception {
        Order o = new Order();
        o.setId(5L);
        o.setStatus("SHIPPED");
        when(orderService.updateOrderStatus(eq(5L), eq("SHIPPED"))).thenReturn(Optional.of(o));

        mockMvc.perform(post("/api/orders/5/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                          {"status":"SHIPPED"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/status -> 404 when order not found")
    void updateOrderStatus_notFound() throws Exception {
        when(orderService.updateOrderStatus(eq(999L), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/orders/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                          {"status":"CANCELLED"}
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/message -> 200 when appended")
    void addOrderMessage_ok() throws Exception {
        Order o = new Order();
        o.setId(8L);
        o.setComment("old");
        when(orderService.addOrderMessage(eq(8L), eq("new msg")))
                .thenReturn(Optional.of(o));

        mockMvc.perform(post("/api/orders/8/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                          {"message":"new msg"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/orders/{id}/message -> 404 when order not found")
    void addOrderMessage_notFound() throws Exception {
        when(orderService.addOrderMessage(eq(404L), anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/orders/404/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                          {"message":"any"}
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order not found"));
    }
}
