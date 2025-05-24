package ba.unsa.etf.nwt.notification_service.repositories;

import ba.unsa.etf.nwt.notification_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByHandle(final UUID handle);

}
