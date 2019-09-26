package com.gkx.cti.caas.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkx.cti.caas.feign.UserClient;
import com.gkx.cti.caas.pojo.Aliyun_recog;
import com.gkx.cti.caas.pojo.Aliyun_synth;
import com.gkx.cti.caas.pojo.Bd_recog;
import com.gkx.cti.caas.pojo.Bd_synth;
import com.gkx.cti.caas.pojo.Logger;
import com.gkx.cti.caas.pojo.Mrcp;
import com.gkx.cti.caas.pojo.Pq_recog;
import com.gkx.cti.caas.pojo.Pq_synth;
import com.gkx.cti.caas.pojo.Server;
import com.gkx.cti.caas.pojo.Sxs_recog;
import com.gkx.cti.caas.pojo.Sxs_synth;
import com.gkx.cti.caas.pojo.Unimrcpserver;
import com.gkx.cti.caas.service.PluginService;
import com.gkx.cti.caas.service.ServerInfoServer;
import com.gkx.cti.caas.util.FileUtil;
import com.gkx.cti.caas.util.SSHUtil;
import com.gkx.cti.caas.util.Thread;
import com.ql.util.api.ResponesData;

@RestController
@SuppressWarnings("static-access")
@RequestMapping("/plugin")
public class PluginController {
	@Resource
	private PluginService pluginservice;
	@Resource
	private ServerInfoServer serverinfoserver;

	@Autowired
	private UserClient userClient;
		
	@RequestMapping("/getdata")
	public ResponesData getdata(@RequestParam("plugin") String pluginname ,@RequestParam("serverip") String serverip) {
		System.out.println(pluginname);
		List<Server> list = serverinfoserver.getServerInfo();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("***************");
            Server s = (Server)list.get(i);
            System.out.println(s.getServerip()+"&&"+s.getRootname()+"&&"+s.getPwd());
		};
		System.out.println(serverip);
		String jsonString = pluginservice.getFormValue(pluginname,serverip);
		System.out.println(jsonString);
		if(jsonString =="null") {
			System.out.println("jsonstring ="+jsonString);
			return ResponesData.success("null");			
		}
		return ResponesData.success(jsonString);
	}

	@PostMapping("/setbd_recog")
	/*
	 * get data from web page about bd_recog
	 * 
	 */
	public ResponesData setbd_recog(HttpServletRequest req, @RequestBody(required = false) Bd_recog fbs) throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
