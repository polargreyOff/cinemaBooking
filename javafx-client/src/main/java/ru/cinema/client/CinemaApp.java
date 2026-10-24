package ru.cinema.client;

import javafx.application.Application;

/** Entry point for IDE and Maven launches. */
public final class CinemaApp {
    private CinemaApp() {}
    public static void main(String[] args) {
        Application.launch(CinemaFxApplication.class, args);
    }
}
