package ba.unsa.etf.nwt.auth.config;

import ba.unsa.etf.nwt.auth.ws.interceptor.EventInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EventConfiguration {

	@Bean
	WebMvcConfigurer eventsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(@NonNull final InterceptorRegistry registry) {
				registry.addInterceptor(new EventInterceptor());
			}
		};
	}
}
