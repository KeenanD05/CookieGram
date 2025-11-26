package com.group2.backend.service;

import com.group2.backend.exception.ResourceNotFoundException;
import com.group2.backend.exception.UnauthorizedException;
import com.group2.backend.model.Cookie;
import com.group2.backend.model.Order;
import com.group2.backend.model.OrderItem;
import com.group2.backend.model.Order.OrderStatus;
import com.group2.backend.payloads.OrderItemRequest;
import com.group2.backend.payloads.OrderRequest;
import com.group2.backend.payloads.OrderResponse;
import com.group2.backend.repository.CookieRepository;
import com.group2.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CookieRepository cookieRepository;

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        throw new UnauthorizedException("Could not determine user email from authentication");
    }

    private void validateOrderOwnership(Order order, String userEmail) {
        if (order == null) {
            throw new ResourceNotFoundException("Order not found");
        }
        if (!order.getCustomerEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not authorized to access this order");
        }
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {


        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerEmail(orderRequest.getCustomerEmail());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setRequiredShippingDate(orderRequest.getRequiredShippingDate());
        order.setStatus(OrderStatus.PENDING);

        Double totalAmount=0.0;

       List<OrderItem> items= new ArrayList<>();
       for(OrderItemRequest orderItemRequest : orderRequest.getItems()) {
           Cookie cookie = cookieRepository.findById(orderItemRequest.getCookieId())
                   .orElseThrow(() -> new ResourceNotFoundException("Cookie not found with id " + orderItemRequest.getCookieId()));
           OrderItem orderItem = new OrderItem();
           orderItem.setCookie(cookie);
           orderItem.setQuantity(orderItemRequest.getQuantity());
           orderItem.setOrder(order);
           orderItem.setPrice(cookie.getBasePrice());
           totalAmount+=orderItemRequest.getQuantity()*cookie.getBasePrice();
           items.add(orderItem);
       }
       order.setItems(items);

       order.setTotalAmount(totalAmount);
       order.setPaymentStatus(Order.PaymentStatus.PENDING);
       orderRepository.save(order);
       return OrderResponse.fromOrder(order);
    }



        


    @Transactional
    @Override
    public OrderResponse cancelOrder(Long orderId) {
        String currentUserEmail = getCurrentUserEmail();
        
        // Find the order or throw exception if not found
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Verify the order belongs to the authenticated user
        validateOrderOwnership(order, currentUserEmail);

        // Only allow cancellation for pending orders
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be cancelled");
        }

        // Check if it's too late to cancel the order (at least 1 day before baking)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime bakingDate = order.getRequiredShippingDate().minusDays(1);
        LocalDateTime minCancelTime = bakingDate.minusDays(1);
        
        if (now.isAfter(minCancelTime)) {
            throw new IllegalStateException("Cannot cancel order. Must cancel at least 1 day before baking.");
        }

        // Update order status to CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        return OrderResponse.fromOrder(cancelledOrder);
    }





    @Transactional
    @Override
    public OrderResponse updateOrder(Long orderId, OrderRequest orderRequest) {
        String currentUserEmail = getCurrentUserEmail();
        
        // Find the order or throw exception if not found
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Verify the order belongs to the authenticated user
        validateOrderOwnership(order, currentUserEmail);
        
        // Verify the request email matches the authenticated user
        if (!orderRequest.getCustomerEmail().equals(currentUserEmail)) {
            throw new UnauthorizedException("Cannot update order for another user");
        }

        // Only allow updates for pending orders
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be updated");
        }

        // Check if it's too late to update the order (at least 2 days before shipping)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minUpdateTime = order.getRequiredShippingDate().minusDays(2);
        if (now.isAfter(minUpdateTime)) {
            throw new IllegalStateException("Cannot update order. Must update at least 2 days before shipping date.");
        }

        // Update order details
        order.setCustomerName(orderRequest.getCustomerName());
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setRequiredShippingDate(orderRequest.getRequiredShippingDate());
        order.setTotalAmount(orderRequest.getTotalAmount());

        // Update order items
        order.getItems().clear();
        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            Cookie cookie = cookieRepository.findById(itemRequest.getCookieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cookie not found with id: " + itemRequest.getCookieId()));
            
            OrderItem item = new OrderItem();
            item.setCookie(cookie);
            item.setQuantity(itemRequest.getQuantity());
            item.setOrder(order);
            order.getItems().add(item);
        }

        Order updatedOrder = orderRepository.save(order);
        return OrderResponse.fromOrder(updatedOrder);
    }


}
