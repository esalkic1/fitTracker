package ba.unsa.etf.nwt.notification_service.ws;

import ba.unsa.etf.nwt.notification_service.clients.WorkoutServiceClient;
import ba.unsa.etf.nwt.notification_service.models.Workout;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/dummy")
public class DummyController {
	private final WorkoutServiceClient workoutServiceClient;

	public DummyController(final WorkoutServiceClient workoutServiceClient) {
		this.workoutServiceClient = workoutServiceClient;

	}

	@GetMapping("{id}")
	public Workout dummy(@PathVariable UUID id) {
		return workoutServiceClient.getById(id);
	}

}
