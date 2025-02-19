package com.Parspec.OrderProcessing.service;

import com.Parspec.OrderProcessing.model.Order;
import com.Parspec.OrderProcessing.model.OrderStatus;
import com.Parspec.OrderProcessing.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @PostConstruct
    public void init() {
        startOrderProcessor();
    }

    public Order createOrder(String userId, Double totalAmount, java.util.List<String> itemIds) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setItemIds(itemIds);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        order = orderRepository.save(order);
        orderQueue.offer(order);
        return order;
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    private void startOrderProcessor() {
        executorService.submit(() -> {
            while (true) {
                try {
                    Order order = orderQueue.take();
                    processOrder(order);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private void processOrder(Order order) {
        try {
            // Update status to processing
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);

            // Simulate processing time (random between 1-5 seconds)
            Thread.sleep(ThreadLocalRandom.current().nextLong(1000, 5000));

            // Complete the order
            order.setStatus(OrderStatus.COMPLETED);
            order.setProcessedAt(LocalDateTime.now());
            order.setProcessingTime(
                    order.getProcessedAt().toInstant(java.time.ZoneOffset.UTC).toEpochMilli() -
                    order.getCreatedAt().toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
            );
            orderRepository.save(order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Map<String, Object> getMetrics() {
        return Map.of(
            "total_orders", orderRepository.count(),
            "pending_orders", orderRepository.countByStatus(OrderStatus.PENDING),
            "processing_orders", orderRepository.countByStatus(OrderStatus.PROCESSING),
            "completed_orders", orderRepository.countByStatus(OrderStatus.COMPLETED),
            "average_processing_time_ms", orderRepository.getAverageProcessingTime()
        );
    }
} 