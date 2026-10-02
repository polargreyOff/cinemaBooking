package gradle.example.com.cinemabooking.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_data")
public class UserData {
    @Id
    private Long id;
    private String name;
    private String login;
    private String phone;
    @Column("password_hash")
    private String password_hash;

    public UserData() {}

    public UserData(Long id, String name, String login, String phone, String password_hash) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.phone = phone;
        this.password_hash = password_hash;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPassword_hash() {
        return password_hash;
    }
    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }
}
