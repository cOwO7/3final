package com.springbootfinal.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.springbootfinal.app.domain.WeatherData;

@Mapper
public interface WeatherMapper {
    void insertWeatherData(WeatherData data);

    List<WeatherData> selectWeatherData(@Param("baseDate") String baseDate,
                                        @Param("baseTime") String baseTime,
                                        @Param("nx") int nx,
                                        @Param("ny") int ny);
}