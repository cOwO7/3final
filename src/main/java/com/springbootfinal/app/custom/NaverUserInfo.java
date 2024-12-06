package com.springbootfinal.app.custom;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverUserInfo implements OAuth2User {
    
    private String id;
    private String name;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    
    // 기본 생성자 수정
    public NaverUserInfo(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")); // 기본 권한 설정
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.singletonMap("email", this.email); // 사용자 정보
    }

    @Override
    public String getName() {
        return this.name;  // OAuth2User의 getName() 오버라이드
    }

    public String getEmail() {
        return email;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Naver API에서 사용자 정보를 받아올 수 있도록 메서드 작성
    public static NaverUserInfo fromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 네이버 사용자 정보 받아오기
        String id = (String) attributes.get("id");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        return new NaverUserInfo(id, name, email);
    }
    
    @GetMapping("/main")
    public String main(HttpSession session, Model model) {
        NaverUserInfo member = (NaverUserInfo) session.getAttribute("member");
        
        if (member != null) {
            model.addAttribute("member", member);
            model.addAttribute("social", member);
        } else {
            model.addAttribute("error", "로그인 정보가 없습니다.");
        }
        
        return "main";  // main 페이지로 리디렉션
    }
}
