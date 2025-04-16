package com.codewithmada.spring6mvc.controllers;

import com.codewithmada.spring6mvc.models.BeerDTO;
import com.codewithmada.spring6mvc.models.BeerStyle;
import com.codewithmada.spring6mvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor

public class BeerController {

    private final BeerService beerService;
    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity<BeerDTO> updateBeerByPatch(@PathVariable("beerId") UUID beerId, @RequestBody BeerDTO beerDTO) {
        beerService.patch(beerId, beerDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity<BeerDTO> deleteBeerById(@PathVariable("beerId") UUID beerId){

        if (!beerService.deleteBeerById(beerId)){
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(BEER_PATH_ID)
    public ResponseEntity<BeerDTO> updateBeer(@PathVariable("beerId") UUID beerId, @RequestBody BeerDTO beerDTO){
        if(beerService.updateBeerById(beerId, beerDTO).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping(BEER_PATH)
    //@RequestMapping(method = RequestMethod.POST)
    //@Validated tells us that our request should be conformed with validation constraints from BeerDTO
    public ResponseEntity<BeerDTO> createBeer(@Validated @RequestBody BeerDTO beerDTO){
        BeerDTO savedBeerDTO = beerService.saveNewBeer(beerDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/v1/beer/" + savedBeerDTO.getId().toString());
        return new ResponseEntity<BeerDTO>(headers, HttpStatus.CREATED);
    }

    @GetMapping(BEER_PATH_ID)
    public BeerDTO getBeer(@PathVariable("beerId") UUID beerId) {
        log.debug("Get beer by id  - in Controller 123");
        return beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);
    }

    @GetMapping(BEER_PATH)
    public Page<BeerDTO> listBeers(@RequestParam(required = false) String beerName,
                                   @RequestParam(required = false) BeerStyle beerStyle,
                                   @RequestParam(required = false) Boolean quantityOnHand,
                                   @RequestParam(required = false) Integer pageNumber,
                                   @RequestParam(required = false) Integer pageSize){
        return beerService.listBeers(beerName, beerStyle, quantityOnHand, 1, 25);
    }

}
