package ba.unsa.etf.nwt.notification_service.notifications.model;

import ba.unsa.etf.nwt.common.notifier.api.Notification;
import ba.unsa.etf.nwt.common.notifier.api.Recipient;

import java.util.UUID;

public abstract class NotificationWithType<T> extends Notification<T> {
	protected static final String USER_NAME_PLACEHOLDER = "${USER_NAME}";
	protected static final String GOAL_FREQUENCY_PLACEHOLDER = "${GOAL_FREQUENCY}";

	private final String parametrizedBody;

	protected NotificationWithType(
			final UUID id,
			final Recipient recipient,
			final String title,
			final String parametrizedBody,
			final T payload
	) {
		super(id, recipient, title, payload);

		this.parametrizedBody = parametrizedBody;
	}

	protected String getParametrizedBody() {
		return parametrizedBody;
	}
}
