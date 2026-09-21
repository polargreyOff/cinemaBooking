package gradle.example.com.cinemabooking.mappers;

import gradle.example.com.cinemabooking.generated.model.CreateSeatRequest;
import gradle.example.com.cinemabooking.generated.model.SeatResponse;
import gradle.example.com.cinemabooking.models.Seat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {
    public SeatResponse toSeatResponse(Seat seat) {
        SeatResponse seatResponse = new SeatResponse();
        seatResponse.setId(seat.getId());
        seatResponse.setSeatNumber(seat.getSeatNumber());
        seatResponse.setHallId(seat.getHallId());
        seatResponse.setRowNumber(seat.getRowNumber());
        return seatResponse;
    }

    public Seat toSeat(SeatResponse seatResponse) {
        Seat seat = new Seat();
        seat.setSeatNumber(seatResponse.getSeatNumber());
        seat.setRowNumber(seatResponse.getRowNumber());
        seat.setHallId(seatResponse.getHallId());
        seat.setId(seatResponse.getId());
        return seat;
    }

    public Seat toSeat(CreateSeatRequest createSeatRequest) {
        Seat seat = new Seat();
        seat.setSeatNumber(createSeatRequest.getSeatNumber());
        seat.setRowNumber(createSeatRequest.getRowNumber());
        return seat;
    }
}
