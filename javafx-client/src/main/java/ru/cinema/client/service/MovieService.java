package ru.cinema.client.service;

import ru.cinema.client.model.Movie;
import java.util.Map;

public final class MovieService {
    private MovieService() {}
    public static Map<String, Object> validate(String title, String durationText, String rating, String description) {
        title = title.strip(); rating = rating.strip(); description = description.strip();
        if (title.isEmpty()) throw new IllegalArgumentException("Укажите название фильма.");
        if (title.length() > 255) throw new IllegalArgumentException("Название не должно быть длиннее 255 символов.");
        if (description.isEmpty()) throw new IllegalArgumentException("Укажите описание фильма.");
        int duration;
        try { duration = Integer.parseInt(durationText.strip()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Длительность должна быть целым числом минут."); }
        if (duration < 1 || duration > 1000) throw new IllegalArgumentException("Длительность должна быть от 1 до 1000 минут.");
        if (rating.length() > 10) throw new IllegalArgumentException("Возрастной рейтинг не должен быть длиннее 10 символов.");
        return Map.of("title", title, "duration", duration, "age_rating", rating, "description", description);
    }
    public static String category(Movie movie) { return movie.duration() < 120 ? "До 2 часов" : "От 2 часов"; }
}

