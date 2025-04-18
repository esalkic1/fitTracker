package ba.unsa.etf.nwt.workout_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ExerciseTemplateDTO {
    private long id;

    @NotNull(message = "Exercise Details ID is required")
    @Positive(message = "Exercise Details ID must be a positive number")
    private Long exerciseDetailsId;

    @NotNull(message = "Workout Template ID is required")
    @Positive(message = "Workout Template ID must be a positive number")
    private Long workoutTemplateId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getExerciseDetailsId() {
        return exerciseDetailsId;
    }

    public void setExerciseDetailsId(Long exerciseDetailsId) {
        this.exerciseDetailsId = exerciseDetailsId;
    }

    public Long getWorkoutTemplateId() {
        return workoutTemplateId;
    }

    public void setWorkoutTemplateId(Long workoutTemplateId) {
        this.workoutTemplateId = workoutTemplateId;
    }
}
