package ba.unsa.etf.nwt.system_events.config;

import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfiguration {

	@Bean
	public Jersey3TransportClientFactories jersey3TransportClientFactories() {
		return new Jersey3TransportClientFactories();
	}
}
