package ba.unsa.etf.nwt.notification_service.clients;

import ba.unsa.etf.nwt.notification_service.models.Workout;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient("workout-service")
public interface WorkoutServiceClient {

	@RequestMapping(method = RequestMethod.GET, value = "/api/v1/workout/{id}")
	Workout getById(@PathVariable UUID id);
}
