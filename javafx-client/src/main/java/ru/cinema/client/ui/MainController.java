package ru.cinema.client.ui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ru.cinema.client.api.CinemaApi;
import ru.cinema.client.model.*;
import ru.cinema.client.service.MovieService;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class MainController {
    @FXML private TextField apiUrl, login, movieSearch;
    @FXML private PasswordField password;
    @FXML private Label accountLabel, statusLabel, statsLabel, sessionCount;
    @FXML private ComboBox<String> ratingFilter, durationFilter;
    @FXML private ComboBox<Movie> sessionMovieFilter;
    @FXML private ComboBox<Hall> sessionHallFilter;
    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> titleColumn, ratingColumn, descriptionColumn;
    @FXML private TableColumn<Movie, Integer> durationColumn;
    @FXML private TableView<Session> sessionTable;
    @FXML private TableView<Hall> hallTable;
    @FXML private TableColumn<Hall, Integer> hallNumberColumn;
    @FXML private TableColumn<Session, String> sessionMovieColumn, sessionHallColumn, sessionTimeColumn, sessionEndColumn;
    @FXML private TableColumn<Session, Integer> sessionPriceColumn;
    @FXML private Button addMovieButton, editMovieButton, deleteMovieButton;
    @FXML private Button addSessionButton, editSessionButton, deleteSessionButton;
    @FXML private Button addHallButton, editHallButton, deleteHallButton;
    @FXML private ComboBox<Session> bookingSession;
    @FXML private ComboBox<SessionSeat> bookingSeat;
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String> bookingMovieColumn, bookingHallColumn, bookingSessionTimeColumn, bookingSeatColumn, bookingTimeColumn;
    @FXML private TableColumn<Booking, Integer> bookingPriceColumn;
    @FXML private Button bookButton, refreshBookingsButton, cancelBookingButton;
    @FXML private Label bookingCount;

    private final ObservableList<Movie> movies = FXCollections.observableArrayList();
    private final ObservableList<Hall> halls = FXCollections.observableArrayList();
    private final ObservableList<Session> sessions = FXCollections.observableArrayList();
    private final ObservableList<Booking> bookings = FXCollections.observableArrayList();
    private FilteredList<Movie> visibleMovies;
    private FilteredList<Session> visibleSessions;
    private CinemaApi api;
    private String currentUrl;
    private boolean admin;
    private boolean authenticated;

    @FXML private void initialize() {
        titleColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().title()));
        durationColumn.setCellValueFactory(x -> new ReadOnlyObjectWrapper<>(x.getValue().duration()));
        ratingColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(Objects.toString(x.getValue().age_rating(), "")));
        descriptionColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(Objects.toString(x.getValue().description(), "")));
        hallNumberColumn.setCellValueFactory(x -> new ReadOnlyObjectWrapper<>(x.getValue().number()));
        hallTable.setItems(halls);
        sessionMovieColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().movie().title()));
        sessionHallColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().hall().toString()));
        sessionTimeColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(formatTime(x.getValue().startTime())));
        sessionEndColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(formatTime(x.getValue().endTime())));
        sessionPriceColumn.setCellValueFactory(x -> new ReadOnlyObjectWrapper<>(x.getValue().price()));

        bookingMovieColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().session().movie().title()));
        bookingHallColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().session().hall().toString()));
        bookingSessionTimeColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(formatTime(x.getValue().session().startTime())));
        bookingSeatColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().seat().toString()));
        bookingPriceColumn.setCellValueFactory(x -> new ReadOnlyObjectWrapper<>(x.getValue().session().price()));
        bookingTimeColumn.setCellValueFactory(x -> new ReadOnlyStringWrapper(formatTime(x.getValue().bookingTime())));
        bookingTable.setItems(bookings);

        visibleMovies = new FilteredList<>(movies);
        SortedList<Movie> sortedMovies = new SortedList<>(visibleMovies);
        sortedMovies.comparatorProperty().bind(movieTable.comparatorProperty());
        movieTable.setItems(sortedMovies);
        visibleSessions = new FilteredList<>(sessions);
        SortedList<Session> sortedSessions = new SortedList<>(visibleSessions);
        sortedSessions.comparatorProperty().bind(sessionTable.comparatorProperty());
        sessionTable.setItems(sortedSessions);
        ratingFilter.getItems().setAll("Все рейтинги"); ratingFilter.setValue("Все рейтинги");
        durationFilter.getItems().setAll("Любая длительность", "До 2 часов", "От 2 часов");
        durationFilter.setValue("Любая длительность");
        movieSearch.textProperty().addListener((a,b,c) -> filterMovies());
        ratingFilter.valueProperty().addListener((a,b,c) -> filterMovies());
        durationFilter.valueProperty().addListener((a,b,c) -> filterMovies());
        sessionMovieFilter.valueProperty().addListener((a,b,c) -> filterSessions());
        sessionHallFilter.valueProperty().addListener((a,b,c) -> filterSessions());
        movieTable.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> updateButtons());
        sessionTable.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> updateButtons());
        hallTable.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> updateButtons());
        bookingSession.valueProperty().addListener((a,b,c) -> loadAvailableSeats());
        bookingSeat.valueProperty().addListener((a,b,c) -> updateButtons());
        bookingTable.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> updateButtons());
        updateButtons();
        Platform.runLater(this::refresh);
    }

    private static String formatTime(String value) { return value == null ? "" : value.replace('T', ' '); }
    private void filterMovies() {
        String query = movieSearch.getText().strip().toLowerCase(Locale.ROOT);
        String rating = ratingFilter.getValue();
        String duration = durationFilter.getValue();
        visibleMovies.setPredicate(m -> (m.title().toLowerCase(Locale.ROOT).contains(query)
                || Objects.toString(m.description(), "").toLowerCase(Locale.ROOT).contains(query))
                && (rating == null || rating.equals("Все рейтинги") || Objects.equals(m.age_rating(), rating))
                && (duration == null || duration.equals("Любая длительность") || MovieService.category(m).equals(duration)));
    }
    private void filterSessions() {
        Movie selectedMovie = sessionMovieFilter.getValue();
        Hall selectedHall = sessionHallFilter.getValue();
        visibleSessions.setPredicate(s -> (selectedMovie == null || selectedMovie.id() == s.movie().id())
                && (selectedHall == null || selectedHall.id() == s.hall().id()));
        sessionCount.setText("Показано: " + visibleSessions.size() + " из " + sessions.size());
    }
    private void updateButtons() {
        addMovieButton.setDisable(!admin); addSessionButton.setDisable(!admin); addHallButton.setDisable(!admin);
        editHallButton.setDisable(!admin || hallTable.getSelectionModel().getSelectedItem() == null);
        deleteHallButton.setDisable(!admin || hallTable.getSelectionModel().getSelectedItem() == null);
        editMovieButton.setDisable(!admin || movieTable.getSelectionModel().getSelectedItem() == null);
        deleteMovieButton.setDisable(!admin || movieTable.getSelectionModel().getSelectedItem() == null);
        editSessionButton.setDisable(!admin || sessionTable.getSelectionModel().getSelectedItem() == null);
        deleteSessionButton.setDisable(!admin || sessionTable.getSelectionModel().getSelectedItem() == null);
        bookButton.setDisable(!authenticated || bookingSession.getValue() == null || bookingSeat.getValue() == null);
        refreshBookingsButton.setDisable(!authenticated);
        Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        cancelBookingButton.setDisable(!authenticated || selectedBooking == null || !isBookable(selectedBooking.session()));
    }
    @FXML private void signIn() {
        if (login.getText().isBlank() || password.getText().isBlank()) { error("Укажите логин и пароль."); return; }
        try { ensureApi(); } catch (RuntimeException ex) { error(ex.getMessage()); return; }
        String user = login.getText().strip(), secret = password.getText();
        runAsync(() -> api.login(user, secret), role -> {
            admin = "ADMIN".equals(role); authenticated = true;
            accountLabel.setText(user + " · " + (admin ? "администратор" : "пользователь"));
            password.clear(); updateButtons(); refresh(); refreshBookings();
        });
    }
    @FXML private void refresh() {
        try { ensureApi(); } catch (RuntimeException ex) { error(ex.getMessage()); return; }
        statusLabel.setText("Загрузка данных...");
        runAsync(() -> new Snapshot(api.movies(), api.halls(), api.sessions().content()), data -> {
            movies.setAll(data.movies()); halls.setAll(data.halls()); sessions.setAll(data.sessions());
            String selectedRating = ratingFilter.getValue();
            List<String> ratings = movies.stream().map(Movie::age_rating).filter(s -> s != null && !s.isBlank())
                    .distinct().sorted().collect(Collectors.toList());
            ratingFilter.getItems().setAll("Все рейтинги"); ratingFilter.getItems().addAll(ratings);
            ratingFilter.setValue(ratings.contains(selectedRating) ? selectedRating : "Все рейтинги");
            long movieId = sessionMovieFilter.getValue() == null ? -1 : sessionMovieFilter.getValue().id();
            long hallId = sessionHallFilter.getValue() == null ? -1 : sessionHallFilter.getValue().id();
            sessionMovieFilter.getItems().setAll(movies);
            sessionMovieFilter.setValue(movies.stream().filter(m -> m.id() == movieId).findFirst().orElse(null));
            sessionHallFilter.getItems().setAll(halls);
            sessionHallFilter.setValue(halls.stream().filter(h -> h.id() == hallId).findFirst().orElse(null));
            long bookingSessionId = bookingSession.getValue() == null ? -1 : bookingSession.getValue().id();
            bookingSession.getItems().setAll(sessions.stream().filter(MainController::isBookable).toList());
            bookingSession.setValue(bookingSession.getItems().stream()
                    .filter(s -> s.id() == bookingSessionId).findFirst().orElse(null));
            filterMovies(); filterSessions(); updateStats(); updateButtons();
            statusLabel.setText("Загружено фильмов: " + movies.size() + ", сеансов: " + sessions.size());
        });
    }
    private void ensureApi() {
        String url = apiUrl.getText().strip();
        if (api == null || !url.equals(currentUrl)) {
            api = new CinemaApi(url); currentUrl = url; admin = false; authenticated = false;
            bookings.clear(); bookingSeat.getItems().clear(); bookingSession.setValue(null);
            bookingCount.setText("Войдите, чтобы увидеть свои бронирования");
            accountLabel.setText("Гость · только просмотр"); updateButtons();
        }
    }
    private static boolean isBookable(Session session) {
        try { return LocalDateTime.parse(session.startTime()).isAfter(LocalDateTime.now()); }
        catch (RuntimeException ex) { return false; }
    }
    private void loadAvailableSeats() {
        Session selected = bookingSession.getValue();
        bookingSeat.getItems().clear(); bookingSeat.setValue(null); updateButtons();
        if (selected == null) return;
        bookingSeat.setPromptText("Загрузка мест...");
        runAsync(() -> api.sessionSeats(selected.id()), seats -> {
            if (bookingSession.getValue() == null || bookingSession.getValue().id() != selected.id()) return;
            bookingSeat.getItems().setAll(seats.stream().filter(SessionSeat::available).toList());
            bookingSeat.setPromptText(bookingSeat.getItems().isEmpty() ? "Свободных мест нет" : "Свободное место");
            updateButtons();
        });
    }
    @FXML private void refreshBookings() {
        if (!authenticated) return;
        runAsync(api::myBookings, data -> {
            bookings.setAll(data);
            bookingCount.setText("Моих бронирований: " + bookings.size());
            updateButtons();
        });
    }
    @FXML private void createBooking() {
        Session selected = bookingSession.getValue(); SessionSeat seat = bookingSeat.getValue();
        if (!authenticated || selected == null || seat == null) return;
        if (!confirm("Подтверждение бронирования", "Забронировать " + seat + " на сеанс «" + selected.movie().title() + "»?")) return;
        runAsync(() -> api.createBooking(selected.id(), seat.id()), created -> {
            statusLabel.setText("Бронирование создано: №" + created.id());
            refreshBookings(); loadAvailableSeats();
        });
    }
    @FXML private void cancelBooking() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null || !confirm("Отмена бронирования", "Отменить бронирование №" + selected.id() + "?")) return;
        runAsync(() -> { api.cancelBooking(selected.id()); return null; }, ignored -> {
            statusLabel.setText("Бронирование №" + selected.id() + " отменено");
            refreshBookings(); loadAvailableSeats();
        });
    }
    private void updateStats() {
        if (movies.isEmpty()) { statsLabel.setText("Фильмов пока нет."); return; }
        IntSummaryStatistics durations = movies.stream().mapToInt(Movie::duration).summaryStatistics();
        long ageRestricted = movies.stream().filter(m -> !Objects.toString(m.age_rating(), "").isBlank()).count();
        statsLabel.setText("Всего фильмов: " + durations.getCount() + "\n"
                + "Суммарная длительность: " + durations.getSum() + " мин\n"
                + "Средняя длительность: " + String.format(Locale.forLanguageTag("ru"), "%.1f", durations.getAverage()) + " мин\n"
                + "Самый короткий фильм: " + durations.getMin() + " мин\n"
                + "Самый длинный фильм: " + durations.getMax() + " мин\n"
                + "Фильмов с указанным возрастным рейтингом: " + ageRestricted + "\n"
                + "Сеансов в списке: " + sessions.size());
    }

    @FXML private void addMovie() { movieDialog(null); }
    @FXML private void editMovie() { Movie m = movieTable.getSelectionModel().getSelectedItem(); if (m != null) movieDialog(m); }
    private void movieDialog(Movie existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/cinema/client/movie-form.fxml"));
            Node form = loader.load(); MovieFormController controller = loader.getController(); controller.setMovie(existing);
            Map<String, Object> values = formDialog(existing == null ? "Новый фильм" : "Изменить фильм", form, controller::values);
            if (values != null) runAsync(() -> api.saveMovie(existing == null ? null : existing.id(), values), saved -> refresh());
        } catch (Exception ex) { error(ex.getMessage()); }
    }
    @FXML private void deleteMovie() {
        Movie m = movieTable.getSelectionModel().getSelectedItem();
        if (m == null || !confirm("Удалить фильм «" + m.title() + "»?")) return;
        runAsync(() -> { api.deleteMovie(m.id()); return null; }, ignored -> refresh());
    }
    @FXML private void addHall() { hallDialog(null); }
    @FXML private void editHall() { Hall h = hallTable.getSelectionModel().getSelectedItem(); if (h != null) hallDialog(h); }
    private void hallDialog(Hall existing) {
        TextInputDialog dialog = new TextInputDialog(existing == null ? "" : Integer.toString(existing.number()));
        dialog.setTitle(existing == null ? "Новый зал" : "Изменить зал");
        dialog.setHeaderText("Номер зала");
        Optional<String> input = dialog.showAndWait();
        if (input.isEmpty()) return;
        int number;
        try { number = Integer.parseInt(input.get().strip()); }
        catch (NumberFormatException ex) { error("Номер зала должен быть целым числом."); return; }
        if (number < 1) { error("Номер зала должен быть больше нуля."); return; }
        int value = number;
        runAsync(() -> api.saveHall(existing == null ? null : existing.id(), value), saved -> refresh());
    }
    @FXML private void deleteHall() {
        Hall h = hallTable.getSelectionModel().getSelectedItem();
        if (h == null || !confirm("Удалить зал №" + h.number() + "?")) return;
        runAsync(() -> { api.deleteHall(h.id()); return null; }, ignored -> refresh());
    }
    @FXML private void addSession() { sessionDialog(null); }
    @FXML private void editSession() { Session s = sessionTable.getSelectionModel().getSelectedItem(); if (s != null) sessionDialog(s); }
    private void sessionDialog(Session existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/cinema/client/session-form.fxml"));
            Node form = loader.load(); SessionFormController controller = loader.getController();
            controller.setData(movies, halls, existing);
            Map<String, Object> values = formDialog(existing == null ? "Новый сеанс" : "Изменить сеанс", form, controller::values);
            if (values != null) runAsync(() -> api.saveSession(existing == null ? null : existing.id(), values), saved -> refresh());
        } catch (Exception ex) { error(ex.getMessage()); }
    }
    @FXML private void deleteSession() {
        Session s = sessionTable.getSelectionModel().getSelectedItem();
        if (s == null || !confirm("Удалить сеанс фильма «" + s.movie().title() + "»?")) return;
        runAsync(() -> { api.deleteSession(s.id()); return null; }, ignored -> refresh());
    }
    private Map<String, Object> formDialog(String title, Node form, Callable<Map<String, Object>> validator) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle(title); dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        final Map<String, Object>[] result = new Map[1];
        Node ok = dialog.getDialogPane().lookupButton(ButtonType.OK);
        ok.addEventFilter(ActionEvent.ACTION, event -> {
            try { result[0] = validator.call(); }
            catch (Exception ex) { error(ex.getMessage()); event.consume(); }
        });
        dialog.setResultConverter(button -> button == ButtonType.OK ? result[0] : null);
        return dialog.showAndWait().orElse(null);
    }
    private boolean confirm(String message) { return confirm("Подтверждение удаления", message); }
    private boolean confirm(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(header);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
    @FXML private void exportMovies() {
        FileChooser chooser = new FileChooser(); chooser.setTitle("Экспорт текущего списка фильмов");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        chooser.setInitialFileName("movies.csv");
        File target = chooser.showSaveDialog(movieTable.getScene().getWindow());
        if (target == null) return;
        StringBuilder csv = new StringBuilder("\uFEFFНазвание;Длительность (мин);Возрастной рейтинг;Описание\r\n");
        for (Movie m : movieTable.getItems()) {
            csv.append(cell(m.title())).append(';').append(m.duration()).append(';')
                    .append(cell(m.age_rating())).append(';').append(cell(m.description())).append("\r\n");
        }
        try { Files.writeString(target.toPath(), csv, StandardCharsets.UTF_8); statusLabel.setText("Экспортировано строк: " + movieTable.getItems().size()); }
        catch (Exception ex) { error("Не удалось сохранить CSV: " + ex.getMessage()); }
    }
    private static String cell(String value) { return "\"" + Objects.toString(value, "").replace("\"", "\"\"") + "\""; }
    private static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, Objects.toString(message, "Неизвестная ошибка"), ButtonType.OK);
        alert.setHeaderText("Операция не выполнена"); alert.showAndWait();
    }
    private <T> void runAsync(Callable<T> job, Consumer<T> success) {
        Thread thread = new Thread(() -> {
            try { T result = job.call(); Platform.runLater(() -> success.accept(result)); }
            catch (Exception ex) { Platform.runLater(() -> { statusLabel.setText("Ошибка: " + ex.getMessage()); error(ex.getMessage()); }); }
        }, "cinema-api");
        thread.setDaemon(true); thread.start();
    }
    private record Snapshot(List<Movie> movies, List<Hall> halls, List<Session> sessions) {}
}


