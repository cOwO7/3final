$(function() {
    // 카카오맵 API가 로드되었는지 확인
    if (typeof kakao !== 'undefined' && kakao.maps) {
        initializeMap();  // 카카오맵이 로드된 경우 바로 실행
    } else {
        console.error("카카오맵 API 로드 실패");
    }
});

// 카카오맵 초기화 코드
function initializeMap() {
    var mapContainer = document.getElementById('map'); // 지도를 표시할 div
    var mapOption = {
        center: new kakao.maps.LatLng(33.450701, 126.570667), // 초기 지도 위치 (임시값)
        level: 3 // 지도 확대/축소 수준
    };

    // 지도 생성
    var map = new kakao.maps.Map(mapContainer, mapOption);

    // 사용자의 현재 위치를 가져오는 함수
    function getCurrentLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                var lat = position.coords.latitude; // 위도
                var lng = position.coords.longitude; // 경도

                // 현재 위치를 카카오맵에 표시
                var locPosition = new kakao.maps.LatLng(lat, lng);
                map.setCenter(locPosition); // 지도 중심을 사용자의 위치로 설정

                // 마커 추가
                var marker = new kakao.maps.Marker({
                    position: locPosition
                });
                marker.setMap(map); // 마커를 지도에 추가
            }, function(error) {
                alert("현재 위치를 가져오는 데 실패했습니다.");
            });
        } else {
            alert("Geolocation을 지원하지 않는 브라우저입니다.");
        }
    }

    // 페이지 로드 시 현재 위치 가져오기
    getCurrentLocation();
}
