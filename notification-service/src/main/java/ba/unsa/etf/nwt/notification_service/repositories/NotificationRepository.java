package ba.unsa.etf.nwt.notification_service.repositories;

import ba.unsa.etf.nwt.notification_service.domain.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
