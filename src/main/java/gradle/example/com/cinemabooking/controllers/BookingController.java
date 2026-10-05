package gradle.example.com.cinemabooking.controllers;

import gradle.example.com.cinemabooking.generated.api.BookingsApi;
import gradle.example.com.cinemabooking.generated.model.BookingResponse;
import gradle.example.com.cinemabooking.generated.model.CreateBookingRequest;
import gradle.example.com.cinemabooking.services.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookingController implements BookingsApi {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public ResponseEntity<BookingResponse> createBooking(CreateBookingRequest request) {
        BookingResponse bookingResponse = bookingService.createBooking(request);

        return ResponseEntity.ok(bookingResponse);
    }

    @Override
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        List<BookingResponse> response =  bookingService.getMyBookings();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> cancelBooking(Long id) {
        bookingService.cancelBooking(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BookingResponse> getBookingById(Long id) {
        BookingResponse response = bookingService.getBooking(id);

        return ResponseEntity.ok(response);
    }
}