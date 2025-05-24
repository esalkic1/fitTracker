package ba.unsa.etf.nwt.notification_service.domain;

import java.time.temporal.ChronoUnit;

public enum GoalFrequency {
	DAILY,
	WEEKLY,
	MONTHLY;

	public ChronoUnit toChronoUnit() {
		return switch (this) {
			case DAILY -> ChronoUnit.DAYS;
			case WEEKLY -> ChronoUnit.WEEKS;
			case MONTHLY -> ChronoUnit.MONTHS;
		};
	}
}
