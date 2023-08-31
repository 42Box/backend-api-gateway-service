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
    ServerHttpRequest request = exchange.getRequest();
    try {
      String jwtToken = null;
      String prefix = envUtil.getEnv("jwt.token.TOKEN_PREFIX");
      String authHeader = request.getHeaders().getFirst("Authorization");
      if (authHeader != null && authHeader.startsWith(prefix)) {
        jwtToken = authHeader.substring(prefix.length() + 1);
      } else {
        jwtToken = extractTokenFromCookie(exchange.getRequest());
      }
      if (jwtToken == null) {
        request = request.mutate().headers(headers -> headers.remove("nickname")).build();
        request = request.mutate().headers(headers -> headers.remove("uuid")).build();
        request = request.mutate().headers(headers -> headers.remove("profileImagePath")).build();
        request = request.mutate().headers(headers -> headers.remove("profileImageUrl")).build();
        throw new Exception();
      }
      DecodedJWT decodeToken = JWT.require(
          Algorithm.HMAC512(envUtil.getEnv("jwt.token.ACCESS_SECRET"))).build().verify(jwtToken);
      String nickname = JWT.decode(jwtToken).getClaim("nickname").toString();
      String uuid = JWT.decode(jwtToken).getClaim("uuid").toString();
      String role = JWT.decode(jwtToken).getClaim("role").toString();
      String profileImagePath = JWT.decode(jwtToken).getClaim("profileImagePath").toString();
      String profileImageUrl = JWT.decode(jwtToken).getClaim("profileImageUrl").toString();
      if (nickname == null || uuid == null || role == null || profileImagePath == null
          || profileImageUrl == null || nickname.isEmpty() || uuid.isEmpty() || role.isEmpty()
          || profileImagePath.isEmpty() || profileImageUrl.isEmpty()) {
        throw new Exception();
      }

      nickname = nickname.replace("\"", "");
      uuid = uuid.replace("\"", "");
      role = role.replace("\"", "");
      profileImagePath = profileImagePath.replace("\"", "");
      profileImageUrl = profileImageUrl.replace("\"", "");

      request.mutate().header("nickname", nickname).build();
      request.mutate().header("uuid", uuid).build();
      request.mutate().header("profileImagePath", profileImagePath).build();
      request.mutate().header("profileImageUrl", profileImageUrl).build();

      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority(role)); // 추후 개선
      return Mono.just(
          new UsernamePasswordAuthenticationToken(authorities, jwtToken, authorities));
    } catch (Exception e) {
      return Mono.empty();
    }
  }

  private String extractTokenFromCookie(ServerHttpRequest request) {
    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
    String tokenName = envUtil.getEnv("jwt.token.AUTH_TOKEN_NAME");
    if (cookies.containsKey(tokenName)) {
      List<HttpCookie> boxAuthCookies = cookies.get(tokenName);
      if (!boxAuthCookies.isEmpty()) {
        return boxAuthCookies.get(0).getValue();
      }
    }
    return null;
  }
}
