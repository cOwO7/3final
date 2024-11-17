package com.springbootfinal.app.service;

import java.io.StringReader;
import java.util.List; // List 타입

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference; // JSON 타입 참조
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootfinal.app.WeatherParser.WeatherParser;
import com.springbootfinal.app.domain.WeatherData;
import com.springbootfinal.app.domain.WeatherResponse;
import com.springbootfinal.app.mapper.WeatherMapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅을 위한 Lombok 어노테이션. 로그 출력을 간편하게 처리.
@Service // Spring Service 어노테이션, 이 클래스가 서비스 계층의 컴포넌트임을 나타냄.
//@RequiredArgsConstructor // Lombok을 이용해 final 필드들을 초기화하는 생성자를 자동으로 생성.
public class WeatherService {

	// 기상청 API 호출에 필요한 URL과 API 키
	private static final String API_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
	private static final String API_KEY = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";

	// DB와 연동하기 위한 Mapper, REST API 호출을 위한 RestTemplate, JSON/XML 데이터 파싱을 위한
	// WeatherParser
	private final WeatherMapper weatherMapper;
	private final RestTemplate restTemplate;
	private final WeatherParser weatherParser;

	// 생성자에서 필드 초기화
    public WeatherService(WeatherMapper weatherMapper, RestTemplate restTemplate, WeatherParser weatherParser) {
        this.weatherMapper = weatherMapper;
        this.restTemplate = restTemplate;
        this.weatherParser = weatherParser;
    }
    public List<WeatherData> getWeatherData(String apiUrl) {
        log.debug("Fetching weather data...");
        try {
            // API 호출
            String response = restTemplate.getForObject(apiUrl, String.class);
            log.info("API 호출 응답: {}", response);

            // JSON 응답을 WeatherData 객체 리스트로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            List<WeatherData> weatherDataList = objectMapper.readValue(
                response,
                new TypeReference<List<WeatherData>>() {}
            );

            return weatherDataList; // 변환된 리스트 반환
        } catch (Exception e) {
            log.error("API 호출 오류: {}", e.getMessage(), e);
            throw new RuntimeException("API 호출 중 오류가 발생했습니다.", e);
        }
    }


   /* public String getWeatherData(String apiUrl) {
    	log.debug("Fetching weather data...");
        try {
            // API 호출
            String response = restTemplate.getForObject(apiUrl, String.class);
            log.info("API 호출 응답: {}", response); 
            return response;  // 응답 리턴
        } catch (Exception e) {
            log.error("API 호출 오류: {}", e.getMessage(), e);
            throw new RuntimeException("API 호출 중 오류가 발생했습니다.", e);
        }
    }*/
   
	// **특정 날짜, 시간, 좌표의 날씨 데이터를 가져오는 메서드**
	public WeatherResponse getWeatherData(String date, String time, int nx, int ny) {
		log.debug("Fetching weather data...");
		// API 요청 URL 생성
		String url = String.format("%s?serviceKey=%s&base_date=%s&base_time=%s&nx=%d&ny=%d", API_URL, API_KEY, date,
				time, nx, ny);
		try {
			// RestTemplate을 이용해 API 호출하고 응답을 문자열로 받음
			String response = restTemplate.getForObject(url, String.class);
			log.info("API 연결 Response: {}", response); // API 응답을 로그로 출력

			// 응답 데이터를 JAXB를 사용하여 WeatherResponse 객체로 변환
			JAXBContext context = JAXBContext.newInstance(WeatherResponse.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (WeatherResponse) unmarshaller.unmarshal(new StringReader(response)); // XML을 객체로 변환
		} catch (JAXBException e) {
			// 예외가 발생하면 로그를 남기고 RuntimeException으로 다시 던짐
			log.error("JAXBException occurred: {}", e.getMessage(), e);
			throw new RuntimeException("XML 데이터를 처리하는 중 오류가 발생했습니다.", e);
		}
	}
	
	// 기상 데이터를 DB에 저장하는 메서드
    public void saveWeatherData(WeatherResponse weatherResponse) {
        // WeatherResponse에서 아이템들을 가져와서 DB에 저장
        weatherResponse.getBody().getItems().forEach(item -> {
            // 예: weatherMapper.insertWeatherData(item);
            log.info("Saving weather data: {}", item);  // 데이터를 저장하는 로직 추가
        });
    }

}
