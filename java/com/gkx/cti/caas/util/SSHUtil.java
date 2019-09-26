package com.gkx.cti.caas.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSHUtil{
	private static String DEFAULTCHART = "UTF-8";

	private static Connection login(String ip, String username, String password) {
		System.out.println("login start");
		boolean flag = false;
		Connection connection = null;
		try {
			connection = new Connection(ip);
			connection.connect();// 连接
			flag = connection.authenticateWithPassword(username, password);// 认证
			if (flag) {
				System.out.println("================登录成功==================");
				return connection;
			}
		} catch (IOException e) {
			System.out.println("=========登录失败=========" + e);
			connection.close();
		}
		return connection;
	}

	/**
     * 远程执行shll脚本或者命令
     * 
     * @param cmd
     *            即将执行的命令
     * @return 命令执行完后返回的结果值
     */
	private static String execmd(Connection connection, String cmd) {
		String result = "";
		try{
			if (connection != null) {
				Session session = connection.openSession();// 打开一个会话
				session.execCommand(cmd);// 执行命令
				result = processStdout(session.getStdout(), DEFAULTCHART);
				connection.close();
				session.close();
				}
			} catch (IOException e) {
			System.out.println("执行命令失败,链接conn:" + connection + ",执行的命令：" + cmd + "  " + e);
			e.printStackTrace();
		}
		return result;
		}

	/**
     * 解析脚本执行返回的结果集
     * 
     * @param in
     *            输入流对象
     * @param charset
     *            编码
     * @return 以纯文本的格式返回
     */
	private static String processStdout(InputStream in, String charset) {
		InputStream stdout = new StreamGobbler(in);
		StringBuffer buffer = new StringBuffer();
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line + "\n");
				if(line.indexOf("ttl=")>0 && line.indexOf("time=")>0) {
					br.close();
					break;
				}
				if(line.indexOf("UniMRCP Server")>0 ) {
					br.close();
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("解析脚本出错：" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("解析脚本出错：" + e.getMessage());
			e.printStackTrace();
		}
		return buffer.toString();
	}

	public static String run(String ip,String username,String password,String cmd) {
		Connection connection = login(ip, username, password);
		String execmd = execmd(connection, cmd);
		return execmd;
		}
}

