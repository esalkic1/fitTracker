package ba.unsa.etf.nwt.notification_service.config.auth;

import ba.unsa.etf.nwt.notification_service.config.auth.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
		return http
				.csrf(CsrfConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new AuthFilter(), UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(requests -> requests
						.anyRequest().permitAll()
				)
				.build();
	}
}
