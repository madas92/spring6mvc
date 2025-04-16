package com.codewithmada.spring6mvc.controllers;

import com.codewithmada.spring6mvc.models.CustomerDTO;
import com.codewithmada.spring6mvc.services.CustomerService;
import com.codewithmada.spring6mvc.services.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomerService customerService;

    @Autowired
    ObjectMapper objectMapper;

    CustomerServiceImpl customerServiceImpl;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> argumentCaptor;

    @Test
    void testPatchCustomer() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.getCustomers().get(0);
        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("customerName", "New Customer Name");

        //todo
        //use verify when is not returning anything
    }

    @Test
    void testDeleteCustomer() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.getCustomers().get(0);

        mockMvc.perform(delete("/api/v1/customer/" + customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());
    }

    @Test
    void testUpdateCustomer() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.getCustomers().get(0);

        mockMvc.perform(put("/api/v1/customer/" + customerDTO.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isNoContent());

        verify(customerService).updateCustomerById(any(UUID.class), any(CustomerDTO.class));
    }
    @Test
    void testCreateNewCustomer() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.getCustomers().get(0);
        customerDTO.setVersion(null);
        customerDTO.setId(null);
        given(customerService.saveNewCustomer(any(CustomerDTO.class)))
                .willReturn(customerServiceImpl.getCustomers().get(1));

        mockMvc.perform(post("/api/v1/customer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void getCustomers() throws Exception {
        given(customerService.getCustomers()).willReturn(customerServiceImpl.getCustomers());

        mockMvc.perform(get("/api/v1/customer").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void getCustomerById() throws Exception {
        CustomerDTO testCustomerDTO =  customerServiceImpl.getCustomers().get(0);

        //configure Mockito
        given(customerService.getCustomerById(testCustomerDTO.getId())).willReturn(Optional.of(testCustomerDTO));

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, testCustomerDTO.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerName", is(testCustomerDTO.getCustomerName())));
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());
        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());

    }
}