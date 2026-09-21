package gradle.example.com.cinemabooking.controllers;

import gradle.example.com.cinemabooking.generated.model.UpdateHallRequest;
import gradle.example.com.cinemabooking.models.Hall;
import gradle.example.com.cinemabooking.services.HallService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HallControllerTest {
    @Test
    void passesPathIdToService() {
        HallService service = mock(HallService.class);
        HallController controller = new HallController(service);
        UpdateHallRequest request = new UpdateHallRequest();
        request.setNumber(2);
        when(service.update(42L, request)).thenReturn(new Hall(42L, 2));

        var response = controller.updateHall(42L, request);

        verify(service).update(42L, request);
        assertNotNull(response.getBody());
        assertEquals(42L, response.getBody().getId());
        assertEquals(2, response.getBody().getNumber());
    }
}
