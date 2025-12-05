package com.group2.backend.repository;

import com.group2.backend.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    Page<Order> findByCustomerEmailOrderByOrderDateDesc(String email, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:customerEmail IS NULL OR o.customerEmail = :customerEmail) AND " +
           "(:userId IS NULL OR o.user.id = :userId) AND " +
           "(:startDate IS NULL OR CAST(o.orderDate AS date) >= :startDate) AND " +
           "(:endDate IS NULL OR CAST(o.orderDate AS date) <= :endDate) AND " +
           "(:requiredShippingDateStart IS NULL OR CAST(o.requiredShippingDate AS date) >= :requiredShippingDateStart) AND " +
           "(:requiredShippingDateEnd IS NULL OR CAST(o.requiredShippingDate AS date) <= :requiredShippingDateEnd) AND " +
           "(:minAmount IS NULL OR o.totalAmount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR o.totalAmount <= :maxAmount) AND " +
           "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus) AND " +
           "(:orderNumber IS NULL OR o.orderNumber LIKE CONCAT('%', :orderNumber, '%'))")

    Page<Order> findWithFilters(
            @Param("status") Order.OrderStatus status,
            @Param("customerEmail") String customerEmail,
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("requiredShippingDateStart") LocalDate requiredShippingDateStart,
            @Param("requiredShippingDateEnd") LocalDate requiredShippingDateEnd,
            @Param("minAmount") Double minAmount,
            @Param("maxAmount") Double maxAmount,
            @Param("paymentStatus") Order.PaymentStatus paymentStatus,
            @Param("orderNumber") String orderNumber,
           // @Param("shippingAddress") String shippingAddress,
            Pageable pageable
    );

    Collection<Order> findByRequiredShippingDate(java.time.LocalDate targetDate);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate and o.status = 'DELIVERED'")
    Double findTotalSaleOverAPeriod(LocalDateTime startDate, LocalDateTime endDate);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
}
