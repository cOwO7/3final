package com.springbootfinal.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.springbootfinal.app.domain.WeatherCoordinate;

@Mapper
public interface WeatherCoordinateMapper {

	// 도시와 구/시 이름으로 좌표를 조회하는 메서드 (XML에서 매핑된 쿼리 호출)
    WeatherCoordinate getCoordinatesByAreaName(
    		@Param("areaName") String areaName);
	
	WeatherCoordinate findByKorCode(String korCode);

	List<WeatherCoordinate> findAll();

	WeatherCoordinate getCoordinatesByKorCode(String korCode);
}
