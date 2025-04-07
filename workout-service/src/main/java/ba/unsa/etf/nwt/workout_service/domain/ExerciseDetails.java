package ba.unsa.etf.nwt.workout_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@EntityListeners(UUIDGenerator.class)
public class ExerciseDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @AutoGenerateUUID
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    private String name;
    private String description;
    private String muscleGroup;
    private String equipment;
    private String difficultyLevel;

    public ExerciseDetails() {}

    public ExerciseDetails(String name, String description, String muscleGroup, String equipment, String difficultyLevel) {
        this.name = name;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
