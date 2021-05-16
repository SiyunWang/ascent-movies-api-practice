package com.siyun.movies;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoviesApplicationTests {
	@Autowired
	MoviesRepository moviesRepository;
	@Autowired
	TestRestTemplate testRestTemplate;

	List<Movie> movies;
	@BeforeEach
	void setup() {
		movies = new ArrayList<>();
		movies.add(new Movie("SDDF89", "Soul", "Pete Docter", 2020));
		movies.add(new Movie("AAB678","La La Land", "Damien Chazelle", 2016));
		movies.add(new Movie("BCD999", "First Man", "Damien Chazelle", 2018));
		movies.add(new Movie("CAAF36", "Joker", "Todd Phillips", 2019));
		movies.add(new Movie("457BYU", "Parasite", "Bong Joon-Ho", 2019));
		movies.add(new Movie("UY77L2", "Casablanca", "Michael Curtiz", 1942));
		movies.add(new Movie("KM6M87", "The Notebook", "Nick Cassavetes", 2004));
		movies.add(new Movie("POL051", "About time", "Richard Curtis", 2013));
		movies.add(new Movie("KKMA09", "Toy Story", "John Lasseter", 1995));
		movies.add(new Movie("STY762", "Green Book", "Peter Farrelly", 2018));
		moviesRepository.saveAll(movies);
	}

	@AfterEach
	void tearDown() {
		moviesRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

}
