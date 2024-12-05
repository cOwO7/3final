document.addEventListener('DOMContentLoaded', function () {
    // 네이버 지도 API 로드 상태를 확인하는 함수
    function checkMapAPI() {
        if (typeof naver !== 'undefined' && typeof naver.maps !== 'undefined' && typeof naver.maps.Service !== 'undefined') {
            console.log("네이버 지도 API 로드됨");  // 디버깅 로그
        } else {
            setTimeout(checkMapAPI, 100);  // 100ms 후 다시 확인
            console.log("네이버 지도 API 로드 대기 중...");  // 디버깅 로그
        }
    }

    // 주소를 위도, 경도로 변환하여 x, y 좌표를 반환하는 함수
    function getCoordinatesFromAddress(address) {
        console.log("주소 API 호출됨: " + address);

        // Geocoder 객체를 생성합니다. 네이버 API가 로드된 후 이 부분이 실행됨
        const geocoder = new naver.maps.services.Geocoder();

        geocoder.addressSearch(address, function (result, status) {
            if (status === naver.maps.services.Status.OK) {
                var latitude = result[0].y;
                var longitude = result[0].x;

                console.log("주소에서 추출된 위도: " + latitude);
                console.log("주소에서 추출된 경도: " + longitude);

                // 위도, 경도를 nx, ny 좌표로 변환하고 날씨 데이터 호출
                getWeatherData(latitude, longitude);
            } else {
                alert("주소 검색에 실패했습니다.");
            }
        });
    }

    // 위도, 경도를 nx, ny로 변환하고 날씨 데이터 가져오기
    function getWeatherData(latitude, longitude) {
        console.log("getWeatherData 호출됨");

        // 위도, 경도를 nx, ny 좌표로 변환
        var coordinates = dfs_xy_conv("toXY", latitude, longitude);
        var nx = coordinates.x;
        var ny = coordinates.y;

        console.log("변환된 nx: " + nx);
        console.log("변환된 ny: " + ny);

        let baseDate = getCurrentDate(); // 현재 날짜를 받아오는 함수
        let baseTime = getValidBaseTime(); // 유효한 예보 시간을 계산하는 함수

        // 기상청 API 호출
        let serviceKey = "Gow/B+pvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT+fYSDLtu0o9k6WY+Rp7E00ZA==";  // 기상청에서 발급받은 서비스 키
        let apiUrl = `https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=${serviceKey}&pageNo=1&numOfRows=100&dataType=JSON&base_date=${baseDate}&base_time=${baseTime}&nx=${nx}&ny=${ny}`;

        console.log("API 호출 URL: " + apiUrl);

        $.ajax({
            url: apiUrl,
            type: "GET",
            dataType: "json",
            success: function (response) {
                console.log("API 호출 성공, 응답 받음");
                console.log(response);

                if (response.response.header.resultCode === "00") {
                    let items = response.response.body.items.item;
                    console.log("응답 받은 날씨 데이터:", items);
                    displayWeatherData(items);
                } else {
                    alert("날씨 데이터를 불러오는 데 실패했습니다.");
                }
            },
            error: function (error) {
                console.error("API 호출 오류:", error);
                alert("날씨 데이터를 가져오는 도중 오류가 발생했습니다.");
            }
        });
    }

    // 날씨 정보를 HTML 테이블에 출력하는 함수
    function displayWeatherData(items) {
        let resultTable = $("#mainWeatherTable").find("tbody");
        resultTable.empty(); // 기존 데이터 제거

        items.forEach(item => {
            let row = $("<tr></tr>");
            row.append(`<td>${item.fcstDate}</td>`);
            row.append(`<td>${item.fcstTime}</td>`);
            row.append(`<td>${item.fcstValue}</td>`);
            resultTable.append(row);
        });
        $("#mainWeatherTable").show(); // 테이블 보이도록
    }

    // 현재 날짜를 반환하는 함수
    function getCurrentDate() {
        let today = new Date();
        let yyyy = today.getFullYear();
        let mm = today.getMonth() + 1; // 월은 0부터 시작하므로 +1
        let dd = today.getDate();

        return `${yyyy}${(mm < 10 ? '0' : '')}${mm}${(dd < 10 ? '0' : '')}${dd}`;
    }

    // 예보 시간 계산 함수
    function getValidBaseTime() {
        const baseTimes = [2, 5, 8, 11, 14, 17, 20, 23];
        let currentHour = new Date().getHours();

        let validBaseTime = baseTimes[0]; 

        for (let i = 0; i < baseTimes.length; i++) {
            if (currentHour < baseTimes[i]) {
                validBaseTime = baseTimes[i];
                break;
            }
        }

        if (currentHour >= baseTimes[baseTimes.length - 1]) {
            validBaseTime = baseTimes[baseTimes.length - 1];
        }

        return `${validBaseTime < 10 ? '0' + validBaseTime : validBaseTime}00`;
    }

});
