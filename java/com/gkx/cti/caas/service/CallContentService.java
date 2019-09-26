package com.gkx.cti.caas.service;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.gkx.cti.caas.mapper.CallContentMapper;
import com.gkx.cti.caas.pojo.CallContent;

@Service
public class CallContentService {

	
	@Resource
	CallContentMapper callContentMapper;

	public List<CallContent> getHistoryContentList(int page, String string) {
		System.out.println("testefg");
		return callContentMapper.getHistoryContentList(10*(page-1),10*page,"%"+string+"%");
		
	}

	public List<CallContent> getContentBySessionid(String session_id) {
		return callContentMapper.getContentBySessionid(session_id);
	}

	public int getContentTotal(String string) {
		return callContentMapper.getContentTotal("%"+string+"%");
	}

	public List<CallContent> getContentByPge(int page,String string) {
		System.out.println("%"+string+"%");
		return callContentMapper.getHistoryContentList(10*(page-1),10*page,"%"+string+"%");
	}

	public List<CallContent> getContentBySearch(String search) {
		return callContentMapper.getContentBySearch(search);
	}

	

}
