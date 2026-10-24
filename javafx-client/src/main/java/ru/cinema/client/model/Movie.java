package ru.cinema.client.model;

public record Movie(long id, String title, int duration, String age_rating, String description) {
    @Override public String toString() { return title; }
}
