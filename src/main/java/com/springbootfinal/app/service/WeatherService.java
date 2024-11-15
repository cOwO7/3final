package com.springbootfinal.app.service;



import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.springbootfinal.app.domain.WeatherResponse;
import com.springbootfinal.app.domain.WeatherResponse.Item;
import com.springbootfinal.app.mapper.WeatherMapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    
    @Autowired
	private WeatherMapper weatherMapper;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.key}")
    private String apiKey;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    // 연동 테스트.ver1
    public void getAndSaveWeatherData(String date, String time, int nx, int ny) throws JAXBException {
        // API URL과 파라미터 설정
        String url = apiUrl + "?serviceKey=" + apiKey + "&base_date=" + date + "&base_time=" + time + "&nx=" + nx + "&ny=" + ny;
        
        RestTemplate restTemplate = new RestTemplate();
        // API 호출 후 응답 받기
        String response = restTemplate.getForObject(url, String.class);
        
        // JAXB를 사용하여 XML을 Java 객체로 변환
        JAXBContext context = JAXBContext.newInstance(WeatherResponse.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        
        // XML 응답을 WeatherResponse 객체로 변환
        WeatherResponse weatherResponse = (WeatherResponse) unmarshaller.unmarshal(new StringReader(response));

        // 각 아이템을 데이터베이스에 저장
        for (WeatherResponse.Item item : weatherResponse.getBody().getItems()) {
            weatherMapper.insertWeatherData(item);
        }
    }
    
    // 테스트.ver2
    public String getWeatherData(String date, String baseTime, String nx, String ny) {
        String apiKey = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";
      
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?"
        	    + "serviceKey=" + apiKey
        	    + "&base_date=" + date
        	    + "&base_time=" + baseTime
        	    + "&nx=" + nx
        	    + "&ny=" + ny
        	    + "&numOfRows=10&pageNo=1&_type=json";

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }
    
    // 테스트
   /* public String getWeatherData() {
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0";
        String apiKey = "Gow/B+pvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT+fYSDLtu0o9k6WY+Rp7E00ZA==";
        String date = "20241114";
        log.info("여기서 실패함");
        String uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("date", date)
                .build()
                .toUriString();

        try {
            String response = restTemplate.getForObject(uri, String.class);
            return response;
        } catch (HttpServerErrorException e) {
            log.error("API 요청 실패: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
        return null;
    }*/
    
    
    // 원본
   /* public String getWeatherData(String date) {
        String apiKey = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0?serviceKey=" 
        + apiKey + "&date=" + date;

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }*/

  /*  public String getWeatherData(String date) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("serviceKey", apiKey)  // 인코딩된 API 키를 여기에 추가
                .queryParam("date", date)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }*/
}
