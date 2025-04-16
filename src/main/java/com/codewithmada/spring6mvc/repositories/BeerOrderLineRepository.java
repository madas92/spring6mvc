package com.codewithmada.spring6mvc.repositories;

import com.codewithmada.spring6mvc.entities.BeerOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerOrderLineRepository extends JpaRepository<BeerOrderLine, UUID> {
}
