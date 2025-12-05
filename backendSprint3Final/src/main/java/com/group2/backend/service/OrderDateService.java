package com.group2.backend.service;

import com.group2.backend.model.Order;
import com.group2.backend.model.OrderItem;
import com.group2.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDateService {
    
    private static final int MAX_COOKIES_PER_DAY = 5;
    private final OrderRepository orderRepository;
    
    public LocalDate getNextAvailableShippingDate(int requiredCookies) {
        LocalDate date = LocalDate.now().plusDays(1); // Start checking from tomorrow
        
        while (true) {
            if (isDateAvailable(date, requiredCookies)) {
                return date;
            }
            date = date.plusDays(1);
        }
    }
    
    public boolean isDateAvailable(LocalDate date, int requiredCookies) {
        if (requiredCookies > MAX_COOKIES_PER_DAY) {
            return false; // Can't fulfill in a single day
        }
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        // Get all orders for the requested date
        List<Order> orders = orderRepository.findByRequiredShippingDateBetween(startOfDay, endOfDay);
        
        // Count total cookies ordered for the day
        int totalCookiesOrdered = orders.stream()
                .flatMap(order -> order.getItems().stream())
                .mapToInt(OrderItem::getQuantity)
                .sum();
                
        return (totalCookiesOrdered + requiredCookies) <= MAX_COOKIES_PER_DAY;
    }
    
    public Map<LocalDate, Integer> getCookieAvailability(LocalDate startDate, int days) {
        return startDate.datesUntil(startDate.plusDays(days))
                .collect(Collectors.toMap(
                        date -> date,
                        this::getAvailableCookiesForDate
                ));
    }
    
    public int getAvailableCookiesForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        List<Order> orders = orderRepository.findByRequiredShippingDateBetween(startOfDay, endOfDay);
        
        int totalCookiesOrdered = orders.stream()
                .flatMap(order -> order.getItems().stream())
                .mapToInt(OrderItem::getQuantity)
                .sum();
                
        return Math.max(0, MAX_COOKIES_PER_DAY - totalCookiesOrdered);
    }
}
