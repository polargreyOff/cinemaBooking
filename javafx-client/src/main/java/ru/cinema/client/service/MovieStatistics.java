package ru.cinema.client.service;

import ru.cinema.client.model.Movie;
import ru.cinema.client.model.Session;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class MovieStatistics {
    private MovieStatistics() {}

    public static String describe(List<Movie> movies, List<Session> sessions) {
        if (movies.isEmpty()) return "Фильмов пока нет.";

        IntSummaryStatistics durations = movies.stream().mapToInt(Movie::duration).summaryStatistics();
        long rated = movies.stream().filter(movie -> !Objects.toString(movie.age_rating(), "").isBlank()).count();

        HashMap<Long, Integer> sessionCounts = new HashMap<>();
        for (Session session : sessions) {
            long movieId = session.movie().id();
            sessionCounts.put(movieId, sessionCounts.getOrDefault(movieId, 0) + 1);
        }

        List<Movie> rankedMovies = new ArrayList<>(movies);
        rankedMovies.removeIf(movie -> !sessionCounts.containsKey(movie.id()));
        rankedMovies.sort(Comparator.comparingInt((Movie movie) -> sessionCounts.get(movie.id())).reversed()
                .thenComparing(Movie::title));
        String mostShown = rankedMovies.isEmpty() ? "нет сеансов"
                : rankedMovies.getFirst().title() + " (" + sessionCounts.get(rankedMovies.getFirst().id()) + ")";

        return "Всего фильмов: " + durations.getCount() + "\n"
                + "Суммарная длительность: " + durations.getSum() + " мин\n"
                + "Средняя длительность: " + String.format(Locale.forLanguageTag("ru"), "%.1f", durations.getAverage()) + " мин\n"
                + "Самый короткий фильм: " + durations.getMin() + " мин\n"
                + "Самый длинный фильм: " + durations.getMax() + " мин\n"
                + "Фильмов с указанным возрастным рейтингом: " + rated + "\n"
                + "Сеансов в списке: " + sessions.size() + "\n"
                + "Фильмов с сеансами: " + rankedMovies.size() + "\n"
                + "Больше всего сеансов: " + mostShown;
    }
}
