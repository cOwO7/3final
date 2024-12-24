package com.springbootfinal.app.service;

import java.util.List;

//import org.jboss.jandex.Main;
import com.sun.tools.javac.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springbootfinal.app.mapper.MainMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MainService {
	
	@Autowired
	private MainMapper mainMapper;
	
	public List<Main> mainList() {
		log.info("MainService: mainList()");
		return mainMapper.mainList();
	}
}
