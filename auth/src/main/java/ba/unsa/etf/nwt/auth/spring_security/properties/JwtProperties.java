package ba.unsa.etf.nwt.auth.spring_security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("jwt")
public record JwtProperties(String headerTitle,
							String tokenPrefix,
							String secretKey,
							String authoritiesKey,
							Duration tokenValidityTime) {
}
