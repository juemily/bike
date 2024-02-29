 package com.example.bike.inftrastructure.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {
	private static Logger log = LoggerFactory.getLogger(TokenProvider.class);
	private static final String AUTHORITIES_KEY = "auth";
	private static final String SECRET_KEY = "netoss" + System.currentTimeMillis();

	public static final String AUTH_REFRESH_TOKEN = "REFRESH_TOKEN";


		@Value("#{1000*${netoss.security.jwt.token.validity:14400}}") // 4h
	private long tokenValidityInMilliseconds;

		@Value("#{1000*${netoss.security.jwt.token-remenber-me.validity:604800}}") // 7d
	private long tokenValidityInMillisecondsForRememberMe;


	public String createToken(Authentication authentication, boolean rememberMe) {
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		long validity = System.currentTimeMillis() + (rememberMe ? tokenValidityInMillisecondsForRememberMe : tokenValidityInMilliseconds);
		return Jwts.builder()
				.setSubject(authentication.getName())
				.claim(AUTHORITIES_KEY, authorities)
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				.setExpiration(new Date(validity))
				.compact();
	}

	public String createRefreshToken(Authentication authentication, boolean rememberMe) {
		long validity = System.currentTimeMillis() + (rememberMe ? tokenValidityInMillisecondsForRememberMe : tokenValidityInMilliseconds);
		validity += 600_000; //Al 'token' de refresco le damos 10 minutos más de validez.
		return Jwts.builder()
				.setSubject(authentication.getName())
				.claim(AUTHORITIES_KEY, AUTH_REFRESH_TOKEN) //Solo tiene el rol de refresco.
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				.setExpiration(new Date(validity))
				.compact();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parser().setSigningKey(SECRET_KEY)
				.parseClaimsJws(token)
				.getBody();

		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.filter(auth -> !auth.trim().isEmpty())
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		User principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.info("Invalid JWT token: {}", e.getMessage());
		}
		return false;
	}
}
