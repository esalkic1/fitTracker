package ba.unsa.etf.nwt.common.notifier.mock;

import ba.unsa.etf.nwt.common.notifier.api.Notification;
import ba.unsa.etf.nwt.common.notifier.api.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockNotifier implements Notifier {
	private static final Logger LOGGER = LoggerFactory.getLogger(MockNotifier.class);

	@Override
	public boolean notify(final Notification<?> notification) {
		LOGGER.info(
				"Pretending to send email. Title={}, Recipient={}, Body={}",
				notification.getTitle(),
				notification.getRecipient(),
				notification.getBody()
		);

		return true;
	}
}
