package com.springbootfinal.app.custom;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    public CustomOAuth2UserService() {
        this.delegate = new DefaultOAuth2UserService();  // 기본 구현체 사용
    }

    public CustomOAuth2UserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate) {
        this.delegate = delegate;
    }

 
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        
        // 네이버 사용자 정보를 NaverUserInfo로 변환
        NaverUserInfo naverUserInfo = NaverUserInfo.fromOAuth2User(oAuth2User);
        
        // 인증 객체 생성
        Authentication authentication = new OAuth2AuthenticationToken(
                naverUserInfo, 
                naverUserInfo.getAuthorities(), 
                userRequest.getClientRegistration().getClientId()
        );
        
        // SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 세션에 로그인 상태 반영
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        session.setAttribute("social", naverUserInfo);  // 세션에 social 정보 저장
        
        System.out.println("세션에 social 정보 저장: " + session.getAttribute("social"));  // 로그 확인

        return oAuth2User;  // 원본 OAuth2User 반환
    }

}

