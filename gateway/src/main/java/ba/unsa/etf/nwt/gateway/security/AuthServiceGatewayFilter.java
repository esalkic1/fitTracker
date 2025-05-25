package ba.unsa.etf.nwt.gateway.security;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AuthServiceGatewayFilter implements GatewayFilter {
	private static final Set<String> ALLOWED_PATHS = Set.of(
			"/api/v1/auth/login",
			"/api/v1/auth/register"
	);

	private final WebClient.Builder webClientBuilder;

	public AuthServiceGatewayFilter(final WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	@Override
	public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
		final ServerHttpRequest request = exchange.getRequest();

		if (ALLOWED_PATHS.contains(request.getURI().getPath())) {
			return chain.filter(exchange);
		}

		final String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		return webClientBuilder.build()
				.get()
				.uri("lb://auth/api/v1/auth/validate")
				.header(HttpHeaders.AUTHORIZATION, token)
				.retrieve()
				.bodyToMono(ValidationResponse.class)
				.map(response -> {
					final ServerHttpRequest withAuthHeaders =
							exchange.getRequest().mutate()
									.header("X-Username", response.username)
									.header("X-Role", response.role)
									.header("X-Handle", response.handle().toString())
									.build();

					return exchange.mutate().request(withAuthHeaders).build();
				})
				.flatMap(chain::filter)
				.onErrorResume(error -> {
					final ServerHttpResponse response = exchange.getResponse();
					response.setStatusCode(HttpStatus.UNAUTHORIZED);

					final DataBuffer buffer = exchange.getResponse()
							.bufferFactory()
							.wrap(produceErrorResult().getBytes(StandardCharsets.UTF_8));

					return response.writeWith(Flux.just(buffer));
				});
	}

	private static String produceErrorResult() {
		try {
			return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(Map.of(
					"type", ErrorType.UNAUTHORIZED,
					"message", "Unauthorized",
					"statusCode", HttpStatus.UNAUTHORIZED.value()
			));
		} catch (final JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private record ValidationResponse(String username, String role, UUID handle) {
	}
}
