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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Набор операций админа для взаимодействия с данными пользователя.")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "Получить пользователя по username",
            parameters = @Parameter(name = "username", description = "Имя пользователя, по которому будет выполнен поиск")
    )
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/{username}")
    @Operation(
            summary = "Обновить данные пользователя",
            parameters = @Parameter(name = "username", description = "Имя пользователя, для которого нужно обновить данные")
    )
    public ResponseEntity<UserDto> updateUser(@PathVariable String username, @RequestBody UserUpdateDto updatedUserDto) {
        return ResponseEntity.ok(userService.updateUser(username, updatedUserDto));
    }

    @DeleteMapping("/{username}")
    @Operation(
            summary = "Удалить пользователя",
            parameters = @Parameter(name = "username", description = "Имя пользователя, которого нужно удалить")
    )
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/{username}/pwdChange")
    @Operation(
            summary = "Изменить пароль пользователя",
            parameters = @Parameter(name = "username", description = "Имя пользователя, для которого нужно изменить пароль")
    )
    public ResponseEntity<String> changePassword(@PathVariable String username, @RequestParam String newPassword) {
        userService.changePassword(username, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }
}