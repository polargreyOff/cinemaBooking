package gradle.example.com.cinemabooking.services;

import gradle.example.com.cinemabooking.exceptions.HallNotFoundException;
import gradle.example.com.cinemabooking.generated.model.UpdateHallRequest;
import gradle.example.com.cinemabooking.models.Hall;
import gradle.example.com.cinemabooking.repositories.HallRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HallServiceTest {
    private final HallRepository repository = mock(HallRepository.class);
    private final HallService service = new HallService(repository);

    @Test
    void updatesHallUsingRequestedId() {
        Hall hall = new Hall(42L, 1);
        when(repository.getByid(42L)).thenReturn(Optional.of(hall));
        when(repository.update(hall)).thenReturn(Optional.of(hall));
        UpdateHallRequest request = new UpdateHallRequest();
        request.setNumber(2);

        Hall result = service.update(42L, request);

        assertEquals(42L, result.getId());
        assertEquals(2, result.getNumber());
        verify(repository).update(hall);
    }

    @Test
    void preservesNumberWhenOmitted() {
        Hall hall = new Hall(42L, 3);
        when(repository.getByid(42L)).thenReturn(Optional.of(hall));
        when(repository.update(hall)).thenReturn(Optional.of(hall));

        Hall result = service.update(42L, new UpdateHallRequest());

        assertEquals(3, result.getNumber());
    }

    @Test
    void rejectsMissingHallBeforeUpdate() {
        when(repository.getByid(42L)).thenReturn(Optional.empty());

        assertThrows(HallNotFoundException.class,
                () -> service.update(42L, new UpdateHallRequest()));

        verify(repository, never()).update(any());
    }

    @Test
    void rejectsHallDeletedBetweenReadAndUpdate() {
        Hall hall = new Hall(42L, 3);
        when(repository.getByid(42L)).thenReturn(Optional.of(hall));
        when(repository.update(hall)).thenReturn(Optional.empty());

        assertThrows(HallNotFoundException.class,
                () -> service.update(42L, new UpdateHallRequest()));
    }
}
