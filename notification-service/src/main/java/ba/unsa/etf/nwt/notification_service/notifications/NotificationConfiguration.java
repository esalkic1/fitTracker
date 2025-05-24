package ba.unsa.etf.nwt.notification_service.notifications;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(NotificationTemplateProperties.class)
@Configuration
public class NotificationConfiguration {

	@Bean
	NotificationFactory notificationBodyFactory(final NotificationTemplateProperties properties) {
		return new NotificationFactory(properties);
	}
}
