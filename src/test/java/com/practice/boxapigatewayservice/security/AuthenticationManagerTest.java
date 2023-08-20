package com.practice.boxapigatewayservice.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

class AuthenticationManagerTest {

  AuthenticationManager authenticationManager;

  @Mock
  Authentication authentication;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    authenticationManager = new AuthenticationManager();
  }

  @Test
  public void Authenticate() {
    assertEquals(authentication, authenticationManager.authenticate(authentication).block());
  }
}