package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.InvalidSeatException;
import gradle.example.com.cinemabooking.exceptions.SeatNotFoundException;
import gradle.example.com.cinemabooking.exceptions.SeatsAlreadyExistException;
import gradle.example.com.cinemabooking.generated.model.CreateSeatRequest;
import gradle.example.com.cinemabooking.generated.model.GenerateSeatsRequest;
import gradle.example.com.cinemabooking.generated.model.SeatResponse;
import gradle.example.com.cinemabooking.generated.model.SessionSeatResponse;
import gradle.example.com.cinemabooking.mappers.SeatMapper;
import gradle.example.com.cinemabooking.models.Seat;
import gradle.example.com.cinemabooking.repositories.MovieSessionRepository;
import gradle.example.com.cinemabooking.repositories.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {
    private SeatRepository seatRepository;
    private HallService hallService;
    private SeatMapper seatMapper;
    private MovieSessionRepository movieSessionRepository;

    @Autowired
    public SeatService(
            SeatRepository seatRepository,
            HallService hallService,
            SeatMapper seatMapper,
            MovieSessionRepository movieSessionRepository) {
        this.seatRepository = seatRepository;
        this.hallService = hallService;
        this.seatMapper = seatMapper;
        this.movieSessionRepository = movieSessionRepository;
    }

    public List<SeatResponse> getByHallId(Long hallId){
        hallService.getHall(hallId);

        return seatRepository.findByHallId(hallId)
                .stream()
                .map(seatMapper::toSeatResponse)
                .toList();
    }

    public SeatResponse getById(Long seatId){
        return seatMapper.toSeatResponse(seatRepository.findById(seatId).orElseThrow(() -> new SeatNotFoundException(seatId)));
    }

    public SeatResponse save(Long hallId, CreateSeatRequest request) {
        hallService.getHall(hallId);
        try {
            return seatMapper.toSeatResponse(seatRepository.save(hallId, seatMapper.toSeat(request)));
        } catch (DuplicateKeyException e) {
            throw new  InvalidSeatException( "Seat already exists: hallId=" + hallId +
                    ", row=" + request.getRowNumber() +
                    ", seat=" + request.getSeatNumber());
        }

    }

    public void deleteById(Long seatId){
        int deletedRows = seatRepository.deleteExistingById(seatId);
        if (deletedRows <= 0) {
            throw new SeatNotFoundException(seatId);
        }
    }

    public List<SessionSeatResponse> getSessionSeats(Long sessionId) {
        movieSessionRepository.findById(sessionId);

        return seatRepository.findSeatsBySessionId(sessionId)
                .stream()
                .map(seatMapper::toSessionSeatResponse)
                .toList();
    }

    @Transactional
    public List<SeatResponse> generateSeats(
            Long hallId,
            GenerateSeatsRequest request
    ) {
        hallService.getHall(hallId);

        try {
            seatRepository.generateSeats(
                    hallId,
                    request.getRows(),
                    request.getSeatsPerRow()
            );
        } catch (DuplicateKeyException ex) {
            throw new SeatsAlreadyExistException();
        }

        return getByHallId(hallId);
    }

}
