package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.MovieSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MovieSessionRepositoryTest {
    @Autowired
    private MovieSessionRepository repository;

    @Test
    void readsSessionTableColumns() {
        MovieSession session = repository.findAll().getFirst();
        assertNotNull(session.getId());
        assertNotNull(session.getMovieId());
        assertNotNull(session.getHallId());
        assertNotNull(session.getStartTime());
        assertNotNull(session.getPrice());
    }

    @Test
    void filtersByHallIdWithoutJoiningOtherTables() {
        MovieSession session = repository.findAll().getFirst();
        assertTrue(repository.findByHallId(session.getHallId()).stream()
                .anyMatch(item -> item.getId().equals(session.getId())));
        assertEquals(session.getMovieId(), repository.findById(session.getId()).orElseThrow().getMovieId());
    }
}
