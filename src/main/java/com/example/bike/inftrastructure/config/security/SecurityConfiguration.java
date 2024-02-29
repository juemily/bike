package com.example.bike.inftrastructure.config.security;


import com.example.bike.inftrastructure.config.security.jwt.JWTConfigurer;
import com.example.bike.inftrastructure.config.security.jwt.TokenProvider;
import com.example.bike.inftrastructure.config.security.ldap.LdapAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

	@Value("${ldap.active:true}")
	private boolean isLdapActive;

	private final TokenProvider tokenProvider;
	private final LdapAuthenticationProvider ldapAuthProvider;


	@Autowired
	public SecurityConfiguration(TokenProvider tokenProvider, LdapAuthenticationProvider ldapAuthProvider) {
		this.tokenProvider = tokenProvider;
		this.ldapAuthProvider = ldapAuthProvider;
	}


	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web
				.ignoring()
				.requestMatchers("swagger-ui");
	}


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		if (!isLdapActive) {
			http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(AbstractHttpConfigurer::disable)
				.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/**").permitAll());
			return http.build();
		}



		//if (Boolean.getBoolean("disable-auth")) {
			//Sin autenticación ni 'cors' para desarrollo rápido en Local.
			CorsConfiguration corsConfig = new CorsConfiguration();
			corsConfig.setAllowedOrigins(List.of("*"));
			corsConfig.setAllowedMethods(List.of("*"));
			corsConfig.setAllowedHeaders(List.of("*"));

			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", corsConfig);

		/*
			http.authorizeRequests().anyRequest().permitAll()
					.and().cors(x -> {
						x.configurationSource(source);
					}).csrf(AbstractHttpConfigurer::disable);
		} else {
		*/
			http.sessionManagement(httpSecuritySessionManagementConfigurer ->
						httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.csrf(AbstractHttpConfigurer::disable)
					.cors(it -> {
						it.configurationSource(source);
					})
				.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
						authorizationManagerRequestMatcherRegistry
								.requestMatchers("/actuator/health").permitAll()
								.requestMatchers("/bkool/bike/v1/auth/authenticate").permitAll()
								.requestMatchers("/bkool/bike/v1/auth/refresh").hasAnyAuthority(TokenProvider.AUTH_REFRESH_TOKEN)
								.anyRequest().authenticated()
				)
				.httpBasic(Customizer.withDefaults())
				.apply(new JWTConfigurer(tokenProvider));
		//}

		return http.build();
	}


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(ldapAuthProvider);
	}


	@Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
