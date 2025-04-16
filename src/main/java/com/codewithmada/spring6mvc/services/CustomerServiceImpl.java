package com.codewithmada.spring6mvc.services;

import com.codewithmada.spring6mvc.models.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private Map<UUID, CustomerDTO> customerMap;
    public CustomerServiceImpl() {
            this.customerMap = new HashMap<>();

            CustomerDTO customerDTO1 = CustomerDTO.builder()
                    .id(UUID.randomUUID())
                    .version(1)
                    .customerName("Customer 1")
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();

        CustomerDTO customerDTO2 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .version(2)
                .customerName("Customer 2")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        CustomerDTO customerDTO3 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .version(3)
                .customerName("Customer 3")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        customerMap.put(customerDTO1.getId(), customerDTO1);
        customerMap.put(customerDTO2.getId(), customerDTO2);
        customerMap.put(customerDTO3.getId(), customerDTO3);
    }

    @Override
    public List<CustomerDTO> getCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID customerId) {
        return Optional.of(customerMap.get(customerId));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        CustomerDTO savedCustomerDTO = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .customerName(customerDTO.getCustomerName())
                .version(customerDTO.getVersion())
                .createdDate(customerDTO.getCreatedDate())
                .updatedDate(customerDTO.getUpdatedDate()).build();

        customerMap.put(customerDTO.getId(), customerDTO);

        return savedCustomerDTO;
    }

    @Override
    public void updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
        CustomerDTO existingCustomerDTO = customerMap.get(customerId);
        existingCustomerDTO.setCustomerName(customerDTO.getCustomerName());
        existingCustomerDTO.setVersion(customerDTO.getVersion());
        customerMap.put(existingCustomerDTO.getId(), existingCustomerDTO);
    }

    @Override
    public void deleteCustomerById(UUID uuid) {
        customerMap.remove(uuid);
    }
}
