package com.ms.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
