package com.springbootfinal.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController {

    // 예시로 간단하게 좌표 -> nx, ny 값을 변환하는 API (실제로는 외부 API나 계산식 필요)
    @PostMapping("/getNxNy")
    public ResponseEntity<Map<String, Integer>> getNxNy(@RequestBody Map<String, Double> location) {
        Double latitude = location.get("latitude");
        Double longitude = location.get("longitude");

        // 실제 nx, ny 계산을 위한 로직 필요 (예: 지도 API 호출)
        int nx = calculateNx(latitude, longitude);
        int ny = calculateNy(latitude, longitude);

        Map<String, Integer> result = new HashMap<>();
        result.put("nx", nx);
        result.put("ny", ny);

        return ResponseEntity.ok(result);
    }

    // 예시로 간단한 계산식
    private int calculateNx(Double latitude, Double longitude) {
        // 실제 nx 계산 로직 필요 (위도, 경도 -> nx 변환)
        return (int) (longitude * 1000); // 임시 예시
    }

    private int calculateNy(Double latitude, Double longitude) {
        // 실제 ny 계산 로직 필요 (위도, 경도 -> ny 변환)
        return (int) (latitude * 1000); // 임시 예시
    }
}
