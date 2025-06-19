package ba.unsa.etf.nwt.workout_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@EntityListeners(UUIDGenerator.class)
public class ExerciseMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @AutoGenerateUUID
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private String additionalNotes;

    public ExerciseMetadata() {}

    public ExerciseMetadata(Exercise exercise, String videoUrl, String imageUrl, String additionalNotes) {
        this.exercise = exercise;
        this.additionalNotes = additionalNotes;
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

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
}
