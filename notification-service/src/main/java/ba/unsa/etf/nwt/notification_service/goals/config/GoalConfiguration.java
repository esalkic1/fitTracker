package ba.unsa.etf.nwt.notification_service.goals.config;

import ba.unsa.etf.nwt.common.notifier.api.Notifier;
import ba.unsa.etf.nwt.notification_service.goals.GoalExecutor;
import ba.unsa.etf.nwt.notification_service.goals.GoalFactory;
import ba.unsa.etf.nwt.notification_service.notifications.NotificationFactory;
import ba.unsa.etf.nwt.notification_service.repositories.GoalRepository;
import ba.unsa.etf.nwt.notification_service.repositories.NotificationRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CronProperties.class)
public class GoalConfiguration {

	@Bean(initMethod = "init", destroyMethod = "destroy")
	public GoalExecutor goalExecutor(
			final GoalRepository goalRepository,
			final NotificationRepository notificationRepository,
			final GoalFactory goalFactory,
			final NotificationFactory notificationFactory,
			final Notifier notifier,
			final CronProperties cronProperties
	) {
		return new GoalExecutor(
				goalRepository,
				notificationRepository,
				goalFactory,
				notificationFactory,
				notifier,
				cronProperties.daily(),
				cronProperties.weekly(),
				cronProperties.monthly()
		);
	}

}
