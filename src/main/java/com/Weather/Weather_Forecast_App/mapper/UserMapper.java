package com.Weather.Weather_Forecast_App.mapper;

import com.Weather.Weather_Forecast_App.dto.UserDto;
import com.Weather.Weather_Forecast_App.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
