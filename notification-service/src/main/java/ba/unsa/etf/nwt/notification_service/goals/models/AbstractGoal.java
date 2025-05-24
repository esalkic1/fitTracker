package ba.unsa.etf.nwt.notification_service.goals.models;

import ba.unsa.etf.nwt.notification_service.domain.GoalEntity;
import ba.unsa.etf.nwt.notification_service.domain.GoalFrequency;
import ba.unsa.etf.nwt.notification_service.domain.GoalType;
import ba.unsa.etf.nwt.notification_service.domain.User;

import java.time.Instant;

public abstract class AbstractGoal {
	private final GoalEntity source;

	public AbstractGoal(final GoalEntity source) {
		this.source = source;
	}

	public long getSourceId() {
		return source.getId();
	}

	public GoalType getType() {
		return source.getType();
	}

	public GoalFrequency getFrequency() {
		return source.getFrequency();
	}

	public User getUser() {
		return source.getUser();
	}

	public boolean shouldCheck() {
		return source.getLastChecked() == null ||
				source.getLastChecked().isBefore(Instant.now().minus(1, getFrequency().toChronoUnit()));
	}

	protected GoalEntity getSource() {
		return source;
	}

	protected String getFromDate() {
		return Instant.now().minus(1, getFrequency().toChronoUnit()).toString();
	}

	public abstract boolean isCompleted();
}
