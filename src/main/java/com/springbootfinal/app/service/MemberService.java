package com.springbootfinal.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.springbootfinal.app.domain.Member;
import com.springbootfinal.app.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberService {
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	@Lazy
	private PasswordEncoder passwordEncoder;
	
	// MemberMapper를 이용해 회원 정보를 수정하는 메서드
	public void updateMember(Member member) {
		
		member.setPass(passwordEncoder.encode(member.getPass()));
		
		memberMapper.updateMember(member);
	}
	
	// 회원 탈퇴 처리 메서드
    public void deletMember(String id) {
        memberMapper.deleteMember(id);
    }
	
	
	// 회원 정보 수정시 기본 비밀번호가 맞는지 체크하는 메서드
	public boolean memberPassCheck(String id, String pass) {
		
		String dbPass = memberMapper.memberPassCheck(id);
		
		boolean result = false;
		
		if(passwordEncoder.matches(pass, dbPass)) {
			
			result = true;
		}
		
		return result;
	}
	
	
	public void addMember(Member member) {
		
		// 비밀번호 암호화 저장
		member.setPass(passwordEncoder.encode(member.getPass()));
		memberMapper.addMember(member);
		
		log.info(member.getPass());
	}
	
	public boolean overlapIdCheck(String id) {
		
		Member member = memberMapper.getMember(id);
		
		if(member == null) {
			
		return false;
		
		}
		
		return true;
	}
	
	// 회원 로그인을 처리하고 결과를 반환하는 메서드
	// 반환값 : -1 = 아이디 없음, 0 = 비밀번호 틀림, 1 = 로그인 성공
	public int login(String id, String pass) {
		int result = -1;
		Member m = memberMapper.getMember(id);
		
		// id가 존재하지 않으면 : -1
		if(m == null) {
			
			return result;
		}
		// 로그인 성공 : 1
		if(passwordEncoder.matches(pass, m.getPass())) {
			result = 1;
		} else {
			result = 0;
		}
		
		return result;
	}
	
	public Member getMember(String id) {
		return memberMapper.getMember(id);
	}
}
