// DOM이 준비되면 실행될 콜백 함수
$(function() {
	
navigator.geolocation.getCurrentPosition(function(position) {
    var latitude = position.coords.latitude;  // 위도
    var longitude = position.coords.longitude; // 경도

    // 서버에 nx, ny 값 요청 (예: Geocoding API를 사용하여 좌표를 기반으로 nx, ny 값 계산)
    fetch('/getNxNy', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ latitude: latitude, longitude: longitude })
    })
    .then(response => response.json())
    .then(data => {
        // 받은 nx, ny 값을 사용하여 날씨 조회
        const nx = data.nx;
        const ny = data.ny;
        
        // 날씨 조회 요청
		fetch('/getWeather', {
		    method: 'POST',
		    headers: {
		        'Content-Type': 'application/json'
		    },
		    body: JSON.stringify({
		        baseDate: "20241122",  // 예시로 고정
		        baseTime: "1400",      // 예시로 고정
		        nx: nx,
		        ny: ny
		    })
		})
		.then(response => {
		    if (!response.ok) {
		        throw new Error('Weather data fetch failed: ' + response.status);
		    }
		    return response.json();
		})
		.then(weatherData => {
		    // 날씨 데이터 처리
		    console.log(weatherData);
		})
		.catch(error => {
		    console.error('Error:', error);  // 오류 메시지 출력

    console.error("위치 정보를 가져오는 데 실패했습니다.", error);
});
});
});
});