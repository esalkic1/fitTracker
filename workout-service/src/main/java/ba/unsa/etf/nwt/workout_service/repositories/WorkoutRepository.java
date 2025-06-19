package ba.unsa.etf.nwt.workout_service.repositories;

import ba.unsa.etf.nwt.workout_service.domain.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.date BETWEEN :from AND :to")
    List<Workout> findWorkoutsByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
    List<Workout> findWorkoutsByUserId(Long userId);
}
