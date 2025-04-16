package com.codewithmada.spring6mvc.controllers;

import com.codewithmada.spring6mvc.models.BeerDTO;
import com.codewithmada.spring6mvc.services.BeerService;
import com.codewithmada.spring6mvc.services.BeerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class) // = test splices; it brings only BeerController dependencies
//@SpringBootTest  //unit test
class BeerControllerTest {

    //@Autowired
    //BeerController beerController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BeerService beerService;

    BeerServiceImpl beerServiceImpl;

    // it will reset the service before each test
    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }
    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;
    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    @Test
    void testPatchBeer() throws Exception {
        BeerDTO beerDTO = beerServiceImpl
                .listBeers(null, null, null, 1, 25).getContent().get(0);
        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "New Name");

        mockMvc.perform(patch(BeerController.BEER_PATH_ID, beerDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))
                .andExpect(status().isNoContent());

        verify(beerService).patch(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        assertThat(beerDTO.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beerMap.get("beerName")).isEqualTo(beerArgumentCaptor.getValue().getBeerName());

        //use verify when is not returning anything
    }

    @Test
    void testUpdateBeer() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(0);

        given(beerService.updateBeerById(any(), any())).willReturn(Optional.of(beerDTO));
        mockMvc.perform(put(BeerController.BEER_PATH_ID, beerDTO.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeerById(any(UUID.class), any(BeerDTO.class));
    }

    @Test
    void testDeleteBeer() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(0);
        //set the service to return the proper value
        given(beerService.deleteBeerById(any())).willReturn(true);

        mockMvc.perform(delete(BeerController.BEER_PATH_ID, beerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //use verify when is not returning anything
        verify(beerService).deleteBeerById(uuidArgumentCaptor.capture());
        assertThat(beerDTO.getId()).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    void testCreateNewBeer() throws Exception {
        //ObjectMapper mapper = new ObjectMapper();
        // InvalidDefinitionException
        //mapper.findAndRegisterModules(); //when a certain type of data is not serialized using Jackson

        BeerDTO beerDTO = beerServiceImpl
                .listBeers(null, null, null, 1, 25).getContent().get(0);
        beerDTO.setVersion(null);
        beerDTO.setId(null);

        given(beerService.saveNewBeer(any(BeerDTO.class)))
                .willReturn(beerServiceImpl
                        .listBeers(null, null, null, 1, 25).getContent().get(1));
        mockMvc.perform(post(BeerController.BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)   //create a JSON
                        .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void testListBeers() throws Exception {
        given(beerService.listBeers(any(), any(), any(), any(), any()))
                .willReturn(beerServiceImpl
                        .listBeers(null, null, false, 1, 25));

        mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", is(3)));
    }

    @Test
    void getBeerByIdNotFound() throws Exception {
        given(beerService.getBeerById(any())).willReturn(Optional.empty());
        mockMvc.perform(get(BeerController.BEER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBeerById() throws Exception {
        BeerDTO testBeerDTO =  beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(0);

        //configure Mockito
        given(beerService.getBeerById(testBeerDTO.getId())).willReturn(Optional.of(testBeerDTO));

        mockMvc.perform(get(BeerController.BEER_PATH_ID, testBeerDTO.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBeerDTO.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(testBeerDTO.getBeerName())));
    }

    @Test
    void testCreateBeerNullBeerName() throws Exception{
        BeerDTO dto = BeerDTO.builder().build(); //create an empty object

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(1));

        MvcResult mvcResult = mockMvc.perform(post(BeerController.BEER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(6)))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }
}