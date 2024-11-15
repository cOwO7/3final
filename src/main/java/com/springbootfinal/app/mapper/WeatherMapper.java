package com.springbootfinal.app.mapper;

import com.springbootfinal.app.domain.WeatherResponse.Body.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WeatherMapper {

    @Insert("INSERT INTO weather_data (base_date, base_time, category, nx, ny, obsr_value) " +
            "VALUES (#{baseDate}, #{baseTime}, #{category}, #{nx}, #{ny}, #{obsrValue})")
    void insertWeatherData(Item item);

    @Select("SELECT base_date, base_time, category, nx, ny, obsr_value " +
            "FROM weather_data WHERE base_date = #{baseDate} " +
            "AND base_time = #{baseTime} AND nx = #{nx} AND ny = #{ny}")
    List<Item> getWeatherDataByDate(String baseDate, String baseTime, int nx, int ny);
}
