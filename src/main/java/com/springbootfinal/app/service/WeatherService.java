
package com.springbootfinal.app.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

	private final RestTemplate restTemplate;

	public WeatherService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getCurrentWeatherStatus() {
		// 테스트를 위해 랜덤 상태 값 반환
		String[] statuses = { "맑음", "구름많음", "흐림", "비", "이슬비", "함박눈" };
		int randomIndex = new Random().nextInt(statuses.length);
		return statuses[randomIndex];
	}

	/**
	 * 초단기실황조회
	 * 
	 * @param weatherDto
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> getUltraSrtNcst(WeatherDto weatherDto) throws IOException {
	    UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl + "/getUltraSrtNcst")
	            .queryParam("serviceKey", apiKey)
	            .queryParam("dataType", "JSON")
	            .queryParam("numOfRows", 10) // 실황 데이터는 보통 10개 이하
	            .queryParam("pageNo", 1)
	            .queryParam("base_date", weatherDto.getBaseDate())
	            .queryParam("base_time", weatherDto.getBaseTime())
	            .queryParam("nx", weatherDto.getNx())
	            .queryParam("ny", weatherDto.getNy())
	            .build();

	    ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, null, String.class);

	    log.info("초단기실황 API 호출 URL: {}", uriBuilder.toUriString());
	    log.info("초단기실황 API 응답: {}", response.getBody());
	    
	    if (!response.getStatusCode().is2xxSuccessful()) {
	        throw new IOException("Failed to fetch Ultra Srt Ncst, HTTP Status: " + response.getStatusCode());
	    }

	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode root = mapper.readTree(response.getBody());
	    JsonNode items = root.path("response").path("body").path("items").path("item");

	    Map<String, String> ultraSrtNcstData = new HashMap<>();
	    for (JsonNode item : items) {
	        String category = item.get("category").asText();
	        String value = item.get("obsrValue").asText(); // 실황은 obsrValue 사용
	        
	        log.info("카테고리: {}, 값: {}", category, value);
	        
	        ultraSrtNcstData.put(category, value);
	    }

	    return ultraSrtNcstData;
	}

	
	
	
	
	
	
	

	/**
	 * 초단기예보조회
	 * 
	 * @param weatherDto
	 * @return
	 * @throws IOException
	 */
	public String getWeatherData(String url) {
		// HttpHeaders 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		// HttpEntity 설정
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		// API 호출 및 응답 받기
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		// 응답 내용 로깅
		log.info("API Response: {}", response.getBody());

		// 응답 내용 반환
		return response.getBody();
	}
	
	public Map<String, Map<String, String>> getWeatherGroupedByTime(WeatherDto weatherDto) throws IOException {
	    UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl)
	            .queryParam("serviceKey", apiKey)
	            .queryParam("dataType", "JSON")
	            .queryParam("numOfRows", 60)
	            .queryParam("pageNo", 1)
	            .queryParam("base_date", weatherDto.getBaseDate())
	            .queryParam("base_time", weatherDto.getBaseTime())
	            .queryParam("nx", weatherDto.getNx())
	            .queryParam("ny", weatherDto.getNy())
	            .build();

	    // RestTemplate 사용하여 요청 보내기
	    ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, null, String.class);

	    // 응답 상태 코드 확인
	    if (!response.getStatusCode().is2xxSuccessful()) {
	        throw new IOException("Failed to fetch weather data, HTTP Status: " + response.getStatusCode());
	    }

	    // 응답 내용 로깅 (디버깅을 위한 출력)
	    log.info("API Response: {}", response.getBody());

	    // JSON 응답 파싱
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode root = mapper.readTree(response.getBody());
	    JsonNode items = root.path("response").path("body").path("items").path("item");

	    Map<String, Map<String, String>> groupedData = new HashMap<>();

	    // 시간별 데이터 그룹화
	    for (JsonNode item : items) {
	        String timeKey = item.get("fcstDate").asText() + item.get("fcstTime").asText(); // 날짜+시간 키
	        String category = item.get("category").asText();
	        String value = item.get("fcstValue").asText();

	        groupedData.computeIfAbsent(timeKey, k -> new HashMap<>()).put(category, value);
	    }

	    return groupedData;
	}

	
	/**
	 * 단기예보조회
	 * 
	 * @param weatherDto
	 * @return
	 * @throws IOException
	 */
	// 추가 api호출 
	public Map<String, Map<String, String>> getShortTermForecast(WeatherDto weatherDto) throws IOException {
	    UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl)
	            .queryParam("serviceKey", apiKey)
	            .queryParam("dataType", "JSON")
	            .queryParam("numOfRows", 10)
	            .queryParam("pageNo", 1)
	            .queryParam("base_date", weatherDto.getBaseDate())
	            .queryParam("base_time", weatherDto.getBaseTime())
	            .queryParam("nx", weatherDto.getNx())
	            .queryParam("ny", weatherDto.getNy())
	            .build();

	    ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, null, String.class);

	    // JSON 응답 파싱
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode root = mapper.readTree(response.getBody());
	    JsonNode items = root.path("response").path("body").path("items").path("item");

	    Map<String, Map<String, String>> shortTermData = new HashMap<>();
	    for (JsonNode item : items) {
	        String category = item.get("category").asText();
	        String value = item.get("fcstValue").asText();

	        // 시간대를 키로 그룹화
	        String timeKey = item.get("fcstDate").asText() + item.get("fcstTime").asText();
	        shortTermData.computeIfAbsent(timeKey, k -> new HashMap<>()).put(category, value);
	    }

	    return shortTermData;
	}

	// api 병합
	public Map<String, Map<String, String>> getMergedWeatherData(WeatherDto weatherDto) throws IOException {
	    // 초단기예보 데이터 가져오기
	    Map<String, Map<String, String>> forecastData = getWeatherGroupedByTime(weatherDto);

	    // 단기예보 데이터 가져오기
	    Map<String, Map<String, String>> shortTermData = getShortTermForecast(weatherDto);

	    // 초단기실황 데이터 가져오기
	    Map<String, String> ultraSrtNcstData = getUltraSrtNcst(weatherDto);

	    // 데이터 병합
	    for (String timeKey : shortTermData.keySet()) {
	        forecastData.computeIfAbsent(timeKey, k -> new HashMap<>()).putAll(shortTermData.get(timeKey));
	    }

	    // 초단기실황 데이터는 가장 최신 데이터로 병합
	    forecastData.computeIfAbsent("current", k -> new HashMap<>()).putAll(ultraSrtNcstData);

	    return forecastData;
	}


	
	
	
	
	
	
	

	/*public Map<String, Map<String, String>> getWeatherGroupedByTime(WeatherDto weatherDto) throws IOException {
		UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl)
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
				conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300 ? conn.getInputStream()
						: conn.getErrorStream()));
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

			groupedData.computeIfAbsent(timeKey, k -> new HashMap<>()).put(category, value);
		}

		return groupedData;
	}*/

}