package ru.cinema.client.model;

public record Hall(long id, int number) {
    @Override public String toString() { return "Зал №" + number; }
}
