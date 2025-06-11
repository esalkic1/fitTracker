package ba.unsa.etf.nwt.workout_service.dto;

import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.UUID;

public class WorkoutDTO {
    private Long id;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Workout date cannot be in the future")
    private Instant date;

    @NotNull(message = "User handle is required")
    private UUID userHandle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public UUID getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(UUID userHandle) {
        this.userHandle = userHandle;
    }
}