//		String useridString = usermap.get("id");
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(fbs);
			System.out.println(json);
			Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "bd_recog", usernameString, json);
			if (resultBool == true) {
				String path = ResourceUtils.getURL("classpath:").getPath();
				String apath = path + "plugin/bd_recog.xml";
				//①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
				DocumentBuilder newDocumentBuilder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//②获取一个与磁盘文件关联的非空Document对象 
				Document doc = newDocumentBuilder.parse(apath);
				//③通过文档对象获得该文档对象的根节点 
				Element root = doc.getDocumentElement(); //查找指定节点
				
				NodeList asrList = root.getElementsByTagName("asr");
				
				Class cls = fbs.getClass();  
	            Field[] fields = cls.getDeclaredFields();
	            int lenth = fields.length;
	            Node[] nodelist =new Node[lenth]; 
	            for(int i=0; i< lenth; i++){  
	                Field f = fields[i];  
	                f.setAccessible(true);  
	                nodelist[i] = doc.createAttribute(f.getName());
		   			nodelist[i].setNodeValue(f.get(fbs).toString());
	                System.out.println("属性名:" + f.getName() + " 属性值:" + f.get(fbs));  
	                asrList.item(0).getAttributes().setNamedItem(nodelist[i]);
	            }   
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		   		transformer.transform(new DOMSource(doc), new StreamResult(apath));
		   		 
		   		String string =FileUtil.testfile("bd_recog.xml");
				SSHUtil ssh = new SSHUtil();
				String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh bd_recog.xml \'"+string+"\'";
				Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
				ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
				return ResponesData.success("setup success!!");
			} else {
				return ResponesData.success("setup false,please check it up!!");
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponesData.success("setup false,please check it up!!");
	}

	@PostMapping("/setbd_synth")
	/*
	 * get data from web page about bd_synth
	 * 
	 */
	public ResponesData setbd_synth(HttpServletRequest req, @RequestBody(required = false) Bd_synth fbs) throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(fbs);
			System.out.println(json);
			Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "bd_synth", usernameString, json);
			if (resultBool == true) {
				String path = ResourceUtils.getURL("classpath:").getPath();
				String apath = path + "plugin/bd_synth.xml";
				//①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
				DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//②获取一个与磁盘文件关联的非空Document对象 
				Document doc = newDocumentBuilder.parse(apath);
				//③通过文档对象获得该文档对象的根节点 
				Element root = doc.getDocumentElement(); //查找指定节点
				
				NodeList asrList = root.getElementsByTagName("tts");
				
				Class cls = fbs.getClass();  
	            Field[] fields = cls.getDeclaredFields();
	            int lenth = fields.length;
	            Node[] nodelist =new Node[lenth]; 
	            for(int i=0; i < lenth; i++){  
	                Field f = fields[i];  
	                f.setAccessible(true);  
	                nodelist[i] = doc.createAttribute(f.getName());
		   			nodelist[i].setNodeValue(f.get(fbs).toString());
	                System.out.println("属性名:" + f.getName() + " 属性值:" + f.get(fbs));  
	                asrList.item(0).getAttributes().setNamedItem(nodelist[i]);
	            }   
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		   		 // Source source = new DOMSource(doc); 
		   		 // Result result = newStreamResult(file); 
		   		 // transformer.transform(source, result);
		   		 transformer.transform(new DOMSource(doc), new StreamResult(apath));
		   		 
		   		String string =FileUtil.testfile("bd_synth.xml");
				SSHUtil ssh = new SSHUtil();
				String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh bd_synth.xml \'"+string+"\'";
				Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
				ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
				return ResponesData.success("setup success!!");
			} else {
				return ResponesData.success("setup false,please check it up!!");
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponesData.success("setup false,please check it up!!");
	}

	@PostMapping("/setaliyun_recog")
	/*
	 * get data from web page about aliyun_recog
	 * 
	 */
	public ResponesData setaliyun_recog(HttpServletRequest req, @RequestBody(required = false) Aliyun_recog fbs) throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(fbs);
			System.out.println(json);
			Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "aliyun_recog", usernameString, json);
			if (resultBool == true) {
				String path = ResourceUtils.getURL("classpath:").getPath();
				String apath = path + "plugin/aliyun_recog.xml";
				//①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
				DocumentBuilder newDocumentBuilder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//②获取一个与磁盘文件关联的非空Document对象 
				Document doc = newDocumentBuilder.parse(apath);
				//③通过文档对象获得该文档对象的根节点 
				Element root = doc.getDocumentElement(); //查找指定节点
				
				NodeList asrList = root.getElementsByTagName("asr");
				
				Class cls = fbs.getClass();  
	            Field[] fields = cls.getDeclaredFields();
	            int lenth = fields.length;
	            Node[] nodelist =new Node[lenth]; 
	            for(int i=0; i< lenth; i++){  
	                Field f = fields[i];  
	                f.setAccessible(true);  
	                nodelist[i] = doc.createAttribute(f.getName());
		   			nodelist[i].setNodeValue(f.get(fbs).toString());
	                System.out.println("属性名:" + f.getName() + " 属性值:" + f.get(fbs));  
	                asrList.item(0).getAttributes().setNamedItem(nodelist[i]);
	            }   
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		   		 // Source source = new DOMSource(doc); 
		   		 // Result result = newStreamResult(file); 
		   		 // transformer.transform(source, result);
		   		 transformer.transform(new DOMSource(doc), new StreamResult(apath));
		   		 
		   		String string =FileUtil.testfile("aliyun_recog.xml");
				SSHUtil ssh = new SSHUtil();
				String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh aliyun_recog.xml \'"+string+"\'";
				//ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
				Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
				ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
				return ResponesData.success("setup success!!");
			} else {
				return ResponesData.success("setup false,please check it up!!");
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponesData.success("setup false,please check it up!!");
	}

	@PostMapping("/setaliyun_synth")
	/*
	 * get data from web page about bd_synth
	 * 
	 */
	public ResponesData setaliyun_synth(HttpServletRequest req, @RequestBody(required = false) Aliyun_synth fbs) throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
		String useridString = usermap.get("id");
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(fbs);
			System.out.println(json);
			Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "aliyun_synth", usernameString, json);
			if (resultBool == true) {
				String path = ResourceUtils.getURL("classpath:").getPath();
				String apath = path + "plugin/aliyun_synth.xml";
				//①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
				DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//②获取一个与磁盘文件关联的非空Document对象 
				Document doc = newDocumentBuilder.parse(apath);
				//③通过文档对象获得该文档对象的根节点 
				Element root = doc.getDocumentElement(); //查找指定节点
				
				NodeList asrList = root.getElementsByTagName("tts");
				
				Class cls = fbs.getClass();  
	            Field[] fields = cls.getDeclaredFields();
	            int lenth = fields.length;
	            Node[] nodelist =new Node[lenth]; 
	            for(int i=0; i < lenth; i++){  
	                Field f = fields[i];  
	                f.setAccessible(true);  
	                nodelist[i] = doc.createAttribute(f.getName());
		   			nodelist[i].setNodeValue(f.get(fbs).toString());
	                System.out.println("属性名:" + f.getName() + " 属性值:" + f.get(fbs));  
	                asrList.item(0).getAttributes().setNamedItem(nodelist[i]);
	            }   
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		   		 // Source source = new DOMSource(doc); 
		   		 // Result result = newStreamResult(file); 
		   		 // transformer.transform(source, result);
		   		 transformer.transform(new DOMSource(doc), new StreamResult(apath));
		   		 
		   		String string =FileUtil.testfile("aliyun_synth.xml");
				SSHUtil ssh = new SSHUtil();
				String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh aliyun_synth.xml \'"+string+"\'";
				//ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
				Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
				ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
				return ResponesData.success("setup success!!");
			} else {
				return ResponesData.success("setup false,please check it up!!");
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponesData.success("setup false,please check it up!!");
	}

	@PostMapping("/setpq_recog")
	/*
	 * get data from web page about aliyun_recog
	 * 
	 */
	public ResponesData setpq_recog(HttpServletRequest req, @RequestBody(required = false) Pq_recog fbs) throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
		String useridString = usermap.get("id");
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(fbs);
			System.out.println(json);
			Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "pq_recog", usernameString, json);
			if (resultBool == true) {
				String path = ResourceUtils.getURL("classpath:").getPath();
				String apath = path + "plugin/pq_recog.xml";
				//①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
				DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//②获取一个与磁盘文件关联的非空Document对象 
				Document doc = newDocumentBuilder.parse(apath);
				//③通过文档对象获得该文档对象的根节点 
				Element root = doc.getDocumentElement(); //查找指定节点
				
				NodeList asrList = root.getElementsByTagName("asr");
				NodeList recog_result = root.getElementsByTagName("recog_result");
				
                Node filename = doc.createAttribute("filename");
                filename.setNodeValue(fbs.getFilename());
                recog_result.item(0).getAttributes().setNamedItem(filename);
                
                Node url = doc.createAttribute("url");
                url.setNodeValue(fbs.getUrl());
                asrList.item(0).getAttributes().setNamedItem(url);
                
                Node host = doc.createAttribute("host");
                host.setNodeValue(fbs.getHost());
                asrList.item(0).getAttributes().setNamedItem(host);
                  
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		   		 // Source source = new DOMSource(doc); 
		   		 // Result result = newStreamResult(file); 
		   		 // transformer.transform(source, result);
		   		 transformer.transform(new DOMSource(doc), new StreamResult(apath));
		   		 
		   		String string =FileUtil.testfile("pq_recog.xml");
				SSHUtil ssh = new SSHUtil();
				String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh pq_recog.xml \'"+string+"\'";
				//ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
				Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
				ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
				return ResponesData.success("setup success!!");
			} else {
				return ResponesData.success("setup false,please check it up!!");
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponesData.success("setup false,please check it up!!");
	}

	@PostMapping("/setpq_synth")
	/*
	 * get data from web page about pq_synth
	 * 
	 */
	public ResponesData setpq_synth(HttpServletRequest req, @RequestBody(required = false) Pq_synth fbs) throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
		String useridString = usermap.get("id");
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(fbs);
			System.out.println(json);
			Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "pq_synth", usernameString, json);
			if (resultBool == true) {
				String path = ResourceUtils.getURL("classpath:").getPath();
				String apath = path + "plugin/pq_synth.xml";
				//①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
				DocumentBuilder newDocumentBuilder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//②获取一个与磁盘文件关联的非空Document对象 
				Document doc = newDocumentBuilder.parse(apath);
				//③通过文档对象获得该文档对象的根节点 
				Element root = doc.getDocumentElement(); //查找指定节点
				
				NodeList fileList = root.getElementsByTagName("file");
				NodeList synthList = root.getElementsByTagName("synth");
				
				Class cls = fbs.getClass();  
	            Field[] fields = cls.getDeclaredFields();
	            int lenth = fields.length;
	            Node[] nodelist =new Node[lenth]; 
	            for(int i=0; i< lenth; i++){  
	                Field f = fields[i];  
	                f.setAccessible(true);  
	                nodelist[i] = doc.createAttribute(f.getName());
		   			nodelist[i].setNodeValue(f.get(fbs).toString());
	                System.out.println("属性名:" + f.getName() + " 属性值:" + f.get(fbs));
	                if(f.getName().equals("filename")) {
	                	fileList.item(0).getAttributes().setNamedItem(nodelist[i]);	                	
	                }else{
	                	synthList.item(0).getAttributes().setNamedItem(nodelist[i]);	 
	                }
	            }   
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		   		 // Source source = new DOMSource(doc); 
		   		 // Result result = newStreamResult(file); 
		   		 // transformer.transform(source, result);
		   		 transformer.transform(new DOMSource(doc), new StreamResult(apath));
		   		 
		   		String string =FileUtil.testfile("pq_synth.xml");
				SSHUtil ssh = new SSHUtil();
				String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh pq_synth.xml \'"+string+"\'";
				//ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
				Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
				ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
				return ResponesData.success("setup success!!");
			} else {
				return ResponesData.success("setup false,please check it up!!");
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponesData.success("setup false,please check it up!!");
	}

	@PostMapping("/setsxs_recog")
	/*
	 * get data from web page about sxs_recog
	 * 
	 */
	public ResponesData setsxs_recog(HttpServletRequest req, @RequestBody(required = false) Sxs_recog fbs) throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
		String useridString = usermap.get("id");
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(fbs);
			System.out.println(json);
			Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "sxs_recog", usernameString, json);
			if (resultBool == true) {
				String path = ResourceUtils.getURL("classpath:").getPath();
				String apath = path + "plugin/sxs_recog.xml";
				//①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
				DocumentBuilder newDocumentBuilder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//②获取一个与磁盘文件关联的非空Document对象 
				Document doc = newDocumentBuilder.parse(apath);
				//③通过文档对象获得该文档对象的根节点 
				Element root = doc.getDocumentElement(); //查找指定节点
				
				NodeList asrList = root.getElementsByTagName("asr");
				
				Class cls = fbs.getClass();  
	            Field[] fields = cls.getDeclaredFields();
	            int lenth = fields.length;
	            Node[] nodelist =new Node[lenth]; 
	            for(int i=0; i< lenth; i++){  
	                Field f = fields[i];  
	                f.setAccessible(true);  
	                nodelist[i] = doc.createAttribute(f.getName());
		   			nodelist[i].setNodeValue(f.get(fbs).toString());
	                System.out.println("属性名:" + f.getName() + " 属性值:" + f.get(fbs));  
	                asrList.item(0).getAttributes().setNamedItem(nodelist[i]);
	            }   
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		   		 // Source source = new DOMSource(doc); 
		   		 // Result result = newStreamResult(file); 
		   		 // transformer.transform(source, result);
		   		 transformer.transform(new DOMSource(doc), new StreamResult(apath));
		   		 
		   		String string =FileUtil.testfile("sxs_recog.xml");
				SSHUtil ssh = new SSHUtil();
				String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh sxs_recog.xml \'"+string+"\'";
				//ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
				Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
				ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
				return ResponesData.success("setup success!!");
			} else {
				return ResponesData.success("setup false,please check it up!!");
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponesData.success("setup false,please check it up!!");
	}

	@PostMapping("/setsxs_synth")
	/*
	 * get data from web page about sxs_synth
	 * 
	 */
	public ResponesData setsxs_synth(HttpServletRequest req, @RequestBody(required = false) Sxs_synth fbs) throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
		String useridString = usermap.get("id");
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("test");
		try {
			json = mapper.writeValueAsString(fbs);
			System.out.println("*********************");
			System.out.println(json);
			Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "sxs_synth", usernameString, json);
			if (resultBool == true) {
				System.out.println("test bool");
				String path = ResourceUtils.getURL("classpath:").getPath();
				String apath = path + "plugin/sxs_synth.xml";
				//①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
				DocumentBuilder newDocumentBuilder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//②获取一个与磁盘文件关联的非空Document对象 
				Document doc = newDocumentBuilder.parse(apath);
				//③通过文档对象获得该文档对象的根节点 
				Element root = doc.getDocumentElement(); //查找指定节点
				
				NodeList fileList = root.getElementsByTagName("file");
				NodeList synthList = root.getElementsByTagName("synth");
				
				Class cls = fbs.getClass();  
	            Field[] fields = cls.getDeclaredFields();
	            int lenth = fields.length;
	            Node[] nodelist =new Node[lenth]; 
	            for(int i=0; i< lenth; i++){  
	                Field f = fields[i];  
	                f.setAccessible(true);  
	                nodelist[i] = doc.createAttribute(f.getName());
		   			nodelist[i].setNodeValue(f.get(fbs).toString());
	                System.out.println("属性名:" + f.getName() + " 属性值:" + f.get(fbs));
	                if(f.getName().equals("filename")) {
	                	fileList.item(0).getAttributes().setNamedItem(nodelist[i]);	                	
	                }else{
	                	synthList.item(0).getAttributes().setNamedItem(nodelist[i]);	 
	                }
	            }   
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		   		 // Source source = new DOMSource(doc); 
		   		 // Result result = newStreamResult(file); 
		   		 // transformer.transform(source, result);
		   		transformer.transform(new DOMSource(doc), new StreamResult(apath));
		   		 
		   		String string =FileUtil.testfile("sxs_synth.xml");
				SSHUtil ssh = new SSHUtil();
				String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh sxs_synth.xml \'"+string+"\'";
				//ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
				Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
				System.out.println(fbs.getServerip());
				System.out.println(server.getRootname());
				System.out.println(server.getPwd());
				System.out.println(cmdString);
				try {
					String result = ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
					System.out.println("result = "+result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("**********aa*********");
				return ResponesData.success("setup success!!");
			} else {
				return ResponesData.success("setup false,please check it up!!");
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponesData.success("setup false,please check it up!!");
	}

	@PostMapping("/setmrcp_conf")
	/*
	 * get data from web page about unimrcp_config
	 * 
	 */
	public ResponesData setmrcp_conf(HttpServletRequest req, @RequestBody(required = false) Mrcp fbs)
			throws IOException {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
		String useridString = usermap.get("id");
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		json = mapper.writeValueAsString(fbs);
		System.out.println(json);
		Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "mrcp_conf", usernameString, json);
		// 修改ini文件
		DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
		org.springframework.core.io.Resource resource = defaultResourceLoader
				.getResource("classpath:/plugin/mrcp.conf");
		File file = resource.getFile();
		Ini ini = new Ini();
		ini.load(file);
		Section section = ini.get("speech-nuance5-mrcp2");
		section.put("server-ip", fbs.getServer_ip());
		section.put("client-ip", fbs.getClient_ip());
		section.put("server-port", fbs.getServer_port());
		section.put("client-port", fbs.getClient_port());
		section.put("rtp-ip", fbs.getRtp_ip());
		ini.store(file);

		// 文件的内容
		String string = FileUtil.testfile("mrcp.conf");

		SSHUtil ssh = new SSHUtil();
//		String dropn = filecontentString.replaceAll("\n", " \\\\n ");
//		String cmdString = "echo -e '" + filecontentString + "' > /usr/local/unimrcp/conf_test/mrcp.conf";
		String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh mrcp.conf \'"+string+"\'";
		//ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
		Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
		ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);

		return ResponesData.success("success!");
	}

	@PostMapping("/setunimrcp_server")
	/*
	 * get data from web page about unimrcp_server
	 * 
	 */
	public void setunimrcp_server(HttpServletRequest req, @RequestBody(required = false) Unimrcpserver fbs)
			throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
		String useridString = usermap.get("id");
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		json = mapper.writeValueAsString(fbs);
		System.out.println(json);
		Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "unimrcp_server", usernameString, json);
		System.out.println(fbs.toString());
		String path = ResourceUtils.getURL("classpath:").getPath();
		String apath = path + "plugin/unimrcpserver.xml";
		
		 //①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
		 DocumentBuilder newDocumentBuilder =
		 DocumentBuilderFactory.newInstance().newDocumentBuilder();
		 //②获取一个与磁盘文件关联的非空Document对象 
		 Document doc = newDocumentBuilder.parse(apath);
		 //③通过文档对象获得该文档对象的根节点 
		 Element root = doc.getDocumentElement(); //查找指定节点
		 
		 //通过根节点获得子节点 
		 NodeList propertiesList = root.getElementsByTagName("properties");
		 NodeList componentsList = root.getElementsByTagName("components");
		 
		 //这里获取第1个节点 
		 Node propertiesitem = propertiesList.item(0); 
		 Element propertiesElement = (Element)propertiesitem;
		 Node componentsitem = componentsList.item(0); 
		 Element componentsElement = (Element)componentsitem; 
		 
