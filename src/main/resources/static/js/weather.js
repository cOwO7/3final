// DOM이 준비되면 실행될 콜백 함수
$(function() {
	
	
	$(document).ready(function () {
	    $("#btn_weather").click(function () {
	        // 입력 값 유효성 검사
	        if ($("#nx").val() === "" || $("#ny").val() === "" || $("#baseDate").val() === "" || $("#baseTime").val() === "") {
	            alert("모든 필수 값을 입력하세요.");
	            return;
	        }

	        // 파라미터 설정
	        let serviceKey = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";
	        let nx = $("#nx").val();
	        let ny = $("#ny").val();
	        let baseDate = $("#baseDate").val();
	        let baseTime = $("#baseTime").val();
	        let pageNo = 1;
	        let numOfRows = 100; // 조회할 데이터 개수
	        let dataType = "JSON";

	        // URL 동적 생성
	        let apiUrl = `https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=${serviceKey}&pageNo=${pageNo}&numOfRows=${numOfRows}&dataType=${dataType}&base_date=${baseDate}&base_time=${baseTime}&nx=${nx}&ny=${ny}`;

	        console.log("API 호출 URL:", apiUrl);

	        // Ajax 호출
	        $.ajax({
	            url: apiUrl,
	            type: "GET",
	            dataType: "json",
	            success: function (response) {
	                console.log(response); // API 응답 확인

	                if (response.response.header.resultCode === "00") {
	                    let items = response.response.body.items.item;
	                    if (items && items.length > 0) {
	                        let resultTable = $("#resultTable tbody");
	                        resultTable.empty(); // 기존 테이블 초기화

	                        // 시간대별로 데이터를 묶기 위한 객체
	                        let weatherDataByTime = {};

	                        // 각 항목 처리
	                        items.forEach(function (item) {
	                            let timeKey = `${item.fcstDate} ${item.fcstTime}`; // 날짜와 시간을 결합해 고유 키 생성

	                            // 해당 시간대의 객체가 없으면 초기화
	                            if (!weatherDataByTime[timeKey]) {
	                                weatherDataByTime[timeKey] = {
	                                    date: item.fcstDate,
	                                    time: item.fcstTime,
	                                    sky: "-",
	                                    pty: "-",
	                                    temp: "-",
	                                    humidity: "-",
										lgt: "-",
										vvv: "-",
										uuu: "-",
										rn1: "-",
										vec: "-",
										wsd: "-",
	                                };
	                            }

	                            // 카테고리별로 데이터 추가
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
								if (item.category === "VEC") {
									weatherDataByTime[timeKey].vec = `${item.fcstValue}deg`;
								}
								if (item.category === "WSD") {
									weatherDataByTime[timeKey].wsd = `${item.fcstValue}m/s`;
								}
								if (item.category === "UUU") {
									weatherDataByTime[timeKey].uuu = `${item.fcstValue}m/s`;
								}
								if (item.category === "VVV") {
									weatherDataByTime[timeKey].vvv = `${item.fcstValue}m/s`;
								}
								if (item.category === "RN1") {
									weatherDataByTime[timeKey].rn1 = `${item.fcstValue}mm`;
								}
								
								/*
								// 낙뢰
								if (item.category === "LGT") {
									weatherDataByTime[timeKey].lgt = code_value("LGT", item.fcstValue);
								}
								// 최저, 최고기온
								if (item.category === "TMN") {
									weatherDataByTime[timeKey].tmn = `${item.fcstValue}℃`;
								}
								if (item.category === "TMX") {
									weatherDataByTime[timeKey].tmx = `${item.fcstValue}℃`;
								}*/
	                        });

	                        // 시간대별로 테이블에 출력
	                        for (let time in weatherDataByTime) {
	                            let weather = weatherDataByTime[time];
	                            let row = $("<tr></tr>");
	                            row.append(`<td>${weather.date}</td>`);	//날짜
	                            row.append(`<td>${weather.time}</td>`);	//시간
	                            row.append(`<td>${weather.sky}</td>`);	//하늘
	                            row.append(`<td>${weather.temp}</td>`); //기온
	                            row.append(`<td>${weather.pty}</td>`);	//강수형태
	                            row.append(`<td>${weather.rn1}</td>`);	//1시간 강수량
	                            row.append(`<td>${weather.humidity}</td>`);	//
	                            row.append(`<td>${weather.vec}\n${weather.wsd}</td>`);	//풍향,풍속
	                            row.append(`<td>${weather.uuu}\n${weather.vvv}</td>`);	//동서풍,남북풍
	                            /*row.append(`<td>${weather.tmn}\n${weather.tmx}</td>`);	//최저,최고기온
	                            row.append(`<td>${weather.lgt}</td>`);	*///낙뢰
	                            resultTable.append(row);
	                        }

	                        // 테이블 표시
	                        $("#resultTable").show();
	                    } else {
	                        alert("예보 데이터가 없습니다.");
	                    }
	                } else {
	                    alert("에러 발생: " + response.response.header.resultMsg);
	                }
	            },
	            error: function (error) {
	                console.error("API 호출 오류:", error);
	                alert("API 호출 중 오류가 발생했습니다.");
	            }
	        });

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
	                } else if (category === "LGT") {
						if (code === "0") value = "없음"
						else if (code === "1") value = "쾅!"
						else if (code === "2") value = "우르쾅!"
						else if (code === "3") value = "우르르쾅쾅!"
						else if (code === "4") value = "거의 제우스 강림!"
					} 
						
					
	            }
	            return value;
	        }
	    });
	});
});
