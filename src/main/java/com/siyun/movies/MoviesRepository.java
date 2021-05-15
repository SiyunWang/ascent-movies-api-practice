package com.siyun.movies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoviesRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByDirectorContains(String director);
    Optional<Movie> findById(String anyString);
}
