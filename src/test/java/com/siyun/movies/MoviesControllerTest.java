package com.siyun.movies;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MoviesController.class)
class MoviesControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    MoviesService moviesService;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void getMoviesReturnsMoviesListForNonEmptyMoviesList() throws Exception {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("CAAF36", "Joker", "Todd Phillips", 2019));
        movies.add(new Movie("AAB678","La La Land", "Damien Chazelle", 2016));
        MoviesList moviesList = new MoviesList(movies);

        when(moviesService.getMovies()).thenReturn(moviesList);
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movies", hasSize(2)));
    }

    @Test
    void getMoviesReturnsNoContentForEmptyMoviesList() throws Exception {
        MoviesList moviesList = new MoviesList();

        when(moviesService.getMovies()).thenReturn(moviesList);
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMoviesByDirectorReturnsMoviesList() throws Exception {
        List<Movie> movies = new ArrayList<>();
//        movies.add(new Movie("Joker", "Todd Phillips", 2019));
        movies.add(new Movie("AAB678","La La Land", "Damien Chazelle", 2016));
        movies.add(new Movie("BCD999", "First Man", "Damien Chazelle", 2018));
        MoviesList moviesList = new MoviesList(movies);

        when(moviesService.getMoviesByDirector(anyString())).thenReturn(moviesList);
        mockMvc.perform(get("/api/movies?director=Damien Chazelle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movies", hasSize(2)));
    }

    @Test
    void addMoviesReturnsMovieAddedIfSucceeds() throws Exception {
        Movie movie = new Movie("AAB678","La La Land", "Damien Chazelle", 2016);

        when(moviesService.addMovie(any(Movie.class))).thenReturn(movie);
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("La La Land"));

    }

    @Test
    void addMoviesReturnsBadRequestForInvalidRequest() throws Exception {
        when(moviesService.getMoviesByDirector(anyString())).thenThrow(InvalidMovieException.class);
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("movie"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMovieByIdReturnsMovieForValidId() throws Exception {
        Movie movie = new Movie("AAB678","La La Land", "Damien Chazelle", 2016);
        when(moviesService.getMovieById(anyString())).thenReturn(movie);
        mockMvc.perform(get("/api/movies/AAB678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("La La Land"));
    }

    @Test
    void getMovieByIdReturnsNoContentForInvalidId() throws Exception {
        when(moviesService.getMovieById(anyString())).thenReturn(null);
        mockMvc.perform(get("/api/movies/AAB679"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateMovieReturnsUpdatedMovieForSuccessfulUpdate() throws Exception {
        Movie movie = new Movie("AAB678","La La Land", "Damien Chazelle", 2016);
        MovieUpdate movieUpdate = new MovieUpdate(8, new String[]{"Emma Stone", "Ryan Gosling"});

        when(moviesService.getMovieById(anyString())).thenReturn(movie);
        movie.setCast(new String[]{"Emma Stone", "Ryan Gosling"});
        movie.setRating(8);
        when(moviesService.updateMovie(anyString(), any(MovieUpdate.class))).thenReturn(movie);

        mockMvc.perform(patch("/api/movies/AAB678")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("rating").value(8));
    }

    @Test
    void updateMovieReturnsNoContentForInvalidId() throws Exception {
        Movie movie = new Movie("AAB678","La La Land", "Damien Chazelle", 2016);
        MovieUpdate movieUpdate = new MovieUpdate(8, new String[]{"Emma Stone", "Ryan Gosling"});
        when(moviesService.updateMovie(anyString(), any(MovieUpdate.class))).thenReturn(null);
        mockMvc.perform(patch("/api/movies/AAB679")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieUpdate)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateMovieReturnsBadRequestForInvalidRequest() throws Exception {
        Movie movie = new Movie("AAB678","La La Land", "Damien Chazelle", 2016);
        when(moviesService.getMovieById(anyString())).thenReturn(movie);
        when(moviesService.updateMovie(anyString(), any(MovieUpdate.class))).thenThrow(InvalidUpdateException.class);
        mockMvc.perform(patch("/api/movies/AAB678")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMovieReturnsAcceptedForValidId() throws Exception {
        mockMvc.perform(delete("/api/movies/AAB678"))
                .andExpect(status().isAccepted());
        verify(moviesService).deleteMovie(anyString());
    }

    @Test
    void deleteMovieReturnsNoContentForInvalidId() throws Exception {
        doThrow(MovieNotFoundException.class).when(moviesService).deleteMovie(anyString());
        mockMvc.perform(delete("/api/movies/AAB679"))
                .andExpect(status().isNoContent());
    }
}