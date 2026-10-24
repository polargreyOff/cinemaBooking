package ru.cinema.client.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ru.cinema.client.model.*;
import ru.cinema.client.service.SessionService;
import java.util.List;
import java.util.Map;

public final class SessionFormController {
    @FXML private ComboBox<Movie> movie;
    @FXML private ComboBox<Hall> hall;
    @FXML private TextField start, price;
    public void setData(List<Movie> movies, List<Hall> halls, Session session) {
        movie.setItems(FXCollections.observableArrayList(movies));
        hall.setItems(FXCollections.observableArrayList(halls));
        if (session != null) {
            movie.getSelectionModel().select(movies.stream().filter(m -> m.id() == session.movie().id()).findFirst().orElse(null));
            hall.getSelectionModel().select(halls.stream().filter(h -> h.id() == session.hall().id()).findFirst().orElse(null));
            start.setText(session.startTime().length() >= 16 ? session.startTime().substring(0, 16) : session.startTime());
            price.setText(Integer.toString(session.price()));
        }
    }
    public Map<String, Object> values() {
        return SessionService.validate(movie.getValue(), hall.getValue(), start.getText(), price.getText());
    }
}
