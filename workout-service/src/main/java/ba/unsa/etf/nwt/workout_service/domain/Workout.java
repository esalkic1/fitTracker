package ba.unsa.etf.nwt.workout_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(UUIDGenerator.class)
public class Workout {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@AutoGenerateUUID
	@Column(unique = true, nullable = false, updatable = false)
	private UUID uuid;

	private Instant date;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@OneToMany(mappedBy = "workout", cascade = CascadeType.ALL)
	private List<Exercise> exercises;

	public Workout() {}

	public Workout(Instant date, User user, List<Exercise> exercises) {
		this.date = date;
		this.user = user;
		this.exercises = exercises;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Exercise> getExercises() {
		return exercises;
	}

	public void setExercises(List<Exercise> exercises) {
		this.exercises = exercises;
	}
}


