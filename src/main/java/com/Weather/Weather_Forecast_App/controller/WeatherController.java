package com.Weather.Weather_Forecast_App.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.Weather.Weather_Forecast_App.model.WeatherData;
import com.Weather.Weather_Forecast_App.service.WeatherService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/weather")
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

        // Authentication check
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        WeatherData weatherLocation = null;
        WeatherData weatherCity = null;
        String errorMsgLocation = null;
        String errorMsgCity = null;

        // Determine active tab
        boolean isLocation = true; // default: Current Location tab
        if ("false".equals(useLocation) || (city != null && !city.isEmpty())) {
            isLocation = false;
        }

        String searchedCity = city != null ? city : "";

        try {
            // Current Location Weather (if tab active or first load)
            if (isLocation) {
                String ip = request.getRemoteAddr();
                String detectedCity;
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    String geoUrl = "https://ipapi.co/" + ip + "/city/";
                    detectedCity = restTemplate.getForObject(geoUrl, String.class);
                    if (detectedCity == null || detectedCity.trim().isEmpty() || "Undefined".equalsIgnoreCase(detectedCity)) {
                        detectedCity = "Pune";
                    }
                } catch (Exception e) {
                    detectedCity = "Pune";
                }

                double lat = 18.5204, lon = 73.8567;
                boolean geoSuccess = false;
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    String geoApiUrl = "https://api.openweathermap.org/geo/1.0/direct?q=" + detectedCity + "&limit=1&appid=" + weatherService.getApiKey();
                    List<?> geoResult = restTemplate.getForObject(geoApiUrl, List.class);
                    if (geoResult != null && !geoResult.isEmpty()) {
                        Map<?, ?> geoMap = (Map<?, ?>) geoResult.get(0);
                        Object latObj = geoMap.get("lat");
                        Object lonObj = geoMap.get("lon");
                        if (latObj != null && lonObj != null) {
                            lat = Double.parseDouble(latObj.toString());
                            lon = Double.parseDouble(lonObj.toString());
                            geoSuccess = true;
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }

                weatherLocation = weatherService.getWeatherDataByCoordinates(lat, lon, true);
                searchedCity = detectedCity;
                if (!geoSuccess || weatherLocation == null) {
                    errorMsgLocation = "Could not determine weather for your current location (" + detectedCity + ").";
                }
            }

            // Search by City
            if (city != null && !city.isEmpty()) {
                weatherCity = weatherService.getWeatherDataByCity(city, weekly);
                if (weatherCity == null) {
                    errorMsgCity = "Weather data could not be retrieved for '" + city + "'.";
                }
            }

        } catch (Exception ex) {
            if (isLocation) {
                errorMsgLocation = "An unexpected error occurred: " + ex.getMessage();
            } else {
                errorMsgCity = "An unexpected error occurred: " + ex.getMessage();
            }
        }

        // Add attributes for Thymeleaf
        model.addAttribute("weatherLocation", weatherLocation);
        model.addAttribute("errorMsgLocation", errorMsgLocation);
        model.addAttribute("weatherCity", weatherCity);
        model.addAttribute("errorMsgCity", errorMsgCity);
        model.addAttribute("searchedCity", searchedCity);
        model.addAttribute("weekly", weekly);
        model.addAttribute("useLocation", isLocation);

        return "dashboard";
    }
}
