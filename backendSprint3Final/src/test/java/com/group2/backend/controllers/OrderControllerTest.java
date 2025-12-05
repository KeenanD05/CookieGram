package com.group2.backend.controllers;

import com.group2.backend.model.Order;
import com.group2.backend.model.OrderItem;
import com.group2.backend.model.Order.OrderStatus;
import com.group2.backend.exception.ResourceNotFoundException;
import com.group2.backend.payloads.*;
import com.group2.backend.service.OrderDateService;
import com.group2.backend.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderDateService orderDateService;

    @InjectMocks
    private OrderController orderController;

    private final ObjectMapper objectMapper = createObjectMapper();
    
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        // Setup test order request
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setCookieId(1L);
        itemRequest.setQuantity(2);

        orderRequest = new OrderRequest();
        orderRequest.setCustomerEmail("test@example.com");
        orderRequest.setCustomerName("Test User");
        orderRequest.setShippingAddress("123 Test St");
        orderRequest.setRequiredShippingDate(now.plusDays(2));
        orderRequest.setItems(Collections.singletonList(itemRequest));

        // Setup test order response
        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setCustomerEmail("test@example.com");
        orderResponse.setStatus(String.valueOf(OrderStatus.PENDING));
    }

    @Test
    void placeOrder_ShouldReturnCreated_WhenDateIsAvailable() throws Exception {
        // Arrange
        when(orderDateService.isDateAvailable(any(LocalDate.class), anyInt())).thenReturn(true);
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerEmail").value("test@example.com"));
    }

    @Test
    void placeOrder_ShouldReturnConflict_WhenDateIsNotAvailable() throws Exception {
        // Arrange
        when(orderDateService.isDateAvailable(any(LocalDate.class), anyInt())).thenReturn(false);
        when(orderDateService.getNextAvailableShippingDate(anyInt())).thenReturn(today.plusDays(3));

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Requested date is not available for the order"))
                .andExpect(jsonPath("$.nextAvailableDate").exists());
    }

    @Test
    void getCookieAvailability_ShouldReturnAvailabilityMap() throws Exception {
        // Arrange
        Map<LocalDate, Integer> availability = new HashMap<>();
        availability.put(today, 5);
        availability.put(today.plusDays(1), 3);
        
        when(orderDateService.getCookieAvailability(any(LocalDate.class), anyInt())).thenReturn(availability);

        // Act & Assert
        mockMvc.perform(get("/api/orders/availability")
                .param("daysAhead", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void cancelOrder_ShouldReturnOk_WhenCancellationIsSuccessful() throws Exception {
        // Arrange
        OrderResponse cancelledOrder = new OrderResponse();
        cancelledOrder.setId(1L);
        cancelledOrder.setStatus(String.valueOf(OrderStatus.CANCELLED));
        
        when(orderService.cancelOrder(1L)).thenReturn(cancelledOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void placeOrder_ShouldReturnBadRequest_WhenInputIsInvalid() throws Exception {
        // Arrange - Create invalid order (missing required fields)
        OrderRequest invalidRequest = new OrderRequest();
        invalidRequest.setItems(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelOrder_ShouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        // Arrange
        when(orderService.cancelOrder(999L))
                .thenThrow(new ResourceNotFoundException("Order not found with id: 999"));

        // Act & Assert
        mockMvc.perform(post("/api/orders/999/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found with id: 999"));
    }
}
