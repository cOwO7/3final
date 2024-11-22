
package com.springbootfinal.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootfinal.app.domain.WeatherDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WeatherService {
 
    @Value("${apiUrl}")
    private String apiUrl;
    
    @Value("${apiKey}")
    private String apiKey;
 
    /**
     * 초단기예보조회
     * @param weatherDto
     * @return
     * @throws IOException
     */
    /*public String getWeather(WeatherDto weatherDto) throws IOException {
        
        UriComponents uriBuilder = UriComponentsBuilder
                .fromHttpUrl(apiUrl)                                // api url
                .queryParam("serviceKey", apiKey)                   // 인증키
                .queryParam("dataType", "JSON")                     // 응답자료형식 (XML or JSON)
                .queryParam("numOfRows", 60)                        // 한 페이지 결과 수
                .queryParam("pageNo", 1)                            // 페이지 번호
                .queryParam("base_date", weatherDto.getBaseDate())  // 발표일자
                .queryParam("base_time", weatherDto.getBaseTime())  // 발표시간 (매시간 30분, API 제공은 매시간 45분 이후)
                .queryParam("nx", weatherDto.getNx())               // 예보지점 X 좌표
                .queryParam("ny", weatherDto.getNy())               // 예보지점 Y 좌표
                .build();
        
        System.out.println("api 요청 URL : " + uriBuilder.toUriString());
 
        URL url                 = new URL(uriBuilder.toUriString());
        HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        
        System.out.println("Response code: " + conn.getResponseCode());
        
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        
        StringBuilder sb = new StringBuilder();
        
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
 
        String result = sb.toString();
        
        System.out.println("result : " + result);
        
        return result;
    }*/
    
    public Map<String, Map<String, String>> getWeatherGroupedByTime(WeatherDto weatherDto) throws IOException {
        UriComponents uriBuilder = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("dataType", "JSON")
                .queryParam("numOfRows", 60)
                .queryParam("pageNo", 1)
                .queryParam("base_date", weatherDto.getBaseDate())
                .queryParam("base_time", weatherDto.getBaseTime())
                .queryParam("nx", weatherDto.getNx())
                .queryParam("ny", weatherDto.getNy())
                .build();

        URL url = new URL(uriBuilder.toUriString());
        URI uri = uriBuilder.toUri();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd = new BufferedReader(new InputStreamReader(
                conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300 
                ? conn.getInputStream() : conn.getErrorStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        // JSON 응답 파싱 및 데이터 가공
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(sb.toString());
        JsonNode items = root.path("response").path("body").path("items").path("item");

        Map<String, Map<String, String>> groupedData = new HashMap<>();
        
        // 시간별 데이터 그룹화
        for (JsonNode item : items) {
            String timeKey = item.get("fcstDate").asText() + item.get("fcstTime").asText(); // 날짜+시간 키
            String category = item.get("category").asText();
            String value = item.get("fcstValue").asText();

            groupedData
                .computeIfAbsent(timeKey, k -> new HashMap<>())
                .put(category, value);
        }

        return groupedData;
    }
    
    
}