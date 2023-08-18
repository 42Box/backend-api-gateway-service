package com.practice.boxapigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Spring Boot 어플리케이션의 메인 클래스입니다.
 * <p>이 클래스는 Spring Boot 어플리케이션을 시작하는 역할을 합니다. {@link SpringBootApplication} 애노테이션을 사용해
 * Spring Boot 의 자동설정, Bean 등록 등을 자동으로 수행합니다.</p>
 * <p>이 클래스에서는 다음과 같은 작업을 수행합니다.</p>
 *
 * @author middlefitting
 * @version 1.0.0
 * @see SpringBootApplication
 * @see BCryptPasswordEncoder
 * @since 2023-04-05
 */
@SpringBootApplication
public class BoxApiGatewayServiceApplication {

  /**
   * SpringApplication 을 실행합니다.
   *
   * @param args 실행하는데 필요한 인자.
   */
  public static void main(String[] args) {
    SpringApplication.run(BoxApiGatewayServiceApplication.class, args);
  }
}
