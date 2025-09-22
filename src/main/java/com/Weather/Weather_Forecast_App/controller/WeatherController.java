package com.Weather.Weather_Forecast_App.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Weather.Weather_Forecast_App.model.WeatherData;
import com.Weather.Weather_Forecast_App.service.WeatherService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/weather")  // Added base path
public class WeatherController {

    @Autowired

    private WeatherService weatherService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String city,
                            @RequestParam(defaultValue = "false") boolean weekly,
                            @RequestParam(required = false) String useLocation,
                            HttpSession session,
                            HttpServletRequest request,
                            Model model) {
        // Enforce authentication
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
    WeatherData weatherData = null;
    String errorMsg = null;
    boolean isLocation = "true".equals(useLocation);
    String searchedCity = city != null ? city : "";
    try {
        if (isLocation) {
            String ip = request.getRemoteAddr();
            String detectedCity;
            try {
               RestTemplate restTemplate = new RestTemplate();
                String geoUrl = "https://ipapi.co/" + ip + "/city/";
                detectedCity = restTemplate.getForObject(geoUrl, String.class);
                if (detectedCity == null || detectedCity.trim().isEmpty() || "Undefined".equalsIgnoreCase(detectedCity)) {
                    detectedCity = "Pune";
                  // detectedCity = null;
                }
            } catch (Exception e) {
                //detectedCity = "Pune";
                detectedCity = null;
            }
            double lat = 18.5204, lon = 73.8567;
            //double lat = 0, lon = 0;
            boolean geoSuccess = false;
            try {
               RestTemplate restTemplate = new RestTemplate();
                String geoApiUrl = "https://api.openweathermap.org/geo/1.0/direct?q=" + detectedCity + "&limit=1&appid=" + weatherService.getApiKey();
                java.util.List<?> geoResult = restTemplate.getForObject(geoApiUrl, java.util.List.class);
                if (geoResult != null && !geoResult.isEmpty()) {
                    java.util.Map<?,?> geoMap = (java.util.Map<?,?>) geoResult.get(0);
                    Object latObj = geoMap.get("lat");
                    Object lonObj = geoMap.get("lon");
                    if (latObj != null && lonObj != null) {
                        lat = Double.parseDouble(latObj.toString());
                        lon = Double.parseDouble(lonObj.toString());
                        geoSuccess = true;
                    }
                }
            } catch (Exception e) {
                // log error
            }
            weatherData = weatherService.getWeatherDataByCoordinates(lat, lon, true);
            searchedCity = detectedCity;
            if (!geoSuccess || weatherData == null) {
                errorMsg = "Could not determine weather for your current location (" + detectedCity + "). Please try again or use city search.";
            }
        } else if (city != null && !city.isEmpty()) {
            weatherData = weatherService.getWeatherDataByCity(city, weekly);
            if (weatherData == null) {
                errorMsg = "Weather data could not be retrieved for '" + city + "'. Please check the city name or try again later.";
            }
        }
    } catch (Exception ex) {
        errorMsg = "An unexpected error occurred: " + ex.getMessage();
    }
    model.addAttribute("weather", weatherData);
    model.addAttribute("searchedCity", searchedCity);
    model.addAttribute("weekly", weekly);
    model.addAttribute("errorMsg", errorMsg);
    model.addAttribute("useLocation", isLocation);
    return "dashboard";
    }
}

