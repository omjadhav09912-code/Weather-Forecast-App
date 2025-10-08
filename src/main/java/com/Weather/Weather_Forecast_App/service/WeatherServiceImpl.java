package com.Weather.Weather_Forecast_App.service;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.Weather.Weather_Forecast_App.model.CurrentWeather;
import com.Weather.Weather_Forecast_App.model.CurrentWeatherResponse;
import com.Weather.Weather_Forecast_App.model.DailyForecast;
import com.Weather.Weather_Forecast_App.model.WeatherData;
import com.Weather.Weather_Forecast_App.model.WeeklyWeatherResponse;

@Service
public class WeatherServiceImpl implements WeatherService {
    public String getApiKey() {
        return apiKey;
    }
    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private static final String API_URL_BY_CITY = "https://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}&units=metric";

    

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WeatherData getWeatherDataByCity(String city, boolean weekly) {
        try {
            String rawCurrentResponse = restTemplate.getForObject(API_URL_BY_CITY, String.class, city, apiKey);
            logger.info("Raw current weather API response for city {}: {}", city, rawCurrentResponse);
            CurrentWeatherResponse currentResponse = restTemplate.getForObject(API_URL_BY_CITY, CurrentWeatherResponse.class, city, apiKey);
            if (currentResponse == null || currentResponse.getMain() == null || currentResponse.getWeather() == null) {
                logger.error("No current weather response for city: {}", city);
                return null;
            }

            WeatherData weatherData = mapCurrentResponseToWeatherData(currentResponse);

            if (weekly && currentResponse.getCoord() != null) {
                Double lat = currentResponse.getCoord().getLat();
                Double lon = currentResponse.getCoord().getLon();
                String weeklyUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
                String rawWeeklyResponse = restTemplate.getForObject(weeklyUrl, String.class);
                logger.info("Raw weekly forecast API response for city {} (lat: {}, lon: {}): {}", city, lat, lon, rawWeeklyResponse);
                WeeklyWeatherResponse weeklyResponse = restTemplate.getForObject(weeklyUrl, WeeklyWeatherResponse.class);
                if (weeklyResponse != null && weeklyResponse.getList() != null) {
                    weatherData.setWeekly(mapForecastResponseToDaily(weeklyResponse));
                } else {
                    logger.error("No weekly weather response for city: {} (lat: {}, lon: {})", city, lat, lon);
                }
            }
            return weatherData;
        } catch (Exception e) {
            logger.error("Error fetching weather data for city: {}", city, e);
            return null;
        }
    }

    @Override
    public WeatherData getWeatherDataByCoordinates(double lat, double lon, boolean weekly) {
        try {
            // Get current weather
            String currentUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
            CurrentWeatherResponse currentResponse = restTemplate.getForObject(currentUrl, CurrentWeatherResponse.class);
            if (currentResponse == null || currentResponse.getMain() == null || currentResponse.getWeather() == null) {
                logger.error("No current weather response for coordinates: lat={}, lon={}", lat, lon);
                return null;
            }
            WeatherData weatherData = mapCurrentResponseToWeatherData(currentResponse);

            // Get weekly forecast using /forecast endpoint
            if (weekly) {
                String weeklyUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
                String rawWeeklyResponse = restTemplate.getForObject(weeklyUrl, String.class);
                logger.info("Raw weekly forecast API response for coordinates (lat: {}, lon: {}): {}", lat, lon, rawWeeklyResponse);
                WeeklyWeatherResponse weeklyResponse = restTemplate.getForObject(weeklyUrl, WeeklyWeatherResponse.class);
                if (weeklyResponse != null && weeklyResponse.getList() != null) {
                    weatherData.setWeekly(mapForecastResponseToDaily(weeklyResponse));
                } else {
                    logger.error("No weekly weather response for coordinates: lat={}, lon={}", lat, lon);
                }
            }
            return weatherData;
        } catch (Exception e) {
            logger.error("Error fetching weather data for coordinates: lat={}, lon={}", lat, lon, e);
            return null;
        }
    }

    private WeatherData mapCurrentResponseToWeatherData(CurrentWeatherResponse resp) {
        WeatherData wd = new WeatherData();
        CurrentWeather cur = new CurrentWeather();
        if (resp.getMain() != null) {
            cur.setTemperature(resp.getMain().getTemp());
            cur.setHumidity(resp.getMain().getHumidity());
            cur.setPressure(resp.getMain().getPressure());
        }
        if (resp.getWeather() != null && resp.getWeather().length > 0) {
            cur.setDescription(resp.getWeather()[0].getDescription());
            cur.setIcon(resp.getWeather()[0].getIcon());
        } else {
            cur.setDescription("No description available");
        }
        if (resp.getWind() != null) {
            cur.setWind(String.valueOf(resp.getWind().getSpeed()));
        }
        if (resp.getSys() != null) {
            if (resp.getSys().getSunrise() > 0) {
                long sunriseEpoch = resp.getSys().getSunrise() * 1000L;
                cur.setSunrise(new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(sunriseEpoch)));
            }
            if (resp.getSys().getSunset() > 0) {
                long sunsetEpoch = resp.getSys().getSunset() * 1000L;
                cur.setSunset(new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(sunsetEpoch)));
            }
        }
        wd.setCurrent(cur);
        return wd;
    }

    // Map /forecast response to daily forecasts (group by date)
    private List<DailyForecast> mapForecastResponseToDaily(WeeklyWeatherResponse resp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Map<String, DailyForecast> dailyMap = new java.util.LinkedHashMap<>();
        for (WeeklyWeatherResponse.ForecastItem item : resp.getList()) {
            String date = sdf.format(new java.util.Date(item.getDt() * 1000L));
            DailyForecast forecast = dailyMap.getOrDefault(date, new DailyForecast());
            forecast.setDate(date);
            if (item.getMain() != null) {
                double min = item.getMain().getTemp_min();
                double max = item.getMain().getTemp_max();
                if (forecast.getMin() == 0 || min < forecast.getMin()) {
                    forecast.setMin(min);
                }
                if (forecast.getMax() == 0 || max > forecast.getMax()) {
                    forecast.setMax(max);
                }
            }
            if (item.getWeather() != null && item.getWeather().length > 0) {
                forecast.setDescription(item.getWeather()[0].getDescription());
                forecast.setIcon(item.getWeather()[0].getIcon());
            }
            dailyMap.put(date, forecast);
        }
        return new java.util.ArrayList<>(dailyMap.values());
    }

    // Define inner static classes or separate classes for:
    // CurrentWeatherResponse, WeeklyWeatherResponse to match API JSON structure
}
