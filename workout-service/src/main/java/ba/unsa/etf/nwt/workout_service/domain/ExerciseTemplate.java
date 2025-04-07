package ba.unsa.etf.nwt.workout_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@EntityListeners(UUIDGenerator.class)
public class ExerciseTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @AutoGenerateUUID
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "exercise_details_id", nullable = false)
    private ExerciseDetails exerciseDetails;

    @ManyToOne
    @JoinColumn(name = "workout_template_id", nullable = false)
    @JsonIgnore
    private WorkoutTemplate workoutTemplate;

    public ExerciseTemplate() {}

    public ExerciseTemplate(ExerciseDetails exerciseDetails, WorkoutTemplate workoutTemplate) {
        this.exerciseDetails = exerciseDetails;
        this.workoutTemplate = workoutTemplate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ExerciseDetails getExerciseDetails() {
        return exerciseDetails;
    }

    public void setExerciseDetails(ExerciseDetails exerciseDetails) {
        this.exerciseDetails = exerciseDetails;
    }

    public WorkoutTemplate getWorkoutTemplate() {
        return workoutTemplate;
    }

    public void setWorkoutTemplate(WorkoutTemplate workoutTemplate) {
        this.workoutTemplate = workoutTemplate;
    }
}
