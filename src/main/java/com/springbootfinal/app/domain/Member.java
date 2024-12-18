package com.springbootfinal.app.domain;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

	private int power;
	private String id;
	private String name;
	private String pass;
	private String mobile;
	private Timestamp regDate;
	private String birthdate;
	private String alarm;
	private String vip;
	
	
	// 소셜 로그인 관련 필드 추가
	// private String provider;         // 소셜 로그인 제공자
	// private String providerId;       // 소셜 로그인 사용자 고유 ID
}
