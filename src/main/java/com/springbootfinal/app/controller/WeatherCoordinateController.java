package com.springbootfinal.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springbootfinal.app.domain.WeatherCoordinate;
import com.springbootfinal.app.service.WeatherCoordinateService;

@Controller
@RequestMapping("/weather")
public class WeatherCoordinateController {

	@Autowired
    private WeatherCoordinateService weatherCoordinateService;

	@GetMapping("/coordinates/{korCode}")
	public String getCoordinatesByKorCode(@PathVariable("korCode") String korCode, Model model) {
	    WeatherCoordinate coordinates = weatherCoordinateService.getCoordinatesByKorCode(korCode);
	    model.addAttribute("gridX", coordinates.getGridX());
	    model.addAttribute("gridY", coordinates.getGridY());
	    model.addAttribute("latitude", coordinates.getLatitude());
	    model.addAttribute("longitude", coordinates.getLongitude());
	    return "index";  // 실제 템플릿 이름으로 반환
	}
	@GetMapping("/api/coordinates/{korCode}")
	public WeatherCoordinate getCoordinatesByKorCode(@PathVariable("korCode") String korCode) {
	    return weatherCoordinateService.getCoordinatesByKorCode(korCode);
	}

}
