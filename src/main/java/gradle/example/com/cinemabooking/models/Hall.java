package gradle.example.com.cinemabooking.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("hall")
public class Hall {
    @Id
    private Long id;
    private int number;

    public Hall(Long id, int number) {
        this.id = id;
        this.number = number;
    }

    public Hall() {

    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
