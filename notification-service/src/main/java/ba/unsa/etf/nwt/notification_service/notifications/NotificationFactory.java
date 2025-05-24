package ba.unsa.etf.nwt.notification_service.notifications;

import ba.unsa.etf.nwt.common.notifier.api.Recipient;
import ba.unsa.etf.nwt.notification_service.goals.models.AbstractGoal;
import ba.unsa.etf.nwt.notification_service.notifications.model.NotificationWithType;
import ba.unsa.etf.nwt.notification_service.notifications.model.NutritionGoalMissedNotification;
import ba.unsa.etf.nwt.notification_service.notifications.model.WorkoutGoalMissedNotification;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificationFactory {
	private final NotificationTemplateProperties properties;

	public NotificationFactory(final NotificationTemplateProperties properties) {
		this.properties = properties;
	}

	public NotificationWithType<?> getNotificationForGoal(final AbstractGoal goal) throws IOException {
		return switch (goal.getType()) {
			case WORKOUT -> getWorkoutGoalMissedNotification(goal);
			case NUTRITION -> getNutritionGoalMissedNotification(goal);
		};
	}

	private NotificationWithType<?> getWorkoutGoalMissedNotification(final AbstractGoal goal) throws IOException {
		return new WorkoutGoalMissedNotification(
				UUID.randomUUID(),
				Recipient.fromMail(goal.getUser().getEmail()),
				readBodyIntoString(properties.workoutGoalMissed()),
				new WorkoutGoalMissedNotification.WeeklyWorkoutGoalMissedNotificationPayload(
						goal.getUser().getEmail(),
						goal.getFrequency()
				)
		);
	}

	private NotificationWithType<?> getNutritionGoalMissedNotification(final AbstractGoal goal) throws IOException {
		return new NutritionGoalMissedNotification(
				UUID.randomUUID(),
				Recipient.fromMail(goal.getUser().getEmail()),
				readBodyIntoString(properties.nutritionGoalMissed()),
				new NutritionGoalMissedNotification.NutritionalGoalMissedNotificationPayload(
						goal.getUser().getEmail(),
						goal.getFrequency()
				)
		);
	}

	private static String readBodyIntoString(final Resource resource) throws IOException {
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
			return reader.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}
}
