package ba.unsa.etf.nwt.auth.spring_security.config;

import ba.unsa.etf.nwt.auth.services.UserService;
import ba.unsa.etf.nwt.auth.spring_security.SecurityConfigurer;
import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

	@Bean
	public AuthenticationManager authenticationManager(
			final HttpSecurity http,
			final PasswordEncoder passwordEncoder,
			final UserService userService
	) throws Exception {
		AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
		builder.userDetailsService(userService).passwordEncoder(passwordEncoder);

		return builder.build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(final HttpSecurity http, final SecurityConfigurer securityConfigurer) throws Exception {
		securityConfigurer.configure(http);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public GrpcAuthenticationReader grpcAuthenticationReader(){
		return new BasicGrpcAuthenticationReader();
	}
}
