package com.practice.boxapigatewayservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(-1)
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

  private final ObjectMapper objectMapper;
  private final Environment env;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable exception) {
    ServerHttpResponse response = exchange.getResponse();
    DataBufferFactory bufferFactory = response.bufferFactory();

    Optional<String> keyOpt = Optional.ofNullable(
      env.getProperty("header.server-checked-error.key"));
    Optional<String> valueOpt = Optional.ofNullable(
      env.getProperty("header.server-checked-error.value"));

    if (keyOpt.isEmpty() || valueOpt.isEmpty()) {
      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      return response.writeWith(Mono.fromSupplier(() -> bufferFactory.wrap(new byte[0])));
    }

    String key = keyOpt.get();
    String value = valueOpt.get();

    if (response.isCommitted()) {
      return Mono.error(exception);
    }

    if (value.equals(exchange.getRequest().getHeaders().getFirst(key))) {
      return Mono.empty();
    }

    if (exception instanceof ResponseStatusException) {
      response.setStatusCode(((ResponseStatusException) exception).getStatus());
    }

    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    return response.writeWith(Mono.fromSupplier(() -> {
      try {
        Map<String, Object> errorResponseMap = new HashMap<>();
        response.getHeaders().set(key, value);
        errorResponseMap.put("msg", env.getProperty("gateway.error.msg"));
        errorResponseMap.put("code", Integer.valueOf(env.getProperty("gateway.error.code")));
        byte[] errorResponse = objectMapper.writeValueAsBytes(errorResponseMap);
        return bufferFactory.wrap(errorResponse);
      } catch (Exception e) {
        return bufferFactory.wrap(new byte[0]);
      }
    }));
  }
}

