package com.practice.boxapigatewayservice.security;

import lombok.RequiredArgsConstructor;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Spring Boot 어플리케이션의 메인 클래스입니다.
 * <p>이 클래스는 {@link EnableWebFluxSecurity} 애노테이션을 사용해 Spring Security 의 설정을 담당합니다</p>
 *
 * @author middlefitting
 * @version 1.0.0
 * @see SecurityConfig
 * @since 2023-08-21
 */
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final ActuatorPathMatcher actuatorPathMatcher;
  private final AuthenticationManager authenticationManager;
  private final JwtServerAuthenticationConverter jwtServerAuthenticationConverter;

  /**
   * Spring Security 의 설정을 담당합니다.
   *
   * @return SecurityWebFilterChain http.build()
   * @see SecurityWebFilterChain
   * @see ServerHttpSecurity
   * @see HttpStatusServerEntryPoint
   * @see AuthenticationWebFilter
   * @see CorsConfigurationSource
   * @see UrlBasedCorsConfigurationSource
   * @see NoOpServerSecurityContextRepository
   * @see ActuatorPathMatcher
   */
  @Bean
  SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange(
        (request) -> request.matchers(actuatorPathMatcher).hasAnyAuthority("ROLE_ADMIN")
            .anyExchange().permitAll().and()
            .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange().and().formLogin().disable().httpBasic()
            .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)).and()
            .headers().frameOptions().disable().and().csrf().disable()
            .cors().configurationSource(corsConfigurationSource()).and()
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()));
    return http.build();
  }


  /*
   * 시큐리티에서 사용하는 AuthenticationWebFilter 를 생성합니다.
   * jwtServerAuthenticationConverter 를 통해 ROLE 을 주입합니다.
   *
   * @return AuthenticationWebFilter
   *
   * @see AuthenticationWebFilter
   * @see AuthenticationManager
   * @see JwtServerAuthenticationConverter
   * @see ServerWebExchangeMatchers
   * */
  private AuthenticationWebFilter authenticationWebFilter() {
    AuthenticationWebFilter authFilter = new AuthenticationWebFilter(authenticationManager);
    authFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);
    authFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange());
    return authFilter;
  }

  /*
   * CorsConfigurationSource 를 생성합니다.
   * 시큐리티의 Cors 설정을 관리합니다.
   *
   * @return CorsConfigurationSource
   *
   * @see CorsConfigurationSource
   * @see CorsConfiguration
   * */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.addAllowedOrigin("https://www.42box.kr");
    configuration.addAllowedOrigin("*");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
//    configuration.setMaxAge(1L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
