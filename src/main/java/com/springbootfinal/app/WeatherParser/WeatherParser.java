package com.springbootfinal.app.WeatherParser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.springbootfinal.app.domain.WeatherData;

@Component
public class WeatherParser {
    public List<WeatherData> parseWeatherData(String jsonData) {
        List<WeatherData> weatherList = new ArrayList<>();

        JSONObject response = new JSONObject(jsonData);
        JSONArray items = response.getJSONObject("response")
                                   .getJSONObject("body")
                                   .getJSONObject("items")
                                   .getJSONArray("item");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            WeatherData data = new WeatherData();

            data.setCategory(item.getString("category"));
            data.setFcstValue(item.getString("fcstValue"));
            data.setFcstDate(item.getString("fcstDate"));
            data.setFcstTime(item.getString("fcstTime"));

            if ("TMP".equals(data.getCategory())) {
                data.setTmp(item.getString("fcstValue"));
            }
            if ("POP".equals(data.getCategory())) {
                data.setPop(item.getString("fcstValue"));
            }
            if ("WSD".equals(data.getCategory())) {
                data.setWsd(item.getString("fcstValue"));
            }

            weatherList.add(data);
        }

        return weatherList;
    }
}
