package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.BookingAccessDeniedException;
import gradle.example.com.cinemabooking.exceptions.BookingNotFoundException;
import gradle.example.com.cinemabooking.exceptions.SeatAlreadyBookedException;
import gradle.example.com.cinemabooking.generated.model.*;
import gradle.example.com.cinemabooking.mappers.BookingMapper;
import gradle.example.com.cinemabooking.models.Booking;
import gradle.example.com.cinemabooking.repositories.BookingRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final AuthService authService;
    private final BookingValidationService bookingValidationService;
    private final MovieSessionService movieSessionService;
    private final SeatService seatService;
    private final BookingMapper bookingMapper;

    public BookingService(
            BookingRepository bookingRepository,
            AuthService authService,
            BookingValidationService bookingValidationService,
            MovieSessionService movieSessionService,
            SeatService seatService,
            BookingMapper bookingMapper
    ) {
        this.bookingRepository = bookingRepository;
        this.authService = authService;
        this.bookingValidationService = bookingValidationService;
        this.movieSessionService = movieSessionService;
        this.seatService = seatService;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        UserResponse user = getCurrentUser();

        SessionResponse session = movieSessionService.getById(request.getSessionId());
        SeatResponse seat = seatService.getById(request.getSeatId());

        bookingValidationService.validateCreateBooking(session, seat);

        Long bookingId;
        try {
            bookingId = bookingRepository.save(
                    session.getId(),
                    seat.getId(),
                    user.getId()
            );
        } catch (DuplicateKeyException e) {
            throw new SeatAlreadyBookedException();
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        return bookingMapper.toBookingResponse(booking);
    }



    public List<BookingResponse> getMyBookings() {
        UserResponse user = getCurrentUser();

        return bookingRepository.findAllByUserId(user.getId())
            .stream()
            .map(bookingMapper::toBookingResponse)
            .toList();
    }

    public BookingResponse getBooking(Long id) {
        UserResponse user = getCurrentUser();

        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException(id)
        );

        if (!booking.getId().equals(user.getId())) {
            throw new BookingAccessDeniedException();
        }

        return bookingMapper.toBookingResponse(booking);
    }

    public void cancelBooking(Long bookingId) {
        UserResponse user = getCurrentUser();

        bookingValidationService.validateDeleteBooking(user, bookingId);

        bookingRepository.deleteById(bookingId);
    }

    private UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResponse user = authService.getByLogin(authentication.getName());
        return user;
    }
}
