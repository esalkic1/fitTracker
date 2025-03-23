package ba.unsa.etf.nwt.auth.spring_security.filters;

import ba.unsa.etf.nwt.auth.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final String headerTitle;
	private final String tokenPrefix;

	public JwtAuthorizationFilter(final JwtService jwtService, final String headerTitle, final String tokenPrefix) {
		this.jwtService = jwtService;
		this.headerTitle = headerTitle;
		this.tokenPrefix = tokenPrefix;
	}

	@Override
	protected void doFilterInternal(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final FilterChain filterChain
	) {
		try {
			final String bearerToken = request.getHeader(this.headerTitle);
			if (bearerToken == null) {
				filterChain.doFilter(request, response);
				return;
			}

			final String token = bearerToken.replace(this.tokenPrefix, "");

			final Claims claims = jwtService.resolveClaims(token);

			if (claims != null && !jwtService.isExpired(claims)) {
				final String username = claims.getSubject();
				final Set<GrantedAuthority> userAuthorities = jwtService.getAuthoritiesFromClaims(claims);

				final Authentication authentication = new UsernamePasswordAuthenticationToken(
						username,
						null,
						userAuthorities
				);

				SecurityContextHolder.getContext().setAuthentication(authentication);
				filterChain.doFilter(request, response);
			}
		} catch (final IOException | ServletException | JwtException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
