package ru.cinema.client.service;

import ru.cinema.client.model.Movie;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

public final class MovieCsvExporter {
    private MovieCsvExporter() {}

    public static void write(Path path, Collection<Movie> movies) throws IOException {
        StringBuilder csv = new StringBuilder("\uFEFFНазвание;Длительность (мин);Возрастной рейтинг;Описание\r\n");
        for (Movie movie : movies) {
            csv.append(cell(movie.title())).append(';').append(movie.duration()).append(';')
                    .append(cell(movie.age_rating())).append(';').append(cell(movie.description())).append("\r\n");
        }
        Files.writeString(path, csv, StandardCharsets.UTF_8);
    }

    private static String cell(String value) {
        return "\"" + Objects.toString(value, "").replace("\"", "\"\"") + "\"";
    }
}
