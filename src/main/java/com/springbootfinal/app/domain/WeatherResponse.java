package com.springbootfinal.app.domain;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "OpenAPI_ServiceResponse")
public class WeatherResponse {

	private Header header;
    private Body body;

    @Data
    @NoArgsConstructor
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD) // 필드 기반 접근 방식 설정
    public static class Body {
        @XmlElementWrapper(name = "items")  // items 요소로 감싸서 매핑
        @XmlElement(name = "item")  // 각 항목을 item 요소로 매핑
        private List<Item> items;
    }

    @Data
    @NoArgsConstructor
    public static class Item {
        @XmlElement(name = "baseDate") // 'baseDate' 요소와 매핑
        private String baseDate;

        @XmlElement(name = "baseTime") // 'baseTime' 요소와 매핑
        private String baseTime;

        @XmlElement(name = "category") // 'category' 요소와 매핑
        private String category;

        @XmlElement(name = "nx") // 'nx' 요소와 매핑
        private int nx;

        @XmlElement(name = "ny") // 'ny' 요소와 매핑
        private int ny;

        @XmlElement(name = "obsrValue") // 'obsrValue' 요소와 매핑
        private String obsrValue;
    }
}

