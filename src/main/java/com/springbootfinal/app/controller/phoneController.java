package com.springbootfinal.app.controller;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.springbootfinal.app.service.phoneService;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

//getmapping으로 saveOne 치면 메세지가 수신번호로 들어감 
// 수신전화번호와 발신전화번호는 디코딩해놓음 만약에 수신전화번호랑 발신전화번호 바꿀거면 인코딩해놓고 그걸 넣어놔야함

@Slf4j
@Controller
public class phoneController {
	
	@Autowired
	private phoneService phoneservice;
	
	
	private String CallerPhoneNumber="MDEwMzUyMTAwMjg=";//발신전화번호 인코딩

	
	 private static final int SALT_LENGTH = 32;
	    private static SecureRandom secureRandom;

	    static {
	        try {
	            secureRandom = SecureRandom.getInstanceStrong();
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException("SecureRandom Instance not created...", e);
	        }
	    }

	    final DefaultMessageService messageService;

	    public phoneController() {
	        // 반드시 계정 내 등록된 유효한 API 키, API Secret Key를 입력
	        this.messageService = NurigoApp.INSTANCE.initialize("NCSIXPHQXJK4QPJ3", "APYPGW2YIQ5J8KOCQLRFYJPIFBDJHPDX", "https://api.coolsms.co.kr");
	    }

		// 인증번호로 보낼 번호 (난수 생성)
	    public String createNumberKey() {
	        log.warn("createNumberKey...");

	        secureRandom = new SecureRandom();

	        int randomNumber = 100000 + secureRandom.nextInt(900000);
	        log.warn("randomNumber : " + randomNumber);

	        return String.valueOf(randomNumber);
	    }

	    // 단일 메시지 발송
	    
	    @GetMapping("/sendOne")
	    public SingleMessageSentResponse sendOne(){

	        String numberKey = createNumberKey();
	        
	        String ReceiverPhoneNumber = "MDEwNTg1Mjg2NTU=";//수신번호 인코딩
	        
	        String callerphonenumber = phoneservice.decodePhoneNumber(CallerPhoneNumber); //발신번호 인코딩 풀기(디코딩)
	        String receiverphonenumber = phoneservice.decodePhoneNumber(ReceiverPhoneNumber); //수신번호 인코딩 풀기(디코딩)
	        
	        Message message = new Message();
	        message.setFrom(callerphonenumber); // 발신번호 입력
	        message.setTo(receiverphonenumber);   // 수신번호 입력
	        message.setText("[SMS] 인증번호: "+numberKey+"를 입력하세요.");

	        log.warn("Sending message: " + message.getText());

	        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
	        log.warn("response: " + response);

	        return response;
	    }


	
}
