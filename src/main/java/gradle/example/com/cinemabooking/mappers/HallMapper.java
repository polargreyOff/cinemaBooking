package gradle.example.com.cinemabooking.mappers;

import gradle.example.com.cinemabooking.generated.model.CreateHallRequest;
import gradle.example.com.cinemabooking.generated.model.HallResponse;
import gradle.example.com.cinemabooking.generated.model.UpdateHallRequest;
import gradle.example.com.cinemabooking.models.Hall;

public class HallMapper {
    public static HallResponse toHallResponse(Hall hall) {
        HallResponse response = new HallResponse();
        response.setId(hall.getId());
        response.setNumber(hall.getNumber());
        return response;
    }

    public static Hall toHall(CreateHallRequest request) {
        Hall hall = new Hall();
        hall.setNumber(request.getNumber());

        return hall;
    }

    public static Hall toHall(UpdateHallRequest request) {
        Hall hall = new Hall();
        hall.setNumber(request.getNumber());
        return hall;
    }
}
