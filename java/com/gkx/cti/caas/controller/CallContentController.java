package com.gkx.cti.caas.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gkx.cti.caas.pojo.CallContent;
import com.gkx.cti.caas.service.CallContentService;
import com.ql.util.api.ResponesData;

import com.gkx.cti.caas.util.SSHUtil;

@RestController
@RequestMapping("/txtcontent")
public class CallContentController {

	@Resource
	private CallContentService callContentService;

	@RequestMapping("/getnowcontent")
	/*
	 * 获取实时的监控信息
	 * 
	 */
	public void getnowcontent(@RequestParam("session") String session, @RequestParam("phonenumber") String phonenumber,
			@RequestParam("content") String content, @RequestParam("content") String spender) {
//		testService.spendTextService(productCode);
		System.out.println(session + phonenumber + content + spender);

	}

	@RequestMapping("/initContentPage")
	/*
	 * 初始化历史所有话务信息列表
	 * 
	 */
	public Object[] initContentPage() {
		Object[] objs = new Object[2];
		objs[0] = callContentService.getHistoryContentList(1, "");
		objs[1] = callContentService.getContentTotal("");
		return objs;
	}
	
	@RequestMapping("/getContentByPage")
	/*
	 * 通过页数来获取历史话务数据
	 * 
	 */
	public  Object[] getContentByPage(@RequestParam("page") int page,@RequestParam("search") String search) {
		Object[] objs = new Object[2];
		objs[0] = callContentService.getContentByPge(page,search);
		objs[1] = callContentService.getContentTotal(search);
		System.out.println(search);
		return objs;
	}
	
	@RequestMapping("/getContentBySearch")
	/*
	 * 通过搜索来获取历史话务数据
	 * 
	 */
	public List<CallContent> getContentBySearch(@RequestParam("page") String search) {
		return callContentService.getContentBySearch(search);
	}

	@RequestMapping("/getContentBySessionid")
	/*
	 * 根据session_id获取某个会话历史的监控信息
	 * 
	 */
	public List<CallContent> getContentBySessionid(@RequestParam("session_id") String session_id) {
		System.out.println(session_id);
		List<CallContent> callContentlist = callContentService.getContentBySessionid(session_id);
		return callContentlist;

	}

	@SuppressWarnings("static-access")
	@RequestMapping("/getservername")
	public ResponesData getservername() {
		SSHUtil ssh = new SSHUtil();
		System.out.println("*****");
		String cmdString = "pwd && ssh root@125.91.35.169 && ls ";
		try {
			ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("********************");
		return ResponesData.success("success!!");
	}
}
