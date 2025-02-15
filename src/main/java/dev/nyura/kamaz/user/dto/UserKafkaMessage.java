package dev.nyura.kamaz.user.dto;

import dev.nyura.kamaz.user.enums.UserOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserKafkaMessage {
    private UserOperation operation;
    private UserDto userData;
}
