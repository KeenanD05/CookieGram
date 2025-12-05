package com.group2.backend.service;

import com.group2.backend.exception.ResourceNotFoundException;
import com.group2.backend.exception.UnauthorizedException;
import com.group2.backend.model.*;
import com.group2.backend.model.Order.OrderStatus;
import com.group2.backend.payloads.OrderItemRequest;
import com.group2.backend.payloads.OrderRequest;
import com.group2.backend.payloads.OrderResponse;
import com.group2.backend.repository.CookieRepository;
import com.group2.backend.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CookieRepository cookieRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private Cookie testCookie;
    private Order testOrder;
    private final String TEST_EMAIL = "test@example.com";
    private final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Setup test data
        testCookie = new Cookie();
        testCookie.setId(1L);
        testCookie.setName("Chocolate Chip");
        testCookie.setBasePrice(2.5);
        testCookie.setDescription("Delicious chocolate chip cookie");

        OrderItem item = new OrderItem();
        item.setCookie(testCookie);
        item.setQuantity(2);
        item.setPrice(2.5);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerEmail(TEST_EMAIL);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setRequiredShippingDate(NOW.plusDays(2));
        testOrder.setItems(Collections.singletonList(item));
        testOrder.setTotalAmount(5.0);

        // Setup security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(TEST_EMAIL);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createOrder_ShouldCreateOrder_WhenValidRequest() {
        // Arrange
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setCookieId(1L);
        itemRequest.setQuantity(2);

        orderRequest = new OrderRequest();
        orderRequest.setCustomerEmail(TEST_EMAIL);
        orderRequest.setCustomerName("Test User");
        orderRequest.setShippingAddress("123 Test St");
        orderRequest.setRequiredShippingDate(NOW.plusDays(2));
        orderRequest.setItems(Collections.singletonList(itemRequest));

        when(cookieRepository.findById(1L)).thenReturn(Optional.of(testCookie));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // Act
        OrderResponse response = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_EMAIL, response.getCustomerEmail());
        assertEquals(1, response.getItems().size());
        assertEquals(5.0, response.getTotalAmount());
        assertEquals(OrderStatus.PENDING.toString(), response.getStatus());
        
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void cancelOrder_ShouldCancelOrder_WhenValid() {
        // Arrange
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderResponse response = orderService.cancelOrder(1L);

        // Assert
        assertNotNull(response);
        assertEquals(OrderStatus.CANCELLED.toString(), response.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void cancelOrder_ShouldThrow_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(999L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_ShouldThrow_WhenUnauthorized() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(authentication.getPrincipal()).thenReturn("different@example.com");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> orderService.cancelOrder(1L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrder_ShouldUpdateOrder_WhenValid() {
        // Arrange
        testOrder.setStatus(OrderStatus.PENDING);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setCookieId(1L);
        itemRequest.setQuantity(3);

        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setCustomerEmail(TEST_EMAIL);
        updateRequest.setCustomerName("Updated Name");
        updateRequest.setShippingAddress("456 New St");
        updateRequest.setRequiredShippingDate(NOW.plusDays(3));
        updateRequest.setItems(Collections.singletonList(itemRequest));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(cookieRepository.findById(1L)).thenReturn(Optional.of(testCookie));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderResponse response = orderService.updateOrder(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Name", response.getCustomerName());
        assertEquals("456 New St", response.getShippingAddress());
        assertEquals(7.5, response.getTotalAmount()); // 3 * 2.5 = 7.5
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
