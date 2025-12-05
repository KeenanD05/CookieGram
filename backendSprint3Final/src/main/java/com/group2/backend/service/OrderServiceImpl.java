package com.group2.backend.service;

import com.group2.backend.exception.ResourceNotFoundException;
import com.group2.backend.exception.UnauthorizedException;
import com.group2.backend.model.*;
import com.group2.backend.model.Order.OrderStatus;
import com.group2.backend.payloads.*;
import com.group2.backend.repository.CookieRepository;
import com.group2.backend.repository.OrderRepository;
import com.group2.backend.repository.ReceiverRepository;
import com.group2.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CookieRepository cookieRepository;

    @Autowired private UserRepository userRepository;
    @Autowired private ReceiverRepository receiverRepository;
    @Autowired private ModelMapper modelMapper;

    // =====================================================
    // Helper Methods
    // =====================================================

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            throw new UnauthorizedException("User not authenticated");

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails)
            return ((UserDetails) principal).getUsername();

        if (principal instanceof String)
            return (String) principal;

        throw new UnauthorizedException("Unable to extract email from authentication");
    }

    private void validateOrderOwnership(Order order, String email) {
        if (order == null) throw new ResourceNotFoundException("Order not found");
        if (!order.getCustomerEmail().equals(email))
            throw new UnauthorizedException("You are not authorized to access this order");
    }

    // =====================================================
    // Create Order
    // =====================================================

    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest request) {

        // Save new receiver
        ReceiverDTO r = request.getReceiver();
        Receiver receiver = new Receiver();
        receiver.setName(r.getName());
        receiver.setEmail(r.getEmail());
        receiver.setPhoneNumber(r.getPhoneNumber());
        receiver.setSpecialInstructions(r.getSpecialInstructions());
        receiver = receiverRepository.save(receiver);

        // Create new order
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerName(request.getCustomerName());
        order.setShippingAddress(request.getShippingAddress());
        order.setRequiredShippingDate(request.getRequiredShippingDate());
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setStatus(OrderStatus.PENDING);
        order.setReceiver(receiver);

        // Build items
        double total = 0.0;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            Cookie cookie = cookieRepository.findById(itemReq.getCookieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cookie not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setCookie(cookie);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(cookie.getBasePrice());
            item.setIcingFlavor(itemReq.getIcingFlavor());
           item.setMessage(itemReq.getMessage());
            total += cookie.getBasePrice() * itemReq.getQuantity();
            items.add(item);
        }

        order.setItems(items);
        order.setTotalAmount(total);

        orderRepository.save(order);

        return OrderResponse.fromOrder(order);
    }

    // =====================================================
    // Cancel Order
    // =====================================================

    @Transactional
    @Override
    public OrderResponse cancelOrder(Long orderId) {
        String email = getCurrentUserEmail();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        validateOrderOwnership(order, email);

        if (order.getStatus() != OrderStatus.PENDING)
            throw new IllegalStateException("Only pending orders can be cancelled");

        LocalDateTime cutoff = order.getRequiredShippingDate().minusDays(2);
        if (LocalDateTime.now().isAfter(cutoff))
            throw new IllegalStateException("Cannot cancel this close to shipping date");

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return OrderResponse.fromOrder(order);
    }

    // =====================================================
    // Update Order
    // =====================================================

    @Transactional
    @Override
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        String email = getCurrentUserEmail();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        validateOrderOwnership(order, email);

        if (!request.getCustomerEmail().equals(email))
            throw new UnauthorizedException("Cannot update another user's order");

        if (order.getStatus() != OrderStatus.PENDING)
            throw new IllegalStateException("Only pending orders can be updated");

        if (LocalDateTime.now().isAfter(order.getRequiredShippingDate().minusDays(2)))
            throw new IllegalStateException("Too late to update order");

        // Basic update
        order.setCustomerName(request.getCustomerName());
        order.setShippingAddress(request.getShippingAddress());
        order.setRequiredShippingDate(request.getRequiredShippingDate());
        order.setTotalAmount(request.getTotalAmount());

        // Replace items
        order.getItems().clear();
        for (OrderItemRequest itemReq : request.getItems()) {
            Cookie cookie = cookieRepository.findById(itemReq.getCookieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cookie not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setCookie(cookie);
            item.setQuantity(itemReq.getQuantity());
            order.getItems().add(item);
        }

        orderRepository.save(order);
        return OrderResponse.fromOrder(order);
    }

    // =====================================================
    // Get Order by ID
    // =====================================================

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return OrderResponse.fromOrder(order);
    }

    // =====================================================
    // Get All Orders
    // =====================================================

    @Override
    public Page<OrderResponse> getAllOrders(int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        return orderRepository.findAll(pageable).map(OrderResponse::fromOrder);
    }

    @Override
    public Page<OrderResponse> getAllOrdersByUser(Integer page, Integer size, String sortBy, String direction, String email) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        return orderRepository.findByCustomerEmailOrderByOrderDateDesc(email, pageable)
                .map(OrderResponse::fromOrder);
    }

    // =====================================================
    // Filter Orders
    // =====================================================

    @Override
    public Page<OrderResponse> filterOrders(OrderFilterRequest f) {

        Pageable pageable = PageRequest.of(
                f.getPage(),
                Math.max(f.getSize(), 10),
                Sort.by(Sort.Direction.fromString(f.getSortDirection()), f.getSortBy())
        );

        // Resolve date ranges
        LocalDate start = f.getStartDate();
        LocalDate end = f.getEndDate();
        LocalDate today = LocalDate.now();

        if (f.getDateRange() != null) {
            switch (f.getDateRange()) {
                case "today": start = end = today; break;
                case "yesterday": start = end = today.minusDays(1); break;
                case "this_week":
                    start = today.with(DayOfWeek.MONDAY);
                    end = today.with(DayOfWeek.SUNDAY);
                    break;
                case "last_7_days":
                    start = today.minusDays(7);
                    end = today;
                    break;
                case "this_month":
                    start = today.withDayOfMonth(1);
                    end = today.withDayOfMonth(today.lengthOfMonth());
                    break;
                case "custom":
                    // Already set
                    break;
            }
        }

        Page<Order> orders = orderRepository.findWithFilters(
                f.getStatus(),
                f.getCustomerEmail(),
                f.getUserId(),
                start,
                end,
                f.getRequiredShippingDateStart(),
                f.getRequiredShippingDateEnd(),
                f.getMinAmount(),
                f.getMaxAmount(),
                f.getPaymentStatus(),
                f.getOrderNumber(),
             
                pageable
        );

        return orders.map(OrderResponse::fromOrder);
    }

    // =====================================================
    // Update Status
    // =====================================================

    @Override
    public OrderResponse changeOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(OrderStatus.valueOf(status));
        orderRepository.save(order);

        return OrderResponse.fromOrder(order);
    }

    // =====================================================
    // Other Utility Methods
    // =====================================================

    @Override
    public List<OrderResponse> getOrdersForDate(LocalDate date) {
        return orderRepository
                .findOrdersForDay(date.atStartOfDay(), date.atTime(23, 59, 59))
                .stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }

    @Override
    public Double getTotalSaleOverAPeriod(String start, String end) {
        return orderRepository.findTotalSaleOverAPeriod(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }

    @Override
    public Page<OrderResponse> getCancelledOrders(int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        return orderRepository.findByStatus(OrderStatus.CANCELLED, pageable)
                .map(OrderResponse::fromOrder);
    }
}
