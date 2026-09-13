package com.freshmart.user.config;

import com.freshmart.user.domain.Customer;
import com.freshmart.user.repo.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedCustomers(CustomerRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.save(new Customer("Nguyễn Minh Anh", "anh@freshmart.vn", "0901000001", "Hà Nội"));
            repository.save(new Customer("Trần Quốc Huy", "huy@freshmart.vn", "0901000002", "Đà Nẵng"));
            repository.save(new Customer("Lê Thị Hoa", "hoa@freshmart.vn", "0901000003", "TP. Hồ Chí Minh"));
            repository.save(new Customer("Phạm Văn Đức", "duc@freshmart.vn", "0901000004", "Cần Thơ"));
        };
    }
}
