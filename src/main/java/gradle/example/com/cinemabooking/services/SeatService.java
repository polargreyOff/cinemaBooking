package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.InvalidSeatException;
import gradle.example.com.cinemabooking.exceptions.SeatNotFoundException;
import gradle.example.com.cinemabooking.generated.model.CreateSeatRequest;
import gradle.example.com.cinemabooking.generated.model.SeatResponse;
import gradle.example.com.cinemabooking.mappers.SeatMapper;
import gradle.example.com.cinemabooking.models.Seat;
import gradle.example.com.cinemabooking.repositories.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {
    private SeatRepository seatRepository;
    private HallService hallService;
    private SeatMapper seatMapper;

    @Autowired
    public SeatService(SeatRepository seatRepository,  HallService hallService, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.hallService = hallService;
        this.seatMapper = seatMapper;
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
        int deletedRows = seatRepository.deleteById(seatId);
        if (deletedRows <= 0) {
            throw new SeatNotFoundException(seatId);
        }
    }
}
