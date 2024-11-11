package com.springbootfinal.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.jboss.jandex.Main;

@Mapper
public interface MainMapper {

	public List<Main> mainList();
}
