package com.springbootfinal.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springbootfinal.app.domain.WeatherResponse;
import com.springbootfinal.app.service.WeatherService;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

   /* @GetMapping("/fetch")
    public WeatherResponse fetchWeatherData(
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("nx") int nx,
            @RequestParam("ny") int ny) throws JAXBException {
        return weatherService.getWeatherData(date, time, nx, ny);
    } */
    
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchWeatherData(
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("nx") int nx,
            @RequestParam("ny") int ny) {
        try {
            WeatherResponse weatherData = weatherService.getWeatherData(date, time, nx, ny);
            return ResponseEntity.ok(weatherData);
        } catch (JAXBException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error parsing weather data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
    

    @PostMapping("/save")
    public String saveWeatherData(
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("nx") int nx,
            @RequestParam("ny") int ny) throws JAXBException {
        WeatherResponse weatherResponse = weatherService.getWeatherData(date, time, nx, ny);
        weatherService.saveWeatherData(weatherResponse);
        return "Weather data saved successfully!";
    }
}
