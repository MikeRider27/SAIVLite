package com.saiv.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "saiv.jwt")
public record JwtProperties(String secret, long expirationHours) {}
