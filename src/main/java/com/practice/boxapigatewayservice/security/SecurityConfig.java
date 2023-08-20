package com.practice.boxapigatewayservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@EnableWebFluxSecurity
public class SecurityConfig {

  private final ActuatorPathMatcher actuatorPathMatcher;
  private final AuthenticationManager authenticationManager;
  private final JwtServerAuthenticationConverter jwtServerAuthenticationConverter;

  public SecurityConfig(ActuatorPathMatcher actuatorPathMatcher,
    AuthenticationManager authenticationManager,
    JwtServerAuthenticationConverter jwtServerAuthenticationConverter) {
    this.actuatorPathMatcher = actuatorPathMatcher;
    this.authenticationManager = authenticationManager;
    this.jwtServerAuthenticationConverter = jwtServerAuthenticationConverter;
  }

  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange(
      (request) -> request.matchers(actuatorPathMatcher).hasAnyAuthority("ROLE_ADMIN")
        .anyExchange().permitAll().and()
        .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
        .authorizeExchange().and().formLogin().disable().httpBasic()
        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)).and()
        .headers().frameOptions().disable().and().csrf().disable()
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()));
    return http.build();
  }

  private AuthenticationWebFilter authenticationWebFilter() {
    AuthenticationWebFilter authFilter = new AuthenticationWebFilter(authenticationManager);
    authFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);
    authFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange());
    return authFilter;
  }
}
