package com.Weather.Weather_Forecast_App.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CurrentWeather {
    private double temperature;
    private int humidity;
    private String description;
    private int pressure;
    private String wind;
    private String sunrise;
    private String sunset;
    private String icon;

}
