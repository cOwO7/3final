package com.springbootfinal.app.custom;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.servlet.http.HttpSession;

@SessionAttributes("social")
@Controller
public class SocialUserController {
	
	// 로그인 후 세션에 저장된 "social" 속성을 사용하여 홈 화면 표시
	@GetMapping("/main/social")
    public String main(HttpSession session, Model model) {
        // SecurityContext에서 Authentication을 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Authentication에서 Principal (OAuth2User) 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        if (oAuth2User != null) {
            // 세션에서 social 정보를 가져와서 모델에 추가
            String name = oAuth2User.getAttribute("name");  // 예시로 'name' 속성 사용
            model.addAttribute("username", name);
            model.addAttribute("social", oAuth2User);  // 전체 사용자 정보를 모델에 추가
        } else {
            model.addAttribute("error", "로그인 정보가 없습니다.");
        }

        return "main";  // main 페이지로 리디렉션
    }

    // 로그인 후 callback 처리하는 메소드
    @GetMapping("/login/callback")
    public String callback(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            // 사용자 정보 출력 (로그인 후 네이버 API에서 받은 사용자 정보)
            String userName = oauth2User.getAttribute("name");
            String userEmail = oauth2User.getAttribute("email");
            return "로그인 성공! 사용자 이름: " + userName + ", 이메일: " + userEmail;
        } else {
            return "로그인 실패!";
        }
    }
}