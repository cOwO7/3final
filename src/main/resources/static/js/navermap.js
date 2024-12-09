/*document.addEventListener("DOMContentLoaded", function() {
	var map, marker;
	var locationBtn;

	// 지도 초기화 및 위치 지정
	function initializeMap(lat, lon) {
		var mapContainer = document.getElementById("map"); // 지도 표시할 div
		var mapOption = {
			center: new naver.maps.LatLng(lat, lon), // 지도 중심 위치
			zoom: 17,
			mapTypeControl: true, // 지도 타입 컨트롤 (지도 종류 변경 버튼)
			scaleControl: true, // 스케일 컨트롤 (지도 크기 조정)
			logoControl: true, // 네이버 지도 로고 컨트롤 활성화 여부
			mapDataControl: true, // 지도 데이터 제어 버튼 (위성, 일반 지도 등)
			zoomControl: false, // 줌 버튼 활성화 여부
			zoomControlOptions: {
				position: naver.maps.Position.TOP_RIGHT // 줌 버튼 위치
			},
			draggable: true, // 지도 드래그 가능 여부
			disableDoubleClickZoom: false, // 더블클릭 줌 기능 비활성화 여부
			keyboardShortcuts: true // 키보드 단축키 활성화
		};

		map = new naver.maps.Map(mapContainer, mapOption);

		marker = new naver.maps.Marker({
			position: map.getCenter(),
			map: map,
		});


		// 커스텀 버튼을 한 번만 추가
		if (!locationBtn) {
			var locationBtnHtml = `
                <button id="current-location-btn" style="position:absolute; bottom: 10px; right: 20px; background-color: #ff6a00; color: white; border-radius: 50%; padding: 10px; z-index: 9999;">
                    📍 현재 위치
                </button>`;

			locationBtn = new naver.maps.CustomControl(locationBtnHtml, {
				position: naver.maps.Position.BOTTOM_RIGHT
			});

			locationBtn.setMap(map);
			
			// 버튼 클릭 시 현재 위치로 이동
			naver.maps.Event.addDOMListener(locationBtn.getElement(), 'click', function() {
				getCurrentLocation(); // 현재 위치로 이동하는 함수 호출
			});
		}
	}
	
	// 현재 위치로 지도 중심을 설정
	function getCurrentLocation() {
	    if (navigator.geolocation) {
	        navigator.geolocation.getCurrentPosition(function(position) {
	            var currentLat = position.coords.latitude;
	            var currentLon = position.coords.longitude;

	            // 수동으로 위치 보정 (예시: 0.01씩 더하기)
	            var correctedLat = currentLat + 0;  // 위도 보정
	            var correctedLon = currentLon + 0;  // 경도 보정

	            // 지도 초기화
	            initializeMap(correctedLat, correctedLon);

	            // 보정된 위치 좌표값을 input 필드에 넣기
	            document.getElementById("latitudeNum").value = correctedLat;
	            document.getElementById("longitudeNum").value = correctedLon;

	            // 기상청 격자 좌표 변환
	            var gridCoordinates = dfs_xy_conv("toXY", correctedLat, correctedLon);
	            document.getElementById("nx").value = gridCoordinates.x;
	            document.getElementById("ny").value = gridCoordinates.y;

	            // 현재 날짜와 시간 설정
	            setLocalDateTime(); // 날짜와 시간 설정
	        }, function(error) {
	            alert("현재 위치를 가져올 수 없습니다.");
	        });
	    } else {
	        alert("이 브라우저는 Geolocation을 지원하지 않습니다.");
	    }
	}


	// 현재 날짜와 시간을 설정하는 함수
	function setLocalDateTime() {
		var now = new Date();  // 현재 날짜와 시간 가져오기
		var year = now.getFullYear();  // 연도
		var month = String(now.getMonth() + 1).padStart(2, '0');  // 월 (0부터 시작하므로 +1)
		var day = String(now.getDate()).padStart(2, '0');  // 일
		var hours = now.getHours();  // 시간
		var minutes = now.getMinutes();  // 분

		// 날짜와 시간을 각각의 input 필드에 설정
		document.getElementById("baseDate").value = `${year}${month}${day}`;  // YYYYMMDD 형식

		// 현재 시간이 몇 번째 3시간 구간에 속하는지 확인하여 baseTime을 결정
		var baseTime = getBaseTime(hours, minutes);
		document.getElementById("baseTime").value = baseTime;  // HH00 형식
	}

	// API 제공 시간을 정확히 맞추는 함수
	function getBaseTime(hours, minutes) {
		// 제공되는 시간: 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10
		var apiTimes = ["0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"];

		// 현재 시간을 "HH:MM" 형식으로 구하기
		var currentTime = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;

		// 현재 시간보다 이전의 가장 가까운 시간 찾기
		for (var i = apiTimes.length - 1; i >= 0; i--) {
			if (currentTime >= apiTimes[i]) {
				return apiTimes[i];  // 가장 가까운 시간 반환
			}
		}

		// 만약 현재 시간이 가장 늦은 시간보다 빠르다면 마지막 시간 반환
		return apiTimes[apiTimes.length - 1]; // 마지막 시간 23:10
	}

	// 초기 위치를 가져옴
	getCurrentLocation();
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
	}
	return rs;
}*/

