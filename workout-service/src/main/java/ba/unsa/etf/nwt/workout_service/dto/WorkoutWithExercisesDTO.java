package ba.unsa.etf.nwt.workout_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class WorkoutWithExercisesDTO {
    @NotNull(message = "Workout data is required")
    @Valid
    private WorkoutDTO workout;

    @NotNull(message = "Exercise list is required")
    @Valid
    private List<ExerciseDTO> exercises;

    public WorkoutDTO getWorkout() {
        return workout;
    }

    public void setWorkout(WorkoutDTO workout) {
        this.workout = workout;
    }

    public List<ExerciseDTO> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseDTO> exercises) {
        this.exercises = exercises;
    }
}
