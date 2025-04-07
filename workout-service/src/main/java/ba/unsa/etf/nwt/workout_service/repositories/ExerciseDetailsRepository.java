package ba.unsa.etf.nwt.workout_service.repositories;

import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseDetailsRepository extends JpaRepository<ExerciseDetails, Long> {
    List<ExerciseDetails> findByMuscleGroup(String muscleGroup);
    List<ExerciseDetails> findByDifficultyLevel(String difficultyLevel);
    List<ExerciseDetails> findByName(String name);
}
