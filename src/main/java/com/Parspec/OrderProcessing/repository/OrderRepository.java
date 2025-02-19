package com.Parspec.OrderProcessing.repository;

import com.Parspec.OrderProcessing.model.Order;
import com.Parspec.OrderProcessing.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, String> {
    long countByStatus(OrderStatus status);
    
    @Query("SELECT AVG(o.processingTime) FROM Order o WHERE o.status = 'COMPLETED'")
    Double getAverageProcessingTime();
} 