package com.siyun.movies;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoviesService {

    public MoviesList getMovies() {
        return null;
    }

    public MoviesList getMoviesByDirector(String director) {
        return null;
    }

    public Movie addMovie(Movie movie) {
        return null;
    }

    public Movie getMovieById(String id) {
        return null;
    }

    public Movie updateMovie(String id, MovieUpdate movieUpdate) {
        return null;
    }

    public void deleteMovie(String id) throws MovieNotFoundException {
        return;
    }
}
