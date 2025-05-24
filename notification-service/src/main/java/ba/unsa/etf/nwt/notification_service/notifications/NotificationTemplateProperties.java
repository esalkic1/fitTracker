package ba.unsa.etf.nwt.notification_service.notifications;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties("notifier.email.templates")
public record NotificationTemplateProperties(
		Resource workoutGoalMissed,
		Resource nutritionGoalMissed
) {
}
