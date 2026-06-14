package com.mitocode.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

//Clase S7
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContextRepository;

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http ) {
		http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.authorizeExchange(authorizeExchangeSpec ->
					authorizeExchangeSpec.pathMatchers(HttpMethod.OPTIONS).permitAll()
					//SWAGGER PARA SPRING SECURITY
					.pathMatchers("/swagger-resources/**").permitAll()
					.pathMatchers("/swagger-ui.html").permitAll()
					.pathMatchers("/v3/api-docs/**").permitAll()
					.pathMatchers("/webjars/**").permitAll()
					//SWAGGER PARA SPRING SECURITY
					.pathMatchers("/login").permitAll()
					.pathMatchers("/v2/login").permitAll()
					.pathMatchers("/v2/**").authenticated()
					.pathMatchers("/dishes/**").authenticated()
					.pathMatchers("/clients/**").authenticated()
					.pathMatchers("/bills/**").authenticated()
					.pathMatchers("/backpressure/**").permitAll()
					.pathMatchers("/menus/**").authenticated()
					.anyExchange().authenticated());
		http
			.exceptionHandling(exception -> exception.authenticationEntryPoint(
			  (swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
			.accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))));
		http
			.authenticationManager(authenticationManager)
			.securityContextRepository(securityContextRepository);

		return http.build();
	}
}