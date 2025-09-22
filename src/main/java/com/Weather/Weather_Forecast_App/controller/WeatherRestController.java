package com.Weather.Weather_Forecast_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Weather.Weather_Forecast_App.model.WeatherData;
import com.Weather.Weather_Forecast_App.service.WeatherService;

@RestController
@RequestMapping("/api/weather")
public class WeatherRestController {
    @Autowired
    private WeatherService weatherService;

    // Diagnostic endpoint to get raw API response for London
    @GetMapping("/diagnostic")
    public ResponseEntity<?> getRawApiResponse() {
        try {
            String apiKey = weatherService.getApiKey();
            String url = "https://api.openweathermap.org/data/2.5/weather?q=London&appid=" + apiKey + "&units=metric";
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String rawResponse = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(rawResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching raw API response: " + e.getMessage());
        }
    }


    // Endpoint for weather by coordinates
    @GetMapping
    public ResponseEntity<?> getWeatherData(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "false") boolean weekly
    ) {
        // Assuming you have a method in Service to get data by coordinates
        WeatherData weather = weatherService.getWeatherDataByCoordinates(lat, lon, weekly);
        if (weather == null) {
            return ResponseEntity.status(500).body("Error retrieving weather data");
        }
        return ResponseEntity.ok(weather);
    }

    // Endpoint for weather by city name
    @GetMapping("/by-city")
    public ResponseEntity<?> getWeatherByCityName(
            @RequestParam String city,
            @RequestParam(defaultValue = "false") boolean weekly
    ) {
        WeatherData weather = weatherService.getWeatherDataByCity(city, weekly);
        if (weather == null) {
            return ResponseEntity.badRequest().body("Invalid city name or error retrieving weather data");
        }
        return ResponseEntity.ok(weather);
    }
}
