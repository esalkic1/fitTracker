package ba.unsa.etf.nwt.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "user_account")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

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

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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
