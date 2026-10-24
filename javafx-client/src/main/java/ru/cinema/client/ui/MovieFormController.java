package ru.cinema.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.cinema.client.model.Movie;
import ru.cinema.client.service.MovieService;
import java.util.Map;

public final class MovieFormController {
    @FXML private TextField title, duration, rating;
    @FXML private TextArea description;
    public void setMovie(Movie movie) {
        if (movie == null) return;
        title.setText(movie.title()); duration.setText(Integer.toString(movie.duration()));
        rating.setText(movie.age_rating()); description.setText(movie.description());
    }
    public Map<String, Object> values() {
        return MovieService.validate(title.getText(), duration.getText(), rating.getText(), description.getText());
    }
}
