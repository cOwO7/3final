document.addEventListener("DOMContentLoaded", function() {
	// 지도 컨테이너
	var mapContainer = document.getElementById("map"); // 지도 표시할 div
	var mapOption = {
		level: 3, // 확대 레벨
	};

	var map, marker; // 지도와 마커를 전역 변수로 선언

	// 현재 위치로 지도 중심을 설정하기 전에, 위치를 가져와야 함
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			// 사용자의 현재 위치
			var currentLat = position.coords.latitude; // 위도
			var currentLon = position.coords.longitude; // 경도

			// 지도 옵션에 현재 위치 설정
			mapOption.center = new kakao.maps.LatLng(currentLat, currentLon);

			// 카카오 지도 생성
			map = new kakao.maps.Map(mapContainer, mapOption); // 지도 객체 생성

			// 지도 중심에 마커 설정
			marker = new kakao.maps.Marker({
				position: map.getCenter(), // 현재 위치에 마커 표시
				map: map,
			});

			// 지도 중심 변경
			map.setCenter(new kakao.maps.LatLng(currentLat, currentLon));

		}, function(error) {
			alert("현재 위치 정보를 가져올 수 없습니다.");
		});
	} else {
		alert("현재 위치를 지원하지 않는 브라우저입니다.");
	}

	// 주소 검색 함수
	function searchAddress() {
		new daum.Postcode({
			oncomplete: function(data) {
				var fullAddress = data.address; // 전체 주소
				document.getElementById("address").value = fullAddress;
				document.getElementById("address-hidden").value = fullAddress;

				// Geocoder를 통해 좌표 변환
				var geocoder = new kakao.maps.services.Geocoder();
				geocoder.addressSearch(fullAddress, function(results, status) {
					if (status === kakao.maps.services.Status.OK) {
						var result = results[0];
						var lat = parseFloat(result.y); // 위도
						var lng = parseFloat(result.x); // 경도

						// 좌표 값 유효성 검사
						if (!isNaN(lat) && !isNaN(lng)) {
							document.getElementById("latitudeNum").value = lat;
							document.getElementById("longitudeNum").value = lng;

							// 기상청 좌표 변환
							var gridCoordinates = dfs_xy_conv("toXY", lat, lng);
							document.getElementById("nx").value = gridCoordinates.x;
							document.getElementById("ny").value = gridCoordinates.y;

							// 지도 및 마커 업데이트
							updateMap(lat, lng);
						} else {
							alert("좌표를 가져오지 못했습니다.");
						}
					} else {
						alert("주소 검색에 실패했습니다.");
					}
				});
			}
		}).open();
	}

	// 지도 및 마커 업데이트
	function updateMap(latitude, longitude) {
		var position = new kakao.maps.LatLng(latitude, longitude); // 새로운 좌표 생성
		map.setCenter(position); // 지도 중심 변경
		marker.setPosition(position); // 마커 위치 업데이트
	}

	// 버튼 클릭 시 주소 검색
	document.getElementById("addressBtn").addEventListener("click", function() {
		searchAddress();
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
	}
	return rs;
}