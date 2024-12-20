package com.springbootfinal.app.service;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.springbootfinal.app.domain.LongWeatherDto;
import com.springbootfinal.app.mapper.LongWeatherMapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LongWeatherService {
	
    //private String serviceKey;
	@Autowired
    private LongWeatherMapper longWeatherMapper;
	@Value("${apiKey}")
	private String ApiKey;
    private final RestTemplate restTemplate;

    public LongWeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private static final String API_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";

    

    public LongWeatherDto getLongWeather(String regId, String tmFc) {
        // 외부 API 요청 URL 생성
        String url = String.format("%s?serviceKey=%s&numOfRows=10&pageNo=1&regId=%s&tmFc=%s", 
                API_URL, ApiKey, regId, tmFc);

        // API 요청을 통해 응답을 문자열로 받기
        String response = restTemplate.getForObject(url, String.class);

        // XML 응답을 DTO로 변환
        return convertXmlToDto(response);
    }

    private LongWeatherDto convertXmlToDto(String xmlResponse) {
        try {
            JAXBContext context = JAXBContext.newInstance(LongWeatherDto.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (LongWeatherDto) unmarshaller.unmarshal(new StringReader(xmlResponse));
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
	
}