package com.Weather.Weather_Forecast_App.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyWeatherResponse {
    private List<ForecastItem> list;

    @Data
    public static class ForecastItem {
        private long dt;
        private Main main;
        private Weather[] weather;
    }

    @Data
    public static class Main {
        private double temp_min;
        private double temp_max;
    }

    @Data
    public static class Weather {
    private String description;
    private String icon;
    }
}
