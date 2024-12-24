package com.springbootfinal.app.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootfinal.app.domain.LongWeatherDto;
import com.springbootfinal.app.domain.LongWeatherTemperatureDto;
import com.springbootfinal.app.mapper.LongWeatherMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Service
@Slf4j
public class LongWeatherService {


    private final ObjectMapper objectMapper;
    @Autowired
    private LongWeatherMapper longWeatherMapper;
	
	/*@Value("${apiKey}")
	private String apiKey;*/

    private String APIKEY = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";
//    private String APIKEY = "Gow/B+pvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT+fYSDLtu0o9k6WY+Rp7E00ZA==";

    private final RestTemplate restTemplate;

    public LongWeatherService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    // 중기 육상 예보
    private static final String API_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";
    // 중기 기온 예보
    private static final String API_URL2 = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";
    // 중기 해상 예보
    private static final String API_URL3 = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidSeaFcst";


    // 재시도 로직
    @SneakyThrows
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )

    /**
     * 중기 육상 예보
     * @param regId
     * @param tmFc
     * @return
     **/
    public LongWeatherDto getLongWeatherForecast(String regId, String tmFc) throws IOException {
        URI url = UriComponentsBuilder.fromUriString("http://apis.data.go.kr")
                .path("/1360000/MidFcstInfoService/getMidLandFcst")
                .queryParam("serviceKey", APIKEY)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("regId", regId)
                .queryParam("tmFc", tmFc)
                .build(true) // true로 설정하면 자동으로 인코딩됨
                .toUri();
        log.info("중기 육상 API URL: {}", url);

        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json"); // JSON 요청 명시
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("API 호출 실패: 상태 코드 " + response.getStatusCode());
            }
            String responseBody = response.getBody();
            log.info("API 응답 데이터: {}", responseBody);
            headers = new HttpHeaders();
            headers.set("Accept", "application/json"); // JSON 응답을 명시적으로 요청

            // JSON 응답 확인 및 파싱
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if (responseBody.trim().startsWith("<")) {
                log.error("XML 응답이 반환되었습니다. JSON으로 변환할 수 없습니다.");
                throw new RuntimeException("JSON 응답이 아님");
            }

            return mapper.readValue(responseBody, LongWeatherDto.class);

        } catch (Exception e) {
            log.error("API 호출 실패", e);
            throw new RuntimeException("API 호출 실패: " + e.getMessage(), e);
        }
    }

    /* *
     * 중기 기온 예보
     * @param regId
     * @param tmFc
     * @return
     * */
    public LongWeatherTemperatureDto getLongWeatherTemperature(String regId, String tmFc) throws IOException {
        URI url = UriComponentsBuilder.fromUriString(API_URL2)
                .queryParam("serviceKey", APIKEY)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("regId", regId)
                .queryParam("tmFc", tmFc)
                .build(true)
                .toUri();

        log.info("중기 기온 API URL: {}", url);

        ResponseEntity<String> response;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<?> entity = new HttpEntity<>(headers);

            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response == null || !response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("API 호출 실패: 응답이 null이거나 상태 코드가 성공적이지 않습니다.");
            }

            String responseBody = response.getBody();
            log.info("API 응답 데이터: {}", responseBody);

            if (responseBody == null) {
                log.error("API 응답 본문이 null입니다.");
                throw new RuntimeException("API 호출 실패: 응답 본문이 null입니다.");
            }

            if (responseBody.trim().startsWith("<")) {
                log.error("XML 응답이 반환되었습니다. JSON으로 변환할 수 없습니다.");
                throw new RuntimeException("JSON 응답이 아님");
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(responseBody, LongWeatherTemperatureDto.class);

        } catch (Exception e) {
            log.error("API 호출 실패", e);
            throw new RuntimeException("API 호출 실패: " + e.getMessage(), e);
        }
    }

}
