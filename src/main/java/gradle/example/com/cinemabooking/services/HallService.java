package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.HallNotFoundException;
import gradle.example.com.cinemabooking.exceptions.InvalidHallException;
import gradle.example.com.cinemabooking.generated.model.CreateHallRequest;
import gradle.example.com.cinemabooking.generated.model.UpdateHallRequest;
import gradle.example.com.cinemabooking.mappers.HallMapper;
import gradle.example.com.cinemabooking.models.Hall;
import gradle.example.com.cinemabooking.repositories.HallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HallService {
    private HallRepository hallRepository;

    @Autowired
    public HallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    public List<Hall> getAll() {
        return hallRepository.getAll();
    }

    public Hall getHall(Long id) {
        return hallRepository.getByid(id).orElseThrow(() -> new HallNotFoundException(id));
    }

    public Hall update(Long id, UpdateHallRequest request) {
        Hall hall = getHall(id);
        if (request.getNumber() != null) {
            hall.setNumber(request.getNumber());
        }
        try {
            return hallRepository.update(hall).orElseThrow(() -> new HallNotFoundException(id));
        } catch(DuplicateKeyException e) {
            throw new InvalidHallException("Hall with this number already exists");
        }
    }

    public Hall save(CreateHallRequest request) {
        Hall hall = HallMapper.toHall(request);
        try {
            return hallRepository.save(hall);
        } catch (DuplicateKeyException e) {
            throw new InvalidHallException("Hall with this number already exists");
        }

    }

    public void delete(Long id) {
        int deletedRows = hallRepository.deleteById(id);
        if (deletedRows <= 0) {
            throw new HallNotFoundException(id);
        }
    }
}
