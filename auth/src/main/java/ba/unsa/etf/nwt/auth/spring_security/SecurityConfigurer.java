package ba.unsa.etf.nwt.auth.spring_security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface SecurityConfigurer {
	void configure(HttpSecurity http) throws Exception;
}
