package com.springbootfinal.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.springbootfinal.app.service.MainService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MainController {
	
	@Autowired
	private MainService mainService;

	
	  @GetMapping("/")
	    public String home() {
	        // 원하는 페이지로 리다이렉트
	        return "redirect:/main";
	    }
	
	@GetMapping("/main")
	public String main() { 
		log.info("main페이지 이동중 오류");
		return "main/main";
	}
}