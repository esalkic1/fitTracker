package ba.unsa.etf.nwt.notification_service.config.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class AuthFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			@NonNull final HttpServletRequest request,
			@NonNull final HttpServletResponse response,
			@NonNull final FilterChain filterChain
	) throws ServletException, IOException {
		final String handle = request.getHeader("X-Handle");
		final String role = request.getHeader("X-Role");

		final Authentication authentication = new UsernamePasswordAuthenticationToken(
				handle,
				null,
				List.of(new SimpleGrantedAuthority(role))
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}
