package dev.nyura.kamaz.user.mapper;

import dev.nyura.kamaz.user.dto.UserDto;
import dev.nyura.kamaz.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
