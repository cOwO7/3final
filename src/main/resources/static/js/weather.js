// DOM이 준비되면 실행될 콜백 함수
/*$(function() {


	$(document).ready(function() {
		$("#btn_weather").click(function() {
			// 입력 값 유효성 검사
			if ($("#nx").val() === "" || $("#ny").val() === "" || $("#baseDate").val() === "" || $("#baseTime").val() === "") {
				alert("현재 위치를 입력 해주세요.");
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
				success: function(response) {
					console.log(response); // API 응답 확인

					if (response.response.header.resultCode === "00") {
						let items = response.response.body.items.item;
						if (items && items.length > 0) {
							let resultTable = $("#resultTable tbody");
							resultTable.empty(); // 기존 테이블 초기화

							// 시간대별로 데이터를 묶기 위한 객체
							let weatherDataByTime = {};

							// 각 항목 처리
							items.forEach(function(item) {
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
										pcp: "-",
										vec: "-",
										wsd: "-",
										sno: "-"
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
								if (item.category === "PCP") {
									weatherDataByTime[timeKey].pcp = `${item.fcstValue}`;
								}
								if (item.category === "SNO") {
									weatherDataByTime[timeKey].sno = `${item.fcstValue}`;
								}
							});

							// 시간대별로 테이블에 출력
							for (let time in weatherDataByTime) {
								let weather = weatherDataByTime[time];
							for (let time in weatherDataByTime) {
								let weather = weatherDataByTime[time];

								// 날짜와 시간 변환
								let originalDate = weather.date;
								let originalTime = weather.time;

								// yyyyMMdd HHmm -> yyyy-MM-dd HH:mm 변환
								let formattedDateTime = formatDateTime(originalDate, originalTime);
								
								// 테이블 행 생성
								let row = $("<tr></tr>");
								row.append(`<td>${weather.date}</td>`);	//날짜
								row.append(`<td>${weather.time}</td>`);	//시간
								row.append(`<td>${weather.sky}</td>`);	//하늘
								row.append(`<td>${weather.temp}</td>`); //기온
								row.append(`<td>${weather.pty}</td>`);	//강수형태
								row.append(`<td>${weather.pcp}<br>${weather.sno}</td>`);	//1시간 강수량
								row.append(`<td>${weather.humidity}</td>`);	//
								row.append(`<td>${weather.vec}<br>${weather.wsd}</td>`);	//풍향,풍속
								row.append(`<td>${weather.uuu}<br>${weather.vvv}</td>`);	//동서풍,남북풍
								
								
								resultTable.append(row);
							}



							// 날짜 및 시간 포맷 변환 함수 
							//추가 데이터
							function formatDateTime(date, time) {
								let luxonDate = luxon.DateTime.fromFormat(date + time, "yyyy-MM-ddHH:mm");
								return {
									date: luxonDate.toFormat("yyyy-MM-dd"), // 날짜: yyyy-MM-dd
									time: luxonDate.toFormat("HH:mm")       // 시간: HH:mm
								};
							}
							// 여기까지 추가 데이터 문제시 삭제
							
							// 테이블 표시
							}
							$("#resultTable").show();
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
					}


				}
				return value;
			}
		});
	});
});*/

