package ba.unsa.etf.nwt.nutrition_service.repositories;

import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByNameContainingIgnoreCase(String name);
    List<Meal> findByUserIdAndDate(Long userId, Instant date);
    List<Meal> findByUserUuidAndDateBetween(UUID userUuid, Instant from, Instant to);
    List<Meal> findByUserIdAndDateBetween(Long userId, Instant from, Instant to);
}
