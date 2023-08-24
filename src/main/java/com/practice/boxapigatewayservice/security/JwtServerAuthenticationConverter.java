package com.practice.boxapigatewayservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.practice.boxapigatewayservice.global.env.EnvUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

  private final EnvUtil envUtil;

  @Override
  public Mono<Authentication> convert(ServerWebExchange exchange) {
    try {
      String jwtToken = extractTokenFromCookie(exchange.getRequest());
      if (jwtToken == null) {
        throw new Exception();
      }
      DecodedJWT decodeToken = JWT.require(
          Algorithm.HMAC512(envUtil.getEnv("jwt.token.ACCESS_SECRET"))).build().verify(jwtToken);
      String nickname = JWT.decode(jwtToken).getClaim("nickname").toString();
      if (nickname == null) {
        throw new Exception();
      }
      exchange.getResponse().getHeaders().add("nickname", nickname);
      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority("ROLE_AUTH_USER")); // 추후 개선
      return Mono.just(
          new UsernamePasswordAuthenticationToken(authorities, jwtToken, authorities));
    } catch (Exception e) {
      return Mono.empty();
    }
  }

  private String extractTokenFromCookie(ServerHttpRequest request) {
    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
    if (cookies.containsKey("box-auth")) {
      List<HttpCookie> boxAuthCookies = cookies.get(envUtil.getEnv("jwt.token.AUTH_TOKEN_NAME"));
      if (!boxAuthCookies.isEmpty()) {
        return boxAuthCookies.get(0).getValue();
      }
    }
    return null;
  }
}