// 최대 8시간 날씨 데이터 출력 및 시간 포맷 24시간제 -> 12시간제 변경
/*$(function() {
	$(document).ready(function() {
		$("#btn_weather").click(function() {
			// 입력 값 유효성 검사
			if ($("#nx").val() === "" || $("#ny").val() === "" || $("#baseDate").val() === "" || $("#baseTime").val() === "") {
				alert("현재 위치를 입력 해주세요.");
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
				success: function(response) {
					console.log("API 응답:", response); // 전체 응답 확인

					if (response.response.header.resultCode === "00") {
						let items = response.response.body.items.item;
						console.log("응답 받은 날씨 데이터:", items);  // 날씨 데이터 출력

						let resultTable = $("#resultTable tbody");
						resultTable.empty(); // 기존 테이블 초기화

						let weatherDataByTime = {};  // 시간별 데이터 저장

						items.forEach(function(item) {
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
									pcp: "-",
									vec: "-",
									wsd: "-",
									sno: "-"
								};
							}

							// 카테고리별로 데이터 추가
							if (item.category === "TMP") {
								weatherDataByTime[timeKey].temp = item.fcstValue || "-"; // 값이 없으면 "-" 처리
							}
							if (item.category === "SKY") {
								weatherDataByTime[timeKey].sky = item.fcstValue ? code_value("SKY", item.fcstValue) : "-"; // 값이 없으면 "-" 처리
							}
							if (item.category === "PTY") {
								weatherDataByTime[timeKey].pty = item.fcstValue || "-"; // 값이 없으면 "-" 처리
							}
							if (item.category === "REH") {
								weatherDataByTime[timeKey].humidity = item.fcstValue ? `${item.fcstValue}%` : "-"; // 값이 없으면 "-" 처리
							}
							if (item.category === "VEC") {
								weatherDataByTime[timeKey].vec = item.fcstValue ? `${item.fcstValue}deg` : "-"; // 값이 없으면 "-" 처리
							}
							if (item.category === "WSD") {
								weatherDataByTime[timeKey].wsd = item.fcstValue ? `${item.fcstValue}m/s` : "-"; // 값이 없으면 "-" 처리
							}
							if (item.category === "UUU") {
								weatherDataByTime[timeKey].uuu = item.fcstValue ? `${item.fcstValue}m/s` : "-"; // 값이 없으면 "-" 처리
							}
							if (item.category === "VVV") {
								weatherDataByTime[timeKey].vvv = item.fcstValue ? `${item.fcstValue}m/s` : "-"; // 값이 없으면 "-" 처리
							}
							if (item.category === "PCP") {
								weatherDataByTime[timeKey].pcp = item.fcstValue || "-"; // 값이 없으면 "-" 처리
							}
							if (item.category === "SNO") {
								weatherDataByTime[timeKey].sno = item.fcstValue || "-"; // 값이 없으면 "-" 처리
							}
						});

						// 시간대별로 테이블에 출력 (누락된 값이 있는 시간대는 제외)
						let count = 0;
						for (let time in weatherDataByTime) {
							let weather = weatherDataByTime[time];

							// 마지막 시간대인 23시를 제외한 나머지 출력
							if (count >= 8) break; // 8개 시간까지만 출력

							let formatted = formatTime(weather.date, weather.time);  // 날짜 및 시간 포맷 변환
							let row = $("<tr></tr>");
							row.append(`<td>${formatted.date}</td>`);  // 날짜
							row.append(`<td>${formatted.time}</td>`);  // 시간
							row.append(`<td>${weather.sky}</td>`);   // 날씨
							row.append(`<td>${weather.temp}℃</td>`);  // 기온
							row.append(`<td>${weather.pty}%</td>`);   // 강수형태
							row.append(`<td>${weather.pcp}mm</td>`);   // 강수량
							row.append(`<td>${weather.humidity}</td>`); // 습도
							row.append(`<td>${weather.vec}<br>${weather.wsd}</td>`);  // 풍향, 풍속
							row.append(`<td>${weather.uuu}<br>${weather.vvv}</td>`);  // 동서풍, 남북풍

							resultTable.append(row);
							count++;
						}

						$("#resultTable").show();  // 테이블 보이도록

					} else {
						alert("예보 데이터가 없습니다.");
					}

				},
				error: function(error) {
					console.error("API 호출 오류:", error);
					alert("API 호출 중 오류가 발생했습니다.");
				}
			});
			
			// 날짜와 시간 포맷 변환 함수
			function formatTime(date, time) {
				let hour = parseInt(time.slice(0, 2));
				let formattedHour = hour > 12 ? hour - 12 : hour; // 12시간제로 변환
				let ampm = hour >= 12 ? '오후' : '오전';  // 오전/오후 구분

				let formattedTime = `${ampm} ${formattedHour}:00`;

				let formattedDate = `${date.slice(0, 4)}-${date.slice(4, 6)}-${date.slice(6, 8)}`;

				return { date: formattedDate, time: formattedTime };
			}
		});
	});
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
		}
	}
	return value;
}*/

