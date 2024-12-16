// DOM이 준비되면 실행될 콜백 함수
$(function() {
	
	
	$("#memberUpdateForm").on("submit", function(e) {
			
			// 비밀번호 확인 유무 체크
			if(! $("#btnPassCheck").attr("disabled")) {
				alert("기존 비밀번호를 확인해 주세요");
				return false;
			}
			
			return joinFormCheck();
		});
	
	// 기존 비밀번호 입력 확인
	$("#btnPassCheck").click(function() {
		let oldPass = $("#oldPass").val();
		let oldId = $("#id").val();
		
		if($.trim(oldPass).length == 0) {
			alert("비밀번호를 입력해 주세요.");
			
			return false;
		}
		
		// 서버로 보낼 데이터 id=id&pass=pass
		let data = "id=" + oldId + "&pass=" + oldPass;
		
		// 비동기 통신(Ajax)		
		// XMLHttpRequest, ES6 - fetch API - 비동기 처리 Promise - Axios
		// jQuery
		$.ajax({
			url: "passCheck.ajax",
			type: "get",
			data: data,
			dataType: "json",
			success: function(resData) {
				console.log(resData.result);
				if(resData.result) {
					alert("비밀번호가 확인되었습니다.");
					$("#btnPassCheck").attr("disabled", true)
					$("#old").attr("readonly", true);
					$("#pass1").focus();
				} else {
					alert("기존 비밀번호가 틀립니다.");
					$("#oldPass").val("").focus();
				}
			},
			error: function(xhr, status) {
				console.log("error : " + status);
			}
		}); 
	});

	// 회원가입 폼이 전송될 떄 유효성 검사
	$("#joinForm").submit(function() {
		return joinFormCheck(true);
	});

	$("#selectDomain").on("change", function() {

		let str = $(this).val();

		if (str == '직접입력') {
			$("#emailDomain").val("");
			$("#emailDomain").attr("readonly", false);
			$("#emailDomain").focus();

		} else if (str == '네이버') {
			$("#emailDomain").val("naver.com");
			$("#emailDomain").attr("readonly", true);

		} else if (str == '다음') {
			$("#emailDomain").val("daum.nat");
			$("#emailDomain").attr("readonly", true);

		} else if (str == '한메일') {
			$("#emailDomain").val("hanmail.com");
			$("#emailDomain").attr("readonly", true);

		} else if (str == '구글') {
			$("#emailDomain").val("gmail.com");
			$("#emailDomain").attr("readonly", true);
		}


	});

	$("#btnZipcode").click(findZipcode)


	// 중복확인 사용버튼 클릭시 값 이동
	$("#btnIdCheckClose").on("click", function() {
		let id = $(this).attr("data-id-value");
		// 사용 버튼을 클릭시 새창의 새로운 id값을 메인페이지 id에 넣어준다.
		opener.document.joinForm.id.value = id;

		// 사용버튼을 클릭시 joinForm의 hidden input의 value를 true로 바꿔준다.
		opener.document.joinForm.isIdCheck.value = true;
		window.close();
	});

	// 취소를 누르면 창이 닫힘
	$("#can").on("click", function() {

		window.close();
	});

	// 새 창에서 폼이 전송될 떄 유효성 검사
	$("#idCheckForm").on("submit", function() {
		let id = $("#checkId").val();

		if (id.length == 0) {
			alert("아이디를 입력해주세요");
			return false;
		}

		if (id.length < 5) {
			alert("아이디좀 늘려주삼~");
			return false;
		}

	});

	// 아이디 중복확인이 클릭되면 나타나는 이벤트
	$("#btnOverlapId").on("click", function() {
		let id = $("#id").val();
		url = "overlapIdCheck?id=" + id;

		if (id.length == 0) {
			alert("아이디를 입력해주세요");
			return false;
		}

		if (id.length < 5) {
			alert("아이디좀 늘려주삼~");
			return false;
		}
		window.open(url, "idCheck", "toolbar=no, scrollbars=no, resizeable=no, "
			+ "status=no, menubar=no, width=500, height=330 ");
	});



	// 아이디 입력란에서 키보드 키가 눌렀다가 떨어질 때 keyUp
	//document.querySelector("#id").addEventListener("keyup", () => {});
	$("#id").on("keyup", function() {
		// new RegExp() 아래꺼랑 동일 
		let regExp = /[^a-zA-Z0-9]/gi;
		if (regExp.test($("#id").val())) {
			alert("영문자와 숫자만 입력할 수 있습니다.")
			$(this).val($(this).val().replace(regExp, ""))
		}

	});

	$("#pass1").on("keyup", inputCharReplace);
	$("#pass2").on("keyup", inputCharReplace);
	$("#emailId").on("keyup", inputCharReplace);
	$("#emailDomain").on("keyup", inputEmailDomainReplace);



	$("#loginForm").submit(function() {
		var id = $("#userId").val();
		var pass = $("#userPass").val();

		if (id.length <= 0) {

			alert("아이디를 입력해주세요.")
			$("#userId").focus();
			return false;
		}

		if (pass.length <= 0) {

			alert("비밀번호를 입력해주세요.")
			$("#userPass").focus();
			return false;
		}
	});

	$("#modalLoginForm").submit(function() {
		var id = $("#modalUserId").val();
		var pass = $("#modalUserPass").val();

		if (id.length <= 0) {

			alert("아이디를 입력해주세요.")
			$("#modalUserId").focus();
			return false;
		}
		if (pass.length <= 0) {

			alert("비밀번호를 입력해주세요.")
			$("#modalUserPass").focus();
			return false;
		}
	});
	
	// 탈퇴 버튼 클릭 시 처리
	    $("#deleteMember").click(function() {
	        let password = $("#oldPass").val();
	        if (!password) {
	            alert("비밀번호를 확인해주세요.");
	            return false;
	        }

	        // 비밀번호 확인 후, 탈퇴 처리 로직 추가
	        $.ajax({
	            url: "/deleteMember",  // 회원 탈퇴 API URL
	            type: "post",
	            data: { password: password },
	            success: function(response) {
	                if (response.success) {
	                    alert("회원 탈퇴가 완료되었습니다.");
	                    window.location.href = "/logout";  // 로그아웃 후 리다이렉트
	                } else {
	                    alert("탈퇴 처리 중 오류가 발생했습니다.");
	                }
	            },
	            error: function(xhr, status, error) {
	                console.log("탈퇴 처리 실패:", error);
	                alert("탈퇴 처리 중 오류가 발생했습니다.");
	            }
	        });
	    });
	
}); // END $(function() {});

