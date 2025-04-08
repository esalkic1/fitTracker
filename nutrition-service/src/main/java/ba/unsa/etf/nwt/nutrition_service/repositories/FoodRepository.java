package ba.unsa.etf.nwt.nutrition_service.repositories;

import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByCaloriesBetween(int minCalories, int maxCalories);
    List<Food> findByNameContainingIgnoreCase(String name);
}
