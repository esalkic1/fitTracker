package ba.unsa.etf.nwt.nutrition_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;


@FeignClient(name = "workout-service", path = "/api/v1/workout")
public interface WorkoutClient {

    @GetMapping("/ping")
    String ping();

    @GetMapping("/intensity-level")
    String getWorkoutIntensityLevel(
            @RequestParam("userId") Long userId,
            @RequestParam String date
    );
}
