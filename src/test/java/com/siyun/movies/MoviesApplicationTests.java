package com.siyun.movies;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
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

	@Test
	void getMoviesReturnsMoviesListForNonEmptyMoviesList() {
		ResponseEntity<MoviesList> response = testRestTemplate.getForEntity("/api/movies", MoviesList.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().isEmpty()).isFalse();
		assertThat(response.getBody().getMovies().size()).isEqualTo(10);
	}

	@Test
	void getMoviesReturnsNoContentForEmptyMoviesList() {
		moviesRepository.deleteAll();
		ResponseEntity<MoviesList> response = testRestTemplate.getForEntity("/api/movies", MoviesList.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(response.getBody()).isNull();
	}

	@Test
	void getMoviesByDirectorReturnsMoviesListForNonEmptyResult() {
		ResponseEntity<MoviesList> response = testRestTemplate.getForEntity("/api/movies?director=Damien Chazelle", MoviesList.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().isEmpty()).isFalse();
		assertThat(response.getBody().getMovies().size()).isEqualTo(2);
	}

	@Test
	void getMoviesByDirectorReturnsNoContentForEmptyResult() {
		ResponseEntity<MoviesList> response = testRestTemplate.getForEntity("/api/movies?director=Steve Jobs", MoviesList.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(response.getBody()).isNull();
	}

	@Test
	void addMoviesReturnsMovieAddedForValidRequest() {
		Movie newMovie = new Movie("RGA728", "The Godfather", "Francis Ford Coppola", 1972);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Movie> request = new HttpEntity<>(newMovie, headers);

		ResponseEntity<Movie> response = testRestTemplate.postForEntity("/api/movies", request, Movie.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getName()).isEqualTo("The Godfather");
	}

	@Test
	void addMoviesReturnsBadRequestForInvalidRequest() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> request = new HttpEntity<>("anything", headers);

		ResponseEntity<Movie> response = testRestTemplate.postForEntity("/api/movies", request, Movie.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void getMoviesByMovieNumberReturnsMovieIfFound() {
		ResponseEntity<Movie> response = testRestTemplate.getForEntity("/api/movies/SDDF89", Movie.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getMovieNumber()).isEqualTo("SDDF89");
	}

	@Test
	void getMoviesByMovieNumberReturnsNoContentIfNotFound() {
		ResponseEntity<Movie> response = testRestTemplate.getForEntity("/api/movies/SDDF88", Movie.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(response.getBody()).isNull();
	}

	@Test
	void updateMoviesReturnsUpdatedMovieIfFound() {
		testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		MovieUpdate update = new MovieUpdate(8, new String[]{"Emma Stone", "Ryan Gosling"});
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<MovieUpdate> request = new HttpEntity<>(update, headers);

		ResponseEntity<Movie> response = testRestTemplate.exchange("/api/movies/AAB678", HttpMethod.PATCH, request, Movie.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getRating()).isEqualTo(8);
		assertThat(response.getBody().getCast().length).isEqualTo(2);
	}

	@Test
	void updateMoviesReturnsNoContentIfNotFound() {
		testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		MovieUpdate update = new MovieUpdate(8, new String[]{"Emma Stone", "Ryan Gosling"});
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<MovieUpdate> request = new HttpEntity<>(update, headers);

		ResponseEntity<Movie> response = testRestTemplate.exchange("/api/movies/SDDF88", HttpMethod.PATCH, request, Movie.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(response.getBody()).isNull();
	}

	@Test
	void updateMoviesReturnsBadRequestForInvalidRequest() {
		testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> request = new HttpEntity<>("anything", headers);

		ResponseEntity<Movie> response = testRestTemplate.exchange("/api/movies/SDDF89", HttpMethod.PATCH, request, Movie.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void deleteMoviesReturnsAcceptedIfFound() {
		testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<?> response = testRestTemplate.exchange("/api/movies/SDDF89", HttpMethod.DELETE, new HttpEntity<>(""), Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		assertThat(response.getBody()).isNull();
	}

	@Test
	void deleteMoviesReturnsNoContentIfNotFound() {
		testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<?> response = testRestTemplate.exchange("/api/movies/SDDF88", HttpMethod.DELETE, new HttpEntity<>(""), Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(response.getBody()).isNull();
	}
}
