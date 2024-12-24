package com.springbootfinal.app.custom;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
	    HttpSession session = request.getSession();
	    Object principal = authentication.getPrincipal();  // 로그인된 사용자 정보
	    
	    if (principal instanceof NaverUserInfo) {
	        NaverUserInfo naverUserInfo = (NaverUserInfo) principal;
	        session.setAttribute("social", naverUserInfo);  // 세션에 social 정보 저장
	    }
	    System.out.println("세션에 저장된 social 정보: " + session.getAttribute("social"));

	    // 리디렉션
	    response.sendRedirect("/main");  // 원하는 페이지로 리디렉션
	}

	
	
	
	
}