package com.springbootfinal.app.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;

import com.springbootfinal.app.domain.LongWeatherDto;

@Mapper
public interface LongWeatherMapper {
	
	List<LongWeatherDto> getLongWeatherData(String regId, String tmFc);
}
