package dev.nyura.kamaz.user.service;

import dev.nyura.kamaz.user.dto.UserDto;
import dev.nyura.kamaz.user.dto.UserKafkaMessage;
import dev.nyura.kamaz.user.dto.UserRegistrationDto;
import dev.nyura.kamaz.user.dto.UserUpdateDto;
import dev.nyura.kamaz.user.entity.Role;
import dev.nyura.kamaz.user.entity.User;
import dev.nyura.kamaz.user.enums.UserOperation;
import dev.nyura.kamaz.user.exception.UserNotFoundException;
import dev.nyura.kamaz.user.exception.UsernameAlreadyTakenException;
import dev.nyura.kamaz.user.mapper.UserMapper;
import dev.nyura.kamaz.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserKafkaMessage> kafkaTemplate;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private void sendKafkaMessage(UserOperation operation, User user) {
        String key = "UserModified-" + user.getUsername();
        UserKafkaMessage message = new UserKafkaMessage(operation, userMapper.toDto(user));
        kafkaTemplate.send("user-changed", key, message);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found."));
    }

    public boolean existsByUsernameAndPassword(String username, String password) {
        return userRepository.existsByUsernameAndPassword(username, passwordEncoder.encode(password));
    }

    public UserDto createUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByUsername(userRegistrationDto.getUsername())) {
            throw new UsernameAlreadyTakenException("Username is already taken.");
        }

        User user = new User();
        user.setUsername(userRegistrationDto.getUsername());
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        sendKafkaMessage(UserOperation.USER_CREATED, savedUser);
        return userMapper.toDto(savedUser);
    }

    public UserDto updateUser(String username, UserUpdateDto updatedUserDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found."));
        user.setFirstName(updatedUserDto.getFirstName());
        user.setLastName(updatedUserDto.getLastName());

        User savedUser = userRepository.save(user);
        sendKafkaMessage(UserOperation.USER_UPDATED, savedUser);
        return userMapper.toDto(savedUser);
    }

    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found."));
        sendKafkaMessage(UserOperation.USER_DELETED, user);
        userRepository.delete(user);
    }

    private static String encrypt(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] encodedhash = digest.digest(
                input.getBytes(StandardCharsets.UTF_8));
        byte[] encodedBytes = Base64.encodeBase64(encodedhash, false);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

}
