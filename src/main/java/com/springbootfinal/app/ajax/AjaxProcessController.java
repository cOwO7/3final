package com.springbootfinal.app.ajax;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springbootfinal.app.service.MemberService;

@RestController
public class AjaxProcessController {

	// 의존객체 주입
	@Autowired
	private MemberService memberService;
	
	// 비밀번호 확인 요청을 처리하는 메서드 - json {"result" : false }
	@GetMapping("passCheck.ajax")
	public Map<String, Boolean> memberPassCheck(
			@RequestParam("id")String id, 
			@RequestParam("pass")String pass) { 
		
		boolean result = memberService.memberPassCheck(id, pass);
		
		Map<String, Boolean> map = new HashMap<>();
		map.put("result", result);
		return map;
	}
}
