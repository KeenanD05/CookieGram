package com.group2.backend.repository;

import com.group2.backend.model.Order;
import com.group2.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerEmailOrderByOrderDateDesc(String email);
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findByStatus(Order.OrderStatus status);

    List<Order> findByRequiredShippingDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT o FROM Order o WHERE o.requiredShippingDate >= :startDate AND o.requiredShippingDate < :endDate")
    List<Order> findOrdersForDay(@Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(oi.quantity) FROM Order o JOIN o.items oi " +
           "WHERE o.requiredShippingDate >= :startDate AND o.requiredShippingDate < :endDate")
    Integer countCookiesForDate(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);

    Collection<Object> findByCustomerEmail(String email);
}
