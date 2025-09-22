package com.Weather.Weather_Forecast_App.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Weather.Weather_Forecast_App.dto.LoginDto;
import com.Weather.Weather_Forecast_App.dto.UserDto;
import com.Weather.Weather_Forecast_App.entity.User;
import com.Weather.Weather_Forecast_App.mapper.UserMapper;
import com.Weather.Weather_Forecast_App.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDto registerUser(UserDto userDto) {
        // Check for duplicate username/email
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        // Basic validation
        if (userDto.getPassword() == null || userDto.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }
        try {
            User user = userMapper.toEntity(userDto);
            User savedUser = userRepository.save(user);
            return userMapper.toDto(savedUser);
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }
    @Override
    public UserDto login(LoginDto loginDto) {
        User user = findByUsernameOrEmail(loginDto.getEmail());
        if (user != null && user.getPassword().equals(loginDto.getPassword())) {
            return userMapper.toDto(user);
        } else {
            throw new RuntimeException("Invalid username/email or password");
        }
    }

    @Override
    public User findByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }
        return userOpt.orElse(null);
    }

}
