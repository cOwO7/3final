package com.springbootfinal.app.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class WeatherData {
    private String category;  // 자료구분코드 (예: TMP, POP 등)
    private String fcstValue; // 예보 값 (실황 데이터)
    private String fcstDate;  // 예보 날짜 (YYYYMMDD)
    private String fcstTime;  // 예보 시간 (HHmm)

    private String tmp; // 기온 (온도)
    private String pop; // 강수확률
    private String wsd; // 풍속
}

