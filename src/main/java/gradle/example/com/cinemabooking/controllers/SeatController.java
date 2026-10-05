package gradle.example.com.cinemabooking.controllers;

import gradle.example.com.cinemabooking.generated.api.SeatsApi;
import gradle.example.com.cinemabooking.generated.model.CreateSeatRequest;
import gradle.example.com.cinemabooking.generated.model.GenerateSeatsRequest;
import gradle.example.com.cinemabooking.generated.model.SeatResponse;
import gradle.example.com.cinemabooking.generated.model.SessionSeatResponse;
import gradle.example.com.cinemabooking.models.Seat;
import gradle.example.com.cinemabooking.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeatController implements SeatsApi {
    private SeatService seatService;

    @Autowired
    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    public ResponseEntity<List<SeatResponse>> getSeatsByHall(Long hallId) {
        return ResponseEntity.ok(seatService.getByHallId(hallId));
    }

    public ResponseEntity<SeatResponse> getSeatById(Long id) {
        return ResponseEntity.ok(seatService.getById(id));
    }

    public ResponseEntity<SeatResponse> createSeat(Long hallId, CreateSeatRequest createSeatRequest) {
        return ResponseEntity.ok(seatService.save(hallId, createSeatRequest));
    }

    public ResponseEntity<Void> deleteSeat(Long seatId) {
        seatService.deleteById(seatId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<SeatResponse>> generateSeats(Long hallId, GenerateSeatsRequest request) {
        List<SeatResponse> response = seatService.generateSeats(hallId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
