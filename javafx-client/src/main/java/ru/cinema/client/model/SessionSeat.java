package ru.cinema.client.model;

public record SessionSeat(long id, int rowNumber, int seatNumber, boolean available) {
    @Override public String toString() { return "Ряд " + rowNumber + ", место " + seatNumber; }
}
