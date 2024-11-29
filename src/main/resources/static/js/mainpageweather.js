document.addEventListener('DOMContentLoaded', function () {
    // 네이버 맵 API 로드 여부 확인
    function checkMapAPI() {
        if (typeof naver !== 'undefined' && typeof naver.maps !== 'undefined') {
            initializeMap(); // API 로드가 완료되면 지도 초기화
        } else {
            setTimeout(checkMapAPI, 100); // 100ms 후 다시 확인
        }
    }

    // 네이버 지도 초기화 함수
    function initializeMap() {
        var position = new naver.maps.LatLng(37.4809615, 126.9521689); // 기본 서울 위치

        // 네이버 맵 초기화
        var map = new naver.maps.Map('map', {
            center: position,
            zoom: 17,
            scaleControl: true,
            zoomControl: true
        });

        // 마커 객체 생성
        var marker = new naver.maps.Marker({
            position: position,
            map: map
        });

        // 지도 클릭 시 좌표 받아오기
        naver.maps.Event.addListener(map, 'click', function (e) {
            var latLng = e.coord;
            marker.setPosition(latLng); // 마커 이동

            // 선택한 위치의 위도, 경도 출력
            console.log("선택된 좌표: ", latLng);

            // 해당 위치의 날씨 정보 가져오기
            getWeatherData(latLng.lat(), latLng.lng()); // 날씨 데이터 요청
        });
    }

    // 날씨 데이터를 가져오는 함수
    function getWeatherData(latitude, longitude) {
        console.log("위도:", latitude);  // 위도 확인
        console.log("경도:", longitude);  // 경도 확인

        // 좌표 변환 요청 (위도, 경도 -> nx, ny)
        var coords = dfs_xy_conv("toXY", latitude, longitude);
        var nx = coords.x;
        var ny = coords.y;

        console.log("변환된 nx, ny:", nx, ny);

        // 기상청 API 호출
        getWeatherFromKma(nx, ny);  // 변환된 nx, ny로 날씨 정보를 요청
    }

	// 기상청 날씨 데이터를 가져오는 함수
	function getWeatherFromKma(nx, ny) {
	    let serviceKey = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";  // 실제 서비스 키로 변경
	    let apiUrl = `https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=${serviceKey}&nx=${nx}&ny=${ny}&dataType=JSON`;

	    $.ajax({
	        url: apiUrl,
	        type: "GET",
	        dataType: "json",
	        success: function(response) {
	            console.log("기상청 API 응답 데이터:", response); // 응답 데이터 확인
	            if (response.response.header.resultCode === "00") {
	                let items = response.response.body.items.item;
	                if (items && items.length > 0) {
	                    let resultTable = $("#weatherTable tbody");
	                    resultTable.empty();  // 기존 데이터 초기화

	                    let weatherDataByTime = {};
	                    items.forEach(function(item) {
	                        let timeKey = `${item.fcstDate} ${item.fcstTime}`;

	                        if (!weatherDataByTime[timeKey]) {
	                            weatherDataByTime[timeKey] = {
	                                date: item.fcstDate,
	                                time: item.fcstTime,
	                                temp: "-",
	                                sky: "-",
	                                pty: "-",
	                                humidity: "-",
	                            };
	                        }

	                        if (item.category === "TMP") {
	                            weatherDataByTime[timeKey].temp = `${item.fcstValue}℃`;
	                        }
	                        if (item.category === "SKY") {
	                            weatherDataByTime[timeKey].sky = code_value("SKY", item.fcstValue);
	                        }
	                        if (item.category === "PTY") {
	                            weatherDataByTime[timeKey].pty = code_value("PTY", item.fcstValue);
	                        }
	                        if (item.category === "REH") {
	                            weatherDataByTime[timeKey].humidity = `${item.fcstValue}%`;
	                        }
	                    });

	                    let currentHour = new Date().getHours();
	                    let forecastHours = [currentHour, currentHour + 1, currentHour + 2]; // 3시간 예보

	                    forecastHours.forEach(function(hour) {
	                        let formattedTime = (hour < 10 ? '0' : '') + hour + "00";
	                        let weather = weatherDataByTime[formattedTime];

	                        if (weather) {
	                            let row = $("<tr></tr>");
	                            row.append(`<td>${weather.date} ${weather.time}</td>`);
	                            row.append(`<td>${weather.temp}</td>`);
	                            row.append(`<td>${weather.sky}</td>`);
	                            row.append(`<td>${weather.pty}</td>`);
	                            row.append(`<td>${weather.humidity}</td>`);
	                            resultTable.append(row);
	                        }
	                    });

	                    $("#weather").show();  // 날씨 정보를 표시
	                } else {
	                    alert("예보 데이터가 없습니다.");
	                }
	            } else {
	                alert("에러 발생: " + response.response.header.resultMsg);
	            }
	        },
	        error: function(error) {
	            console.error("API 호출 오류:", error);
	            alert("API 호출 중 오류가 발생했습니다.");
	        }
	    });
	}



    // 기상청 카테고리 코드 변환 함수
    function code_value(category, code) {
        let value = "-";
        if (code) {
            if (category === "SKY") {
                if (code === "1") value = "맑음";
                else if (code === "3") value = "구름 많음";
                else if (code === "4") value = "흐림";
            } else if (category === "PTY") {
                if (code === "0") value = "없음";
                else if (code === "1") value = "비";
                else if (code === "2") value = "비/눈";
                else if (code === "3") value = "눈";
                else if (code === "5") value = "빗방울";
                else if (code === "6") value = "빗방울눈날림";
                else if (code === "7") value = "눈날림";
            }
        }
        return value;
    }

    // 네이버 맵 API 로드 여부 확인
    checkMapAPI(); // 맵 API가 로드되었는지 확인하고 초기화
});
