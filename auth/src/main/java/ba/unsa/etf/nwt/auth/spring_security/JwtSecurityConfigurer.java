package ba.unsa.etf.nwt.auth.spring_security;

import ba.unsa.etf.nwt.auth.spring_security.filters.JwtAuthorizationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfigurer implements SecurityConfigurer {
	private final JwtAuthorizationFilter filter;

	public JwtSecurityConfigurer(final JwtAuthorizationFilter filter) {
		this.filter = filter;
	}

	@Override
	public void configure(final HttpSecurity http) throws Exception {
		http
				.csrf(CsrfConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(this.filter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(
						requests -> requests
								.requestMatchers("/api/v1/auth/**").permitAll()
								.requestMatchers("/api/v1/**").authenticated()
								.anyRequest().permitAll()
				);
	}
}
