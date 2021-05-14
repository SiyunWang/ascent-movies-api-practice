package com.siyun.movies;

public class MovieUpdate {
    private int rating;
    private String[] cast;

    public MovieUpdate() {
    }

    public MovieUpdate(int rating, String[] cast) {
        this.rating = rating;
        this.cast = cast;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String[] getCast() {
        return cast;
    }

    public void setCast(String[] cast) {
        this.cast = cast;
    }
}
