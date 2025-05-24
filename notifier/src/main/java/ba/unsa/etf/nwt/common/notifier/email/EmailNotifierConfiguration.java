package ba.unsa.etf.nwt.common.notifier.email;

import ba.unsa.etf.nwt.common.notifier.api.Notifier;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(value = "notifier.mode", havingValue = "enabled")
@EnableConfigurationProperties(EmailProperties.class)
public class EmailNotifierConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Session session(final EmailProperties emailProperties) {
		return Session.getInstance(
				emailProperties.javaMail(),
				new UsernamePasswordEmailAuthenticator(
						emailProperties.auth().username(),
						emailProperties.auth().password()
				)
		);
	}

	@Bean
	@ConditionalOnMissingBean
	public Notifier notifier(final Session session, final EmailProperties emailProperties) {
		return new EmailNotifier(session, emailProperties.sender());
	}

	static class UsernamePasswordEmailAuthenticator extends Authenticator {
		private final String username;
		private final String password;

		public UsernamePasswordEmailAuthenticator(final String username, final String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}
}
