package ba.unsa.etf.nwt.workout_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ExerciseDTO {
    private Long id;

    @NotNull(message = "Weight is required")
    @Min(value = 0, message = "Weight must be a non-negative number")
    private Double weight;

    @NotNull(message = "Reps is required")
    @Positive(message = "Reps must be a positive integer")
    private Integer reps;

    @NotNull(message = "Sets is required")
    @Positive(message = "Sets must be a positive integer")
    private Integer sets;

    @NotNull(message = "Exercise details ID is required")
    @Positive(message = "Exercise details ID must be a positive number")
    private Long exerciseDetailsId;

    //@NotNull(message = "Workout ID is required")
    //@Positive(message = "Workout ID must be a positive number")
    private Long workoutId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Long getExerciseDetailsId() {
        return exerciseDetailsId;
    }

    public void setExerciseDetailsId(Long exerciseDetailsId) {
        this.exerciseDetailsId = exerciseDetailsId;
    }

    public Long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }
}
