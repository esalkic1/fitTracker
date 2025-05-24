package ba.unsa.etf.nwt.common.notifier.mock;

import ba.unsa.etf.nwt.common.notifier.api.Notifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(value = "notifier.mode", havingValue = "mock", matchIfMissing = true)
public class MockNotifierConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public Notifier notifier() {
		return new MockNotifier();
	}
}
