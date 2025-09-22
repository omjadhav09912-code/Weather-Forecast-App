package com.Weather.Weather_Forecast_App.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DailyForecast {
    private String date;
    private double min;
    private double max;
    private String description;
    private String icon;


}