//		 //获取personElement下面的子节点 
		 NodeList ipList = propertiesElement.getElementsByTagName("ip");
		 NodeList mrcpv2_uasList = componentsElement.getElementsByTagName("mrcpv2-uas"); 
		 
		 ipList.item(0).setTextContent(fbs.getIp());
		 mrcpv2_uasList.item(0).setTextContent(fbs.getMrcp_port());
		 mrcpv2_uasList.item(0).setTextContent(fbs.getMrcp_ip());
		 
		 NodeList resourceList = root.getElementsByTagName("resource");
		 NodeList engineList = root.getElementsByTagName("engine");

		 Node[] truenodelist =new Node[20]; 
		 for (int i = 0; i < 20; i++) {
			 truenodelist[i] = doc.createAttribute("enable");
			 truenodelist[i].setNodeValue("true");
		}
		 Node[] falsenodelist =new Node[20]; 
		 for (int i = 0; i < 20; i++) {
			 falsenodelist[i] = doc.createAttribute("enable");
			 falsenodelist[i].setNodeValue("false");
		}
		 
		 resourceList.item(0).getAttributes().setNamedItem((fbs.getSpeechsynth().equals("true"))? truenodelist[0] :falsenodelist[0]);
		 resourceList.item(1).getAttributes().setNamedItem((fbs.getSpeechrecog().equals("true"))? truenodelist[1] :falsenodelist[1]);
		 resourceList.item(2).getAttributes().setNamedItem((fbs.getRecorder().equals("true"))? truenodelist[2] :falsenodelist[2]);
		 resourceList.item(3).getAttributes().setNamedItem((fbs.getSpeakverify().equals("true"))? truenodelist[3] :falsenodelist[3]);
		 
		 engineList.item(3).getAttributes().setNamedItem((fbs.getMrcprecorder().equals("true"))? truenodelist[4] :falsenodelist[4]);
		 engineList.item(4).getAttributes().setNamedItem((fbs.getPlugin_start().equals("bd"))? truenodelist[5] :falsenodelist[5]);
		 engineList.item(5).getAttributes().setNamedItem((fbs.getPlugin_start().equals("bd"))? truenodelist[6] :falsenodelist[6]);
		 engineList.item(6).getAttributes().setNamedItem((fbs.getPlugin_start().equals("aliyun"))? truenodelist[7] :falsenodelist[7]);
		 engineList.item(7).getAttributes().setNamedItem((fbs.getPlugin_start().equals("aliyun"))? truenodelist[8] :falsenodelist[8]);
		 engineList.item(8).getAttributes().setNamedItem((fbs.getPlugin_start().equals("sxs"))? truenodelist[9] :falsenodelist[9]);
		 engineList.item(9).getAttributes().setNamedItem((fbs.getPlugin_start().equals("sxs"))? truenodelist[10] :falsenodelist[10]);
		 engineList.item(10).getAttributes().setNamedItem((fbs.getPlugin_start().equals("pq"))? truenodelist[11] :falsenodelist[11]);
		 engineList.item(11).getAttributes().setNamedItem((fbs.getPlugin_start().equals("pq"))? truenodelist[12] :falsenodelist[12]);
		 
