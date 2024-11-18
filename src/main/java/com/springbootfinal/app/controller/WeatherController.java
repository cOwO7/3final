package com.springbootfinal.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springbootfinal.app.WeatherParser.WeatherParser;
import com.springbootfinal.app.domain.WeatherData;
import com.springbootfinal.app.domain.WeatherResponse;
import com.springbootfinal.app.service.WeatherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/weather")
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherParser weatherParser;

    private String buildApiUrl(String location) {
        String baseUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String apiKey = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";
        String format = "json";
        return String.format("%s?location=%s&apiKey=%s&format=%s", baseUrl, location, apiKey, format);
    }

    @GetMapping("/data")
    public String getWeatherData(@RequestParam(required = false) String location, Model model) {
        if (location == null || location.isEmpty()) {
            location = "Seoul"; // 기본 위치 설정
        }

        String apiUrl = buildApiUrl(location);
        try {
            List<WeatherData> weatherData = weatherService.getWeatherData(apiUrl);
            model.addAttribute("weatherData", weatherData);
        } catch (Exception e) {
            log.error("날씨 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("weatherData", null);
            model.addAttribute("error", "날씨 데이터를 불러오는 데 실패했습니다.");
        }
        return "weather";
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchWeatherData(@RequestParam("date") String date, 
                                              @RequestParam("time") String time,
                                              @RequestParam("nx") int nx, 
                                              @RequestParam("ny") int ny) {
        try {
            WeatherResponse weatherData = weatherService.getWeatherData(date, time, nx, ny);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            log.error("날씨 데이터 호출 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching weather data: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public String saveWeatherData(@RequestParam("date") String date, 
                                  @RequestParam("time") String time,
                                  @RequestParam("nx") int nx, 
                                  @RequestParam("ny") int ny) {
        try {
            WeatherResponse weatherResponse = weatherService.getWeatherData(date, time, nx, ny);
            weatherService.saveWeatherData(weatherResponse);
            return "Weather data saved successfully!";
        } catch (Exception e) {
            log.error("날씨 데이터 저장 중 오류 발생: {}", e.getMessage(), e);
            return "Failed to save weather data.";
        }
    }
}

