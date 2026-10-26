package ru.cinema.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import ru.cinema.client.api.CinemaApi;
import ru.cinema.client.model.Session;
import ru.cinema.client.model.SessionSeat;

import java.time.LocalDateTime;

public final class SessionBookingController {
    @FXML private ComboBox<SessionSeat> bookingSeat;
    @FXML private Button bookButton;

    private ClientContext context;
    private Session selectedSession;
    private long seatsRequest;

    @FXML private void initialize() {
        bookingSeat.valueProperty().addListener((old, before, after) -> updateButton());
        updateButton();
    }

    void setContext(ClientContext context) {
        this.context = context;
        updateButton();
    }

    void showSession(Session session) {
        selectedSession = session;
        refreshAvailableSeats();
    }

    void clearAccount() {
        seatsRequest++;
        bookingSeat.getItems().clear();
        bookingSeat.setValue(null);
        updateButton();
    }

    void updateButton() {
        boolean authenticated = context != null && context.isAuthenticated();
        bookButton.setDisable(!authenticated || selectedSession == null || !isFuture(selectedSession)
                || bookingSeat.getValue() == null);
    }

    void refreshAvailableSeats() {
        Session session = selectedSession;
        long request = ++seatsRequest;
        bookingSeat.getItems().clear();
        bookingSeat.setValue(null);
        updateButton();
        if (session == null || !isFuture(session)) {
            bookingSeat.setPromptText(session == null ? "Выберите сеанс" : "Сеанс уже начался");
            return;
        }
        bookingSeat.setPromptText("Загрузка мест...");
        CinemaApi requestedApi = context.api();
        context.runAsync(() -> requestedApi.sessionSeats(session.id()), seats -> {
            // Пользователь мог выбрать другой сеанс, пока загружались места.
            if (context.api() != requestedApi || request != seatsRequest
                    || selectedSession == null || selectedSession.id() != session.id()) return;
            bookingSeat.getItems().setAll(seats.stream().filter(SessionSeat::available).toList());
            bookingSeat.setPromptText(bookingSeat.getItems().isEmpty() ? "Свободных мест нет" : "Свободное место");
            updateButton();
        });
    }

    @FXML private void createBooking() {
        Session session = selectedSession;
        SessionSeat seat = bookingSeat.getValue();
        if (!context.isAuthenticated() || session == null || !isFuture(session) || seat == null) return;
        if (!Dialogs.confirm("Подтверждение бронирования", "Забронировать " + seat + " на сеанс «" + session.movie().title() + "»?")) return;
        context.runAsync(() -> context.api().createBooking(session.id(), seat.id()), created -> {
            context.showStatus("Бронирование создано: №" + created.id());
            context.refreshBookings();
        });
    }

    private static boolean isFuture(Session session) {
        try { return LocalDateTime.parse(session.startTime()).isAfter(LocalDateTime.now()); }
        catch (RuntimeException ex) { return false; }
    }
}
