package ba.unsa.etf.nwt.auth.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "user_account")
@EntityListeners(UUIDGenerator.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@AutoGenerateUUID
	@Column(unique = true, nullable = false)
	private UUID handle;

	@NotNull(message = "User must have an email.")
	@Column(unique = true, nullable = false)
	private String email;

	@NotNull(message = "User must have a password.")
	@Column(nullable = false)
	@JsonIgnore
	private String password;

	@NotNull(message = "User must have a role.")
	@Column(nullable = false)
	private Role role;

	public User() {
	}

	public User(final String email, final String password) {
		this.email = email;
		this.password = password;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
