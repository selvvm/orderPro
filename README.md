# Order Processing System

A Spring Boot application that provides a backend system to manage and process orders in an e-commerce platform.

## Features

- RESTful API for order management
- Asynchronous order processing using in-memory queue
- Order status tracking (Pending, Processing, Completed)
- Metrics reporting (total orders, processing times, status counts)
- H2 in-memory database for data persistence

## Technical Stack

- Java 17
- Spring Boot 3.4.2
- Spring Data JPA
- H2 Database
- Maven
- Lombok

## Setup and Running

1. Clone the repository
2. Make sure you have Java 17 installed
3. Run the application using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
4. The application will start on `http://localhost:8080`
5. H2 Console is available at `http://localhost:8080/h2-console`

## API Documentation

### Create Order
```http
POST /api/orders
Content-Type: application/json

{
    "userId": "user123",
    "totalAmount": 99.99,
    "itemIds": ["item1", "item2", "item3"]
}
```

### Get Order Status
```http
GET /api/orders/{orderId}
```

### Get Metrics
```http
GET /api/orders/metrics
```

Response example:
```json
{
    "total_orders": 100,
    "pending_orders": 10,
    "processing_orders": 5,
    "completed_orders": 85,
    "average_processing_time_ms": 2500
}
```

## Design Decisions and Trade-offs

1. **In-Memory Queue**
   - Used `LinkedBlockingQueue` for thread-safe order processing
   - Trade-off: Orders are lost if application restarts

2. **Concurrent Processing**
   - Fixed thread pool of 10 threads for order processing
   - Trade-off: Limited by available system resources

3. **Database Choice**
   - H2 in-memory database for simplicity
   - Trade-off: Data persistence limited to application lifecycle

4. **Processing Simulation**
   - Random processing time between 1-5 seconds
   - Trade-off: Not representative of real-world processing complexity

## Assumptions

1. Orders are processed in FIFO order
2. User IDs and Item IDs are pre-validated
3. Total amount is pre-calculated and validated
4. System can handle the load with 10 concurrent processing threads
5. Temporary data loss on restart is acceptable

## Scalability Considerations

The current implementation can handle 1,000 concurrent orders through:
- Efficient queue implementation
- Multiple processing threads
- Database connection pooling
- Stateless API design

For production scenarios, consider:
1. Using a persistent message queue (RabbitMQ, Apache Kafka)
2. Implementing horizontal scaling
3. Using a production-grade database
4. Adding caching layer
5. Implementing retry mechanisms 