package ba.unsa.etf.nwt.system_events.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String timestamp;
	private String microserviceName;

	@Column(name = "triggered_by")
	private String user;

	private String action;
	private String resource;
	private String responseType;

	public Event() {
	}

	public Event(
			final String timestamp,
			final String microserviceName,
			final String user,
			final String action,
			final String resource,
			final String responseType
	) {
		this.timestamp = timestamp;
		this.microserviceName = microserviceName;
		this.user = user;
		this.action = action;
		this.resource = resource;
		this.responseType = responseType;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getMicroserviceName() {
		return microserviceName;
	}

	public void setMicroserviceName(String microserviceName) {
		this.microserviceName = microserviceName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
}
