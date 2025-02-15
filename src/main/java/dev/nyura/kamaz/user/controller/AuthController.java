package dev.nyura.kamaz.user.controller;

import dev.nyura.kamaz.user.dto.UserDto;
import dev.nyura.kamaz.user.dto.UserRegistrationDto;
import dev.nyura.kamaz.user.security.JwtService;
import dev.nyura.kamaz.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Набор операций по аунтефикации пользователя.")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @GetMapping("/login")
    @Operation(
            summary = "Авторизация пользователя",
            parameters = {
                    @Parameter(name = "username", description = "Имя пользователя"),
                    @Parameter(name = "password", description = "Пароль пользователя")
            }
    )
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        JwtService.Proccesor accessTokenProccesor = jwtService.getAccessTokenProccesor();
        JwtService.Proccesor refreshTokenProccesor = jwtService.getRefreshTokenProccesor();
        String accessToken = accessTokenProccesor.generateToken(userDetails);
        String refreshToken = refreshTokenProccesor.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("access_token", accessToken, "refresh_token", refreshToken));
    }

    @GetMapping("/refresh")
    @Operation(
            summary = "Обновление токена доступа",
            parameters = {
                    @Parameter(name = "refreshToken", description = "Refresh токен")
            }
    )
    public ResponseEntity<Map<String, String>> refresh(@RequestParam String refreshToken) {
        JwtService.Proccesor accessTokenProccesor = jwtService.getAccessTokenProccesor();
        JwtService.Proccesor refreshTokenProccesor = jwtService.getRefreshTokenProccesor();
        String username;
        try {
            username = refreshTokenProccesor.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!refreshTokenProccesor.isTokenValid(refreshToken, userDetails)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        String accessToken = accessTokenProccesor.generateToken(userDetails);
        String newRefreshToken = refreshTokenProccesor.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("access_token", accessToken, "refresh_token", newRefreshToken));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя"
    )
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        return ResponseEntity.ok(userService.createUser(userRegistrationDto));
    }
}
