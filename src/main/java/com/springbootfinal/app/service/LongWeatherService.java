package com.springbootfinal.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootfinal.app.domain.LongWeatherDto;
import com.springbootfinal.app.mapper.LongWeatherMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LongWeatherService {
	
    
	@Autowired
    private LongWeatherMapper longWeatherMapper;
	
	/*@Value("${apiKey}")
	private String apiKey;*/
	
	private String APIKEY = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";
	
    private final RestTemplate restTemplate;

    public LongWeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    //private static final String API_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService";
    private static final String API_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";
    
    public LongWeatherDto getLongWeatherForecast(String regId, String tmFc) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", APIKEY)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("regId", regId)
                .queryParam("tmFc", tmFc)
                .toUriString();

        log.info("API 호출 URL: {}", url);
        log.info("API 호출 Key: {}", APIKEY);

        try {
            // API 호출
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            log.info("API Response Status: {}", response.getStatusCode());
            log.info("API Response Body: {}", response.getBody());

            // HTTP 응답 상태 체크
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("API 호출 실패: HTTP 상태 코드 {}", response.getStatusCode());
                throw new RuntimeException("API 호출 실패: 상태 코드 " + response.getStatusCode());
            }

            // 응답이 JSON 형식인지 확인
            if (response.getBody() == null || !response.getBody().trim().startsWith("{")) {
                log.error("응답 데이터가 JSON 형식이 아님. Body: {}", response.getBody());
                throw new RuntimeException("응답 데이터가 JSON 형식이 아닙니다.");
            }

            // JSON -> DTO 매핑
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            LongWeatherDto dto = mapper.readValue(response.getBody(), LongWeatherDto.class);

            if (dto == null || dto.getResponse() == null || dto.getResponse().getBody() == null) {
                log.error("DTO 매핑 실패: 응답 데이터가 누락되었습니다.");
                log.error("Parsed DTO: {}", dto);
                throw new RuntimeException("DTO 매핑 실패: 응답 데이터가 올바르지 않습니다.");
            }

            log.info("Parsed DTO: {}", dto);
            return dto;

        } catch (RuntimeException e) {
            log.error("런타임 예외 발생: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("API 호출 또는 JSON 파싱 중 오류 발생", e);
            throw new RuntimeException("API 호출 실패 또는 JSON 파싱 오류", e);
        }
    }


}