/* 병합 어느정도 성공하긴했지만 약간 수정이 필요함
$(function() {
	// 이전 시간대 계산
	function getPrevBaseTime(baseTime) {
		let baseTimeInt = parseInt(baseTime, 10);  // baseTime을 숫자형으로 변환
		if (isNaN(baseTimeInt)) {
			console.error("잘못된 baseTime 값:", baseTime);  // 잘못된 baseTime을 디버깅
			return null; // baseTime이 잘못되었을 경우 null 반환
		}

		// 기존 로직
		if (baseTimeInt === 2000) return "1700";
		if (baseTimeInt === 1700) return "1400";
		if (baseTimeInt === 1400) return "1100";
		if (baseTimeInt === 1100) return "0800";
		if (baseTimeInt === 800) return "0500";   // 선행 0 제거
		if (baseTimeInt === 500) return "0200";   // 선행 0 제거
		if (baseTimeInt === 2300) return "2000";
		if (baseTimeInt === 200) return "2300";   // 선행 0 제거
		return null;
	}

	function getNextBaseTime(baseTime) {
		let baseTimeInt = parseInt(baseTime, 10);  // baseTime을 숫자형으로 변환
		if (isNaN(baseTimeInt)) {
			console.error("잘못된 baseTime 값:", baseTime);  // 잘못된 baseTime을 디버깅
			return null; // baseTime이 잘못되었을 경우 null 반환
		}

		// 기존 로직
		if (baseTimeInt === 2000) return "2300";
		if (baseTimeInt === 1700) return "2000";
		if (baseTimeInt === 1400) return "1700";
		if (baseTimeInt === 1100) return "1400";
		if (baseTimeInt === 800) return "1100";   // 선행 0 제거
		if (baseTimeInt === 500) return "800";    // 선행 0 제거
		if (baseTimeInt === 2300) return "0200";
		if (baseTimeInt === 200) return "0500";   // 선행 0 제거
		return null;
	}

	$(document).ready(function() {
		$("#btn_weather").click(function() {
			// 입력 값 유효성 검사
			if ($("#nx").val() === "" || $("#ny").val() === "" || $("#baseDate").val() === "" || $("#baseTime").val() === "") {
				alert("현재 위치를 입력 해주세요.");
				return;
			}

			let serviceKey = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";
			let nx = $("#nx").val();
			let ny = $("#ny").val();
			let baseDate = $("#baseDate").val();
			let baseTime = $("#baseTime").val();
			let pageNo = 1;
			let numOfRows = 100; // 조회할 데이터 개수
			let dataType = "JSON";

			// baseTime을 기준으로 이전, 이후 시간대 데이터 요청
			let prevBaseTime = getPrevBaseTime(baseTime);  // 이전 baseTime 계산
			let nextBaseTime = getNextBaseTime(baseTime);  // 이후 baseTime 계산

			// baseTime이 잘못되었을 경우 처리
			if (!prevBaseTime || !nextBaseTime) {
				alert("올바른 baseTime 값이 아닙니다. 다시 확인해주세요.");
				return;
			}

			// 두 시간대에 대해 API 호출
			let apiUrl1 = `https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=${serviceKey}&pageNo=${pageNo}&numOfRows=${numOfRows}&dataType=${dataType}&base_date=${baseDate}&base_time=${prevBaseTime}&nx=${nx}&ny=${ny}`;
			let apiUrl2 = `https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=${serviceKey}&pageNo=${pageNo}&numOfRows=${numOfRows}&dataType=${dataType}&base_date=${baseDate}&base_time=${nextBaseTime}&nx=${nx}&ny=${ny}`;

			console.log("API1 호출 URL:", apiUrl1);
			console.log("API2 호출 URL:", apiUrl2);

			// 첫 번째 API 호출 (이전 시간대 데이터)
			$.ajax({
				url: apiUrl1,
				type: "GET",
				dataType: "json",
				success: function(response) {
					console.log("API1 응답:", response);

					if (response.response.header.resultCode !== "00") {
						console.error("API1 호출 오류:", response.response.header.resultMsg);
						return;
					}

					if (!response.response.body.items || response.response.body.items.item.length === 0) {
						console.error("API1 응답에 데이터가 없습니다.");
						return;
					}

					let items = response.response.body.items.item;
					handleWeatherData(items);
				},
				error: function(error) {
					console.error("API1 호출 오류:", error);
				}
			});

			// 두 번째 API 호출 (이후 시간대 데이터)
			$.ajax({
				url: apiUrl2,
				type: "GET",
				dataType: "json",
				success: function(response) {
					console.log("API2 응답:", response);

					if (response.response.header.resultCode !== "00") {
						console.error("API2 호출 오류:", response.response.header.resultMsg);
						return;
					}

					if (!response.response.body.items || response.response.body.items.item.length === 0) {
						console.error("API2 응답에 데이터가 없습니다.");
						return;
					}

					let items = response.response.body.items.item;
					handleWeatherData(items);
				},
				error: function(error) {
					console.error("API2 호출 오류:", error);
				}
			});

			function handleWeatherData(items) {
				let resultTable = $("#resultTable tbody");
				resultTable.empty(); // 기존 테이블 초기화

				let weatherDataByTime = {};  // 시간별 데이터 저장

				items.forEach(function(item) {
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
							pcp: "-",
							vec: "-",
							wsd: "-",
							sno: "-"
						};
					}

					// 카테고리별로 데이터 추가
					if (item.category === "TMP") {
						weatherDataByTime[timeKey].temp = item.fcstValue || "-"; // 값이 없으면 "-" 처리
					}
					if (item.category === "SKY") {
						weatherDataByTime[timeKey].sky = item.fcstValue ? code_value("SKY", item.fcstValue) : "-"; // 값이 없으면 "-" 처리
					}
					if (item.category === "PTY") {
						weatherDataByTime[timeKey].pty = item.fcstValue || "-"; // 값이 없으면 "-" 처리
					}
					if (item.category === "REH") {
						weatherDataByTime[timeKey].humidity = item.fcstValue ? `${item.fcstValue}%` : "-"; // 값이 없으면 "-" 처리
					}
					if (item.category === "VEC") {
						weatherDataByTime[timeKey].vec = item.fcstValue ? `${item.fcstValue}deg` : "-"; // 값이 없으면 "-" 처리
					}
					if (item.category === "WSD") {
						weatherDataByTime[timeKey].wsd = item.fcstValue ? `${item.fcstValue}m/s` : "-"; // 값이 없으면 "-" 처리
					}
					if (item.category === "UUU") {
						weatherDataByTime[timeKey].uuu = item.fcstValue ? `${item.fcstValue}m/s` : "-"; // 값이 없으면 "-" 처리
					}
					if (item.category === "VVV") {
						weatherDataByTime[timeKey].vvv = item.fcstValue ? `${item.fcstValue}m/s` : "-"; // 값이 없으면 "-" 처리
					}
					if (item.category === "PCP") {
						weatherDataByTime[timeKey].pcp = item.fcstValue || "-"; // 값이 없으면 "-" 처리
					}
					if (item.category === "SNO") {
						weatherDataByTime[timeKey].sno = item.fcstValue || "-"; // 값이 없으면 "-" 처리
					}
				});

				let count = 0;
				for (let time in weatherDataByTime) {
					let weather = weatherDataByTime[time];

					// 9개 시간까지만 출력
					if (count >= 9) break;

					let row = $("<tr></tr>");
					row.append(`<td>${weather.date}</td>`);
					row.append(`<td>${weather.time}</td>`);
					row.append(`<td>${weather.sky}</td>`);
					row.append(`<td>${weather.temp}℃</td>`);
					row.append(`<td>${weather.pty}</td>`);
					row.append(`<td>${weather.pcp}</td>`);
					row.append(`<td>${weather.humidity}</td>`);
					row.append(`<td>${weather.vec}<br>${weather.wsd}</td>`);
					row.append(`<td>${weather.uuu}<br>${weather.vvv}</td>`);

					resultTable.append(row);
					count++;
				}

				$("#resultTable").show();  // 테이블 보이도록
			}

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
		});
	});

});*/



