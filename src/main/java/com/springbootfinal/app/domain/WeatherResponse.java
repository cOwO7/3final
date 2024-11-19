package com.springbootfinal.app.domain;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@XmlRootElement(name = "OpenAPI_ServiceResponse")
@Data
@NoArgsConstructor
public class WeatherResponse {

    private Response response;

    @Data
    @NoArgsConstructor
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    @NoArgsConstructor
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    @NoArgsConstructor
    public static class Body {
        private int totalCount;       // 총 데이터 개수
        private int numOfRows;        // 한 페이지당 데이터 수
        private int pageNo;           // 현재 페이지 번호
        private List<Item> items;     // 데이터 항목
    }

    @Data
    @NoArgsConstructor
    public static class Item {
        private String baseDate;
        private String baseTime;
        private String category;
        private String fcstValue;
    }

    public List<WeatherData> toWeatherDataList() {
        if (response == null || response.getBody() == null || response.getBody().getItems() == null) {
            return Collections.emptyList();
        }
        return response.getBody().getItems().stream()
                .map(item -> {
                    WeatherData data = new WeatherData();
                    data.setFcstDate(item.getBaseDate());
                    data.setFcstTime(item.getBaseTime());
                    data.setCategory(item.getCategory());
                    data.setFcstValue(item.getFcstValue());
                    return data;
                })
                .collect(Collectors.toList());
    }
}
