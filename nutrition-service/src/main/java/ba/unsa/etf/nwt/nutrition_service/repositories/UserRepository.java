package ba.unsa.etf.nwt.nutrition_service.repositories;

import ba.unsa.etf.nwt.nutrition_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
