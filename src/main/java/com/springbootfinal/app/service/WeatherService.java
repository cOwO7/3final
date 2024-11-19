package com.springbootfinal.app.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.springbootfinal.app.WeatherParser.WeatherParser;
import com.springbootfinal.app.domain.WeatherResponse;
import com.springbootfinal.app.mapper.WeatherMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅을 위한 Lombok 어노테이션. 로그 출력을 간편하게 처리.
@Service // Spring Service 어노테이션, 이 클래스가 서비스 계층의 컴포넌트임을 나타냄.
public class WeatherService {

	// DB와 연동하기 위한 Mapper, REST API 호출을 위한 RestTemplate, JSON/XML 데이터 파싱을 위한
	// WeatherParser
	private final WeatherMapper weatherMapper;
	private final RestTemplate restTemplate;
	private final WeatherParser weatherParser;
	
	// 기상청 API 호출에 필요한 URL과 API 키
	private static final String API_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
	private static final String API_KEY = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";

	// 생성자에서 필드 초기화
	public WeatherService(WeatherMapper weatherMapper, RestTemplate restTemplate, WeatherParser weatherParser) {
        this.weatherMapper = weatherMapper;
        this.restTemplate = restTemplate;
        this.weatherParser = weatherParser; // Spring이 주입
    }

    
	// **특정 날짜, 시간, 좌표의 날씨 데이터를 가져오는 메서드**
    public WeatherResponse getWeatherData(String baseDate, String baseTime, int nx, int ny, int numOfRows, int pageNo) {
        String url = String.format(
                "%s?serviceKey=%s&base_date=%s&base_time=%s&nx=%d&ny=%d&numOfRows=%d&pageNo=%d&dataType=JSON",
                API_URL, API_KEY, baseDate, baseTime, nx, ny, numOfRows, pageNo
        );

        ResponseEntity<WeatherResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }
	

}
