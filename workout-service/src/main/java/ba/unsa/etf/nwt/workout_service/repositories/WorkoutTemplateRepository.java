package ba.unsa.etf.nwt.workout_service.repositories;

import ba.unsa.etf.nwt.workout_service.domain.WorkoutTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkoutTemplateRepository extends JpaRepository<WorkoutTemplate, Long> {
}
