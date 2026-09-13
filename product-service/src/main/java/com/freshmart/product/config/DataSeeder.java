package com.freshmart.product.config;

import com.freshmart.product.domain.Product;
import com.freshmart.product.repo.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedProducts(ProductRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.save(new Product("Gạo ST25 5kg", "Lương thực", 165000L, 40));
            repository.save(new Product("Sữa tươi Vinamilk 1L", "Đồ uống", 32000L, 80));
            repository.save(new Product("Trứng gà ta 10 quả", "Tươi sống", 38000L, 25));
            repository.save(new Product("Dầu ăn Neptune 1L", "Gia vị", 45000L, 8));
            repository.save(new Product("Mì gói Hảo Hảo thùng", "Khô", 120000L, 15));
        };
    }
}
