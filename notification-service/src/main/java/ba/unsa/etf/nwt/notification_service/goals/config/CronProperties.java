package ba.unsa.etf.nwt.notification_service.goals.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("notifier.cron")
public record CronProperties(String daily, String weekly, String monthly) {
}
