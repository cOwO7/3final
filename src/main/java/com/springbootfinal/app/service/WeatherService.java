package com.springbootfinal.app.service;

import java.io.StringReader;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.springbootfinal.app.domain.WeatherResponse;
import com.springbootfinal.app.mapper.WeatherMapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

	private final WeatherMapper weatherMapper;
	private final RestTemplate restTemplate;

	// API 엔드 포인트 , 키
	private final String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
	private final String apiKey = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";


	// 기상청 데이터 조회
	public WeatherResponse getWeatherData(String date, String time, int nx, int ny) throws JAXBException {
		String url = apiUrl + "?serviceKey=" + apiKey + "&base_date=" + date + "&base_time=" + time + "&nx=" + nx
				+ "&ny=" + ny;

		String response = restTemplate.getForObject(url, String.class);
		System.out.println("API 연결 Response: " + response); // 로그 출력

		JAXBContext context = JAXBContext.newInstance(WeatherResponse.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		return (WeatherResponse) unmarshaller.unmarshal(new StringReader(response));
	}
	
	// 테스트
	// 기상청 데이터 저장
    public void saveWeatherData(WeatherResponse weatherResponse) {
        for (WeatherResponse.Item item : weatherResponse.getBody().getItems()) {
            weatherMapper.insertWeatherData(item);
        }
    }

	
	
	
	// 원본
	/*public void saveWeatherData(WeatherResponse weatherResponse) {
		for (WeatherResponse.Item item : weatherResponse.getBody().getItems()) {
			weatherMapper.insertWeatherData(item);
		}*/
	
}
