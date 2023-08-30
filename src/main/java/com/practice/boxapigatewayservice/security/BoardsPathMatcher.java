package com.practice.boxapigatewayservice.security;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

/**
 * BoardsPathMatcher.
 *
 * @author : middlefitting
 * @since : 2023/08/30
 */
@Component
public class BoardsPathMatcher implements ServerWebExchangeMatcher {

  private final PathPatternParser pathPatternParser = new PathPatternParser();

  @Override
  public Mono<MatchResult> matches(ServerWebExchange exchange) {
    String path = exchange.getRequest().getURI().getPath();
    HttpMethod method = exchange.getRequest().getMethod();
    if (method == HttpMethod.POST || method == HttpMethod.PUT
        || method == HttpMethod.DELETE || method == HttpMethod.PATCH) {
      if (pathPatternParser.parse("/board-service/script-boards")
          .matches(PathContainer.parsePath(path))
          || pathPatternParser.parse("/board-service/script-boards**")
          .matches(PathContainer.parsePath(path))
          || pathPatternParser.parse("/board-service/script-boards/**")
          .matches(PathContainer.parsePath(path))) {
        return MatchResult.match();
      }
    }
    return MatchResult.notMatch();
  }
}