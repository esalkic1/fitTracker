package ba.unsa.etf.nwt.workout_service.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private UUID uuid;

    private String name;
    private double weight;
    private int reps;
    private int sets;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @OneToOne
    @JoinColumn(name = "exercise_metadata_id", nullable = false)
    private ExerciseMetadata metadata;

    public Exercise() {
        this.uuid = UUID.randomUUID();
    }

    public Exercise(String name, double weight, int reps, int sets, Workout workout, ExerciseMetadata metadata) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.weight = weight;
        this.reps = reps;
        this.sets = sets;
        this.workout = workout;
        this.metadata = metadata;
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

    public ExerciseMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ExerciseMetadata metadata) {
        this.metadata = metadata;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


