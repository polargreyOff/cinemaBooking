package gradle.example.com.cinemabooking.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("booking")
public class Booking {

    @Id
    private Long id;
    @Column("user_id")
    private Long userId;
    @Column("session_id")
    private Long sessionId;
    @Column("seat_id")
    private Long seatId;
    @ReadOnlyProperty
    @Column("booking_time")
    private LocalDateTime bookingTime;

    public Booking() {
    }

    public Booking(Long id, Long userId, Long sessionId, Long seatId, LocalDateTime bookingTime) {
        this.id = id;
        this.userId = userId;
        this.sessionId = sessionId;
        this.seatId = seatId;
        this.bookingTime = bookingTime;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getSeatId() {
        return seatId;
    }
}
