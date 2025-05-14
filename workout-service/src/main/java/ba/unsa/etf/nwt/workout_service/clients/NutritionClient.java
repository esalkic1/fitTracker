package ba.unsa.etf.nwt.workout_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;

@FeignClient(name = "nutrition-service", path = "/api/v1/meal")
public interface NutritionClient {

    @GetMapping("/recent")
    boolean hasRecentMeal(
            @RequestParam("userId") Long userId,
            @RequestParam("workoutTime") Instant workoutTime
    );
}

