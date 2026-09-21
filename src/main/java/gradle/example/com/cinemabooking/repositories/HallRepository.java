package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.generated.model.HallResponse;
import gradle.example.com.cinemabooking.models.Hall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HallRepository {
    private JdbcClient jdbcClient;

    @Autowired
    public HallRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Hall> getAll() {
        return jdbcClient.sql("""
        SELECT * FROM hall
        """)
        .query(Hall.class)
        .list();
    }

    public Optional<Hall> getByid(Long id) {
        return jdbcClient.sql("""
        SELECT * FROM hall WHERE id = :id
        """)
        .param("id", id)
        .query(Hall.class)
        .optional();
    }

    public Hall save(Hall hall) {
        return jdbcClient.sql("""
        INSERT INTO hall(number) VALUES (:number)
        RETURNING id, number;
        """)
        .param("number", hall.getNumber())
        .query(Hall.class)
        .single();
    }

    public Optional<Hall> update(Hall hall) {
        return jdbcClient.sql("""
        UPDATE hall SET number = :number WHERE id = :id
        RETURNING id, number;
        """)
        .param("id", hall.getId())
        .param("number", hall.getNumber())
        .query(Hall.class)
        .optional();
    }

    public int deleteById(Long id) {
        return jdbcClient.sql("""
        DELETE FROM hall WHERE id = :id;
        """)
        .param("id", id)
        .update();
    }
}
