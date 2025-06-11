package ba.unsa.etf.nwt.workout_service.clients;

import ba.unsa.etf.nwt.workout_service.config.FeignClientHeaderInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.UUID;

@FeignClient(name = "nutrition-service", path = "/api/v1/meal", configuration = FeignClientHeaderInterceptor.class)
public interface NutritionClient {

    @GetMapping("/recent")
    boolean hasRecentMeal(
            @RequestParam("userUuid") UUID userId,
            @RequestParam("workoutTime") Instant workoutTime
    );
}

