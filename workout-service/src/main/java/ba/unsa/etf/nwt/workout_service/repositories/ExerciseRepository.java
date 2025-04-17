package ba.unsa.etf.nwt.workout_service.repositories;

import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
