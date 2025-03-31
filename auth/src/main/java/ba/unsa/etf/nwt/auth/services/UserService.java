package ba.unsa.etf.nwt.auth.services;

import ba.unsa.etf.nwt.auth.domain.User;
import ba.unsa.etf.nwt.auth.exceptions.UserServiceException;
import ba.unsa.etf.nwt.auth.repositories.UserRepository;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;

	public UserService(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User get(final Long id) throws UserServiceException {
		return userRepository.findById(id)
				.orElseThrow(() ->
						new UserServiceException("Could not find user with id: " + id, ErrorType.ENTITY_NOT_FOUND)
				);
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final User user =  userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));

		return org.springframework.security.core.userdetails.User
				.withUsername(username)
				.password(user.getPassword())
				.authorities(List.of(new SimpleGrantedAuthority(user.getRole().name())))
				.build();
	}
}
