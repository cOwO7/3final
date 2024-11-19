package com.springbootfinal.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.springbootfinal.app.domain.WeatherData;

@Mapper
public interface WeatherMapper {
	void insertWeatherData(WeatherData data);

	List<WeatherData> selectWeatherData(
			@Param("baseDate") String baseDate, // 데이터 타입(xml,json)
			@Param("dataType") String dataType, // 발표 날짜
			@Param("baseTime") String baseTime, // 발표 시간
			@Param("pagaNo") String pageNo, // 페이지 수 
			@Param("numOfRows") int numOfRows, // 한 페이지 출력 수
			@Param("nx") int nx, // 예보지점 x좌표 
			@Param("ny") int ny);// 예보지점 y좌표
}