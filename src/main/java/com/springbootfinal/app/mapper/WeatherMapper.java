package com.springbootfinal.app.mapper;

import com.springbootfinal.app.domain.WeatherResponse.Item;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WeatherMapper {
    void insertWeatherData(Item item);
}