document.addEventListener("DOMContentLoaded", function() {
    var map, marker;
    
    // 지도 초기화 및 위치 지정
    function initializeMap(lat, lon) {
        var mapContainer = document.getElementById("map"); // 지도 표시할 div

        // 신버전 타일 스타일 설정
        var mapTypes = new naver.maps.MapTypeRegistry({
            'normal': naver.maps.NaverStyleMapTypeOptions.getNormalMap(),  // 기존 일반 지도
            'satellite': naver.maps.NaverStyleMapTypeOptions.getSatelliteMap(), // 위성 지도
            'terrain': naver.maps.NaverStyleMapTypeOptions.getTerrainMap(), // 지형 지도
            'dark': naver.maps.NaverStyleMapTypeOptions.getDarkMap()  // 어두운 테마 지도
        });

        var mapOption = {
            center: new naver.maps.LatLng(lat, lon), // 지도 중심 위치
            zoom: 17,
            mapTypes: mapTypes, // 맵 타일을 신버전으로 설정
            mapTypeControl: true, // 지도 타입 컨트롤 (지도 종류 변경 버튼)
            scaleControl: true, // 스케일 컨트롤 (지도 크기 조정)
            logoControl: true, // 네이버 지도 로고 컨트롤 활성화 여부
            mapDataControl: true, // 지도 데이터 제어 버튼 (위성, 일반 지도 등)
            zoomControl: false, // 줌 버튼 활성화 여부
            zoomControlOptions: {
                position: naver.maps.Position.TOP_RIGHT // 줌 버튼 위치
            },
            draggable: true, // 지도 드래그 가능 여부
            disableDoubleClickZoom: false, // 더블클릭 줌 기능 비활성화 여부
            keyboardShortcuts: true // 키보드 단축키 활성화
        };

        map = new naver.maps.Map(mapContainer, mapOption);

        marker = new naver.maps.Marker({
            position: map.getCenter(),
            map: map,
        });
    }

    // 현재 위치로 지도 중심을 설정
    function getCurrentLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                var currentLat = position.coords.latitude;
                var currentLon = position.coords.longitude;

                // 수동으로 위치 보정 (예시: 0.01씩 더하기)
                var correctedLat = currentLat + 0;  // 위도 보정
                var correctedLon = currentLon + 0;  // 경도 보정

                // 지도 초기화
                initializeMap(correctedLat, correctedLon);

                // 보정된 위치 좌표값을 input 필드에 넣기
                document.getElementById("latitudeNum").value = correctedLat;
                document.getElementById("longitudeNum").value = correctedLon;

                // 기상청 격자 좌표 변환
                var gridCoordinates = dfs_xy_conv("toXY", correctedLat, correctedLon);
                document.getElementById("nx").value = gridCoordinates.x;
                document.getElementById("ny").value = gridCoordinates.y;

                // 현재 날짜와 시간 설정
                setLocalDateTime(); // 날짜와 시간 설정
            }, function(error) {
                alert("현재 위치를 가져올 수 없습니다.");
            });
        } else {
            alert("이 브라우저는 Geolocation을 지원하지 않습니다.");
        }
    }

    // 현재 날짜와 시간을 설정하는 함수
    function setLocalDateTime() {
        var now = new Date();  // 현재 날짜와 시간 가져오기
        var year = now.getFullYear();  // 연도
        var month = String(now.getMonth() + 1).padStart(2, '0');  // 월 (0부터 시작하므로 +1)
        var day = String(now.getDate()).padStart(2, '0');  // 일
        var hours = now.getHours();  // 시간
        var minutes = now.getMinutes();  // 분

        // 날짜와 시간을 각각의 input 필드에 설정
        document.getElementById("baseDate").value = `${year}${month}${day}`;  // YYYYMMDD 형식

        // 현재 시간이 몇 번째 3시간 구간에 속하는지 확인하여 baseTime을 결정
        var baseTime = getBaseTime(hours, minutes);
        document.getElementById("baseTime").value = baseTime;  // HH00 형식
    }

    // API 제공 시간을 정확히 맞추는 함수
    function getBaseTime(hours, minutes) {
        // 제공되는 시간: 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10
        var apiTimes = ["0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"];

        // 현재 시간을 "HH:MM" 형식으로 구하기
        var currentTime = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;

        // 현재 시간보다 이전의 가장 가까운 시간 찾기
        for (var i = apiTimes.length - 1; i >= 0; i--) {
            if (currentTime >= apiTimes[i]) {
                return apiTimes[i];  // 가장 가까운 시간 반환
            }
        }

        // 만약 현재 시간이 가장 늦은 시간보다 빠르다면 마지막 시간 반환
        return apiTimes[apiTimes.length - 1]; // 마지막 시간 23:10
    }

    // 버튼 클릭 시 현재 위치로 이동
    document.getElementById("current-location-btn").addEventListener("click", function() {
        getCurrentLocation(); // 현재 위치로 이동하는 함수 호출
    });

    // 초기 위치를 가져옴
    getCurrentLocation();
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
    }
    return rs;
}
