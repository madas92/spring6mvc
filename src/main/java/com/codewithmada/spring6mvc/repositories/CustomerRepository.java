package com.codewithmada.spring6mvc.repositories;

import com.codewithmada.spring6mvc.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
