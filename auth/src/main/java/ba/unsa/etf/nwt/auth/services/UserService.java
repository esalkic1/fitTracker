package ba.unsa.etf.nwt.auth.services;

import ba.unsa.etf.nwt.auth.domain.User;
import ba.unsa.etf.nwt.auth.dto.UserUpdateRequest;
import ba.unsa.etf.nwt.auth.exceptions.UserServiceException;
import ba.unsa.etf.nwt.auth.repositories.UserRepository;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;

	public UserService(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User get(final UUID handle) throws UserServiceException {
		return userRepository.findByHandle(handle);
	}

	public User findByUsername(final String username) throws UserServiceException {
		return userRepository.findByEmail(username)
				.orElseThrow(() ->
						new UserServiceException("No user found with username: " + username, ErrorType.ENTITY_NOT_FOUND)
				);
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));

		return org.springframework.security.core.userdetails.User
				.withUsername(username)
				.password(user.getPassword())
				.authorities(List.of(new SimpleGrantedAuthority(user.getRole().name())))
				.build();
	}

	public void delete(UUID handle) {
		User user = userRepository.findByHandle(handle);
		userRepository.deleteById(user.getId());
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User updateUser(final UUID handle, final UserUpdateRequest request) throws UserServiceException {
		User existingUser = userRepository.findByHandle(handle);

		if (request.getEmail() != null && !request.getEmail().isEmpty()) {
			existingUser.setEmail(request.getEmail());
		}
		if (request.getRole() != null) {
			existingUser.setRole(request.getRole());
		}

		return userRepository.save(existingUser); // Save the updated user
	}
}
