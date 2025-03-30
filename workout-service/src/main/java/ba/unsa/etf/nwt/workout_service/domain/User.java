package ba.unsa.etf.nwt.workout_service.domain;

import jakarta.persistence.*;

import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "user_account")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WorkoutTemplate> workoutTemplates;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Workout> workouts;

    public User() {
        this.uuid = UUID.randomUUID();
    }

    public User(List<WorkoutTemplate> workoutTemplates, List<Workout> workouts) {
        this.uuid = UUID.randomUUID();
        this.workoutTemplates = workoutTemplates;
        this.workouts = workouts;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<WorkoutTemplate> getWorkoutTemplates() {
        return workoutTemplates;
    }

    public void setWorkoutTemplates(List<WorkoutTemplate> workoutTemplates) {
        this.workoutTemplates = workoutTemplates;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }
}
