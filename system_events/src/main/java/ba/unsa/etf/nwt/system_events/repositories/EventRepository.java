package ba.unsa.etf.nwt.system_events.repositories;

import ba.unsa.etf.nwt.system_events.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
