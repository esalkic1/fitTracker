package ba.unsa.etf.nwt.notification_service.goals;

import ba.unsa.etf.nwt.notification_service.clients.NutritionServiceClient;
import ba.unsa.etf.nwt.notification_service.clients.WorkoutServiceClient;
import ba.unsa.etf.nwt.notification_service.domain.GoalEntity;
import ba.unsa.etf.nwt.notification_service.goals.models.AbstractGoal;
import ba.unsa.etf.nwt.notification_service.goals.models.NutritionGoal;
import ba.unsa.etf.nwt.notification_service.goals.models.WorkoutGoal;
import org.springframework.stereotype.Service;

@Service
public class GoalFactory {
	private final WorkoutServiceClient workoutServiceClient;
	private final NutritionServiceClient nutritionServiceClient;

	public GoalFactory(
			final WorkoutServiceClient workoutServiceClient,
			final NutritionServiceClient nutritionServiceClient
	) {
		this.workoutServiceClient = workoutServiceClient;
		this.nutritionServiceClient = nutritionServiceClient;
	}

	public AbstractGoal createGoal(final GoalEntity goalEntity) {
		switch (goalEntity.getType()) {
			case WORKOUT:
				return new WorkoutGoal(goalEntity, workoutServiceClient);
			case NUTRITION:
				return new NutritionGoal(goalEntity, nutritionServiceClient);
			default:
				throw new IllegalArgumentException("Unsupported goal type: " + goalEntity.getType());
		}
	}
}
