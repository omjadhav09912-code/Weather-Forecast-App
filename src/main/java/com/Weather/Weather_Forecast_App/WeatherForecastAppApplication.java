
package com.Weather.Weather_Forecast_App;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class WeatherForecastAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherForecastAppApplication.class, args);
	}

}


