package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.UserData;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface UserRepository extends ListCrudRepository<UserData, Long> {
    Optional<UserData> findByLogin(String login);
}
