package com.springbootfinal.app.custom;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SocialUserController {
	
	@GetMapping("/home")
    public String home(@AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            model.addAttribute("username", user.getUsername());  // 네이버 로그인 후 사용자 정보
        } else {
            model.addAttribute("error", "로그인 실패");
        }
        return "main";  // 홈 페이지로 리다이렉트
    }
}