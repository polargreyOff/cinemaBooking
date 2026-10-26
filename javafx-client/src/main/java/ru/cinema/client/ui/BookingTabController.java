package ru.cinema.client.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ru.cinema.client.model.Booking;

import java.time.LocalDateTime;

public final class BookingTabController {
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String> bookingMovieColumn, bookingHallColumn;
    @FXML private TableColumn<Booking, String> bookingSessionTimeColumn, bookingSeatColumn, bookingTimeColumn;
    @FXML private TableColumn<Booking, Integer> bookingPriceColumn;
    @FXML private Button refreshBookingsButton, cancelBookingButton;
    @FXML private Label bookingCount;

    private final ObservableList<Booking> bookings = FXCollections.observableArrayList();
    private ClientContext context;
    private long bookingsRequest;

    @FXML private void initialize() {
        bookingMovieColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(row.getValue().session().movie().title()));
        bookingHallColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(row.getValue().session().hall().toString()));
        bookingSessionTimeColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(formatTime(row.getValue().session().startTime())));
        bookingSeatColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(row.getValue().seat().toString()));
        bookingPriceColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(row.getValue().session().price()));
        bookingTimeColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(formatTime(row.getValue().bookingTime())));
        bookingTable.setItems(bookings);
        bookingTable.getSelectionModel().selectedItemProperty().addListener((old, before, after) -> updateButtons());
        updateButtons();
    }

    void setContext(ClientContext context) {
        this.context = context;
        updateButtons();
    }

    void clearAccount() {
        bookingsRequest++;
        bookings.clear();
        bookingCount.setText("Войдите, чтобы увидеть свои бронирования");
        updateButtons();
    }

    void updateButtons() {
        boolean authenticated = context != null && context.isAuthenticated();
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        refreshBookingsButton.setDisable(!authenticated);
        cancelBookingButton.setDisable(!authenticated || selected == null || !isFuture(selected.session().startTime()));
    }

    @FXML void refreshBookings() {
        if (!context.isAuthenticated()) return;
        var requestedApi = context.api();
        long request = ++bookingsRequest;
        context.runAsync(requestedApi::myBookings, loaded -> {
            if (requestedApi != context.api() || request != bookingsRequest) return;
            bookings.setAll(loaded);
            bookingCount.setText("Моих бронирований: " + bookings.size());
            updateButtons();
        });
    }

    @FXML private void cancelBooking() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null || !Dialogs.confirm("Отмена бронирования", "Отменить бронирование №" + selected.id() + "?")) return;
        context.runAsync(() -> { context.api().cancelBooking(selected.id()); return null; }, ignored -> {
            context.showStatus("Бронирование №" + selected.id() + " отменено");
            context.refreshBookings();
        });
    }

    private static boolean isFuture(String startTime) {
        try { return LocalDateTime.parse(startTime).isAfter(LocalDateTime.now()); }
        catch (RuntimeException ex) { return false; }
    }

    private static String formatTime(String value) {
        return value == null ? "" : value.replace('T', ' ');
    }
}
