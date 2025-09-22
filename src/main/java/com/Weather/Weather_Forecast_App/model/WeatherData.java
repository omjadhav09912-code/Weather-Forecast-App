package com.Weather.Weather_Forecast_App.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class WeatherData {
    private CurrentWeather current;
    private List<DailyForecast> weekly;


}
