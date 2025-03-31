package ba.unsa.etf.nwt.error_logging.config;

import ba.unsa.etf.nwt.error_logging.error_advice.BaseErrorAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ErrorHandlingAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public BaseErrorAdvice baseErrorAdvice() {
		return new BaseErrorAdvice();
	}
}