//		 NodeList resource-factoryList = componentsList.getElementsByTagName("resource-factory");

		 //注意：XML文件是被加载到内存中 修改也是在内存中 ==》因此需要将内存中的数据同步到磁盘中
		 /*
		 *static TransformerFactory newInstance():获取 TransformerFactory 的新实例。 abstract
		 *Transformer newTransformer():创建执行从 Source 到 Result 的复制的新 Transformer。
		 *abstract void transform(Source xmlSource, Result outputTarget):将 XML Source转换为 Result。
		 */
		 Transformer transformer = TransformerFactory.newInstance().newTransformer();
		 // Source source = new DOMSource(doc); 
		 // Result result = newStreamResult(file); 
		 // transformer.transform(source, result);
		 transformer.transform(new DOMSource(doc), new StreamResult(apath));
		 
		 String string =FileUtil.testfile("unimrcpserver.xml");
		 SSHUtil ssh = new SSHUtil();
		 String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh unimrcpserver.xml \'"+string+"\'";
		//ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
		Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
		ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
}

	@PostMapping("/setlogger")
	/*
	 * get data from web page about Logger
	 * 
	 */
	public ResponesData setlogger(HttpServletRequest req, @RequestBody(required = false) Logger fbs) throws Exception {
		String usernameString = (String) req.getSession().getAttribute("username");
		Map<String, String> usermap = userClient.findByUsername(usernameString);
		String useridString = usermap.get("id");
//		usermap.forEach((key, value) -> {
//			System.out.println(key + ":" + value);
//		});
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(fbs);
			System.out.println(json);
			Boolean resultBool = pluginservice.SavePluginService(fbs.getServerip(), "logger", usernameString, json);
			if (resultBool == true) {
				String path = ResourceUtils.getURL("classpath:").getPath();
				String apath = path + "plugin/logger.xml";
				//①获得解析器DocumentBuilder的工厂实例DocumentBuilderFactory 然后拿到DocumentBuilder对象
				DocumentBuilder newDocumentBuilder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//②获取一个与磁盘文件关联的非空Document对象 
				Document doc = newDocumentBuilder.parse(apath);
				//③通过文档对象获得该文档对象的根节点 
				Element root = doc.getDocumentElement(); //查找指定节点
				
				NodeList outputList = root.getElementsByTagName("output");
				NodeList headersList = root.getElementsByTagName("headers");
				NodeList priorityList = root.getElementsByTagName("priority");
				NodeList maskingList = root.getElementsByTagName("masking");
				
				outputList.item(0).setTextContent(fbs.getOutput());
				headersList.item(0).setTextContent(fbs.getHeaders());
				priorityList.item(0).setTextContent(fbs.getPriority());
				maskingList.item(0).setTextContent(fbs.getMasking());
				
				
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		   		 // Source source = new DOMSource(doc); 
		   		 // Result result = newStreamResult(file); 
		   		 // transformer.transform(source, result);
		   		 transformer.transform(new DOMSource(doc), new StreamResult(apath));
		   		 
		   		String string =FileUtil.testfile("logger.xml");
				SSHUtil ssh = new SSHUtil();
				String cmdString ="cd /root/sh_wwz/ && ./replace_xml.sh logger.xml \'"+string+"\'";
				//ssh.run("125.91.33.50", "root", "124!@$qweQWE", cmdString);
				Server server = serverinfoserver.getServerBySeverIp(fbs.getServerip());
				ssh.run(fbs.getServerip(), server.getRootname(), server.getPwd(), cmdString);
			} else {
				return ResponesData.success("setup false,please check it up!!");
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponesData.success("setup false,please check it up!!");
	}
	
	//通过服务器名称获取启用的插件以及unimrcp启用状态	
	@RequestMapping("/get_plugin_which")
	public String get_plugin_which(@RequestParam("serverip") String serverip) throws Exception {
		String string = serverinfoserver.get_plugin_which(serverip); 
		
		SSHUtil ssh = new SSHUtil();
		String cmdString =" cd /root/sh_wwz && ./status_unimrcp.sh";
		Server server = serverinfoserver.getServerBySeverIp(serverip);
		String statusstring = ssh.run(serverip, server.getRootname(), server.getPwd(), cmdString);
		
		
		if(string == null)
		//System.out.println(string);
		{
			ObjectMapper mapper = new ObjectMapper();
			String nullString = mapper.writeValueAsString("null");
			return nullString;
		}
		
		return string;
	}
	
	//重启服务器unimrcp服务	
	@RequestMapping("/restart_plugin")
	public String restart_plugin(@RequestParam("serverip") String serverip) throws Exception {
		SSHUtil ssh = new SSHUtil();
		String cmdString =" cd /root/sh_wwz && ./restart_unimrcp.sh";
		Server server = serverinfoserver.getServerBySeverIp(serverip);
		Thread thread = new Thread(serverip);
		
//		String string = ssh.run(serverip, server.getRootname(), server.getPwd(), cmdString);
		String string = thread.run(ssh, server, serverip, cmdString);
		System.out.println("**************************");
		System.out.println(string);
		return string;
	}
	
	//获得服务器unimrcp服务开启状态	
	@RequestMapping("/status_plugin")
	public String status_plugin(@RequestParam("serverip") String serverip) throws Exception {
		System.out.println(serverip);
		SSHUtil ssh = new SSHUtil();
		String cmdString =" cd /root/sh_wwz && ./status_unimrcp.sh";
		Server server = serverinfoserver.getServerBySeverIp(serverip);
//		String string = ssh.run(serverip, server.getRootname(), server.getPwd(), cmdString);
		Thread thread = new Thread(serverip);
		String string = thread.run(ssh, server, serverip, cmdString);

		System.out.println(string);
		return string;
	}
}
