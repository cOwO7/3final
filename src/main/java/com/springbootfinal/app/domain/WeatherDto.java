package com.springbootfinal.app.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WeatherDto {
 
    /*String nx;
    String ny;*/
	Integer nx;
	Integer ny;
    String baseDate;
    String baseTime;
    String regld;
    String tmFc;
 
}
