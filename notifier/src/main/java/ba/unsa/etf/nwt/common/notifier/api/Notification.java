package ba.unsa.etf.nwt.common.notifier.api;

import java.util.UUID;

public abstract class Notification<T> {
	private final UUID id;
	private final Recipient recipient;
	private final String title;
	private final T payload;

	public Notification(final UUID id, final Recipient recipient, final String title, final T payload) {
		this.id = id;
		this.recipient = recipient;
		this.title = title;
		this.payload = payload;
	}

	public UUID getId() {
		return id;
	}

	public Recipient getRecipient() {
		return recipient;
	}

	public String getTitle() {
		return title;
	}

	protected T getPayload() {
		return payload;
	}

	public abstract String getBody();
}
