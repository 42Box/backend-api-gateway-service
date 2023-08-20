package com.practice.boxapigatewayservice.security;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

class ActuatorPathMatcherTest {

  private ActuatorPathMatcher matcher;

  @Mock
  private ServerWebExchange exchange;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    matcher = new ActuatorPathMatcher();
  }

  @Test
  public void MethodMatches_Match() {
    List<String> pathList = new ArrayList<>();
    pathList.add("/actuator");
    pathList.add("/actuator/**");
    pathList.add("/*/actuator");
    pathList.add("/*/actuator/**");
    pathList.add("/*/*/actuator/**");
    pathList.add("/*/*/*/actuator/**");
    pathList.add("/*/*/*/*/actuator/**");

    pathList.forEach(path -> {
      mockRequestPath(path);
      StepVerifier.create(matcher.matches(exchange)).assertNext(matchResult -> {
        assertThat(matchResult.isMatch()).isTrue();
      }).verifyComplete();
    });
  }

  @Test
  void MethodMatches_NotMatch() {
    List<String> pathList = new ArrayList<>();
    pathList.add("/test");
    pathList.add("/*/*/*/*/*/actuator/**");

    pathList.forEach(path -> {
      mockRequestPath(path);
      StepVerifier.create(matcher.matches(exchange)).assertNext(matchResult -> {
        assertThat(matchResult.isMatch()).isFalse();
      }).verifyComplete();
    });
  }

  private void mockRequestPath(String path) {
    MockServerHttpRequest request = MockServerHttpRequest.method(HttpMethod.GET, path).build();
    when(exchange.getRequest()).thenReturn(request);
  }
}
