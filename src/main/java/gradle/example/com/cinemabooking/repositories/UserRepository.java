package gradle.example.com.cinemabooking.repositories;

import gradle.example.com.cinemabooking.models.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    private JdbcClient jdbcClient;

    @Autowired
    public UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public UserData save(UserData user) {
        return jdbcClient.sql("""
            INSERT INTO user_data(name, login, password_hash, phone)
            VALUES (:name, :login, :password_hash, :phone)
            RETURNING id, name, login, password_hash, phone
        """)
        .param("name", user.getName())
        .param("login", user.getLogin())
        .param("phone", user.getPhone())
        .param("password_hash", user.getPassword_hash())
        .query(UserData.class)
        .single();
    }

    public Optional<UserData> findByLogin(String login) {
        return jdbcClient.sql("""
            SELECT * FROM user_data WHERE login = :login;
        """)
        .param("login", login)
        .query(UserData.class)
        .optional();
    }
}
