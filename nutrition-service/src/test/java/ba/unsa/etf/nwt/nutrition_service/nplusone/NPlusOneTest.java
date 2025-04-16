package ba.unsa.etf.nwt.nutrition_service.nplusone;

import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.repositories.FoodRepository;
import ba.unsa.etf.nwt.nutrition_service.repositories.MealRepository;
import ba.unsa.etf.nwt.nutrition_service.repositories.UserRepository;
import ba.unsa.etf.nwt.nutrition_service.services.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NPlusOneTest {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatisticsService statisticsService;

    @BeforeEach
    public void setUp() {
        statisticsService.clear();
    }

    @Test
    @Transactional(readOnly = true)
    public void getAllFoodsNPlusOneTest() {
        List<Food> foods = foodRepository.findAll();

        for (Food food : foods) {
            if (food.getMeal() != null) {
                food.getMeal().getName();
            }
        }

        statisticsService.logStatistics();

        // We should have 1 query to get all foods + N queries for meals (N+1 problem)
        long queryCount = statisticsService.getQueryCount();
        System.out.println("Query count: " + queryCount);

        assertTrue(queryCount > 1, "Expected more than 1 query (N+1 problem), but got " + queryCount);
    }

    @Test
    @Transactional(readOnly = true)
    public void getAllMealsNPlusOneTest() {
        List<Meal> meals = mealRepository.findAll();

        for (Meal meal : meals) {
            if (meal.getUser() != null) {
                meal.getUser().getId();
            }
            meal.getFoods().size();
        }

        statisticsService.logStatistics();

        // We should have 1 query to get all meals + N queries for users + M queries for food collections
        long queryCount = statisticsService.getQueryCount();
        System.out.println("Query count: " + queryCount);

        assertTrue(queryCount > 1, "Expected more than 1 query (N+1 problem), but got " + queryCount);
    }

    @Test
    @Transactional(readOnly = true)
    public void getAllUsersNPlusOneTest() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            user.getMeals().size();
        }

        statisticsService.logStatistics();

        // We should have 1 query to get all users + N queries for meal collections
        long queryCount = statisticsService.getQueryCount();
        System.out.println("Query count: " + queryCount);

        assertTrue(queryCount > 1, "Expected more than 1 query (N+1 problem), but got " + queryCount);
    }

    @Test
    @Transactional(readOnly = true)
    public void searchFoodsByNameNPlusOneTest() {
        List<Food> foods = foodRepository.findByNameContainingIgnoreCase("a");

        System.out.println(foods);
        for (Food food : foods) {
            if (food.getMeal() != null) {
                food.getMeal().getName();
            }
        }

        statisticsService.logStatistics();

        long queryCount = statisticsService.getQueryCount();
        System.out.println("Query count for food search: " + queryCount);

        assertTrue(queryCount > 1, "Expected more than 1 query (N+1 problem), but got " + queryCount);
    }
}