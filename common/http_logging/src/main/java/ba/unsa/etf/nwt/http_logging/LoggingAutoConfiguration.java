package ba.unsa.etf.nwt.http_logging;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
public class LoggingAutoConfiguration {

	@Bean
	public WebMvcConfigurer loggingConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(@NonNull InterceptorRegistry registry) {
				registry.addInterceptor(new LoggingInterceptor());
			}
		};
	}
}
