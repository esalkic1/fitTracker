package ba.unsa.etf.nwt.notification_service.goals.models;

import ba.unsa.etf.nwt.notification_service.clients.NutritionServiceClient;
import ba.unsa.etf.nwt.notification_service.domain.GoalEntity;
import ba.unsa.etf.nwt.notification_service.models.Food;
import ba.unsa.etf.nwt.notification_service.models.Meal;

import java.time.Instant;
import java.util.List;

public class NutritionGoal extends AbstractGoal {
	private final NutritionServiceClient nutritionServiceClient;

	public NutritionGoal(final GoalEntity source, final NutritionServiceClient nutritionServiceClient) {
		super(source);

		this.nutritionServiceClient = nutritionServiceClient;
	}

	@Override
	public boolean isCompleted() {
		final List<Meal> meals = nutritionServiceClient.getMealsByUserAndDate(
				getUser().getId(),
				getFromDate(),
				Instant.now().toString()
		);

		return meals
				.stream()
				.flatMap(meal -> meal.getFoods().stream())
				.map(Food::getCalories)
				.reduce(0D, Double::sum) < getSource().getTarget();
	}
}
