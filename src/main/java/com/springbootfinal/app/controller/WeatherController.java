package com.springbootfinal.app.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springbootfinal.app.domain.ResultDto;
import com.springbootfinal.app.domain.WeatherDto;
import com.springbootfinal.app.service.WeatherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class WeatherController {

	private final WeatherService weatherService;

	@RequestMapping("/api/weather/status")
	public ResponseEntity<String> getWeatherStatus() {
		try {
			// 날씨 상태를 동적으로 가져오는 로직 (예: WeatherService를 통해 API 호출)
			String status = weatherService.getCurrentWeatherStatus();

			// 로그로 상태 확인
			log.info("날씨 상태 호출: {}", status);

			// 상태값 반환
			return ResponseEntity.ok(status);
		} catch (Exception e) {
			log.error("날씨 상태를 가져오는 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
		}
	}

	/**
	 * 메인 페이지
	 * 
	 * @return
	 */
	@RequestMapping("/weather")
	public String index(Model model) {
		// 현재 시각
		LocalDateTime now = LocalDateTime.now();

		// 단기 예보의 Base_time 목록
		int[] shortTermBaseHours = { 2, 5, 8, 11, 14, 17, 20, 23 };

		// 현재 시간 기준으로 가장 가까운 발표 시간을 계산
		int baseTimeHour = getClosestBaseTime(now.getHour(), shortTermBaseHours);

		// 발표 시간에 맞는 날짜와 시간을 계산
		String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String baseTime = String.format("%02d00", baseTimeHour);
		String fullBaseTime = baseDate + " " + baseTime; // baseDate와 baseTime을 합쳐서 전체 base_time 생성
		// Thymeleaf 템플릿에 전달
		model.addAttribute("baseDate", baseDate);
		model.addAttribute("baseTime", baseTime);
		model.addAttribute("fullBaseTime", fullBaseTime); // 전체 base_time을 추가
		return "index"; // index.html
	}

	/**
	 * 현재 시간에 가장 가까운 Base_time을 반환하는 메소드
	 * 
	 * @param currentHour 현재 시각의 시간 (예: 12시)
	 * @param baseHours   Base_time으로 사용할 시간 배열
	 * @return 가장 가까운 Base_time (예: 1100, 1400 등)
	 */
	private int getClosestBaseTime(int currentHour, int[] baseHours) {
		int closestBaseTime = baseHours[0]; // 기본값으로 첫 번째 Base_time을 설정
		for (int i = 0; i < baseHours.length; i++) {
			if (currentHour < baseHours[i]) {
				// 현재 시간이 Base_time 사이에 있으면 더 큰 값을 선택
				if (i > 0 && (currentHour - baseHours[i - 1] <= baseHours[i] - currentHour)) {
					closestBaseTime = baseHours[i - 1];
				} else {
					closestBaseTime = baseHours[i];
				}
				break;
			}
		}
		return closestBaseTime;
	}

	/**
	 * 초단기예보조회
	 * 
	 * @param weatherDto
	 * @return
	 * @throws IOException
	 */
	
	@PostMapping(value = "/getWeather")
	@ResponseBody
	public ResponseEntity<ResultDto> getWeather(@RequestBody WeatherDto weatherDto) throws IOException {
	    try {
	        // 병합된 데이터 가져오기
	        Map<String, Map<String, String>> mergedData = weatherService.getMergedWeatherData(weatherDto);

	        // ResultDto에 데이터 포함
	        ResultDto result = ResultDto.builder()
	                .resultCode("SUCCESS")
	                .message("조회가 완료되었습니다.")
	                .resultData(mergedData)
	                .build();

	        return new ResponseEntity<>(result, HttpStatus.OK);
	    } catch (Exception e) {
	        log.error("Weather data retrieval failed", e);
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	
	
	
	
	
	
	
	
	
	
	/*@PostMapping(value = "/getWeather")
	@ResponseBody
	public ResponseEntity<ResultDto> getWeather(@RequestBody WeatherDto weatherDto) throws IOException {
	    try {
	        // 병합된 데이터 가져오기
	        Map<String, Map<String, String>> mergedData = weatherService.getMergedWeatherData(weatherDto);

	        // ResultDto에 데이터 포함
	        ResultDto result = ResultDto.builder()
	                .resultCode("SUCCESS")
	                .message("조회가 완료되었습니다.")
	                .resultData(mergedData)
	                .build();

	        return new ResponseEntity<>(result, HttpStatus.OK);
	    } catch (Exception e) {
	        log.error("Weather data retrieval failed", e);
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}*/


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*@PostMapping(value = "/getWeather")
	@ResponseBody
	public ResponseEntity<ResultDto> getWeather(@RequestBody WeatherDto weatherDto) throws IOException {
		try {
			// 로그로 요청 데이터 출력
	        log.info("Received weatherDto: {}", weatherDto);
	     // 데이터 검증 (필수 필드 확인)
	        if (weatherDto.getNx() == null || weatherDto.getNy() == null) {
	            return ResponseEntity.badRequest().body(
	                    ResultDto.builder()
	                            .resultCode("ERROR")
	                            .message("nx or ny is missing")
	                            .build());
	        }
			Map<String, Map<String, String>> groupedWeatherData = weatherService.getWeatherGroupedByTime(weatherDto);

			ResultDto result = ResultDto.builder()
					.resultCode("SUCCESS")
					.message("조회가 완료되었습니다.")
					.resultData(groupedWeatherData)
					.url(null)
					.build();

			return new ResponseEntity<>(result, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Weather data retrieval failed", e); // 로그로 예외 메시지 출력
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
					
		}

	}*/
}
