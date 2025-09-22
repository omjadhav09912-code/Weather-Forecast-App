package com.Weather.Weather_Forecast_App.service;

import com.Weather.Weather_Forecast_App.model.WeatherData;

public interface WeatherService {
    String getApiKey();

    WeatherData getWeatherDataByCity(String city, boolean weekly);

    WeatherData getWeatherDataByCoordinates(double lat, double lon, boolean weekly);
}
