package com.codewithmada.spring6mvc.services;

import com.codewithmada.spring6mvc.models.BeerDTO;
import com.codewithmada.spring6mvc.models.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    Optional<BeerDTO> getBeerById(UUID id);

    Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize);

    BeerDTO saveNewBeer(BeerDTO beerDTO);

    Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO);

    boolean deleteBeerById(UUID beerId);

    Optional<BeerDTO> patch(UUID beerId, BeerDTO beerDTO);
}
