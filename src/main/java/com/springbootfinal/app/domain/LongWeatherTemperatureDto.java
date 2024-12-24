package com.springbootfinal.app.domain;

import lombok.Data;

import java.util.List;

@Data
public class LongWeatherTemperatureDto {
    private Response response;

    @Data
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body {
        private String dataType;
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Data
    public static class Items {
        private List<Item> item;
    }

    @Data
    public static class Item {
        private String regId;       // 예보구역코드
        private String taMin4;      // 4일 후 최저기온
        private String taMax4;      // 4일 후 최고기온
        private String taMin5;      // 5일 후 최저기온
        private String taMax5;      // 5일 후 최고기온
        private String taMin6;      // 6일 후 최저기온
        private String taMax6;      // 6일 후 최고기온
        private String taMin7;      // 7일 후 최저기온
        private String taMax7;      // 7일 후 최고기온
        private String taMin8;      // 8일 후 최저기온
        private String taMax8;      // 8일 후 최고기온
        private String taMin9;      // 9일 후 최저기온
        private String taMax9;      // 9일 후 최고기온
        private String taMin10;     // 10일 후 최저기온
        private String taMax10;     // 10일 후 최고기온
    }
}
