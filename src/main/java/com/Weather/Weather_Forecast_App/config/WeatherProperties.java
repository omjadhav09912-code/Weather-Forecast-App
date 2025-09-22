package com.Weather.Weather_Forecast_App.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "weather")
public class WeatherProperties {
    private String ipapiUrl;
    private Api api;

    @Data
    public static class Api {
        private String key;
        private Geo geo;

        @Data
        public static class Geo {
            private String url;
        }
    }
}
