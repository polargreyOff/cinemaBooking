package gradle.example.com.cinemabooking.models;

public class Hall {
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
