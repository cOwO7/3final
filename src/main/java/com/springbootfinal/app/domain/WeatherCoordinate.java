package com.springbootfinal.app.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherCoordinate {
	
	private String korCode;  // 고유 코드 (Primary Key)
    private String areaName; // 지역명
    private int gridX;       // 격자 X 좌표
    private int gridY;       // 격자 Y 좌표
    private double longitude; // 경도
    private double latitude;  // 위도
}
