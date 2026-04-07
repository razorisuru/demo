package com.hello.demo.controllers;

import java.util.List;

// import java.util.UUID;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.hello.demo.entity.Order;
import com.hello.demo.repository.OrderRepository;
import com.hello.demo.util.ApiResponse;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    // Constructor Injection: Spring automatically injects the repository bean
    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * GET: Retrieve all orders from PostgreSQL
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok()
                .header("Custom-Header", "FetchAll")
                .body(orders);
    }

    /**
     * GET: Find a specific order by its UUID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        return orderRepository.findById(id)
                .map(order -> ResponseEntity.ok().body(order))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST: Create a new order
     * Expects JSON: { "item": "Pizza", "price": 15.99 }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody Order order) {
        Order savedOrder = orderRepository.save(order);

        ApiResponse<Order> response = new ApiResponse<>("order created successfully", savedOrder);

        return ResponseEntity.ok()
                .header("Custom-Header", "Created")
                .body(response);
    }

    /**
     * PATCH: Partially update an existing order
     * URL: /api/orders/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Order> patchOrder(@PathVariable UUID id, @RequestBody Order partialOrder) {
        return orderRepository.findById(id)
                .map(existingOrder -> {
                    // Only update fields that are not null in the request
                    if (partialOrder.getItem() != null) {
                        existingOrder.setItem(partialOrder.getItem());
                    }
                    if (partialOrder.getPrice() != null) {
                        existingOrder.setPrice(partialOrder.getPrice());
                    }

                    Order updatedOrder = orderRepository.save(existingOrder);
                    return ResponseEntity.ok(updatedOrder);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE: Remove an order by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID id) {
        // 1. Fetch the order first
        Order order = orderRepository.findById(id).orElse(null);

        if (order != null) {
            // 2. Delete it from the database
            orderRepository.deleteById(id);

            // 3. Return the captured 'order' object in your response
            ApiResponse<Order> response = new ApiResponse<>("success", order);

            return ResponseEntity.ok()
                    .header("Custom-Header", "Deleted")
                    .body(response);
        }

        // 4. Handle the case where the order doesn't exist
        ApiResponse<String> errorResponse = new ApiResponse<>("error", "Order with ID " + id + " not found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("Custom-Header", "DeleteAttempted")
                .body(errorResponse);
    }
    // @GetMapping("/order")
    // public String getOrder(@RequestParam String item) { // Use @RequestParam
    // return "Your " + item + " order is being processed.";
    // }

    // @GetMapping("/order/{item}") // Use curly braces
    // public ResponseEntity<Order> getOrder(@PathVariable String item) { // Use
    // @PathVariable
    // return ResponseEntity.ok()
    // .header("Custom-Header", "value")
    // .body(new Order(UUID.randomUUID(), item, 9.99)); // Return an Order object in
    // the body
    // }

    // @GetMapping("/order")
    // public ResponseEntity<List<Order>> allOrders() {
    // // 1. Properly initialize the list
    // List<Order> orders = List.of(
    // new Order(UUID.randomUUID(), "Pizza", 12.99),
    // new Order(UUID.randomUUID(), "Burger", 8.99),
    // new Order(UUID.randomUUID(), "Coffee", 2.99));

    // // 2. Return the list in the body
    // return ResponseEntity.ok()
    // .header("Custom-Header", "value")
    // .body(orders);
    // }
}
