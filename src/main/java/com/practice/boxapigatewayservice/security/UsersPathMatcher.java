package com.practice.boxapigatewayservice.security;

import org.springframework.http.server.PathContainer;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

/**
 * UsersPathMatcher.
 *
 * @author : middlefitting
 * @description :
 * @since : 2023/08/25
 */
@Component
public class UsersPathMatcher implements ServerWebExchangeMatcher {

  private final PathPatternParser pathPatternParser = new PathPatternParser();

  @Override
  public Mono<MatchResult> matches(ServerWebExchange exchange) {

    String path = exchange.getRequest().getURI().getPath();
    if (pathPatternParser.parse("/user-service/users/me/**").matches(PathContainer.parsePath(path))
        || pathPatternParser.parse("/user-service/users/me**")
        .matches(PathContainer.parsePath(path))
        || pathPatternParser.parse("/user-service/users/me")
        .matches(PathContainer.parsePath(path))) {
      return MatchResult.match();
    }
    return MatchResult.notMatch();
  }
}
