package ru.cinema.client.model;

public record Session(long id, Movie movie, Hall hall, String startTime, int price, String endTime) {
    @Override public String toString() {
        return movie.title() + " · " + hall + " · " + startTime.replace('T', ' ') + " · " + price + " ₽";
    }
}

