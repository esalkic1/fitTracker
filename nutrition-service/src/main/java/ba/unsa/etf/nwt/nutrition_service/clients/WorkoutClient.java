package ba.unsa.etf.nwt.nutrition_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "workout-service")
public interface WorkoutClient {
    @GetMapping("/api/v1/workout/ping")
    String ping();
}
