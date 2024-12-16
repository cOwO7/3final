// DOM이 준비되면 실행될 콜백 함수
$(function() {
    function getLocationAndSubmit() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const latitudeNum = position.coords.latitude;
                    const longitudeNum = position.coords.longitude;

                    // 기상청의 좌표 변환 함수 적용 (위경도 -> 격자 좌표)
                    const result = dfs_xy_conv("toXY", latitudeNum, longitudeNum);

                    // 변환된 격자 좌표
                    const nx = result.x;
                    const ny = result.y;

                    // nx, ny를 텍스트 필드에 업데이트
                    $("#nx").val(nx);
                    $("#ny").val(ny);
					$("#latitudeNum").val(latitudeNum);
					$("#longitudeNum").val(longitudeNum);

                    // 서버로 데이터 전송
                    const weatherDto = {
                        baseDate: $("#baseDate").val(),
                        baseTime: $("#baseTime").val(),
                        nx: nx,
                        ny: ny,
                    };

                    // AJAX로 서버에 데이터 전송
					fetch('/getWeather', {
					    method: 'POST',
					    headers: {
					        'Content-Type': 'application/json',
					    },
					    body: JSON.stringify(weatherDto),
					})
					.then(response => {
					    if (!response.ok) {
					        return response.text().then(text => {
					            console.error("서버 오류:", text);
					            throw new Error('날씨 데이터를 가져오지 못했습니다.');
					        });
					    }
					    return response.json();
					})
					.then(data => {
					    console.log("날씨 데이터: ", data);
					})
					.catch(error => {
					    console.error("에러:", error);
					});

                },
                (error) => {
                    console.error("위치정보 에러: ", error);
                    switch (error.code) {
                        case error.PERMISSION_DENIED:
                            alert("위치 권한이 거부되었습니다.");
                            break;
                        case error.POSITION_UNAVAILABLE:
                            alert("위치 정보를 사용할 수 없습니다.");
                            break;
                        case error.TIMEOUT:
                            alert("위치 정보를 가져오는 데 시간이 초과되었습니다.");
                            break;
                        default:
                            alert("알 수 없는 오류가 발생했습니다.");
                            break;
                    }
                }
            );
        } else {
            alert("이 브라우저에서는 위치정보가 지원되지 않습니다.");
        }
    }

    // 버튼 클릭 이벤트 리스너 등록
    $("#getWeatherButton").on("click", getLocationAndSubmit);
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
