package com.siyun.movies;

import com.siyun.movies.Exceptions.MovieNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoviesService {
    MoviesRepository moviesRepository;

    public MoviesService(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    public MoviesList getMovies() {
        return new MoviesList(moviesRepository.findAll());
    }

    public MoviesList getMoviesByDirector(String director) {
        return new MoviesList(moviesRepository.findByDirectorContains(director));
    }

    public Movie addMovie(Movie movie) {
        return moviesRepository.save(movie);
    }

    public Movie getMovieByMovieNumber(String movieNumber) {
        Optional<Movie> movie = moviesRepository.findByMovieNumber(movieNumber);
        return movie.orElse(null);
    }

    public Movie updateMovie(String movieNumber, MovieUpdate movieUpdate) {
        Optional<Movie> movie = moviesRepository.findByMovieNumber(movieNumber);
        if (!movie.isPresent()) return null;
        movie.get().setRating(movieUpdate.getRating());
        movie.get().setCast(movieUpdate.getCast());
        return moviesRepository.save(movie.get());
    }

    public void deleteMovie(String movieNumber) throws MovieNotFoundException {
        Optional<Movie> movie = moviesRepository.findByMovieNumber(movieNumber);
        if (movie.isPresent()) {
            moviesRepository.delete(movie.get());
        } else {
            throw new MovieNotFoundException();
        }
    }
}
