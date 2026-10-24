package ru.cinema.client.model;

public record Seat(long id, long hallId, int rowNumber, int seatNumber) {
    @Override public String toString() { return "Ряд " + rowNumber + ", место " + seatNumber; }
}
