package com.freshmart.order.web;

import com.freshmart.order.client.ProductClient;
import com.freshmart.order.client.UserClient;
import com.freshmart.order.domain.SalesOrder;
import com.freshmart.order.repo.OrderRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repository;
    private final UserClient userClient;
    private final ProductClient productClient;

    public OrderController(OrderRepository repository, UserClient userClient, ProductClient productClient) {
        this.repository = repository;
        this.userClient = userClient;
        this.productClient = productClient;
    }

    @GetMapping
    public List<SalesOrder> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrder> findById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateOrderRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số lượng phải lớn hơn 0"));
        }
        try {
            userClient.getById(request.getCustomerId());
        } catch (FeignException.NotFound ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Không tìm thấy khách hàng #" + request.getCustomerId()));
        }

        ProductClient.ProductDto product;
        try {
            product = productClient.getById(request.getProductId());
        } catch (FeignException.NotFound ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Không tìm thấy sản phẩm #" + request.getProductId()));
        }

        if (product.getStock() < request.getQuantity()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không đủ tồn kho"));
        }

        productClient.adjustStock(product.getId(), -request.getQuantity());

        SalesOrder order = new SalesOrder();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setUnitPrice(product.getPrice());
        order.setTotalAmount(product.getPrice() * request.getQuantity());
        order.setStatus("CONFIRMED");
        order.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(order));
    }
}
