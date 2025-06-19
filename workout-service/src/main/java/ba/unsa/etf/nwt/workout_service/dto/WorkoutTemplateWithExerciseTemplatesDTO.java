package ba.unsa.etf.nwt.workout_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public class WorkoutTemplateWithExerciseTemplatesDTO {
    @NotNull(message = "Workout template data is required")
    @Valid
    private WorkoutTemplateWithUserUuidDTO workoutTemplate;

    @NotNull(message = "Exercise list is required")
    @Valid
    private List<ExerciseTemplateDTO> exerciseTemplates;

    public WorkoutTemplateWithUserUuidDTO getWorkoutTemplate() {
        return workoutTemplate;
    }

    public void setWorkoutTemplate(WorkoutTemplateWithUserUuidDTO workoutTemplate) {
        this.workoutTemplate = workoutTemplate;
    }

    public List<ExerciseTemplateDTO> getExerciseTemplates() {
        return exerciseTemplates;
    }

    public void setExerciseTemplates(List<ExerciseTemplateDTO> exerciseTemplates) {
        this.exerciseTemplates = exerciseTemplates;
    }
}
