package ba.unsa.etf.nwt.workout_service.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class ExerciseMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private UUID uuid;

    private String muscleGroup;
    private String equipment;
    private String difficultyLevel;

    public ExerciseMetadata() {
        this.uuid = UUID.randomUUID();
    }

    public ExerciseMetadata(String muscleGroup, String equipment, String difficultyLevel) {
        this.uuid = UUID.randomUUID();
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.difficultyLevel = difficultyLevel;
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

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}

