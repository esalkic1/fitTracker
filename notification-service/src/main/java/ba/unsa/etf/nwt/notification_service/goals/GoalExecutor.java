package ba.unsa.etf.nwt.notification_service.goals;

import ba.unsa.etf.nwt.common.notifier.api.Notification;
import ba.unsa.etf.nwt.common.notifier.api.Notifier;
import ba.unsa.etf.nwt.notification_service.domain.GoalEntity;
import ba.unsa.etf.nwt.notification_service.domain.GoalFrequency;
import ba.unsa.etf.nwt.notification_service.domain.NotificationEntity;
import ba.unsa.etf.nwt.notification_service.goals.models.AbstractGoal;
import ba.unsa.etf.nwt.notification_service.notifications.NotificationFactory;
import ba.unsa.etf.nwt.notification_service.repositories.GoalRepository;
import ba.unsa.etf.nwt.notification_service.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;

public class GoalExecutor {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoalExecutor.class);

	private static final int GOAL_FETCH_WINDOW_SIZE = 10;
	private static final Sort GOAL_SORT = Sort.by(new Sort.Order(Sort.Direction.ASC, "id"));

	private final GoalRepository goalRepository;
	private final NotificationRepository notificationRepository;
	private final GoalFactory goalFactory;
	private final NotificationFactory notificationFactory;
	private final Notifier notifier;

	private final String dailyCron;
	private final String weeklyCron;
	private final String monthlyCron;

	private final ThreadPoolTaskScheduler dailyGoalExecutor;
	private final ThreadPoolTaskScheduler weeklyGoalExecutor;
	private final ThreadPoolTaskScheduler monthlyGoalExecutor;

	public GoalExecutor(
			final GoalRepository goalRepository,
			final NotificationRepository notificationRepository,
			final GoalFactory goalFactory,
			final NotificationFactory notificationFactory,
			final Notifier notifier,
			final String dailyCron,
			final String weeklyCron,
			final String monthlyCron
	) {
		this.goalRepository = goalRepository;
		this.notificationRepository = notificationRepository;
		this.goalFactory = goalFactory;
		this.notificationFactory = notificationFactory;
		this.notifier = notifier;

		this.dailyCron = dailyCron;
		this.weeklyCron = weeklyCron;
		this.monthlyCron = monthlyCron;

		this.dailyGoalExecutor = new ThreadPoolTaskScheduler();
		this.weeklyGoalExecutor = new ThreadPoolTaskScheduler();
		this.monthlyGoalExecutor = new ThreadPoolTaskScheduler();
	}

	public void init() {
		initExecutor(dailyGoalExecutor, "daily-goal-executor");
		dailyGoalExecutor.schedule(
				() -> execute(GoalFrequency.DAILY),
				new CronTrigger(dailyCron, ZoneId.of("Europe/Sarajevo"))
		);

		initExecutor(weeklyGoalExecutor, "weekly-goal-executor");
		weeklyGoalExecutor.schedule(
				() -> execute(GoalFrequency.WEEKLY),
				new CronTrigger(weeklyCron, ZoneId.of("Europe/Sarajevo"))
		);

		initExecutor(monthlyGoalExecutor, "monthly-goal-executor");
		monthlyGoalExecutor.schedule(
				() -> execute(GoalFrequency.MONTHLY),
				new CronTrigger(monthlyCron, ZoneId.of("Europe/Sarajevo"))
		);
	}

	public void execute(final GoalFrequency frequency) {
		LOGGER.info("Executing scheduled check of goals with frequency {}", frequency);

		int page = 0;
		int notificationsSent = 0;
		int failedNotificationsSent = 0;

		Page<GoalEntity> goalsPage;

		do {
			goalsPage = goalRepository.findAllByFrequencyEquals(
					frequency,
					PageRequest.of(page, GOAL_FETCH_WINDOW_SIZE, GOAL_SORT)
			);

			for (final GoalEntity goalEntity : goalsPage) {
				final AbstractGoal goal = goalFactory.createGoal(goalEntity);

				if (goal.shouldCheck() && !goal.isCompleted()) {
					try {
						final Notification<?> notification = notificationFactory.getNotificationForGoal(goal);

						boolean successfullySent = notifier.notify(notification);

						if (successfullySent) {
							notificationRepository.save(new NotificationEntity(
									Instant.now(),
									notification.getRecipient().email()
							));
							notificationsSent++;
						} else {
							failedNotificationsSent++;
						}
					} catch (final IOException e) {
						LOGGER.error("Failed creating notification for goal {}", goal.getSourceId(), e);
					}

					// Mark goal as checked
					goalEntity.setLastChecked(Instant.now());
					goalRepository.save(goalEntity);
				}
			}

			page++;
		} while (!goalsPage.isLast());

		LOGGER.info(
				"Sent {} notifications. Failed send for {} notifications.",
				notificationsSent,
				failedNotificationsSent
		);
	}

	public void destroy() {
		this.dailyGoalExecutor.shutdown();
		this.weeklyGoalExecutor.shutdown();
		this.monthlyGoalExecutor.shutdown();
	}

	private static void initExecutor(
			final ThreadPoolTaskScheduler executor,
			final String name
	) {
		executor.setThreadNamePrefix(name);
		executor.setDaemon(true);
		executor.initialize();
	}
}
