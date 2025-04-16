package com.codewithmada.spring6mvc.services;

import com.codewithmada.spring6mvc.models.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    List<CustomerDTO> getCustomers();
    Optional<CustomerDTO> getCustomerById(UUID customerId);

    CustomerDTO saveNewCustomer(CustomerDTO customerDTO);

    void updateCustomerById(UUID customerId, CustomerDTO customerDTO);

    void deleteCustomerById(UUID uuid);
}
