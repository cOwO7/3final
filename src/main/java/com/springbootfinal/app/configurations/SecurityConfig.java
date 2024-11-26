package com.springbootfinal.app.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/*import com.springbootfinal.app.custom.CustomOAuth2LoginFailureHandler;
import com.springbootfinal.app.custom.CustomOAuth2LoginSuccessHandler;
import com.springbootfinal.app.service.SocialService;
import com.springbootfinal.app.mapper.SocialMemberMapper;*/
import com.springbootfinal.app.mapper.MemberMapper;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}
/*
		@Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	        http
	            .authorizeHttpRequests(authorize -> authorize
	                // 기본 페이지와 일부 공개 경로는 모든 사용자에게 허용
	                .requestMatchers( 
	                                 new AntPathRequestMatcher("/main"),
	                                 new AntPathRequestMatcher("/public/**"),
	                				 new AntPathRequestMatcher("/css/**"),
	                				 new AntPathRequestMatcher("/member/member**"),
	            					 new AntPathRequestMatcher("/js/**"),
	            					 new AntPathRequestMatcher("/bootstrap/**"),
									 new AntPathRequestMatcher("/images/**")).permitAll()
	                // 인증이 필요한 경로 설정
	                .requestMatchers(new AntPathRequestMatcher("/secure/**")).authenticated()
	                //.anyRequest().permitAll()
	                .anyRequest().authenticated()
	            )
	            .csrf(csrf -> csrf.disable())
	            .formLogin(formLogin -> formLogin
	                .loginPage("/loginForm")      // 사용자 정의 로그인 페이지 설정
	                .loginProcessingUrl("/login") // 로그인 처리 URL
	                .defaultSuccessUrl("/main", true) // 로그인 성공 시 기본 페이지로 리다이렉트
	                .permitAll()                   // 로그인 페이지는 누구나 접근 가능하도록 설정
	            )
	            .logout(logout -> logout
	                .logoutSuccessUrl("/main")  // 로그아웃 성공 시 리다이렉트 페이지
	                .invalidateHttpSession(true)
	            );
	            

	        return http.build();
	    }*/
	
	// 소셜
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
	            .requestMatchers(new AntPathRequestMatcher("/error")).permitAll() // /error 경로 허용
	            .requestMatchers(new AntPathRequestMatcher("/weather/**")).permitAll() // 날씨 경로 허용
	            .requestMatchers(new AntPathRequestMatcher("/**")).permitAll() // 기타 모든 요청 허용
	        )
	        .csrf(csrf -> csrf
	            .ignoringRequestMatchers(
	                new AntPathRequestMatcher("/h2-console/**"),
	                new AntPathRequestMatcher("/error") // /error 경로 CSRF 비활성화
	               // ,new AntPathRequestMatcher("/login/oauth2/code/**") // OAuth2 로그인 콜백 경로 CSRF 비활성화
	            )
	            .disable() // 전체 CSRF 비활성화 (필요시)
	        )
	        /*.oauth2Login(oauth2Login -> oauth2Login
	            .loginPage("/login") // 로그인 페이지 URL (선택사항)
	            .defaultSuccessUrl("/home", true) // 로그인 성공 후 리디렉션 URL (선택사항)
	            .failureUrl("/login?error") // 로그인 실패 후 리디렉션 URL (선택사항)
				
				.successHandler(new CustomOAuth2LoginSuccessHandler()) // 로그인 성공 후 핸들러 (선택사항)
				.failureHandler(new CustomOAuth2LoginFailureHandler()) // 로그인 실패 후 핸들러 (선택사항)
				 	        )*/
	        .formLogin(login -> login
	                .loginPage("/loginForm") // 로그인 페이지 경로
	                .defaultSuccessUrl("/main", true) // 로그인 후 리디렉션 URL
	                .failureUrl("/login?error=true") // 로그인 실패 시 리디렉션 URL
	                .permitAll() // 로그인 페이지는 모두 허용
	            )
	        .logout(logout -> logout
	            .logoutSuccessUrl("/loginForm") // 로그아웃 후 리디렉션 URL
	            .invalidateHttpSession(true) // 세션 무효화
	        );

	    return http.build();
	}


	/*@Bean
    public SocialService socialservice(
    		MemberMapper membermapper, 
    		SocialMemberMapper socialMemberMapper) {
        return new SocialService(membermapper, socialMemberMapper);
    }*/
}
