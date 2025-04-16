package com.codewithmada.spring6mvc.mappers;

import com.codewithmada.spring6mvc.entities.Beer;
import com.codewithmada.spring6mvc.models.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {

    Beer beerDTOToBeer(BeerDTO dto);
    BeerDTO beerToBeerDTO(Beer beer);

}
