package com.Weather.Weather_Forecast_App.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentWeatherResponse {
    private Coord coord;
    private Main main;
    private Weather[] weather;
    private Wind wind;
    private Sys sys;

    @Data
    public static class Coord {
        private double lat;
        private double lon;
    }

    @Data
    public static class Main {
    private double temp;
    private int humidity;
    private int pressure;
    }

    @Data
    public static class Weather {
        private String description;
        private String icon;
    }
    @Data
    public static class Wind {
        private double speed;
    }
    @Data
    public static class Sys {
        private long sunrise;
        private long sunset;
    }
}
