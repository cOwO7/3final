
package com.springbootfinal.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
    public String getWeather(WeatherDto weatherDto) throws IOException {
        
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
    }
    
}