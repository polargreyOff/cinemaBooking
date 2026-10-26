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

        bookingValidationService.validateCreateBooking(
                session, seat, bookingRepository.existsBySessionIdAndSeatId(session.getId(), seat.getId()));

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

        return bookingMapper.toBookingResponse(booking, session, seat);
    }



    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {
        UserResponse user = getCurrentUser();

        return bookingRepository.findAllByUserIdOrderByBookingTimeDesc(user.getId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id) {
        UserResponse user = getCurrentUser();

        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException(id)
        );

        if (!booking.getUserId().equals(user.getId())) {
            throw new BookingAccessDeniedException();
        }

        return toResponse(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        UserResponse user = getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        if (!booking.getUserId().equals(user.getId())) throw new BookingAccessDeniedException();
        SessionResponse session = movieSessionService.getById(booking.getSessionId());
        bookingValidationService.validateDeleteBooking(user, booking, session);
        bookingRepository.deleteById(bookingId);
    }

    private BookingResponse toResponse(Booking booking) {
        SessionResponse session = movieSessionService.getById(booking.getSessionId());
        SeatResponse seat = seatService.getById(booking.getSeatId());
        return bookingMapper.toBookingResponse(booking, session, seat);
    }

    private UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResponse user = authService.getByLogin(authentication.getName());
        return user;
    }
}
