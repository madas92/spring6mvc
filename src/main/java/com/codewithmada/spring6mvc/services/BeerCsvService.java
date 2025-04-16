package com.codewithmada.spring6mvc.services;


import com.codewithmada.spring6mvc.models.BeerCSVRecord;

import java.io.File;
import java.util.List;

public interface BeerCsvService {

     List<BeerCSVRecord> convertCSV(File file);

}
