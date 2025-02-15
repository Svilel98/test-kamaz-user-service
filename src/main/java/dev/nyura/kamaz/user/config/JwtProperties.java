package dev.nyura.kamaz.user.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;


@ConfigurationProperties("security.jwt")
@Data
@Validated
public class JwtProperties {
    @NestedConfigurationProperty
    @Valid
    private Token accessToken = new Token();
    @NestedConfigurationProperty
    @Valid
    private Token refreshToken = new Token();

    @Data
    public static class Token {
        @NotEmpty
        private String secret;
        @NotNull
        private Duration expiration;
    }

}
