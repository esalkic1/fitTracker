package ba.unsa.etf.nwt.notification_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(UUIDGenerator.class)
public class GoalEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@AutoGenerateUUID
	@Column(nullable = false, unique = true)
	private UUID handle;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private GoalType type;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private GoalFrequency frequency;

	@Column
	private Long target;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column
	private Instant lastChecked;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public GoalType getType() {
		return type;
	}

	public void setType(GoalType type) {
		this.type = type;
	}

	public GoalFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(GoalFrequency frequency) {
		this.frequency = frequency;
	}

	public long getTarget() {
		return target;
	}

	public void setTarget(Long target) {
		this.target = target;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UUID getHandle() {
		return handle;
	}

	public void setHandle(UUID handle) {
		this.handle = handle;
	}

	public Instant getLastChecked() {
		return lastChecked;
	}

	public void setLastChecked(Instant lastChecked) {
		this.lastChecked = lastChecked;
	}
}
