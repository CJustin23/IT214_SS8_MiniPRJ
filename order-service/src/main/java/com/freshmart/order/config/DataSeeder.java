package com.freshmart.order.config;

import com.freshmart.order.domain.SalesOrder;
import com.freshmart.order.repo.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedOrders(OrderRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.save(order(1L, 1L, 2, 165000L));
            repository.save(order(2L, 2L, 6, 32000L));
            repository.save(order(3L, 4L, 1, 45000L));
            repository.save(order(1L, 5L, 1, 120000L));
        };
    }

    private SalesOrder order(Long customerId, Long productId, int quantity, long unitPrice) {
        SalesOrder order = new SalesOrder();
        order.setCustomerId(customerId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(unitPrice * quantity);
        order.setStatus("CONFIRMED");
        order.setCreatedAt(LocalDateTime.now().minusHours(customerId));
        return order;
    }
}
