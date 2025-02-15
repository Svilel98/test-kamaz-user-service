package dev.nyura.kamaz.user.controller;

import dev.nyura.kamaz.user.dto.UserDto;
import dev.nyura.kamaz.user.dto.UserUpdateDto;
import dev.nyura.kamaz.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Набор операций пользователя")
@SecurityRequirement(name = "Bearer Authentication")
public class UsersController {
    private final UserService userService;

    @GetMapping("/current")
    @Operation(
            summary = "Получить текущего пользователя"
    )
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserByUsername(authentication.getName()));
    }

    @PutMapping("/pwdChange")
    @Operation(
            summary = "Изменить пароль текущего пользователя",
            parameters = @Parameter(name = "newPassword", description = "Новый пароль")

    )
    public ResponseEntity<String> changePassword(Authentication authentication, @RequestParam String newPassword) {
        userService.changePassword(authentication.getName(), newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PutMapping("/current")
    @Operation(
            summary = "Обновить данные текущего пользователя"

    )
    public ResponseEntity<UserDto> updateCurrentUser(Authentication authentication, @RequestBody UserUpdateDto updatedUserDto) {
        return ResponseEntity.ok(
                userService.updateUser(authentication.getName(), updatedUserDto));
    }
}