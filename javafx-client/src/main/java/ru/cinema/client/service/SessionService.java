package ru.cinema.client.service;

import ru.cinema.client.model.Hall;
import ru.cinema.client.model.Movie;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

public final class SessionService {
    private SessionService() {}
    public static Map<String, Object> validate(Movie movie, Hall hall, String start, String priceText) {
        if (movie == null) throw new IllegalArgumentException("Выберите фильм.");
        if (hall == null) throw new IllegalArgumentException("Выберите зал.");
        LocalDateTime time;
        try { time = LocalDateTime.parse(start.strip()); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Время укажите как ГГГГ-ММ-ДДTЧЧ:ММ, например 2026-11-01T18:30."); }
        if (!time.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("Время сеанса должно быть в будущем.");
        int price;
        try { price = Integer.parseInt(priceText.strip()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Цена должна быть целым числом."); }
        if (price < 1 || price > 1000000) throw new IllegalArgumentException("Цена должна быть от 1 до 1 000 000.");
        return Map.of("movieId", movie.id(), "hallId", hall.id(), "startTime", time.toString(), "price", price);
    }
}
