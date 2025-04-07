package ba.unsa.etf.nwt.workout_service.repositories;

import ba.unsa.etf.nwt.workout_service.domain.ExerciseMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseMetadataRepository extends JpaRepository<ExerciseMetadata, Long> {
    Optional<ExerciseMetadata> findByExerciseId(Long exerciseId);
}
