package ba.unsa.etf.nwt.common.notifier.email;

import ba.unsa.etf.nwt.common.notifier.api.Notification;
import ba.unsa.etf.nwt.common.notifier.api.Notifier;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotifier implements Notifier {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotifier.class);

	private final Session session;
	private final EmailProperties.Sender sender;

	public EmailNotifier(final Session session, final EmailProperties.Sender sender) {
		this.session = session;
		this.sender = sender;
	}

	@Override
	public boolean notify(final Notification<?> notification) {
		try {
			final Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(sender.address()));
			message.setRecipients(
					Message.RecipientType.TO,
					InternetAddress.parse(notification.getRecipient().email())
			);

			message.setSubject(notification.getTitle());
			message.setContent(notification.getBody(), "text/html");

			LOGGER.info("Sending notification. To={}", notification.getRecipient().email());
			Transport.send(message);

			return true;
		} catch (final MessagingException e) {
			LOGGER.error("Error sending email", e);
			return false;
		}
	}
}
