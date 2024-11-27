package com.springbootfinal.app.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll() // /error 경로 허용
                .requestMatchers(new AntPathRequestMatcher("/weather/**")).permitAll() // 날씨 경로 허용
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll() // 기타 모든 요청 허용
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/h2-console/**"),
                new AntPathRequestMatcher("/error") // /error 경로 CSRF 비활성화
            ))
            .csrf(csrf -> csrf.disable()) // CSRF 전체 비활성화 (필요 시)
            .logout(logout -> logout
                .logoutSuccessUrl("/loginForm")
                .invalidateHttpSession(true)
            );

        return http.build();
    }
}
	
	
	
	
	
	
	
	
	

	/*	@Bean
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
	
