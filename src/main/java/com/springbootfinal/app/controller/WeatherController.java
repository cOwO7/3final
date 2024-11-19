package com.springbootfinal.app.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

	//private final WeatherService weatherService;
	private final WeatherParser weatherParser;
	private final WeatherService weatherService;

	
	@Autowired
	public WeatherController(WeatherService weatherService) {
        this.weatherParser = new WeatherParser();
		this.weatherService = weatherService;
    }
	
	
	private String buildApiUrl(String baseDate, String baseTime, int nx, int ny) {
        String baseUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String serviceKey = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";
        int numOfRows = 10;
        int pageNo = 1;
        String dataType = "JSON";

        return String.format(
                "%s?serviceKey=%s&numOfRows=%d&pageNo=%d&dataType=%s&base_date=%s&base_time=%s&nx=%d&ny=%d",
                baseUrl, serviceKey, numOfRows, pageNo, dataType, baseDate, baseTime, nx, ny
        );
    }

	// 날짜 계산 메서드
	private String getCurrentBaseDate() {
		// 현재 날짜를 yyyyMMdd 형식으로 반환
		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	private String getCurrentBaseTime() {
		// 현재 시간 기준으로 가장 최근 정시를 반환
		LocalTime now = LocalTime.now();
		int hour = now.getHour();

		// 3시간 단위로 시간 계산 (예: 00, 03, 06 ...)
		int baseHour = (hour / 3) * 3;

		// 정시 시간을 HHmm 형식으로 반환
		return String.format("%02d00", baseHour);
	}

	// 컨트롤러 메서드
	@GetMapping("/data")
    public ResponseEntity<?> getWeatherData(
            @RequestParam(required = false) String baseDate,
            @RequestParam(required = false) String baseTime,
            @RequestParam(defaultValue = "55") int nx,
            @RequestParam(defaultValue = "127") int ny,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "1") int pageNo) {

        // 기본값 처리
        if (baseDate == null || baseDate.isEmpty()) {
            baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        if (baseTime == null || baseTime.isEmpty()) {
            baseTime = String.format("%02d00", LocalTime.now().getHour() / 3 * 3);
        }

        try {
            WeatherResponse weatherResponse = weatherService.getWeatherData(baseDate, baseTime, nx, ny, numOfRows, pageNo);
            List<WeatherData> weatherDataList = weatherResponse.toWeatherDataList();
            return ResponseEntity.ok(weatherDataList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("날씨 데이터를 불러오는 데 실패했습니다.");
        }
    }
}
