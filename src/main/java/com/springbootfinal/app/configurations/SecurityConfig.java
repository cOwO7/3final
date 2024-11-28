package com.springbootfinal.app.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.springbootfinal.app.custom.CustomAuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화 설정
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http//.authorizeHttpRequests(authorizeHttpRequests -> 
       // authorizeHttpRequests
            .csrf().disable() // CSRF 보호 비활성화
            .authorizeRequests()
                .requestMatchers("/login","/**").permitAll()  // 로그인 경로 허용
                .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .successHandler(customAuthenticationSuccessHandler) // 로그인 성공 시 핸들러 설정
            .loginPage("/login")
            .defaultSuccessUrl("/main", true)
            .failureUrl("/login?error=true")
            .and()
            .logout()
                .logoutSuccessUrl("/login");

        return http.build();
    }
}














/*@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfiguration {
	
	
	@Bean // 비밀번호 암호화
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}
	
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/login", "/oauth2/authorization/**", "/error", "/weather/**").permitAll()  // 로그인 경로 허용
                    .anyRequest().authenticated()  // 다른 모든 요청은 인증 필요
            )
            .csrf(csrf -> csrf.disable())  // CSRF 비활성화 (필요 시)
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .loginPage("/login")  // 로그인 페이지 설정
                    .defaultSuccessUrl("/main", true)  // 기본 성공 페이지 (리디렉션) 설정
                    .successHandler(customAuthenticationSuccessHandler)  // 커스텀 성공 핸들러 설정
            )
            .logout(logout ->
                logout
                    .logoutSuccessUrl("/login")  // 로그아웃 후 리디렉션 설정
            );
    }
}*/