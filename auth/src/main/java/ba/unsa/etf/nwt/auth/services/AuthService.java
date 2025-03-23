package ba.unsa.etf.nwt.auth.services;

import ba.unsa.etf.nwt.auth.domain.Role;
import ba.unsa.etf.nwt.auth.domain.User;
import ba.unsa.etf.nwt.auth.exceptions.JwtException;
import ba.unsa.etf.nwt.auth.exceptions.UserServiceException;
import ba.unsa.etf.nwt.auth.repositories.UserRepository;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
public class AuthService {
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	public AuthService(
			final UserRepository userRepository,
			final JwtService jwtService,
			final PasswordEncoder passwordEncoder,
			final AuthenticationManager authenticationManager
	) {
		this.userRepository = userRepository;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
	}


	public AuthResponse register(final User user) throws UserServiceException {
		try {
			if (userRepository.existsByEmail(user.getEmail())) {
				throw new UserServiceException(
						"User already exists with email: " + user.getEmail(), ErrorType.ALREADY_EXISTS
				);
			}

			user.setRole(Role.USER); // for now...
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			final User newUser = userRepository.save(user);

			final String generatedToken = jwtService.generateToken(user);

			return new AuthResponse(generatedToken, newUser);
		} catch (final JwtException e) {
			throw new UserServiceException("Failed generating token for user.", ErrorType.INTERNAL_ERROR);
		}
	}

	public AuthResponse login(final String email, final String password) throws UserServiceException {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			final User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new AuthenticationCredentialsNotFoundException(email));

			final String generatedToken = jwtService.generateToken(user);

			return new AuthResponse(generatedToken, user);
		} catch (final AuthenticationException e) {
			throw new UserServiceException(e.getMessage(), ErrorType.UNAUTHORIZED);
		} catch (final JwtException e) {
			throw new UserServiceException("Failed generating token for user.", ErrorType.INTERNAL_ERROR);
		}
	}

	public record AuthResponse(String token, User user) {}
}
