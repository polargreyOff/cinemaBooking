package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.generated.model.BookingResponse;
import gradle.example.com.cinemabooking.generated.model.CreateBookingRequest;
import gradle.example.com.cinemabooking.models.UserData;
import gradle.example.com.cinemabooking.repositories.BookingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final AuthService authService;
    private

    public BookingService(
            BookingRepository bookingRepository,
            AuthService authService
    ) {
        this.bookingRepository = bookingRepository;
        this.authService = authService;
    }

    public BookingResponse createBooking(CreateBookingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserData user = authService.getByLogin(authentication.getName());
    }
}
