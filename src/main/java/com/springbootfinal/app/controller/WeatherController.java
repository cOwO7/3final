package com.springbootfinal.app.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springbootfinal.app.domain.ResultDto;
import com.springbootfinal.app.domain.WeatherDto;
import com.springbootfinal.app.service.WeatherService;

import lombok.RequiredArgsConstructor;
 
@RequiredArgsConstructor
@Controller
public class WeatherController {
    
    private final WeatherService weatherService;
 
    /**
     * 메인 페이지
     * @return
     */
    @GetMapping("/weather")
    public String index(Model model) {
        // 현재 시각
        LocalDateTime now = LocalDateTime.now();

        // 단기 예보의 Base_time 목록
        int[] shortTermBaseHours = {2, 5, 8, 11, 14, 17, 20, 23};

        // 현재 시간 기준으로 가장 가까운 발표 시간을 계산
        int baseTimeHour = getClosestBaseTime(now.getHour(), shortTermBaseHours);
        
        // 발표 시간에 맞는 날짜와 시간을 계산
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = String.format("%02d00", baseTimeHour);
        String fullBaseTime = baseDate + " " + baseTime;  // baseDate와 baseTime을 합쳐서 전체 base_time 생성
        // Thymeleaf 템플릿에 전달
        model.addAttribute("baseDate", baseDate);
        model.addAttribute("baseTime", baseTime);
        model.addAttribute("fullBaseTime", fullBaseTime);  // 전체 base_time을 추가
        return "index"; // index.html
    }

    /**
     * 현재 시간에 가장 가까운 Base_time을 반환하는 메소드
     * @param currentHour 현재 시각의 시간 (예: 12시)
     * @param baseHours Base_time으로 사용할 시간 배열
     * @return 가장 가까운 Base_time (예: 1100, 1400 등)
     */
    private int getClosestBaseTime(int currentHour, int[] baseHours) {
        int closestBaseTime = baseHours[0]; // 기본값으로 첫 번째 Base_time을 설정
        for (int i = 0; i < baseHours.length; i++) {
            if (currentHour < baseHours[i]) {
                // 현재 시간이 Base_time 사이에 있으면 더 큰 값을 선택
                if (i > 0 && (currentHour - baseHours[i-1] <= baseHours[i] - currentHour)) {
                    closestBaseTime = baseHours[i-1];
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
     * @param weatherDto
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/getWeather")
    @ResponseBody
    public ResponseEntity<ResultDto> getWeather(@RequestBody WeatherDto weatherDto) throws IOException {
    	Map<String, Map<String, String>> groupedWeatherData = weatherService.getWeatherGroupedByTime(weatherDto);
    	
    	ResultDto result = ResultDto.builder()
    			.resultCode("SUCCESS")
    			.message("조회가 완료되었습니다.")
    			.resultData(groupedWeatherData) // 가공된 데이터 전달
    			.url(null)
    			.build();
    	
    	return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
}
