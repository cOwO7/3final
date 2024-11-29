document.addEventListener('DOMContentLoaded', function () {
    // 네이버 맵 API가 로드되었는지 확인하는 함수
    function checkMapAPI() {
        if (typeof naver !== 'undefined' && typeof naver.maps !== 'undefined' && typeof naver.maps.Service !== 'undefined') {
            initializeMap(); // API 로드가 완료되면 지도 초기화
        } else {
            setTimeout(checkMapAPI, 100); // 100ms 후 다시 확인
        }
    }

    // 네이버 맵 API 로드 확인 후 지도 초기화
    function initializeMap() {
        var position = new naver.maps.LatLng(37.4809615, 126.9521689); // 기본 서울 위치

        // 네이버 맵 초기화 (사용자 정의 컨트롤 및 기본 컨트롤 설정)
        var map = new naver.maps.Map('map', {
            center: position,
            zoom: 17,
            scaleControl: true,      // 스케일 컨트롤 활성화
            logoControl: false,      // 네이버 로고 컨트롤 비활성화
            mapDataControl: true,    // 지도 데이터 컨트롤 비활성화
            zoomControl: true,       // 줌 컨트롤 활성화
            minZoom: 7               // 최소 줌 레벨 설정
        });

        // 마커 객체 생성
        var marker = new naver.maps.Marker({
            position: position,
            map: map
        });

        // 기본 위치로 돌아가지 않도록 `isLocationSet` 플래그 추가
        var isLocationSet = false; // 기본 위치가 설정되었는지 여부

        // 현재 위치로 이동하는 버튼 HTML
        var locationBtnHtml = '<a href="#" class="btn_mylct"><span class="spr_trff spr_ico_mylct">현재 위치로 이동</span></a>';

        // 사용자 정의 컨트롤로 버튼을 맵에 추가
        var customControl = new naver.maps.CustomControl(locationBtnHtml, {
            position: naver.maps.Position.BOTTOM_RIGHT  // 버튼을 지도 오른쪽 하단에 배치
        });
        customControl.setMap(map);

        // 버튼 클릭 시 현재 위치로 이동
        naver.maps.Event.addDOMListener(customControl.getElement(), 'click', function () {
            getCurrentLocation(); // 현재 위치로 이동하는 함수 호출
        });

        // 현재 위치를 가져와서 지도 이동
        function getCurrentLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function (position) {
                    var lat = position.coords.latitude;
                    var lon = position.coords.longitude;
                    var currentPosition = new naver.maps.LatLng(lat, lon);
                    
                    // 지도 중심을 현재 위치로 설정
                    map.setCenter(currentPosition);
                    marker.setPosition(currentPosition); // 마커도 현재 위치로 변경
                }, function () {
                    alert("현재 위치를 찾을 수 없습니다.");
                });
            } else {
                alert("이 브라우저는 Geolocation을 지원하지 않습니다.");
            }
        }

        // 카카오 주소 검색 API를 사용하는 함수
        function openPostcode() {
            // 카카오 주소 검색 API
            new daum.Postcode({
                oncomplete: function(data) {
                    // 데이터에서 주소와 함께 위도, 경도 정보 가져오기
                    var fullAddr = data.address; // 전체 주소 (도로명 주소)
                    var latitude = data.y;  // 위도
                    var longitude = data.x; // 경도

                    // 주소를 입력한 입력 필드에 도로명 주소만 설정
                    document.getElementById("address").value = fullAddr;

                    // 네이버 지도를 해당 위치로 이동
                    var latLng = new naver.maps.LatLng(latitude, longitude);
                    map.setCenter(latLng);  // 지도 중심을 해당 위치로 설정
                    marker.setPosition(latLng);  // 마커도 해당 위치로 이동

                    // 주소 검색 후에는 현재 위치 버튼의 기능을 비활성화
                    isLocationSet = true; // 주소가 설정되었으므로 현재 위치 버튼 기능을 끈다.
                }
            }).open();
        }

        // 검색 버튼 클릭 시 해당 주소로 지도가 이동하는 함수
        function searchAddress(e) {
            e.preventDefault(); // 기본 동작 방지 (폼 제출 방지)

            var address = document.getElementById("address").value;

            if (address.trim() === "") {
                alert("주소를 입력하세요.");
                return;
            }

            // 도로명 주소를 입력받아 위도, 경도를 구하기 위한 네이버 Geocoding API
            naver.maps.Service.geocode({
                address: address
            }, function(status, response) {
                if (status === naver.maps.Service.Status.OK) {
                    var result = response.result.items[0];
                    var latLng = new naver.maps.LatLng(result.point.y, result.point.x); // point.y, point.x로 위도와 경도 접근

                    // 콘솔에 위도, 경도 값 확인
                    console.log("Geocoding API 응답:", result); // Geocoding 응답 확인
                    console.log("위도:", result.point.y, "경도:", result.point.x);  // 위도와 경도 출력

                    // 지도 중심을 해당 위치로 이동
                    map.setCenter(latLng);
                    marker.setPosition(latLng);  // 마커도 해당 위치로 이동
                } else {
                    alert("주소를 찾을 수 없습니다.");
                }
            });
        }

        // 엔터 키로도 검색할 수 있게 이벤트 리스너 추가
        document.getElementById("address").addEventListener("keydown", function(event) {
            if (event.key === "Enter") {
                searchAddress(event);  // 엔터 키 입력 시 검색
            }
        });

        // "검색" 버튼 클릭 시 검색
        // 여기서 `searchAddress(e)` 호출하면서 e.preventDefault()를 사용하여 페이지 새로고침 방지
        document.getElementById("submit").addEventListener("click", function(e) {
            searchAddress(e); // e.preventDefault()로 페이지 새로고침 방지
        });

        // 주소 입력창 클릭 시 카카오 주소 검색 팝업 열기
        document.getElementById("address").addEventListener("click", openPostcode);

        // 초기 위치는 한 번만 설정하고, 주소 검색 후에는 현재 위치로 돌아가지 않도록
        if (!isLocationSet) {
            // 기본 위치로 설정 (서울)
            map.setCenter(position);
            marker.setPosition(position); // 기본 마커 위치 설정
        }
    }

    // 네이버 맵 API 로드 여부 확인
    checkMapAPI(); // 맵 API가 로드되었는지 확인하고 초기화
});
