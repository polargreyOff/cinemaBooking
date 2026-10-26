package ru.cinema.client.ui;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

final class Dialogs {
    private Dialogs() {}

    static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(title);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    static void error(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, Objects.toString(message, "Неизвестная ошибка"), ButtonType.OK);
        alert.setHeaderText("Операция не выполнена");
        alert.showAndWait();
    }

    static Map<String, Object> form(String title, Node content, Callable<Map<String, Object>> values) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        final Map<String, Object>[] result = new Map[1];
        dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
            try {
                result[0] = values.call();
            } catch (Exception ex) {
                error(ex.getMessage());
                event.consume();
            }
        });
        dialog.setResultConverter(button -> button == ButtonType.OK ? result[0] : null);
        return dialog.showAndWait().orElse(null);
    }
}
