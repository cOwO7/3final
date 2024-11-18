package com.springbootfinal.app.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class WeatherData {
    private String category;  // 예: "TMP" (온도), "POP" (강수확률)
    private String fcstValue; // 예: "15" (온도 값)
    private String fcstDate;  // 예: "20241116" (예보 날짜)
    private String fcstTime;  // 예: "0600" (예보 시간)
    
    private String tmp;   // 기온
    private String pop;   // 강수확률
    private String wsd;   // 풍속
}
