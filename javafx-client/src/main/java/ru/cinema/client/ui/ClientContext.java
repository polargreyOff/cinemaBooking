package ru.cinema.client.ui;

import ru.cinema.client.api.CinemaApi;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public interface ClientContext {
    CinemaApi api();
    boolean isAdmin();
    boolean isAuthenticated();
    void refreshCatalog();
    void refreshBookings();
    void showStatus(String message);
    void showError(String message);
    <T> void runAsync(Callable<T> job, Consumer<T> success);
}
