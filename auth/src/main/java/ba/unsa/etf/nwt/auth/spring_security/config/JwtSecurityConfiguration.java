package ba.unsa.etf.nwt.auth.spring_security.config;

import ba.unsa.etf.nwt.auth.services.JwtService;
import ba.unsa.etf.nwt.auth.spring_security.JwtSecurityConfigurer;
import ba.unsa.etf.nwt.auth.spring_security.filters.JwtAuthorizationFilter;
import ba.unsa.etf.nwt.auth.spring_security.properties.JwtProperties;
import ba.unsa.etf.nwt.auth.spring_security.SecurityConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtSecurityConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public SecurityConfigurer securityConfigurer(final JwtService jwtService, final JwtProperties jwtProperties) {
		return new JwtSecurityConfigurer(
				new JwtAuthorizationFilter(jwtService, jwtProperties.headerTitle(), jwtProperties.tokenPrefix())
		);
	}

	@Bean
	public JwtService jwtService(final JwtProperties jwtProperties) {
		return new JwtService(
				jwtProperties.secretKey(),
				jwtProperties.authoritiesKey(),
				jwtProperties.tokenValidityTime()
		);
	}
}
