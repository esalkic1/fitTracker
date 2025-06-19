package ba.unsa.etf.nwt.workout_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@EntityListeners(UUIDGenerator.class)
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @AutoGenerateUUID
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    private double weight;
    private int reps;
    private int sets;

    @OneToOne
    @JoinColumn(name = "exercise_details_id", nullable = false)
    private ExerciseDetails exerciseDetails;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    @JsonIgnore
    private Workout workout;

    public Exercise() {}

    public Exercise(double weight, int reps, int sets, Workout workout, ExerciseDetails exerciseDetails) {
        this.weight = weight;
        this.reps = reps;
        this.sets = sets;
        this.workout = workout;
        this.exerciseDetails = exerciseDetails;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public ExerciseDetails getExerciseDetails() {
        return exerciseDetails;
    }

    public void setExerciseDetails(ExerciseDetails exerciseDetails) {
        this.exerciseDetails = exerciseDetails;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
