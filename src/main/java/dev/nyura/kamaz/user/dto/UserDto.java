package dev.nyura.kamaz.user.dto;

import dev.nyura.kamaz.user.entity.Role;
import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
}
