package com.springbootfinal.app.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LongWeatherDto {
	
	private String regId;
    private String tmFc;
    private List<WeatherDto> weatherList;
}
