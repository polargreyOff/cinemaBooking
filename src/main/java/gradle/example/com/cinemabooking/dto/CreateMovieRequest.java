package gradle.example.com.cinemabooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateMovieRequest {
    @NotBlank(message = "Movie title must not be empty")
    private String title;

    @NotBlank(message="Movie description must not be empty")
    private String description;

    @NotNull(message = "Movie duration is required")
    @Positive(message = "Movie duration must be greater than 0")
    private Integer duration;

    private String age_rating;

    public String getTitle() {
        return title;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getAge_rating() {
        return age_rating;
    }

    public String getDescription() {
        return description;
    }
}