$(function() {
    $(document).ready(function() {
        $("#btn_weather").click(function() {
            // 입력 값 유효성 검사
            if ($("#nx").val() === "" || $("#ny").val() === "" || $("#baseDate").val() === "" || $("#baseTime").val() === "") {
                alert("현재 위치를 입력 해주세요.");
                return;
            }

            // 파라미터 설정
            let serviceKey = "Gow%2FB%2BpvwKtRdRGfWEsPYdmR4X8u8LB342Dka9AaCg6XgZaYHeeOBcWH8aK9VT%2BfYSDLtu0o9k6WY%2BRp7E00ZA%3D%3D";
            let nx = $("#nx").val();
            let ny = $("#ny").val();
            let baseDate = $("#baseDate").val();
            let baseTime = $("#baseTime").val();
            let pageNo = 1;
            let numOfRows = 100;
            let dataType = "JSON";

            // URL 동적 생성
            let apiUrl = `https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=${serviceKey}&pageNo=${pageNo}&numOfRows=${numOfRows}&dataType=${dataType}&base_date=${baseDate}&base_time=${baseTime}&nx=${nx}&ny=${ny}`;

            console.log("API 호출 URL:", apiUrl);

            // Ajax 호출
            $.ajax({
                url: apiUrl,
                type: "GET",
                dataType: "json",
                success: function(response) {
                    console.log("API 응답:", response);

                    if (response.response.header.resultCode === "00") {
                        let items = response.response.body.items.item;
                        console.log("응답 받은 날씨 데이터:", items);

                        let resultTable = $("#resultTable tbody");
                        resultTable.empty(); // 기존 테이블 초기화

                        let weatherDataByTime = {};

                        // 현재 시간 계산
                        let now = new Date();
                        let currentDate = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, "0")}${String(now.getDate()).padStart(2, "0")}`;
                        let currentHour = String(now.getHours()).padStart(2, "0") + "00";

                        // 일몰시간 계산 (예: 18:00을 기준으로 설정)
                        let sunsetTime = 18; // 일몰시간을 18:00으로 가정
                        let isDayTime = now.getHours() < sunsetTime; // 낮/밤 여부

                        // 시간별 데이터 정리
                        items.forEach(function(item) {
                            let timeKey = `${item.fcstDate} ${item.fcstTime}`;
                            if (!weatherDataByTime[timeKey]) {
                                weatherDataByTime[timeKey] = {
                                    date: item.fcstDate,
                                    time: item.fcstTime,
									sky: "-",          // 하늘 상태 (예: 맑음, 구름 많음, 흐림 등)
									pty: "-",          // 강수 형태 (예: 없음, 비, 눈, 비/눈, 빗방울 등)
									temp: "-",         // 기온 (예: 20℃)
									humidity: "-",     // 습도 (예: 50%)
									lgt: "-",          // 적설량 (예: 0cm) -> 단기예보에서 제공되는 데이터는 없을 수 있음
									vvv: "-",          // 남북풍 (예: -1.2m/s)
									uuu: "-",          // 동서풍 (예: 1.5m/s)
									pcp: "-",          // 강수량 (예: 0mm) -> 강수량이 없는 경우 "0mm" 또는 "-"로 처리됨
									vec: "-",          // 풍향 (예: 270deg, 0deg 등) -> 0~360도 사이의 값으로 풍향을 나타냄
									wsd: "-",          // 풍속 (예: 5m/s)
									sno: "-"           // 적설량 (예: 0cm) -> 일부 API에서 제공되는 데이터, 없을 수도 있음
                                };
                            }

                            if (item.category === "TMP") {
                                weatherDataByTime[timeKey].temp = item.fcstValue || "-";
                            }
                            if (item.category === "SKY") {
                                weatherDataByTime[timeKey].sky = item.fcstValue ? code_value("SKY", item.fcstValue) : "-";
                            }
                            if (item.category === "PTY") {
                                weatherDataByTime[timeKey].pty = item.fcstValue || "-";
                            }
                            if (item.category === "REH") {
                                weatherDataByTime[timeKey].humidity = item.fcstValue ? `${item.fcstValue}%` : "-";
                            }
                            if (item.category === "VEC") {
                                weatherDataByTime[timeKey].vec = item.fcstValue ? `${item.fcstValue}deg` : "-";
                            }
                            if (item.category === "WSD") {
                                weatherDataByTime[timeKey].wsd = item.fcstValue ? `${item.fcstValue}m/s` : "-";
                            }
                            if (item.category === "UUU") {
                                weatherDataByTime[timeKey].uuu = item.fcstValue ? `${item.fcstValue}m/s` : "-";
                            }
                            if (item.category === "VVV") {
                                weatherDataByTime[timeKey].vvv = item.fcstValue ? `${item.fcstValue}m/s` : "-";
                            }
                            if (item.category === "PCP") {
                                weatherDataByTime[timeKey].pcp = item.fcstValue || "-";
                            }
                            if (item.category === "SNO") {
                                weatherDataByTime[timeKey].sno = item.fcstValue || "-";
                            }
                        });

                        let count = 0;
                        for (let time in weatherDataByTime) {
                            let weather = weatherDataByTime[time];

                            if (count >= 8) break;

                            let formatted = formatTime(weather.date, weather.time);
                            let row = $("<tr></tr>");
                            row.append(`<td>${formatted.date}</td>`);
                            row.append(`<td>${formatted.time}</td>`);

                            // sky 상태가 '맑음'일 때 낮/밤 구분 추가
                            let skyLabel = weather.sky;
                            if (skyLabel === "맑음") {
                                skyLabel = isDayTime ? "맑음(낮)" : "맑음(밤)";
                            }

                            let weatherImg = getWeatherImage(weather.sky);
                            row.append(`<td><img src="${weatherImg}" alt="weather icon" style="width: 50px; height: 50px; text-align: center;"/><br>${skyLabel}</td>`);
                            row.append(`<td>${weather.temp}℃</td>`);
                            row.append(`<td>${weather.pty}%</td>`);
                            row.append(`<td>${weather.pcp}</td>`);
                            row.append(`<td>${weather.humidity}</td>`);
                            let windImgData = getWindDirectionImage(weather.vec);
                            row.append(
                                `<td><img src="${windImgData.image}" alt="wind direction" style="width: 50px; height: 50px; transform: rotate(${windImgData.rotation}deg); transform-origin: center;" /><br>${weather.wsd}</td>`
                            );
                            row.append(`<td>${weather.uuu}<br>${weather.vvv}</td>`);

                            resultTable.append(row);
                            count++;
                        }

                        $("#resultTable").show();

                    } else {
                        alert("예보 데이터가 없습니다.");
                    }

                },
                error: function(error) {
                    console.error("API 호출 오류:", error);
                    alert("API 호출 중 오류가 발생했습니다.");
                },
            });

            // 날씨 상태에 따른 이미지 반환
            function getWeatherImage(sky) {
                switch (sky) {
                    case "맑음":
                        return "images/weather/맑음.gif";
                    case "맑음(밤)":
                        return "images/weather/맑음밤.gif";
                    case "구름 많음":
                        return "images/weather/구름많음.gif";
                    case "흐림":
                        return "images/weather/흐림아침.gif";
                    case "흐림밤":
                        return "images/weather/흐림밤.gif";
                    case "비":
                        return "images/weather/비.gif";
                    case "눈":
                        return "images/weather/함박눈.gif";
                    default:
                        return "images/weather/default.gif";
                }
            }

            // 풍향에 따른 이미지 반환
            function getWindDirectionImage(direction) {
                if (!direction) return { image: "images/weather/value/ARROW.png", rotation: 0 };
                let deg = parseFloat(direction);
                return { image: "images/weather/value/ARROW.png", rotation: deg };
            }

            // 풍속에 따른 이미지 반환
            /*function getWindSpeedImage(speed) {
                if (!speed) return "images/default.png";
                let speedValue = parseFloat(speed);
                if (speedValue < 2) return "images/weather/value/light_wind.png"; // 약한 바람
                if (speedValue < 5) return "images/weather/value/moderate_wind.png"; // 중간 바람
                return "images/weather/value/strong_wind.png"; // 강한 바람
            }*/

            function code_value(category, code) {
                let value = "-";
                if (code) {
                    if (category === "SKY") {
                        if (code === "1") value = "맑음";
						else if (code === "2") value = "밤";
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
        });
    });

    function formatTime(date, time) {
        let hour = parseInt(time.slice(0, 2));
        let formattedHour = hour > 12 ? hour - 12 : hour; // 12시간제로 변환
        let ampm = hour >= 12 ? "오후" : "오전"; // 오전/오후 구분

        let formattedTime = `${ampm} ${formattedHour}:00`;

        let formattedDate = `${date.slice(0, 4)}-${date.slice(4, 6)}-${date.slice(6, 8)}`;

        return { date: formattedDate, time: formattedTime };
    }
});
