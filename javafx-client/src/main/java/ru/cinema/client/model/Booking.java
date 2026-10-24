package ru.cinema.client.model;

public record Booking(long id, Session session, Seat seat, String bookingTime) {}
