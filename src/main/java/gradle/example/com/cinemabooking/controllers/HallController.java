package gradle.example.com.cinemabooking.controllers;

import gradle.example.com.cinemabooking.generated.api.HallsApi;
import gradle.example.com.cinemabooking.generated.model.CreateHallRequest;
import gradle.example.com.cinemabooking.generated.model.HallResponse;
import gradle.example.com.cinemabooking.generated.model.UpdateHallRequest;
import gradle.example.com.cinemabooking.mappers.HallMapper;
import gradle.example.com.cinemabooking.services.HallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HallController implements HallsApi {

    private HallService hallService;

    @Autowired
    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    public ResponseEntity<List<HallResponse>> getHalls() {
        List<HallResponse> response = hallService.getAll()
        .stream()
        .map(HallMapper::toHallResponse)
        .toList();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<HallResponse> getHallById(Long id) {
        HallResponse response = HallMapper.toHallResponse(hallService.getHall(id)) ;
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<HallResponse> createHall(CreateHallRequest request) {
        HallResponse response = HallMapper.toHallResponse(hallService.save(request));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<HallResponse> updateHall(Long id, UpdateHallRequest request) {
        HallResponse response = HallMapper.toHallResponse(hallService.update(id, request));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Void> deleteHall(Long id) {
        hallService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
