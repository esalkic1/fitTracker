package ba.unsa.etf.nwt.workout_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(UUIDGenerator.class)
public class WorkoutTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AutoGenerateUUID
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "workoutTemplate", cascade = CascadeType.ALL)
    private List<ExerciseTemplate> exerciseTemplates;

    public WorkoutTemplate() {}

    public WorkoutTemplate(String name, String description, User user, List<ExerciseTemplate> exerciseTemplates) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.exerciseTemplates = exerciseTemplates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ExerciseTemplate> getExerciseTemplates() {
        return exerciseTemplates;
    }

    public void setExerciseTemplates(List<ExerciseTemplate> exerciseTemplates) {
        this.exerciseTemplates = exerciseTemplates;
    }
}
