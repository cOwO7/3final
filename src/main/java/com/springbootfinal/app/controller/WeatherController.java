package com.springbootfinal.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springbootfinal.app.WeatherParser.WeatherParser;
import com.springbootfinal.app.domain.WeatherData;
import com.springbootfinal.app.domain.WeatherResponse;
import com.springbootfinal.app.service.WeatherService;

import org.springframework.ui.Model;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/weather")
@Slf4j
public class WeatherController {
    
	private final WeatherService weatherService;  // weatherService 주입
    private final WeatherParser weatherParser;    // weatherParser 주입

    
    
    
    // 생성자에서 의존성 주입
    @GetMapping("/weather")
    public String getWeather(Model model) {
    	log.debug("Weather API called!");
        String date = "20241116";  // 예시 날짜
        String time = "0600";  // 예시 시간
        int nx = 60;  // 예시 x 좌표
        int ny = 127;  // 예시 y 좌표

        // 날씨 데이터를 가져옵니다.
        WeatherResponse weatherResponse = weatherService.getWeatherData(date, time, nx, ny);

        // 날씨 데이터를 DB에 저장합니다.
        weatherService.saveWeatherData(weatherResponse);

        // WeatherResponse.Item을 WeatherData로 변환하여 리스트에 저장
        List<WeatherData> weatherDataList = weatherResponse.getBody().getItems().stream()
            .map(item -> {
                WeatherData data = new WeatherData();
                data.setCategory(item.getCategory()); // WeatherResponse.Item의 category를 WeatherData에 설정
                data.setFcstValue(item.getObsrValue()); // WeatherResponse.Item의 obsrValue를 WeatherData에 설정
                data.setFcstDate(item.getBaseDate()); // WeatherResponse.Item의 baseDate를 WeatherData에 설정
                data.setFcstTime(item.getBaseTime()); // WeatherResponse.Item의 baseTime을 WeatherData에 설정
                return data;
            })
            .collect(Collectors.toList());
        
        log.info("날씨 데이터: {}", weatherDataList);
        // 모델에 날씨 데이터를 추가
        model.addAttribute("weatherData", weatherDataList);

        // weather.html을 호출
        return "weather";
    }

    // API 호출 메서드
    public String callWeatherApi() {
        // weatherService를 사용하여 API 호출 처리
    	String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        return weatherService.getWeatherData(apiUrl); 
    }

    // 날씨 데이터 파싱 메서드
    public List<WeatherData> parseWeatherData(String apiResponse) {
        return weatherParser.parseWeatherData(apiResponse);  // weatherParser를 사용하여 파싱
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchWeatherData(
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("nx") int nx,
            @RequestParam("ny") int ny) {
    	try {
    	    WeatherResponse weatherData = weatherService.getWeatherData(date, time, nx, ny);
    	    return ResponseEntity.ok(weatherData);
    	} catch (Exception e) {  // JAXBException 대신 일반 Exception을 처리
    	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    	            .body("Error fetching or parsing weather data: " + e.getMessage());
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
