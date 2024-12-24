package com.springbootfinal.app.mapper;

import com.sun.tools.javac.Main;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MainMapper {

	public List<Main> mainList();
}
