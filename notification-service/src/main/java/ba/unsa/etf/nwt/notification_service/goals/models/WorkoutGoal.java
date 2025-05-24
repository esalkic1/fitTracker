package ba.unsa.etf.nwt.notification_service.goals.models;

import ba.unsa.etf.nwt.notification_service.clients.WorkoutServiceClient;
import ba.unsa.etf.nwt.notification_service.domain.GoalEntity;
import ba.unsa.etf.nwt.notification_service.domain.GoalFrequency;
import ba.unsa.etf.nwt.notification_service.models.Workout;

import java.time.Instant;
import java.util.List;

public class WorkoutGoal extends AbstractGoal {
	private final WorkoutServiceClient workoutServiceClient;

	public WorkoutGoal(final GoalEntity source, final WorkoutServiceClient workoutServiceClient) {
		super(source);

		this.workoutServiceClient = workoutServiceClient;
	}

	@Override
	public boolean isCompleted() {
		final List<Workout> workouts = workoutServiceClient.getByUserIdAndDateRange(
				getUser().getId(),
				getFromDate(),
				Instant.now().toString()
		);

		return workouts.size() >= getSource().getTarget();
	}
}
