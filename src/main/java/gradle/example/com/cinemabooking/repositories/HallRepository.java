package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.Hall;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface HallRepository extends ListCrudRepository<Hall, Long> {
    default List<Hall> getAll() {
        return findAll();
    }

    default Optional<Hall> getByid(Long id) {
        return findById(id);
    }

    default Optional<Hall> update(Hall hall) {
        if (!existsById(hall.getId())) {
            return Optional.empty();
        }
        return Optional.of(save(hall));
    }

    @Modifying
    @Query("DELETE FROM hall WHERE id = :id")
    int deleteExistingById(Long id);
}
