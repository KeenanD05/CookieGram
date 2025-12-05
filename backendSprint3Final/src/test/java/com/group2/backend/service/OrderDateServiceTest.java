package com.group2.backend.service;

import com.group2.backend.model.Cookie;
import com.group2.backend.model.Order;
import com.group2.backend.model.OrderItem;
import com.group2.backend.model.Order.OrderStatus;
import com.group2.backend.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDateServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderDateService orderDateService;

    private Order testOrder1, testOrder2;
    private final LocalDate TODAY = LocalDate.now();
    private final LocalDateTime START_OF_DAY = TODAY.atStartOfDay();
    private final LocalDateTime END_OF_DAY = TODAY.atTime(LocalTime.MAX);

    @BeforeEach
    void setUp() {
        // Create test cookies
        Cookie cookie1 = new Cookie();
        cookie1.setId(1L);
        cookie1.setName("Chocolate Chip");
        cookie1.setBasePrice(2.5);

        Cookie cookie2 = new Cookie();
        cookie2.setId(2L);
        cookie2.setName("Oatmeal Raisin");
        cookie2.setBasePrice(2.0);

        // Create order items
        OrderItem item1 = new OrderItem();
        item1.setCookie(cookie1);
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setCookie(cookie2);
        item2.setQuantity(3);

        // Create test orders
        testOrder1 = new Order();
        testOrder1.setId(1L);
        testOrder1.setStatus(OrderStatus.PENDING);
        testOrder1.setRequiredShippingDate(START_OF_DAY.plusHours(12));
        testOrder1.setItems(List.of(item1));

        testOrder2 = new Order();
        testOrder2.setId(2L);
        testOrder2.setStatus(OrderStatus.PENDING);
        testOrder2.setRequiredShippingDate(START_OF_DAY.plusHours(14));
        testOrder2.setItems(List.of(item2));
    }

    @Test
    void isDateAvailable_ShouldReturnTrue_WhenEnoughCapacity() {

        when(orderRepository.findByRequiredShippingDateBetween(any(), any()))
                .thenReturn(List.of(testOrder1)); // Only 2 cookies ordered for today


        assertTrue(orderDateService.isDateAvailable(TODAY, 3)); // 3 more cookies should fit (2 + 3 = 5 <= 5)
    }

    @Test
    void isDateAvailable_ShouldReturnFalse_WhenNotEnoughCapacity() {
        // Arrange
        when(orderRepository.findByRequiredShippingDateBetween(any(), any()))
                .thenReturn(List.of(testOrder1, testOrder2)); // 5 cookies ordered for today (max capacity)

        // Act & Assert
        assertFalse(orderDateService.isDateAvailable(TODAY, 1)); // Even 1 more cookie should not fit
    }

    @Test
    void getNextAvailableShippingDate_ShouldReturnNextAvailableDate() {
        // The implementation always starts checking from tomorrow (LocalDate.now().plusDays(1))
        // So we need to mock the first available day as tomorrow + 1 day
        
        // First day (tomorrow) is fully booked
        when(orderRepository.findByRequiredShippingDateBetween(any(), any()))
                .thenReturn(List.of(testOrder1, testOrder2)) // First call: tomorrow is full
                .thenReturn(List.of()); // Second call: day after tomorrow is available

        // Act
        LocalDate nextAvailable = orderDateService.getNextAvailableShippingDate(3);

        // Assert - Should return the day after tomorrow since tomorrow is full
        assertEquals(TODAY.plusDays(2), nextAvailable);
    }

    @Test
    void getCookieAvailability_ShouldReturnCorrectAvailability() {

        LocalDate startDate = TODAY;
        int daysAhead = 3;
        

        when(orderRepository. findByRequiredShippingDateBetween(
                startDate.atStartOfDay(), 
                startDate.atTime(LocalTime.MAX)))
                .thenReturn(List.of(testOrder1, testOrder2)); // 5 cookies (max capacity)
        
        LocalDate tomorrow = startDate.plusDays(1);
        when(orderRepository.findByRequiredShippingDateBetween(
                tomorrow.atStartOfDay(), 
                tomorrow.atTime(LocalTime.MAX)))
                .thenReturn(List.of(testOrder1)); // 2 cookies
        
        LocalDate dayAfterTomorrow = startDate.plusDays(2);
        when(orderRepository.findByRequiredShippingDateBetween(
                dayAfterTomorrow.atStartOfDay(), 
                dayAfterTomorrow.atTime(LocalTime.MAX)))
                .thenReturn(List.of()); // No orders

        // Act
        Map<LocalDate, Integer> availability = orderDateService.getCookieAvailability(startDate, daysAhead);

        // Assert
        assertEquals(3, availability.size());
        assertEquals(0, availability.get(startDate)); // 5/5 used (0 available)
        assertEquals(3, availability.get(tomorrow)); // 2/5 used (3 available)
        assertEquals(5, availability.get(dayAfterTomorrow)); // 0/5 used (5 available)
    }

    @Test
    void getAvailableCookiesForDate_ShouldReturnCorrectCount() {

        when(orderRepository.findByRequiredShippingDateBetween(any(), any()))
                .thenReturn(List.of(testOrder1, testOrder2));


        int available = orderDateService.getAvailableCookiesForDate(TODAY);


        assertEquals(0, available);
    }
}
