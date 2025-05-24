package ba.unsa.etf.nwt.notification_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.notification_service.clients.AuthServiceClient;
import ba.unsa.etf.nwt.notification_service.domain.GoalEntity;
import ba.unsa.etf.nwt.notification_service.domain.User;
import ba.unsa.etf.nwt.notification_service.exceptions.GoalException;
import ba.unsa.etf.nwt.notification_service.repositories.GoalRepository;
import ba.unsa.etf.nwt.notification_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoalService {
	private final GoalRepository goalRepository;
	private final UserRepository userRepository;
	private final AuthServiceClient authServiceClient;

	public GoalService(
			final GoalRepository goalRepository,
			final UserRepository userRepository,
			final AuthServiceClient authServiceClient
	) {
		this.goalRepository = goalRepository;
		this.userRepository = userRepository;
		this.authServiceClient = authServiceClient;
	}

	public List<GoalEntity> findAllByUser(final UUID userHandle) {
		return goalRepository.findAllByUser_Handle(userHandle);
	}

	public GoalEntity save(final GoalEntity goalEntity) {
		final Optional<User> optionalExistingUser = userRepository.findByHandle(goalEntity.getUser().getHandle());

		if (optionalExistingUser.isEmpty()) {
			// This is the first goal for the user. Create entry for user.
			final User newUser = authServiceClient.getUser(goalEntity.getUser().getHandle());
			goalEntity.setUser(newUser);
		} else {
			goalEntity.setUser(optionalExistingUser.get());
		}

		return goalRepository.save(goalEntity);
	}

	public void delete(final Long id) throws GoalException {
		final GoalEntity goalEntity = goalRepository.findById(id)
				.orElseThrow(() ->
						new GoalException("Tried deleting non-existent goal with id" + id, ErrorType.ENTITY_NOT_FOUND)
				);

		goalRepository.delete(goalEntity);
	}
}
