package com.practice.boxapigatewayservice.security;

import org.springframework.http.server.PathContainer;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

@Component
public class ActuatorPathMatcher implements ServerWebExchangeMatcher {

  private final PathPatternParser pathPatternParser = new PathPatternParser();

  @Override
  public Mono<MatchResult> matches(ServerWebExchange exchange) {

    String path = exchange.getRequest().getURI().getPath();
    if (pathPatternParser.parse("/actuator").matches(PathContainer.parsePath(path))
      || pathPatternParser.parse("/actuator/**").matches(PathContainer.parsePath(path))
      || pathPatternParser.parse("/*/actuator").matches(PathContainer.parsePath(path))
      || pathPatternParser.parse("/*/actuator/**").matches(PathContainer.parsePath(path))
      || pathPatternParser.parse("/*/*/actuator/**").matches(PathContainer.parsePath(path))
      || pathPatternParser.parse("/*/*/*/actuator/**").matches(PathContainer.parsePath(path))
      || pathPatternParser.parse("/*/*/*/*/actuator/**")
      .matches(PathContainer.parsePath(path))) {
      return MatchResult.match();
    }
    return MatchResult.notMatch();
  }
}