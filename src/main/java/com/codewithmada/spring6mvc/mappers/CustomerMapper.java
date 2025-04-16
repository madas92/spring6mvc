package com.codewithmada.spring6mvc.mappers;

import com.codewithmada.spring6mvc.entities.Customer;
import com.codewithmada.spring6mvc.models.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDTOToCustomer(CustomerDTO dto);
    CustomerDTO customerToCustomerDTO(Customer customer);
}
