package com.springbootfinal.app.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    public String index() {
        return "index";
    }
 
    /**
     * 초단기예보조회
     * @param weatherDto
     * @return
     * @throws IOException
     */
   /* @PostMapping(value = "/getWeather")
    @ResponseBody
    public ResponseEntity getWeather(@RequestBody WeatherDto weatherDto) throws IOException {
        ResultDto result = new ResultDto();
 
        result = ResultDto.builder()
                .resultCode("SUCCESS")
                .message("조회가 완료되었습니다.")
                .resultData(weatherService.getWeather(weatherDto))
                .url(null)
                .build();
        
        return new ResponseEntity(result, HttpStatus.OK);
    }*/
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
