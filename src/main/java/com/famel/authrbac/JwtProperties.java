package com.famel.authrbac;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Falha na inicializacao se o segredo faltar (ou tiver menos de 256 bits, exigido pelo HS256). */
@Validated
@ConfigurationProperties("app.jwt")
public record JwtProperties(@NotBlank @Size(min = 32) String secret, @Min(1) long expirationSeconds) {}
