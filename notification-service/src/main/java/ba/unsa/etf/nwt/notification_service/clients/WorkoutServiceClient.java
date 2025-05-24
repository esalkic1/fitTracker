package ba.unsa.etf.nwt.notification_service.clients;

import ba.unsa.etf.nwt.notification_service.models.Workout;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("workout-service")
public interface WorkoutServiceClient {

	@RequestMapping(method = RequestMethod.GET, value = "/api/v1/workout/by-user-and-date")
	List<Workout> getByUserIdAndDateRange(@RequestParam Long userId, @RequestParam String from, @RequestParam String to);

}
