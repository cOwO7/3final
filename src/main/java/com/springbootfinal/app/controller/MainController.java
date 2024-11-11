package com.springbootfinal.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.springbootfinal.app.service.MainService;

@Controller
public class MainController {
	
	@Autowired
	private MainService mainService;

	@GetMapping({"/main"})
	public String mainList(Model model) { 
		model.addAttribute("mList", mainService.mainList());
		
		return "main";
	}
}
