package com.codewithmada.spring6mvc.services;

import com.codewithmada.spring6mvc.models.BeerCSVRecord;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
@Slf4j
public class BeerCsvServiceImpl implements BeerCsvService {

    @Override
    public List<BeerCSVRecord> convertCSV(File file) {
        try {
            return new CsvToBeanBuilder<BeerCSVRecord>(new FileReader(file))
                            .withType(BeerCSVRecord.class)
                            .build().parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
