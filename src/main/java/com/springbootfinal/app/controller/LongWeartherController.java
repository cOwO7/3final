package com.springbootfinal.app.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springbootfinal.app.domain.LongWeatherDto;
import com.springbootfinal.app.service.LongWeatherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@RequiredArgsConstructor
@Controller
@Slf4j
public class LongWeartherController {
	
	@Autowired
	private final LongWeatherService longWeatherService;

    public LongWeartherController(LongWeatherService longWeatherService) {
        this.longWeatherService = longWeatherService;
    }

    @GetMapping("/longweather")
    public String getLongWeatherPage() {
        return "weather/longWeather";  // 템플릿 파일 경로 (src/main/resources/templates/views/longweather.html)
    }

    // longweather API (JSON 데이터 반환)
    @GetMapping("/longweather/data")
    @ResponseBody
    public LongWeatherDto getWeatherData(@RequestParam String regId, @RequestParam String tmFc) {
        return longWeatherService.getLongWeather(regId, tmFc);
    }
	
}