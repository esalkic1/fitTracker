package ba.unsa.etf.nwt.notification_service.notifications.model;

import ba.unsa.etf.nwt.common.notifier.api.Recipient;
import ba.unsa.etf.nwt.notification_service.domain.GoalFrequency;
import ba.unsa.etf.nwt.notification_service.utils.StringUtils;

import java.util.UUID;

public class NutritionGoalMissedNotification
		extends NotificationWithType<NutritionGoalMissedNotification.NutritionalGoalMissedNotificationPayload> {

	public NutritionGoalMissedNotification(
			final UUID id,
			final Recipient recipient,
			final String parametrizedBody,
			final NutritionalGoalMissedNotificationPayload payload
	) {
		super(id, recipient, "NWT | Nutrition Goal Missed", parametrizedBody, payload);
	}

	@Override
	public String getBody() {
		return getParametrizedBody()
				.replace(USER_NAME_PLACEHOLDER, getPayload().username())
				.replace(GOAL_FREQUENCY_PLACEHOLDER, StringUtils.capitalize(getPayload().goalFrequency().name()));
	}

	public record NutritionalGoalMissedNotificationPayload(String username, GoalFrequency goalFrequency) {
	}
}
