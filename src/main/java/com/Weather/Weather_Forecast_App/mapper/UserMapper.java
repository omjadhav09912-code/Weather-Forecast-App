package com.Weather.Weather_Forecast_App.mapper;

import org.mapstruct.Mapper;

import com.Weather.Weather_Forecast_App.dto.UserDto;
import com.Weather.Weather_Forecast_App.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
