package com.univice.cse364project.rating;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/")
public class RatingController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final RatingRepository ratingRepository;

    public RatingController(RatingRepository ratingRepository) throws IOException, CsvException {
        this.ratingRepository = ratingRepository;
        readDataFromCsv("ratings.csv");
    }

    @RequestMapping(value = "/ratings", method = RequestMethod.GET)
    public List<Rating> getAllRatings(){
        return ratingRepository.findAll();
    }
    @RequestMapping(value = "/rating/create", method = RequestMethod.POST)
    public Rating addNewRating(@RequestBody Rating rating){
        return ratingRepository.save(rating);
    }
    public void readDataFromCsv(String fileName) throws IOException, CsvException {
        ClassLoader classLoader = getClass().getClassLoader();
        CSVReader reader = new CSVReader(new FileReader(classLoader.getResource(fileName).getPath()));
        List<String[]> rows = reader.readAll();
        for (String[] row : rows) {
            Rating data = new Rating();
            data.setUserId(Long.parseLong(row[0]));
            data.setMovieId(Long.parseLong(row[1]));
            data.setRatingValue(Integer.parseInt(row[2]));
            data.setTimeStamp(row[3]);
            ratingRepository.save(data);
        }
    }

}