function findZipcode() {
	new daum.Postcode({
		oncomplete: function(data) {
			// 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

			// 각 주소의 노출 규칙에 따라 주소를 조합한다.
			// 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
			var addr = ''; // 주소 변수
			var extraAddr = ''; // 참고항목 변수

			addr = data.roadAddress;

			// 법정동명이 있을 경우 추가한다. (법정리는 제외)
			// 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
			if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
				extraAddr += data.bname;
			}
			// 건물명이 있고, 공동주택일 경우 추가한다.
			if (data.buildingName !== '' && data.apartment === 'Y') {
				extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
			}
			// 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
			if (extraAddr !== '') {
				extraAddr = ' (' + extraAddr + ')';
			}

			// 상세주소까지 조합된 주소 정보
			addr += extraAddr;

			// 우편번호는 우편번호 입력란에
			$("#zipcode").val(data.zonecode);

			// 주소는 주소 입력란에 입력한다
			$("#address1").val(addr);

			// 상세 주소에 커서가 가게한다
			$("#address2").focus();
		}
	}).open();
}

function joinFormCheck(idJoinForm) {

	let name = $("#name").val();
	let id = $("#id").val();
	let pass1 = $("#pass1").val();
	let pass2 = $("#pass2").val();
	let zipcode = $("#zipcode").val();
	let address1 = $("#address1").val();
	let address2 = $("#address2").val();
	let emailId = $("#emailId").val();
	let emailDomain = $("#emailDomain").val();
	let mobile2 = $("#mobile2").val();
	let mobile3 = $("#mobile3").val();
	let isIdCheck = $("#isIdCheck").val();

	if (name.length == 0) {
		alert("이름을 입력해주세요.")
		$("#name").focus();
		return false;
	} else if (id.length == 0) {
		alert("아이디를 입력해주세요.")
		$("#id").focus();
		return false;
	} else if (pass1.length == 0) {
		alert("비밀번호를 입력해주세요.")
		$("#pass1").focus();
		return false;
	} else if (pass2.length == 0) {
		alert("비밀번호를 확인해주세요.")
		$("#pass2").focus();
		return false;
	} else if (zipcode.length == 0) {
		alert("우편번호를 입력해주세요.")
		$("#zipcode").focus();
		return false;

	} else if (address1.length == 0) {
		alert("주소를 입력해주세요.")
		$("#address1").focus();
		return false;
	} else if (address2.length == 0) {
		alert("상세주소를 입력해주세요.")
		$("#address2").focus();
		return false;
	} else if (emailId.length == 0) {
		alert("이메일을 입력해주세요.")
		$("#emailId").focus();
		return false;
	} else if (emailDomain.length == 0) {
		alert("이메일을 확인해주세요.")
		$("#pass2").focus();
		return false;
	} else if (mobile2.length == 0 || mobile3.length == 0) {
		alert("휴대폰 번호를 입력해주세요.")
		$("#mobile2").focus();
		return false;
	} else if (isIdCheck.length == 0) {
		alert("아이디 중복확인을 해주세요.")
		$("#isIdCheck").focus();
		return false;
	}
}

function inputCharReplace() {
	let regExp = /[^a-zA-Z0-9]/gi;
	if (regExp.test($(this).val())) {
		alert("영문자와 숫자만 입력할 수 있습니다.")
		$(this).val($(this).val().replace(regExp, ""))
	}
}

function inputEmailDomainReplace() {
	let regExp = /[^a-zA-Z0-9\.]/gi;
	if (regExp.test($(this).val())) {
		alert("영문자, 숫자, 특수문자만 입력할 수 있습니다.")
		$(this).val($(this).val().replace(regExp, ""))
	}
}
