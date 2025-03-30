package ba.unsa.etf.nwt.workout_service.repositories;

import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseDetailsRepository extends JpaRepository<ExerciseDetails, Long> {
}
