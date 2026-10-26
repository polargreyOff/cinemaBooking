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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import ru.cinema.client.model.Movie;
import ru.cinema.client.service.MovieCsvExporter;
import ru.cinema.client.service.MovieService;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class MovieTabController {
    @FXML private TextField movieSearch;
    @FXML private ComboBox<String> ratingFilter, durationFilter;
    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> titleColumn, ratingColumn, descriptionColumn;
    @FXML private TableColumn<Movie, Integer> durationColumn;
    @FXML private Button addMovieButton, editMovieButton, deleteMovieButton;

    private final ObservableList<Movie> movies = FXCollections.observableArrayList();
    private FilteredList<Movie> visibleMovies;
    private ClientContext context;

    @FXML private void initialize() {
        titleColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(row.getValue().title()));
        durationColumn.setCellValueFactory(row -> new ReadOnlyObjectWrapper<>(row.getValue().duration()));
        ratingColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(Objects.toString(row.getValue().age_rating(), "")));
        descriptionColumn.setCellValueFactory(row -> new ReadOnlyStringWrapper(Objects.toString(row.getValue().description(), "")));

        visibleMovies = new FilteredList<>(movies);
        SortedList<Movie> sortedMovies = new SortedList<>(visibleMovies);
        sortedMovies.comparatorProperty().bind(movieTable.comparatorProperty());
        movieTable.setItems(sortedMovies);

        ratingFilter.getItems().setAll("Все рейтинги");
        ratingFilter.setValue("Все рейтинги");
        durationFilter.getItems().setAll("Любая длительность", "До 2 часов", "От 2 часов");
        durationFilter.setValue("Любая длительность");
        movieSearch.textProperty().addListener((old, before, after) -> applyFilters());
        ratingFilter.valueProperty().addListener((old, before, after) -> applyFilters());
        durationFilter.valueProperty().addListener((old, before, after) -> applyFilters());
        movieTable.getSelectionModel().selectedItemProperty().addListener((old, before, after) -> updateButtons());
        updateButtons();
    }

    void setContext(ClientContext context) {
        this.context = context;
        updateButtons();
    }

    void showMovies(List<Movie> loadedMovies) {
        movies.setAll(loadedMovies);
        String selectedRating = ratingFilter.getValue();
        List<String> ratings = movies.stream().map(Movie::age_rating)
                .filter(rating -> rating != null && !rating.isBlank()).distinct().sorted().toList();
        ratingFilter.getItems().setAll("Все рейтинги");
        ratingFilter.getItems().addAll(ratings);
        ratingFilter.setValue(ratings.contains(selectedRating) ? selectedRating : "Все рейтинги");
        applyFilters();
        updateButtons();
    }

    void updateButtons() {
        boolean admin = context != null && context.isAdmin();
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        addMovieButton.setDisable(!admin);
        editMovieButton.setDisable(!admin || selected == null);
        deleteMovieButton.setDisable(!admin || selected == null);
    }

    private void applyFilters() {
        String query = movieSearch.getText().strip().toLowerCase(Locale.ROOT);
        String rating = ratingFilter.getValue();
        String duration = durationFilter.getValue();
        visibleMovies.setPredicate(movie ->
                (movie.title().toLowerCase(Locale.ROOT).contains(query)
                        || Objects.toString(movie.description(), "").toLowerCase(Locale.ROOT).contains(query))
                        && (rating == null || rating.equals("Все рейтинги") || Objects.equals(movie.age_rating(), rating))
                        && (duration == null || duration.equals("Любая длительность")
                        || MovieService.category(movie).equals(duration)));
    }

    @FXML private void refresh() { context.refreshCatalog(); }
    @FXML private void addMovie() { openMovieForm(null); }
    @FXML private void editMovie() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected != null) openMovieForm(selected);
    }

    private void openMovieForm(Movie existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/cinema/client/movie-form.fxml"));
            Node form = loader.load();
            MovieFormController controller = loader.getController();
            controller.setMovie(existing);
            Map<String, Object> values = Dialogs.form(existing == null ? "Новый фильм" : "Изменить фильм", form, controller::values);
            if (values != null) context.runAsync(
                    () -> context.api().saveMovie(existing == null ? null : existing.id(), values),
                    saved -> context.refreshCatalog());
        } catch (Exception ex) {
            context.showError(ex.getMessage());
        }
    }

    @FXML private void deleteMovie() {
        Movie movie = movieTable.getSelectionModel().getSelectedItem();
        if (movie == null || !Dialogs.confirm("Подтверждение удаления", "Удалить фильм «" + movie.title() + "»?")) return;
        context.runAsync(() -> { context.api().deleteMovie(movie.id()); return null; },
                ignored -> context.refreshCatalog());
    }

    @FXML private void exportMovies() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Экспорт текущего списка фильмов");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        chooser.setInitialFileName("movies.csv");
        File target = chooser.showSaveDialog(movieTable.getScene().getWindow());
        if (target == null) return;
        try {
            MovieCsvExporter.write(target.toPath(), movieTable.getItems());
            context.showStatus("Экспортировано строк: " + movieTable.getItems().size());
        } catch (Exception ex) {
            context.showError("Не удалось сохранить CSV: " + ex.getMessage());
        }
    }
}
