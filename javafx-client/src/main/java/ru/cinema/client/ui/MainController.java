package ru.cinema.client.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.cinema.client.api.CinemaApi;
import ru.cinema.client.model.Hall;
import ru.cinema.client.model.Movie;
import ru.cinema.client.model.Session;
import ru.cinema.client.service.MovieStatistics;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public final class MainController implements ClientContext {
    @FXML private TextField apiUrl, login;
    @FXML private PasswordField password;
    @FXML private Label accountLabel, statusLabel, statsLabel;
    @FXML private MovieTabController movieTabController;
    @FXML private SessionTabController sessionTabController;
    @FXML private BookingTabController bookingTabController;

    private CinemaApi api;
    private String currentUrl;
    private boolean admin;
    private boolean authenticated;
    private long catalogRequest;

    @FXML private void initialize() {
        movieTabController.setContext(this);
        sessionTabController.setContext(this);
        bookingTabController.setContext(this);
        Platform.runLater(this::refreshCatalog);
    }

    @FXML private void signIn() {
        if (login.getText().isBlank() || password.getText().isBlank()) {
            showError("Укажите логин и пароль.");
            return;
        }
        try { connect(); }
        catch (RuntimeException ex) { showError(ex.getMessage()); return; }
        String username = login.getText().strip();
        String secret = password.getText();
        CinemaApi loginApi = api;
        runAsync(() -> loginApi.login(username, secret), role -> {
            if (loginApi != api) return;
            admin = "ADMIN".equals(role);
            authenticated = true;
            accountLabel.setText(username + " · " + (admin ? "администратор" : "пользователь"));
            password.clear();
            updateButtons();
            refreshCatalog();
            refreshBookings();
        });
    }

    private void connect() {
        String url = apiUrl.getText().strip();
        if (api != null && url.equals(currentUrl)) return;
        api = new CinemaApi(url);
        currentUrl = url;
        admin = false;
        authenticated = false;
        accountLabel.setText("Гость · только просмотр");
        bookingTabController.clearAccount();
        sessionTabController.clearAccount();
        updateButtons();
    }

    @Override public void refreshCatalog() {
        try { connect(); }
        catch (RuntimeException ex) { showError(ex.getMessage()); return; }
        showStatus("Загрузка данных...");
        CinemaApi requestedApi = api;
        long request = ++catalogRequest;
        runAsync(() -> new Catalog(requestedApi.movies(), requestedApi.halls(), requestedApi.sessions().content()), catalog -> {
            if (requestedApi != api || request != catalogRequest) return;
            movieTabController.showMovies(catalog.movies());
            sessionTabController.showCatalog(catalog.movies(), catalog.halls(), catalog.sessions());
            statsLabel.setText(MovieStatistics.describe(catalog.movies(), catalog.sessions()));
            showStatus("Загружено фильмов: " + catalog.movies().size() + ", сеансов: " + catalog.sessions().size());
        });
    }

    @Override public void refreshBookings() {
        bookingTabController.refreshBookings();
        sessionTabController.refreshAvailableSeats();
    }

    private void updateButtons() {
        movieTabController.updateButtons();
        sessionTabController.updateButtons();
        bookingTabController.updateButtons();
    }

    @Override public CinemaApi api() { return api; }
    @Override public boolean isAdmin() { return admin; }
    @Override public boolean isAuthenticated() { return authenticated; }
    @Override public void showStatus(String message) { statusLabel.setText(message); }
    @Override public void showError(String message) { Dialogs.error(message); }

    @Override public <T> void runAsync(Callable<T> job, Consumer<T> success) {
        Thread thread = new Thread(() -> {
            try {
                T result = job.call();
                Platform.runLater(() -> success.accept(result));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    showStatus("Ошибка: " + ex.getMessage());
                    showError(ex.getMessage());
                });
            }
        }, "cinema-api");
        thread.setDaemon(true);
        thread.start();
    }

    private record Catalog(List<Movie> movies, List<Hall> halls, List<Session> sessions) {}
}
