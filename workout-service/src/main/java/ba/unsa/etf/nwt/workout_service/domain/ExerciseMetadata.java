package ba.unsa.etf.nwt.workout_service.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class ExerciseMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private String videoUrl;
    private String imageUrl;
    private String additionalNotes;

    public ExerciseMetadata() {
        this.uuid = UUID.randomUUID();
    }

    public ExerciseMetadata(Exercise exercise, String videoUrl, String imageUrl, String additionalNotes) {
        this.uuid = UUID.randomUUID();
        this.exercise = exercise;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
}

