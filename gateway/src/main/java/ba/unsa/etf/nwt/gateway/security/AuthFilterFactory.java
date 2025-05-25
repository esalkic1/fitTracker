package ba.unsa.etf.nwt.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthFilterFactory extends AbstractGatewayFilterFactory<AuthFilterFactory.NullConfig> {
	private final WebClient.Builder webClientBuilder;

	public AuthFilterFactory(final WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	@Override
	public GatewayFilter apply(final NullConfig config) {
		return new AuthServiceGatewayFilter(webClientBuilder);
	}

	public GatewayFilter apply() {
		return apply(new NullConfig());
	}

	public static class NullConfig {}
}
