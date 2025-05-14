package ba.unsa.etf.nwt.nutrition_service.repositories;

import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByNameContainingIgnoreCase(String name);
    List<Meal> findByUserIdAndDate(Long userId, Instant date);
    boolean existsByUserIdAndDateBetween(Long userId, Instant from, Instant to);
}
