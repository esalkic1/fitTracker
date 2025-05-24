package ba.unsa.etf.nwt.notification_service.ws;

import ba.unsa.etf.nwt.notification_service.domain.GoalEntity;
import ba.unsa.etf.nwt.notification_service.domain.GoalFrequency;
import ba.unsa.etf.nwt.notification_service.domain.GoalType;
import ba.unsa.etf.nwt.notification_service.domain.User;
import ba.unsa.etf.nwt.notification_service.exceptions.GoalException;
import ba.unsa.etf.nwt.notification_service.services.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/goal")
public class GoalController {
	private final GoalService goalService;

	public GoalController(final GoalService goalService) {
		this.goalService = goalService;
	}

	@GetMapping
	public ResponseEntity<?> findAll(@RequestParam(name = "user_handle") final UUID userHandle) {
		return ResponseEntity.ok(goalService.findAllByUser(userHandle));
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody final GoalCreateDto goal) {
		return ResponseEntity.ok(goalService.save(goal.toDomain()));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> delete(@PathVariable final Long id) throws GoalException {
		goalService.delete(id);
		return ResponseEntity.noContent().build();
	}

	public record GoalCreateDto(GoalType type, GoalFrequency frequency, Long target, UUID userHandle) {
		public GoalEntity toDomain() {
			final GoalEntity goalEntity = new GoalEntity();

			goalEntity.setType(type);
			goalEntity.setFrequency(frequency);
			goalEntity.setUser(new User(userHandle));
			goalEntity.setTarget(target);

			return goalEntity;
		}
	}
}
