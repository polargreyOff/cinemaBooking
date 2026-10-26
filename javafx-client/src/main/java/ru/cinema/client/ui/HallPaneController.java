package ru.cinema.client.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import ru.cinema.client.model.Hall;

import java.util.List;
import java.util.Optional;

public final class HallPaneController {
    @FXML private TableView<Hall> hallTable;
    @FXML private TableColumn<Hall, Integer> hallNumberColumn;
    @FXML private Button addHallButton, editHallButton, deleteHallButton;

    private final ObservableList<Hall> halls = FXCollections.observableArrayList();
    private ClientContext context;

    @FXML private void initialize() {
        hallNumberColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(row.getValue().number()));
        hallTable.setItems(halls);
        hallTable.getSelectionModel().selectedItemProperty().addListener((old, before, after) -> updateButtons());
        updateButtons();
    }

    void setContext(ClientContext context) {
        this.context = context;
        updateButtons();
    }

    void showHalls(List<Hall> loadedHalls) {
        halls.setAll(loadedHalls);
        updateButtons();
    }

    void updateButtons() {
        boolean admin = context != null && context.isAdmin();
        boolean selected = hallTable.getSelectionModel().getSelectedItem() != null;
        addHallButton.setDisable(!admin);
        editHallButton.setDisable(!admin || !selected);
        deleteHallButton.setDisable(!admin || !selected);
    }

    @FXML private void addHall() { openHallForm(null); }
    @FXML private void editHall() {
        Hall selected = hallTable.getSelectionModel().getSelectedItem();
        if (selected != null) openHallForm(selected);
    }

    private void openHallForm(Hall existing) {
        TextInputDialog dialog = new TextInputDialog(existing == null ? "" : Integer.toString(existing.number()));
        dialog.setTitle(existing == null ? "Новый зал" : "Изменить зал");
        dialog.setHeaderText("Номер зала");
        Optional<String> input = dialog.showAndWait();
        if (input.isEmpty()) return;
        int number;
        try { number = Integer.parseInt(input.get().strip()); }
        catch (NumberFormatException ex) { context.showError("Номер зала должен быть целым числом."); return; }
        if (number < 1) { context.showError("Номер зала должен быть больше нуля."); return; }
        context.runAsync(() -> context.api().saveHall(existing == null ? null : existing.id(), number),
                saved -> context.refreshCatalog());
    }

    @FXML private void deleteHall() {
        Hall selected = hallTable.getSelectionModel().getSelectedItem();
        if (selected == null || !Dialogs.confirm("Подтверждение удаления", "Удалить зал №" + selected.number() + "?")) return;
        context.runAsync(() -> { context.api().deleteHall(selected.id()); return null; }, ignored -> context.refreshCatalog());
    }
}
