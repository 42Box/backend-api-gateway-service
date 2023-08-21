package com.practice.boxapigatewayservice.filter.filter;

import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

	private final DiscoveryClient discoveryClient;

	public GlobalFilter(DiscoveryClient discoveryClient) {
		super(Config.class);
		this.discoveryClient = discoveryClient;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			log.info("Global Filter: baseMessage: {}", config.getBaseMessage());
			if (config.isPreLogger()) {
				log.info("Global Filter Start: request id -> {}", request.getId());
				log.info("Request URI: {}", request.getURI());

				List<ServiceInstance> instances = discoveryClient.getInstances("AUTH-SERVICE");
				for (ServiceInstance instance : instances) {
					log.info("Available instance: {} {}", instance.getHost(), instance.getPort());
				}
			}

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				if (config.isPostLogger()) {
					log.info("Global POST Filter Start: request id -> {}",
							response.getStatusCode());
				}
			}));
		};
//		return (exchange, chain) -> {
//			ServerHttpRequest request = exchange.getRequest();
//
//			log.info("Global Filter: baseMessage: {}", config.getBaseMessage());
//			if (config.isPreLogger()) {
//				log.info("Global Filter Start: request id -> {}", request.getId());
//				log.info("Request URI: {}", request.getURI());
//
//				List<ServiceInstance> instances = discoveryClient.getInstances("AUTH-SERVICE");
//				for (ServiceInstance instance : instances) {
//					log.info("Available instance: {} {}", instance.getHost(), instance.getPort());
//				}
//			}
//			return null;
//		};
	}


	//Getter Setter를 위해 어노테이션 추가
	@Data
	public static class Config {

		private String baseMessage;
		private boolean preLogger;
		private boolean postLogger;
	}
}
