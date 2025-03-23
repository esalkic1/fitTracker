package ba.unsa.etf.nwt.auth.services;

import ba.unsa.etf.nwt.auth.domain.User;
import ba.unsa.etf.nwt.auth.exceptions.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

public class JwtService {
	private final String secretKey;
	private final String authoritiesKey;
	private final Duration tokenValidityTime;

	public JwtService(final String secretKey, final String authoritiesKey, final Duration tokenValidityTime) {
		this.secretKey = secretKey;
		this.authoritiesKey = authoritiesKey;
		this.tokenValidityTime = tokenValidityTime;
	}

	public String generateToken(final User user) throws JwtException {
		try {
			return Jwts.builder()
					.subject(user.getEmail())
					.claim(this.authoritiesKey, user.getRole().toString())
					.issuedAt(Date.from(Instant.now()))
					.expiration(Date.from(Instant.now().plus(this.tokenValidityTime)))
					.signWith(getSignInKey())
					.compact();
		} catch (final Exception e) {
			throw new JwtException(e.getMessage());
		}
	}

	public Claims resolveClaims(final String token) {
		return Jwts.parser()
				.verifyWith((SecretKey) getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public boolean isExpired(final Claims claims) {
		final Instant expiration = claims.getExpiration().toInstant();
		return expiration.isBefore(Instant.now());
	}

	public Set<GrantedAuthority> getAuthoritiesFromClaims(final Claims claims) {
		return Set.of(new SimpleGrantedAuthority(claims.get(this.authoritiesKey).toString()));
	}

	private Key getSignInKey(){
		byte[] keyBytes = this.secretKey.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
