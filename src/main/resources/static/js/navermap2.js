document.addEventListener("DOMContentLoaded", function () {
    var map, marker;

    // 지도 초기화 및 위치 지정
    function initializeMap(lat, lon) {
        var mapContainer = document.getElementById("map"); // 지도 표시할 div
        var mapOption = {
            center: new naver.maps.LatLng(lat, lon), // 지도 중심 위치
            zoom: 17,
        };

        map = new naver.maps.Map(mapContainer, mapOption);
        marker = new naver.maps.Marker({
            position: map.getCenter(),
            map: map,
        });
    }

    // 현재 위치로 지도 중심을 설정
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var currentLat = position.coords.latitude;
            var currentLon = position.coords.longitude;

            // 지도 초기화
            initializeMap(currentLat, currentLon);

            // 현재 위치 좌표값을 input 필드에 넣기
            document.getElementById("latitudeNum").value = currentLat;
            document.getElementById("longitudeNum").value = currentLon;

            // 기상청 격자 좌표 변환
            var gridCoordinates = dfs_xy_conv("toXY", currentLat, currentLon);
            document.getElementById("nx").value = gridCoordinates.x;
            document.getElementById("ny").value = gridCoordinates.y;

        }, function(error) {
            alert("현재 위치를 가져올 수 없습니다.");
        });
    } else {
        alert("이 브라우저는 Geolocation을 지원하지 않습니다.");
    }

    // 주소 검색 함수
    function searchAddress() {
        new daum.Postcode({
            oncomplete: function(data) {
                var fullAddr = data.address; // 전체 주소
                var lat = data.y;  // 위도
                var lon = data.x; // 경도

                // 새로운 좌표가 입력되면 기존 값을 덮어쓰도록
                document.getElementById("address").value = fullAddr;
                document.getElementById("address-hidden").value = fullAddr;

                // 위도, 경도 필드 덮어쓰기
                document.getElementById("latitudeNum").value = lat;
                document.getElementById("longitudeNum").value = lon;

                // 기상청 좌표 변환
                var gridCoordinates = dfs_xy_conv("toXY", lat, lon);
                document.getElementById("nx").value = gridCoordinates.x;
                document.getElementById("ny").value = gridCoordinates.y;

                // 지도 및 마커 업데이트
                updateMap(lat, lon);
            }
        }).open();
    }

    // 지도 및 마커 업데이트
    function updateMap(latitude, longitude) {
        var position = new naver.maps.LatLng(latitude, longitude); // 새로운 좌표 생성
        map.setCenter(position); // 지도 중심 변경
        marker.setPosition(position); // 마커 위치 업데이트
    }

    // 검색 버튼 클릭 시 주소 검색
    document.getElementById("submit").addEventListener("click", function(e) {
        e.preventDefault(); // 기본 동작 방지 (폼 제출 방지)
        searchAddress();
    });

    // 현재 위치 버튼 클릭 시 지도 중심을 현재 위치로 설정
    document.getElementById("current-location-btn").addEventListener("click", function() {
        navigator.geolocation.getCurrentPosition(function(position) {
            var currentLat = position.coords.latitude;
            var currentLon = position.coords.longitude;
            updateMap(currentLat, currentLon);
        });
    });
});

// 기상청 좌표 변환 함수 (위경도 -> 격자 좌표 변환)
function dfs_xy_conv(code, v1, v2) {
    var RE = 6371.00877; // 지구 반경(km)
    var GRID = 5.0; // 격자 간격(km)
    var SLAT1 = 30.0; // 투영 위도1(degree)
    var SLAT2 = 60.0; // 투영 위도2(degree)
    var OLON = 126.0; // 기준점 경도(degree)
    var OLAT = 38.0; // 기준점 위도(degree)
    var XO = 43; // 기준점 X좌표(GRID)
    var YO = 136; // 기준점 Y좌표(GRID)

    var DEGRAD = Math.PI / 180.0;
    var RADDEG = 180.0 / Math.PI;

    var re = RE / GRID;
    var slat1 = SLAT1 * DEGRAD;
    var slat2 = SLAT2 * DEGRAD;
    var olon = OLON * DEGRAD;
    var olat = OLAT * DEGRAD;

    var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
    sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
    var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
    sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
    var ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
    ro = re * sf / Math.pow(ro, sn);

    var rs = {};
    if (code === "toXY") {
        var ra = Math.tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        var theta = v2 * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;
        rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
        rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
    } else {
        rs.x = v1;
        rs.y = v2;
        var xn = v1 - XO;
        var yn = ro - v2 + YO;
        var ra = Math.sqrt(xn * xn + yn * yn);
        if (sn < 0.0) {
            ra = -ra;
        }
        var alat = Math.pow((re * sf / ra), (1.0 / sn));
        alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

        var theta = 0.0;
        if (Math.abs(xn) <= 0.0) {
            theta = 0.0;
        } else {
            if (Math.abs(yn) <= 0.0) {
                theta = Math.PI * 0.5;
                if (xn < 0.0) {
                    theta = -theta;
                }
            } else theta = Math.atan2(xn, yn);
        }
        var alon = theta / sn + olon;
        rs.lat = alat * RADDEG;
        rs.lng = alon * RADDEG;
    }
    return rs;
}
