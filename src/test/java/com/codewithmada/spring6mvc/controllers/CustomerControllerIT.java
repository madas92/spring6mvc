package com.codewithmada.spring6mvc.controllers;

import com.codewithmada.spring6mvc.entities.Beer;
import com.codewithmada.spring6mvc.entities.Customer;
import com.codewithmada.spring6mvc.models.BeerDTO;
import com.codewithmada.spring6mvc.models.CustomerDTO;
import com.codewithmada.spring6mvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testGetAllCustomers() {
        List<CustomerDTO> customers = customerController.getCustomers();
        assertThat(customers.size()).isEqualTo(3);

    }

    @Test
    @Transactional
    @Rollback
    void testEmptyList() {
        customerRepository.deleteAll();
        List<CustomerDTO> dtos = customerController.getCustomers();
        assertThat(dtos.size()).isEqualTo(0);
    }

    @Test
    void testGetCustomerById() {
        CustomerDTO dto = customerController.getCustomers().get(0);
        assertThat(dto).isNotNull();
    }

    @Test
    void testNotFoundCustomerId() {
        assertThrows(NotFoundException.class,
                ()-> customerController.getCustomerById(UUID.randomUUID()));
    }


    @Test
    @Transactional
    @Rollback
    void testSaveNewCustomer() {
//        CustomerDTO customerDTO = CustomerDTO.builder().customerName("Customer from Integration Test").build();
//        ResponseEntity<CustomerDTO> responseEntity = customerController.createCustomer(customerDTO);
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.valueOf(201));
//        String [] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
//        UUID savedUUID = UUID.fromString(locationUUID[4]);

//        Customer customer = customerRepository.findById(savedUUID).get();
//        assertThat(customer).isNotNull();
    }
}