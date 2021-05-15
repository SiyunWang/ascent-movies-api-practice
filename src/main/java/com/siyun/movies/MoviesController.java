package com.siyun.movies;

import com.siyun.movies.Exceptions.InvalidMovieException;
import com.siyun.movies.Exceptions.InvalidUpdateException;
import com.siyun.movies.Exceptions.MovieNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MoviesController {

    private MoviesService moviesService;
    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    @GetMapping
    public ResponseEntity<MoviesList> getMovies(@RequestParam(required = false) String director) {
        MoviesList moviesList;
        if (director == null) {
            moviesList = moviesService.getMovies();
        } else {
            moviesList = moviesService.getMoviesByDirector(director);
        }
        return moviesList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(moviesList);
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        try {
            return ResponseEntity.ok(moviesService.addMovie(movie));
        } catch (InvalidMovieException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable String id) {
        Movie movie = moviesService.getMovieById(id);
        return movie == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(movie);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String id, @RequestBody MovieUpdate movieUpdate) {
        Movie movie = moviesService.getMovieById(id);
        if (movie == null) return ResponseEntity.noContent().build();
        try {
            return ResponseEntity.ok(moviesService.updateMovie(id, movieUpdate));
        } catch (InvalidUpdateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable String id) {
        try {
            moviesService.deleteMovie(id);
            return ResponseEntity.accepted().build();
        } catch (MovieNotFoundException e){
            return ResponseEntity.noContent().build();
        }
    }
}
