package ba.unsa.etf.nwt.gateway.routes;

import ba.unsa.etf.nwt.gateway.security.AuthFilterFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfig {

	@Bean
	@LoadBalanced
	public RouteLocator routeLocator(final RouteLocatorBuilder builder, final AuthFilterFactory authFilterFactory) {
		return builder.routes()
				.route("workout-service", route -> route
						.path("/api/v1/workout/**")
						.filters(f -> f.filter(authFilterFactory.apply()))
						.uri("lb://workout-service")
				)
				.build();
	}
}
