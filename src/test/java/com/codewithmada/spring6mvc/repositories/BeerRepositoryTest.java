package com.codewithmada.spring6mvc.repositories;

import com.codewithmada.spring6mvc.entities.Beer;
import com.codewithmada.spring6mvc.models.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testSaveBeer() {
        assertThrows(ConstraintViolationException.class, () ->{
            Beer savedBeer = beerRepository.save(Beer.builder()
                    .beerName("My Beer1111111111111111111111111111111111111111111111111111")
                    .beerStyle(BeerStyle.LAGER)
                    .upc("UPC")
                    .price(new BigDecimal("10.99"))
                    .build());
            // write immediately to DB
            beerRepository.flush();
        });
    }

    @Test
    void testGerBeerListByName() {
        Page<Beer> beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%", null);
        assertThat(beerList.getContent()).isEmpty();
    }
}