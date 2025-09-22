package com.Weather.Weather_Forecast_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Weather.Weather_Forecast_App.dto.LoginDto;
import com.Weather.Weather_Forecast_App.dto.UserDto;
import com.Weather.Weather_Forecast_App.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        UserDto registeredUser = userService.registerUser(userDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody LoginDto loginDto) {
        UserDto user = userService.login(loginDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
