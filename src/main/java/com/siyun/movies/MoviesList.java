package com.siyun.movies;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class MoviesList {
    private List<Movie> movies;

    public MoviesList() {
        movies = new ArrayList<>();
    }
    public MoviesList(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return movies.size() == 0;
    }
}
