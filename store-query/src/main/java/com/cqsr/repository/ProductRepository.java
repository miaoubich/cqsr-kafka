package com.cqsr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cqsr.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
