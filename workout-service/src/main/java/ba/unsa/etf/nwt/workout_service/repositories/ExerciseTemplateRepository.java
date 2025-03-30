package ba.unsa.etf.nwt.workout_service.repositories;

import ba.unsa.etf.nwt.workout_service.domain.ExerciseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseTemplateRepository extends JpaRepository<ExerciseTemplate, Long> {
}
