package ru.cinema.client.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import ru.cinema.client.model.Hall;
import ru.cinema.client.model.Movie;
import ru.cinema.client.model.Session;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SessionTabController {
    @FXML private TextField sessionMovieSearch;
    @FXML private ComboBox<HallOption> sessionHallFilter;
    @FXML private DatePicker sessionDateFrom, sessionDateTo;
    @FXML private ComboBox<String> sessionTimeFilter;
    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, String> sessionMovieColumn, sessionHallColumn, sessionTimeColumn, sessionEndColumn;
    @FXML private TableColumn<Session, Integer> sessionPriceColumn;
    @FXML private HallPaneController hallPaneController;
    @FXML private SessionBookingController sessionBookingController;
    @FXML private Button addSessionButton, editSessionButton, deleteSessionButton;
    @FXML private Label sessionCount;

    private final ObservableList<Movie> movies = FXCollections.observableArrayList();
    private final ObservableList<Hall> halls = FXCollections.observableArrayList();
    private final ObservableList<Session> sessions = FXCollections.observableArrayList();
    private FilteredList<Session> visibleSessions;
    private ClientContext context;

    @FXML private void initialize() {
        sessionMovieColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(row.getValue().movie().title()));
        sessionHallColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(row.getValue().hall().toString()));
        sessionTimeColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(formatTime(row.getValue().startTime())));
        sessionEndColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(formatTime(row.getValue().endTime())));
        sessionPriceColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(row.getValue().price()));

        visibleSessions = new FilteredList<>(sessions);
        SortedList<Session> sortedSessions = new SortedList<>(visibleSessions);
        sortedSessions.comparatorProperty().bind(sessionTable.comparatorProperty());
        sessionTable.setItems(sortedSessions);

        sessionTimeFilter.getItems().setAll("Любое время", "До 12:00", "12:00–17:59", "С 18:00");
        sessionTimeFilter.setValue("Любое время");
        sessionHallFilter.getItems().setAll(new HallOption(-1, "Все залы"));
        sessionHallFilter.setValue(sessionHallFilter.getItems().getFirst());
        sessionMovieSearch.textProperty().addListener((old, before, after) -> applyFilters());
        sessionHallFilter.valueProperty().addListener((old, before, after) -> applyFilters());
        sessionDateFrom.valueProperty().addListener((old, before, after) -> applyFilters());
        sessionDateTo.valueProperty().addListener((old, before, after) -> applyFilters());
        sessionTimeFilter.valueProperty().addListener((old, before, after) -> applyFilters());
        sessionTable.getSelectionModel().selectedItemProperty().addListener((old, before, after) -> {
            sessionBookingController.showSession(after);
            updateButtons();
        });
        updateButtons();
    }

    void setContext(ClientContext context) {
        this.context = context;
        hallPaneController.setContext(context);
        sessionBookingController.setContext(context);
        updateButtons();
    }

    void showCatalog(List<Movie> loadedMovies, List<Hall> loadedHalls, List<Session> loadedSessions) {
        movies.setAll(loadedMovies);
        halls.setAll(loadedHalls);
        sessions.setAll(loadedSessions);
        hallPaneController.showHalls(loadedHalls);

        long selectedHallId = sessionHallFilter.getValue() == null ? -1 : sessionHallFilter.getValue().id();
        sessionHallFilter.getItems().setAll(new HallOption(-1, "Все залы"));
        sessionHallFilter.getItems().addAll(halls.stream().map(hall -> new HallOption(hall.id(), hall.toString())).toList());
        sessionHallFilter.setValue(sessionHallFilter.getItems().stream()
                .filter(option -> option.id() == selectedHallId).findFirst().orElse(sessionHallFilter.getItems().getFirst()));
        applyFilters();
        updateButtons();
    }

    void clearAccount() {
        sessionBookingController.clearAccount();
        updateButtons();
    }

    void updateButtons() {
        boolean admin = context != null && context.isAdmin();
        Session selected = sessionTable.getSelectionModel().getSelectedItem();
        addSessionButton.setDisable(!admin);
        editSessionButton.setDisable(!admin || selected == null);
        deleteSessionButton.setDisable(!admin || selected == null);
        hallPaneController.updateButtons();
        sessionBookingController.updateButton();
    }

    private void applyFilters() {
        String title = sessionMovieSearch.getText().strip().toLowerCase(Locale.ROOT);
        HallOption hall = sessionHallFilter.getValue();
        LocalDate from = sessionDateFrom.getValue();
        LocalDate to = sessionDateTo.getValue();
        String time = sessionTimeFilter.getValue();
        visibleSessions.setPredicate(session -> session.movie().title().toLowerCase(Locale.ROOT).contains(title)
                && (hall == null || hall.id() == -1 || hall.id() == session.hall().id())
                && matchesTime(session, from, to, time));
        if (!visibleSessions.contains(sessionTable.getSelectionModel().getSelectedItem())) {
            sessionTable.getSelectionModel().clearSelection();
        }
        sessionCount.setText("Показано: " + visibleSessions.size() + " из " + sessions.size());
    }

    private static boolean matchesTime(Session session, LocalDate from, LocalDate to, String timeOfDay) {
        try {
            LocalDateTime start = LocalDateTime.parse(session.startTime());
            LocalDate date = start.toLocalDate();
            if ((from != null && date.isBefore(from)) || (to != null && date.isAfter(to))) return false;
            return switch (timeOfDay == null ? "Любое время" : timeOfDay) {
                case "До 12:00" -> start.getHour() < 12;
                case "12:00–17:59" -> start.getHour() >= 12 && start.getHour() < 18;
                case "С 18:00" -> start.getHour() >= 18;
                default -> true;
            };
        } catch (RuntimeException ex) {
            return false;
        }
    }

    @FXML private void refresh() { context.refreshCatalog(); }
    @FXML private void clearSessionMovieSearch() { sessionMovieSearch.clear(); }
    @FXML private void clearSessionTimeFilters() {
        sessionDateFrom.setValue(null);
        sessionDateTo.setValue(null);
        sessionTimeFilter.setValue("Любое время");
    }

    void refreshAvailableSeats() { sessionBookingController.refreshAvailableSeats(); }

    @FXML private void addSession() { openSessionForm(null); }
    @FXML private void editSession() {
        Session selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected != null) openSessionForm(selected);
    }

    private void openSessionForm(Session existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/cinema/client/session-form.fxml"));
            Node form = loader.load();
            SessionFormController controller = loader.getController();
            controller.setData(movies, halls, existing);
            Map<String, Object> values = Dialogs.form(existing == null ? "Новый сеанс" : "Изменить сеанс", form, controller::values);
            if (values != null) context.runAsync(
                    () -> context.api().saveSession(existing == null ? null : existing.id(), values),
                    saved -> context.refreshCatalog());
        } catch (Exception ex) {
            context.showError(ex.getMessage());
        }
    }

    @FXML private void deleteSession() {
        Session selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected == null || !Dialogs.confirm("Подтверждение удаления", "Удалить сеанс фильма «" + selected.movie().title() + "»?")) return;
        context.runAsync(() -> { context.api().deleteSession(selected.id()); return null; }, ignored -> context.refreshCatalog());
    }

    private static String formatTime(String value) {
        return value == null ? "" : value.replace('T', ' ');
    }

    private record HallOption(long id, String label) {
        @Override public String toString() { return label; }
    }
}
