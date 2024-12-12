package com.springbootfinal.app.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.springbootfinal.app.domain.Member;
import com.springbootfinal.app.service.MemberService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


@Controller
@SessionAttributes("member")
@Slf4j
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping("/memberUpdateForm")
	public String updateForm() {
		log.info("로그인페이지 이동 오류5");
		return "member/memberUpdateForm";
	}
	
	// 회원 탈퇴 요청을 처리하는 메서드
	@PostMapping("/deleteMember")
    public ResponseEntity<Map<String, Object>> deleteMember(Authentication authentication) {
        String loggedInUsername = authentication.getName(); // 현재 로그인된 사용자 정보

        try {
            memberService.deletMember(loggedInUsername);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);  // 탈퇴 성공
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);  // 탈퇴 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
	
	@RequestMapping("/checkPassword")
    public ResponseEntity<Map<String, Object>> checkPassword(@RequestBody Map<String, String> body, Authentication authentication) {
        String loggedInUsername = authentication.getName(); // 현재 로그인된 사용자 정보
        String password = body.get("password");

        boolean isPasswordCorrect = memberService.memberPassCheck(loggedInUsername, password);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isPasswordCorrect);  // 비밀번호가 맞으면 true, 아니면 false
        
        return ResponseEntity.ok(response);
    }
	
	// 회원 수정 폼에서 들어오는 회원 정보 수정 요청을 처리하는 메서드
	@RequestMapping("/memberUpdateResult")
	public String memberUpdateResult(Model model, Member member, 
			@RequestParam("pass1") String pass1,
			@RequestParam("mobile1") String mobile1,
			@RequestParam("mobile2") String mobile2,
			@RequestParam("mobile3") String mobile3,
			@RequestParam("birthdate1") String birthdate1,
			@RequestParam("birthdate2") String birthdate2,
			@RequestParam("birthdate3") String birthdate3) {
		member.setPass(pass1);
		member.setMobile(mobile1 + "-" + mobile2 + "-" + mobile3);
		member.setBirthdate(birthdate1 + "-" + birthdate2 + "-" + birthdate3);
		
		// 회원 정보를 수정
		memberService.updateMember(member);
		log.info(member.getName() + "님 회원 수정 완료...");
		model.addAttribute("member", member);
		
		return "redirect:/main";
	}
	
	@RequestMapping("/joinResult")
	public String joinResult(Model model, Member member, 
			@RequestParam("pass1") String pass1,
			@RequestParam("mobile1") String mobile1,
			@RequestParam("mobile2") String mobile2,
			@RequestParam("mobile3") String mobile3,
			@RequestParam("birthdate1") String birthdate1,
			@RequestParam("birthdate2") String birthdate2,
			@RequestParam("birthdate3") String birthdate3) {
		member.setPass(pass1);
		member.setMobile(mobile1 + "-" + mobile2 + "-" + mobile3);
		member.setBirthdate(birthdate1 + "-" + birthdate2 + "-" + birthdate3);

		log.info("로그인페이지 이동 오류3");
		// 회원정보를 등록
		memberService.addMember(member);
		log.info(member.getName() + "님 회원가입 완료...");
		return "redirect:/loginForm";
	}
	
	@RequestMapping("/overlapIdCheck")
	public String overlapIdCheck(Model model, 
			@RequestParam("id")String id) {
		log.info("로그인페이지 이동 오류2");
		boolean overlap = memberService.overlapIdCheck(id);
		
		model.addAttribute("id", id);
		model.addAttribute("overlap", overlap);
		return "member/overlapIdCheck";
	}
	
	@GetMapping("/memberLogout")
	public String logout(HttpSession session) {
		session.invalidate();
		log.info("로그인페이지 이동 오류1");
		return "redirect:/main";
	}
	
	@PostMapping("/login")
	public String login(Model model, 
			@RequestParam("userId") String id,
			@RequestParam("pass") String pass,
			HttpSession session, HttpServletResponse response) 
					throws IOException {
		log.info("로그인페이지 이동 오류");
		int result = memberService.login(id, pass);
		
		if(result == -1) {
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("	alert('존재하지 않는 아이디 입니다.');");
			out.println("	history.back();");
			out.println("</script>");
			
			return null;
			
		} else if (result == 0) {
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("	alert('비밀번호가 다릅니다.');");
			out.println("	history.back();");
			out.println("</script>");
			
			return null;
		}
		
		Member member = memberService.getMember(id);
		session.setAttribute("isLogin", true);
		//session.setAttribute("m", member);
		model.addAttribute("member", member);
		//log.info(member.getId());
		System.out.println("member.name : " + member.getName());
		// 로그인이 성공하면
		return "redirect:main";
	}
}
