package com.siyun.movies;

import com.siyun.movies.Exceptions.MovieNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoviesServiceTest {
    MoviesService moviesService;
    @Mock
    MoviesRepository moviesRepository;

    @BeforeEach
    void setup() {
        moviesService = new MoviesService(moviesRepository);
    }

    @Test
    void getMovies() {
        // setup
        Movie movie1 = new Movie("AAB678","La La Land", "Damien Chazelle", 2016);
        Movie movie2 = new Movie("CAAF36", "Joker", "Todd Phillips", 2019);
        List<Movie> movies = new ArrayList<>(Arrays.asList(movie1, movie2));
        when(moviesRepository.findAll()).thenReturn(movies);
        // execute
        MoviesList moviesList = moviesService.getMovies();
        // assert
        assertThat(moviesList).isNotNull();
        assertThat(moviesList.isEmpty()).isFalse();
    }

    @Test
    void getMoviesByDirector() {
        // setup
        Movie movie1 = new Movie("AAB678","La La Land", "Damien Chazelle", 2016);
        Movie movie2 = new Movie("BCD999", "First Man", "Damien Chazelle", 2018);
        List<Movie> movies = new ArrayList<>(Arrays.asList(movie1, movie2));
        when(moviesRepository.findByDirectorContains(anyString())).thenReturn(movies);
        // execute
        MoviesList moviesList = moviesService.getMoviesByDirector("Damien Chazelle");
        // assert
        assertThat(moviesList).isNotNull();
        assertThat(moviesList.isEmpty()).isFalse();
        assertThat(moviesList.getMovies().size()).isEqualTo(2);
    }

    @Test
    void addMovie() {
        // setup
        Movie movie = new Movie("CAAF36", "Joker", "Todd Phillips", 2019);
        when(moviesRepository.save(any(Movie.class))).thenReturn(movie);
        // execute
        Movie addedMovie = moviesService.addMovie(movie);
        // assert
        assertThat(addedMovie).isNotNull();
        assertThat(addedMovie.getName()).isEqualTo(movie.getName());
    }

    @Test
    void getMovieById() {
        // setup
        Movie movie = new Movie("CAAF36", "Joker", "Todd Phillips", 2019);
        when(moviesRepository.findById(anyString())).thenReturn(Optional.of(movie));
        // execute
        Movie movieById = moviesService.getMovieById(movie.getId());
        // assert
        assertThat(movieById).isNotNull();
        assertThat(movieById.getName()).isEqualTo("Joker");
    }

    @Test
    void updateMovie() {
        // setup
        Movie movie = new Movie("AAB678","La La Land", "Damien Chazelle", 2016);
        MovieUpdate update = new MovieUpdate(8, new String[]{"Emma Stone", "Ryan Gosling"});
        when(moviesRepository.findById(anyString())).thenReturn(Optional.of(movie));
        movie.setRating(update.getRating());
        movie.setCast(update.getCast());
        when(moviesRepository.save(any(Movie.class))).thenReturn(movie);
        // execute
        Movie updatedMovie = moviesService.updateMovie("AAB678", update);
        // assert
        assertThat(updatedMovie.getName()).isEqualTo("La La Land");
        assertThat(updatedMovie.getRating()).isEqualTo(8);

    }

    @Test
    void deleteMovieForValidId() {
        // setup
        Movie movie = new Movie("AAB678","La La Land", "Damien Chazelle", 2016);
        when(moviesRepository.findById(anyString())).thenReturn(Optional.of(movie));
        // execute
        moviesService.deleteMovie(movie.getId());
        // verify
        verify(moviesRepository).delete(any(Movie.class));
    }

    @Test
    void deleteMovieForInvalidId() {
        // setup
        when(moviesRepository.findById(anyString())).thenReturn(Optional.empty());
        // assert
        assertThatExceptionOfType(MovieNotFoundException.class)
                .isThrownBy(() -> {
                    // execute
                    moviesService.deleteMovie("WWQQR8");
                });
    }
}