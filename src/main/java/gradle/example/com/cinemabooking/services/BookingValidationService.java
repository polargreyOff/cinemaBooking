package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.generated.model.CreateBookingRequest;
import org.springframework.stereotype.Service;

@Service
public class BookingValidationService {
    private MovieSessionService movieSessionService;
    private SeatService seatService;

    public BookingValidationService(
            MovieSessionService movieSessionService,
            SeatService seatService
    ) {
        this.movieSessionService = movieSessionService;
        this.seatService = seatService;
    }

    public void validateCreate(CreateBookingRequest request) {
        movieSessionService.getById(request.getSessionId());
        seatService.getById(request.getSeatId());
    }
}
