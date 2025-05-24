package ba.unsa.etf.nwt.common.notifier.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@ConfigurationProperties("notifier.email")
public record EmailProperties(Sender sender, EmailAuth auth, Properties javaMail) {

	public record Sender(String address) {
	}

	public record EmailAuth(String username, String password) {
	}
}
