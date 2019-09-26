package com.gkx.cti.caas.feign;

import java.util.Map;

import feign.Headers;
import feign.Param;
import feign.RequestLine;


/**
 *<p>组织机构客户端</p>
 * @author 陈贤平
 * @time 2019年2月22日上午11:02:26
 * @version 1.0
 */
public interface OrganClient {
	
	
	
	/**
	 * <p>根据机构ID查询机构信息</p>
	 * @param id 机构ID
	 * @author 杨雪令
	 * @time 2019年3月20日
	 * @version 1.0
	 */
	@Headers({"Content-Type: application/json","Accept: application/json"})
	@RequestLine("GET /organ/get?id={id}")
	Map<String, String> get(@Param("id") String id);
	
}
