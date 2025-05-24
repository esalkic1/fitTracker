package ba.unsa.etf.nwt.auth.repositories;

import ba.unsa.etf.nwt.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	User findByHandle(UUID handle);

	boolean existsByEmail(String email);
}
