package com.Weather.Weather_Forecast_App.service;

import com.Weather.Weather_Forecast_App.dto.LoginDto;
import com.Weather.Weather_Forecast_App.dto.UserDto;

public interface UserService {
    UserDto registerUser(UserDto userDto);
    UserDto login(LoginDto loginDto);
    com.Weather.Weather_Forecast_App.entity.User findByUsernameOrEmail(String usernameOrEmail);

}
