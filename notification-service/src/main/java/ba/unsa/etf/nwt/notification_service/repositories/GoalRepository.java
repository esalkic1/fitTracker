package ba.unsa.etf.nwt.notification_service.repositories;

import ba.unsa.etf.nwt.notification_service.domain.GoalEntity;
import ba.unsa.etf.nwt.notification_service.domain.GoalFrequency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<GoalEntity, Long> {
	Page<GoalEntity> findAllByFrequencyEquals(final GoalFrequency frequency, final Pageable pageable);

	List<GoalEntity> findAllByUser_Handle(final UUID handle);

}
