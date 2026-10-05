package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.MovieSession;

import java.time.LocalDate;
import java.util.List;

public interface MovieSessionRepositoryCustom {
    List<MovieSession> findAll(
            String movieTitle,
            LocalDate date,
            int page,
            int size,
            String sort,
            String direction
    );
}
