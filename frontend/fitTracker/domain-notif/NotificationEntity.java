package ba.unsa.etf.nwt.notification_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

@Entity
@EntityListeners(UUIDGenerator.class)
public class NotificationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@AutoGenerateUUID
	private UUID handle;

	@Column(nullable = false)
	private Instant time;

	@Column(nullable = false)
	private String recipient;

	public NotificationEntity() {

	}

	public NotificationEntity(final Instant time, final String recipient) {
		this.time = time;
		this.recipient = recipient;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getHandle() {
		return handle;
	}

	public void setHandle(UUID handle) {
		this.handle = handle;
	}

	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
}
