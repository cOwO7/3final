package com.springbootfinal.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springbootfinal.app.domain.WeatherResponse;
import com.springbootfinal.app.service.WeatherService;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }
    // 테스트.ver2 
    @GetMapping("/api/weather")
    public String getWeather(
        @RequestParam(name = "date") String date,
        @RequestParam(name = "baseTime") String baseTime,
        @RequestParam(name = "nx") String nx,
        @RequestParam(name = "ny") String ny) {
        return weatherService.getWeatherData(date, baseTime, nx, ny);
    }
    
    // 원본
   /* @GetMapping("/api/weather")
    public String getWeather(@RequestParam(name = "date") String date) {
        return weatherService.getWeatherData(date);
    }*/
}