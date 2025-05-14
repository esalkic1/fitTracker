package ba.unsa.etf.nwt.nutrition_service.seeders;

import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.repositories.FoodRepository;
import ba.unsa.etf.nwt.nutrition_service.repositories.MealRepository;
import ba.unsa.etf.nwt.nutrition_service.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;

    public DataSeeder(UserRepository userRepository,
                      MealRepository mealRepository,
                      FoodRepository foodRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // db already seeded
        }

        //foodRepository.deleteAll();
        //mealRepository.deleteAll();
        //userRepository.deleteAll();

        Food oatmeal = new Food("Oatmeal", 150.0, null);
        Food banana = new Food("Banana", 100.0, null);
        Food chicken = new Food("Chicken Breast", 250.0, null);
        Food rice = new Food("Rice", 200.0, null);
        Food salad = new Food("Salad", 80.0, null);

        foodRepository.saveAll(Arrays.asList(oatmeal, banana, chicken, rice, salad));

        User user1 = new User(null);
        User user2 = new User(null);

        userRepository.saveAll(Arrays.asList(user1, user2));

        Meal breakfast = new Meal("Breakfast", user1, null, Instant.now());
        Meal lunch = new Meal("Lunch", user1, null, Instant.now());
        Meal dinner = new Meal("Dinner", user2, null, Instant.now());

        mealRepository.saveAll(Arrays.asList(breakfast, lunch, dinner));

        oatmeal.setMeal(breakfast);
        banana.setMeal(breakfast);
        chicken.setMeal(lunch);
        rice.setMeal(lunch);
        salad.setMeal(dinner);

        mealRepository.saveAll(Arrays.asList(breakfast, lunch, dinner));

        breakfast.setFoods(Arrays.asList(oatmeal, banana));
        lunch.setFoods(Arrays.asList(chicken, rice));
        dinner.setFoods(List.of(salad));

        foodRepository.saveAll(Arrays.asList(oatmeal, banana, chicken, rice, salad));

        user1.setMeals(Arrays.asList(breakfast, lunch));
        user2.setMeals(List.of(dinner));

        userRepository.saveAll(Arrays.asList(user1, user2));
    }
}
