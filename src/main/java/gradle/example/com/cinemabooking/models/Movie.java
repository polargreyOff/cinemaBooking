package gradle.example.com.cinemabooking.models;

public class Movie {

    private Long id;
    private String title;
    private int duration;
    private String age_rating;
    private String description;

    public Movie() {

    }

    public Movie(Long id, String title, int duration, String age_rating, String description) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.age_rating =  age_rating;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public String getAge_rating() {
        return age_rating;
    }
    public String getDescription() {
        return description;
    }

    public void setAge_rating(String age_rating) {
        this.age_rating = age_rating;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}