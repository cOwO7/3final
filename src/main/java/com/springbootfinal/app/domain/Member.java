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
}
