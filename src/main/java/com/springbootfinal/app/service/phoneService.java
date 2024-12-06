package com.springbootfinal.app.service;

import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class phoneService {
	
	
	 // Base64 디코딩(발신 전화번호 디코딩)
    public String decodePhoneNumber(String encodedPhoneNumber) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedPhoneNumber);
        return new String(decodedBytes);
    }
	
	

}
