package com.codewithmada.spring6mvc.controllers;

import com.codewithmada.spring6mvc.models.CustomerDTO;
import com.codewithmada.spring6mvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    public static final String CUSTOMER_PATH = "/api/v1/customer";
    public static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<CustomerDTO> deleteCustomerById(@PathVariable("customerId") UUID uuid){
        customerService.deleteCustomerById(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable("customerId") UUID customerId,
                                                      @RequestBody CustomerDTO customerDTO){
        customerService.updateCustomerById(customerId, customerDTO);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO){
        CustomerDTO savedCustomerDTO = customerService.saveNewCustomer(customerDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", CUSTOMER_PATH + "/" + savedCustomerDTO.getId().toString());
        return new ResponseEntity<> (headers, HttpStatus.CREATED);
    }
    @GetMapping(CUSTOMER_PATH)
    public List<CustomerDTO> getCustomers(){
        log.debug("Get Customers  - in Controller");
        return customerService.getCustomers();
    }

    @GetMapping(CUSTOMER_PATH_ID)
    public CustomerDTO getCustomerById(@PathVariable("customerId") UUID customerId){
        log.debug("Get Customers by Id - in Controller");
        return customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new);
    }
}
