package com.freshmart.order.repo;

import com.freshmart.order.domain.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<SalesOrder, Long> {
}
