package com.springbootfinal.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springbootfinal.app.service.WeatherService;


@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public String getWeatherData(
    		@RequestParam(value = "baseDate", required = false, defaultValue = "20241119") String baseDate,
            @RequestParam(value = "baseTime", required = false, defaultValue = "0600") String baseTime,
            @RequestParam(value = "nx", required = false, defaultValue = "55") int nx,
            @RequestParam(value = "ny", required = false, defaultValue = "127") int ny) {
        return weatherService.getWeatherData(baseDate, baseTime, nx, ny);
    }
}
