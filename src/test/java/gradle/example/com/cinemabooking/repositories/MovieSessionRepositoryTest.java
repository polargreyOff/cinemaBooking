package gradle.example.com.cinemabooking.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class MovieSessionRepositoryTest {
    @Autowired
    private MovieSessionRepository repository;

    @Test
    void supportsMissingOptionalFiltersAndSorting() {
        assertDoesNotThrow(() -> repository.findAll(null, null, 0, 20, null, null));
        assertDoesNotThrow(() -> repository.count(null, null));
    }

    @Test
    void supportsPaginationAndAllowedSortFields() {
        assertDoesNotThrow(() -> repository.findAll(null, null, 1, 10, "price", "desc"));
        assertDoesNotThrow(() -> repository.findAll(null, null, 0, 10, "movieTitle", "asc"));
    }
}
