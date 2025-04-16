package com.codewithmada.spring6mvc.controllers;

import com.codewithmada.spring6mvc.entities.Beer;
import com.codewithmada.spring6mvc.mappers.BeerMapper;
import com.codewithmada.spring6mvc.models.BeerDTO;
import com.codewithmada.spring6mvc.models.BeerStyle;
import com.codewithmada.spring6mvc.repositories.BeerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.core.Is.is;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;
    
    @Autowired
    BeerMapper beerMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void testPatchBeerBadName() throws Exception {
        Beer beer = beerRepository.findAll().get(0);
        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "New Name1");

        MvcResult mvcResult = mockMvc.perform(patch(BeerController.BEER_PATH_ID, beer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))
                .andExpect(status().isNoContent())
                //.andExpect(jsonPath("$.length()", is(1)))
                .andReturn();
    }

    @Test
    void testListBeers() {
        Page<BeerDTO> dtos = beerController.listBeers(null, BeerStyle.IPA, null, 1, 25);
        assertThat(dtos.getContent().size()).isEqualTo(548);
    }

    @Test
    @Rollback  //this test will rollback
    @Transactional
    void testEmptyList() {
        beerRepository.deleteAll();;
        Page<BeerDTO> dtos = beerController.listBeers(null, BeerStyle.LAGER, null, 1, 25);
        assertThat(dtos.getContent().size()).isEqualTo(0);
    }

    @Test
    void testGetById() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO dto = beerController.getBeer(beer.getId());
        assertThat(dto).isNotNull();
    }

    @Test
    void testBeerIdNotFound() {
        assertThrows(NotFoundException.class, 
                ()-> beerController.getBeer(UUID.randomUUID()));
    }

    @Test
    @Transactional
    @Rollback
    void testSaveNewBeer() {
        BeerDTO beerDTO = BeerDTO.builder().beerName("Beer from Integration Test").build();
        ResponseEntity<BeerDTO> responseEntity = beerController.createBeer(beerDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        String [] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);

        Beer beer = beerRepository.findById(savedUUID).get();
        assertThat(beer).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateExistingBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerMapper.beerToBeerDTO(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        final String UPDATED = "UPDATED";
        beerDTO.setBeerName(UPDATED);

        //persist in DB
        ResponseEntity<BeerDTO> responseEntity = beerController.updateBeer(beer.getId(), beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo(UPDATED);
    }

    @Test
    void testUpdateNotFound() {
        assertThrows(NotFoundException.class,
                () -> beerController.updateBeer(UUID.randomUUID(), BeerDTO.builder().build()));
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteByIdFound() {
        Beer beer = beerRepository.findAll().get(0);
        ResponseEntity<BeerDTO> responseEntity = beerController.deleteBeerById(beer.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteByIdNotFound() {
        assertThrows(NotFoundException.class,
                () -> beerController.deleteBeerById(UUID.randomUUID()));

    }

    @Test
    void testPatchBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerMapper.beerToBeerDTO(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        final String PATCH = "Patch";
        beerDTO.setBeerName(PATCH);

        //persist in DB
        ResponseEntity<BeerDTO> responseEntity = beerController.updateBeerByPatch(beer.getId(), beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo(PATCH);
    }

    @Test
    void testListBeersByName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerName", "IPA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(11)));
    }

    @Test
    void testListBeerByStyleAndName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                .queryParam("beerName", "IPA")
                .queryParam("styleName", BeerStyle.PALE_ALE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(11)));
    }

    @Test
    void testListBeerByStyleNameAndNameAndShowInventory() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("beerName", "IPA")
                        .queryParam("styleName", BeerStyle.PALE_ALE.name())
                        .queryParam("quantityOnHand", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(11)));
                //.andExpect(jsonPath("$.[0].quantityOnHand").value(IsNull.notNullValue()));
    }
}